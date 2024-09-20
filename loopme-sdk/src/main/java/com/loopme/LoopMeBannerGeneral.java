package com.loopme;

import static com.loopme.Constants.DISMISS_AD_REASON.EXPIRED;
import static com.loopme.Constants.DISMISS_AD_REASON.HIDE;
import static com.loopme.Constants.DISMISS_AD_REASON.LOAD_FAIL;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.UiUtils;

/**
 * The `LoopMeBanner` class provides facilities to display a custom size ads
 * during natural transition points in your application.
 * <p>
 * It is recommended to implement `LoopMeBanner.Listener` to stay informed about ad state changes,
 * such as when an ad has been loaded or has failed to load its content, when video ad has been watched completely,
 * when an ad has been presented or dismissed from the screen, and when an ad has expired or received a tap.
 */
public class LoopMeBannerGeneral extends LoopMeAd {

    private static final String LOG_TAG = LoopMeBannerGeneral.class.getSimpleName();

    private Listener mAdListener;
    public void setListener(@NonNull Listener listener) { mAdListener = listener; }
    public Listener getListener() { return mAdListener; }
    @Override
    public void removeListener() { mAdListener = null; }

    private volatile FrameLayout mBannerView;

    private int mWidth = 0;
    private int mHeight = 0;
    public int getWidth() { return mWidth; }
    public int getHeight() { return mHeight; }
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public interface Listener {
        void onLoopMeBannerLoadSuccess(LoopMeBannerGeneral banner);
        void onLoopMeBannerLoadFail(LoopMeBannerGeneral banner, LoopMeError error);
        void onLoopMeBannerShow(LoopMeBannerGeneral banner);
        void onLoopMeBannerHide(LoopMeBannerGeneral banner);
        void onLoopMeBannerClicked(LoopMeBannerGeneral banner);
        void onLoopMeBannerLeaveApp(LoopMeBannerGeneral banner);
        void onLoopMeBannerVideoDidReachEnd(LoopMeBannerGeneral banner);
        void onLoopMeBannerExpired(LoopMeBannerGeneral banner);
    }

    public LoopMeBannerGeneral(@NonNull Activity activity, @NonNull String appKey) {
        super(activity, appKey);
        Logging.out(LOG_TAG, "Start creating banner with app key: " + appKey);
    }

    public static LoopMeBannerGeneral getInstance(@NonNull String appKey, @NonNull Activity activity) {
        return new LoopMeBannerGeneral(activity, appKey);
    }

    @Override
    public void destroy() {
        runOnUiThread(() -> {
            dismiss();
            LoopMeBannerGeneral.super.destroy();
        });
    }

    /**
     * @param frameLayout - @link FrameLayout (container for ad) where ad will be displayed.
     */
    public void bindView(FrameLayout frameLayout) {
        if (frameLayout != null) {
            mBannerView = frameLayout;
            mContainerView = frameLayout;
        } else {
            LoopMeTracker.post(packErrorInfo("Bind view is null"));
        }
    }

    public FrameLayout getBannerView() { return mBannerView; }
    public boolean isViewBinded() { return mBannerView != null; }

    @Override
    public void show() {
        if (!isReady()) {
            Logging.out(LOG_TAG, "Ad is not ready");
            return;
        }
        if (isShowing()) {
            Logging.out(LOG_TAG, "Ad is already showing");
            return;
        }
        if (!isViewBinded()) {
            Logging.out(LOG_TAG, "Ad view is not bound");
            return;
        }
        showInternal();
        getDisplayController().postImpression();
        if (isLoopMeAd() || isMraidAd()) {
            resume();
        } else if (isVastAd() || isVpaidAd() && mDisplayController != null) {
            getDisplayController().onPlay(0);
        }
        Logging.out(LOG_TAG, "Ad did start showing");
    }

    private void showInternal() {
        setAdState(Constants.AdState.SHOWING);
        stopTimer();
        mContainerView = mBannerView;
        buildAdView();
        mBannerView.setVisibility(View.VISIBLE);
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerShow(this);
        }
        Logging.out(LOG_TAG, "Ad appeared on screen");
    }

    @Override
    public void pause() {
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).setWebViewState(Constants.WebviewState.HIDDEN);
        } else {
            super.pause();
        }
    }

    @NonNull
    @Override
    public Constants.AdFormat getAdFormat() { return Constants.AdFormat.BANNER; }

    @Override
    public Constants.PlacementType getPlacementType() { return Constants.PlacementType.BANNER; }

    @Override
    public AdSpotDimensions getAdSpotDimensions() {
        if (mBannerView != null) {
            int width = mBannerView.getLayoutParams().width;
            int height = mBannerView.getLayoutParams().height;
            return new AdSpotDimensions(width, height);
        }
        return new AdSpotDimensions(0, 0);
    }

    @Override
    public void onAdAlreadyLoaded() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLoadSuccess(this);
        }
    }

    /**
     * Triggered when your application is about to go to the background, initiated by the SDK.
     * This may happen in various ways, f.e if user wants open the SDK's browser web page in native browser or clicks
     * on `mailto:` links...
     */
    @Override
    public void onAdLeaveApp() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLeaveApp(LoopMeBannerGeneral.this);
        }
        Logging.out(LOG_TAG, "Leaving application");
    }

    /**
     * Triggered when the user taps the banner ad and the banner is about to perform extra actions
     * Those actions may lead to displaying a modal browser or leaving your application.
     */
    @Override
    public void onAdClicked() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerClicked(this);
        }
        Logging.out(LOG_TAG, "Ad received click event");
    }

    /**
     * Triggered only when banner's video was played until the end.
     * It won't be sent if the video was skipped or the banner was dissmissed during the displaying process
     */
    @Override
    public void onAdVideoDidReachEnd() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerVideoDidReachEnd(this);
        }
        Logging.out(LOG_TAG, "Video did reach end");
    }

    /**
     * Triggered when the banner has successfully loaded the ad content
     */
    @Override
    public void onAdLoadSuccess() {
        long currentTime = System.currentTimeMillis();
        long loadingTime = currentTime - mStartLoadingTime;

        setReady(true);
        setAdState(Constants.AdState.NONE);
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLoadSuccess(this);
        }
        Logging.out(LOG_TAG, "Ad successfully loaded (" + loadingTime + "ms)");
    }

    /**
     * Triggered when banner ad failed to load ad content
     * @param error - error of unsuccesful ad loading attempt
     */
    @Override
    public void onAdLoadFail(final LoopMeError error) { destroyAd(LOAD_FAIL, error); }

    /**
     * Triggered when the banner's loaded ad content is expired.
     * Expiration happens when loaded ad content wasn't displayed during some period of time, approximately one hour.
     * Once the banner is presented on the screen, the expiration is no longer tracked and banner won't
     * receive this message
     */
    @Override
    public void onAdExpired() { destroyAd(EXPIRED, null); }

    @Override
    public void dismiss() {
        Logging.out(LOG_TAG, "Ad will be dismissed");
        if (!isShowing()) {
            Logging.out(LOG_TAG, "Can't dismiss ad, it's not displaying");
            return;
        }
        if (!isNoneState()) {
            Logging.out(LOG_TAG, "Can't dismiss ad, Ad is not in NONE state");
            return;
        }
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).dismiss();
        }
        if (mBannerView != null) {
            mBannerView.setVisibility(View.GONE);
            mBannerView.removeAllViews();
        }
        destroyAd(HIDE, null);
    }

    private void destroyAd(String reason, LoopMeError error) {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        runOnUiThread(() -> {
            UiUtils.broadcastIntent(getContext(), Constants.DESTROY_INTENT, getAdId());
            if (mAdListener == null) return;
            if (EXPIRED.equals(reason))
                mAdListener.onLoopMeBannerExpired(this);
            if (LOAD_FAIL.equals(reason))
                mAdListener.onLoopMeBannerLoadFail(this, error);
            if (HIDE.equals(reason))
                mAdListener.onLoopMeBannerHide(this);
        });
        Logging.out(
            LOG_TAG,
            "Ad " + reason + (error == null ? "" : " with error: " + error.getMessage())
        );
    }
}
