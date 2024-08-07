package com.loopme;

import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.PLACEMENT_TYPE;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.time.TimersType;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

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

    private volatile FrameLayout mBannerView;

    private boolean mIsVideoFinished;
    private int mWidth = 0;
    private int mHeight = 0;

    public void setMinimizedMode(MinimizedMode mode) {
        if (isLoopMeController()) {
            ((DisplayControllerLoopMe) mDisplayController).setMinimizedMode(mode);
        }
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

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * @param frameLayout - @link FrameLayout (container for ad) where ad will be displayed.
     */
    public void bindView(FrameLayout frameLayout) {
        if (frameLayout != null) {
            mBannerView = frameLayout;
            mContainerView = frameLayout;
        } else {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Bind view is null");
            errorInfo.put(PLACEMENT_TYPE, getPlacementType().name().toLowerCase());
            LoopMeTracker.post(errorInfo);
        }
    }

    public FrameLayout getBannerView() {
        return mBannerView;
    }

    public boolean isViewBinded() {
        return mBannerView != null;
    }

    public void setListener(Listener listener) {
        if (listener != null) {
            mAdListener = listener;
        } else {
            Logging.out(LOG_TAG, "Warning listener is null.");
        }
    }

    public Listener getListener() {
        return mAdListener;
    }

    @Override
    public void removeListener() {
        mAdListener = null;
    }

    void showNativeVideo() {
        if (isReady() && isViewBinded() && !isShowing()) {
            showInternal();
            Logging.out(LOG_TAG, "Banner did start showing ad (native)");
        } else {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Banner is not ready");
            errorInfo.put(PLACEMENT_TYPE, getPlacementType().name().toLowerCase());
            LoopMeTracker.post(errorInfo);
        }
    }

    @Override
    public void show() {
        if (isReady() && isViewBinded() && !isShowing()) {
            showInternal();
            getDisplayController().postImpression();
            if (isLoopMeAd() || isMraidAd()) {
                resume();
            } else if (isVastAd() || isVpaidAd() && mDisplayController != null) {
                getDisplayController().onPlay(0);
            }
            Logging.out(LOG_TAG, "Banner did start showing ad");
        } else {
            Logging.out(LOG_TAG, "Banner is not ready");
        }
    }

    private void showInternal() {
        setAdState(Constants.AdState.SHOWING);
        stopTimer(TimersType.EXPIRATION_TIMER);
        mContainerView = mBannerView;
        buildAdView();
        mBannerView.setVisibility(View.VISIBLE);
        mIsVideoFinished = false;
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerShow(this);
        }
        Logging.out(LOG_TAG, "Ad appeared on screen");
    }

    @Override
    public void pause() {
        if (isLoopMeController()) {
            ((DisplayControllerLoopMe) mDisplayController).setWebViewState(Constants.WebviewState.HIDDEN);
        } else {
            super.pause();
        }
    }

    public void switchToMinimizedMode() {
        if (isLoopMeController() && isShowing() && !mIsVideoFinished) {
            DisplayControllerLoopMe displayControllerLoopMe = (DisplayControllerLoopMe) getDisplayController();
            if (displayControllerLoopMe.isMinimizedModeEnable()) {
                displayControllerLoopMe.switchToMinimizedMode();
            } else {
                pause();
            }
        }
    }

    private boolean isLoopMeController() {
        return getDisplayController() instanceof DisplayControllerLoopMe;
    }

    public void switchToNormalMode() {
        if (isLoopMeAd() && getDisplayController() != null && isShowing()) {
            ((DisplayControllerLoopMe) getDisplayController()).switchToNormalMode();
        }
    }

    @NonNull
    @Override
    public Constants.AdFormat getAdFormat() {
        return Constants.AdFormat.BANNER;
    }

    @Override
    public Constants.PlacementType getPlacementType() {
        return Constants.PlacementType.BANNER;
    }

    @Override
    public AdSpotDimensions getAdSpotDimensions() {
        if (mBannerView != null) {
            int width = mBannerView.getLayoutParams().width;
            int height = mBannerView.getLayoutParams().height;
            return new AdSpotDimensions(width, height);
        }
        return new AdSpotDimensions(0, 0);
    }

    /**
     * Triggered when the banner's loaded ad content is expired.
     * Expiration happens when loaded ad content wasn't displayed during some period of time, approximately one hour.
     * Once the banner is presented on the screen, the expiration is no longer tracked and banner won't
     * receive this message
     */
    @Override
    public void onAdExpired() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerExpired(this);
        }
        Logging.out(LOG_TAG, "Ad content is expired");
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
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
        Logging.out(LOG_TAG, "Ad successfully loaded (" + loadingTime + "ms)");
    }

    @Override
    public void onAdAlreadyLoaded() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLoadSuccess(this);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
    }

    /**
     * Triggered when banner ad failed to load ad content
     *
     * @param error - error of unsuccesful ad loading attempt
     */
    @Override
    public void onAdLoadFail(final LoopMeError error) {
        mHandler.post(() -> {
            setReady(false);
            setAdState(Constants.AdState.NONE);
            destroyDisplayController();
            if (mAdListener != null) {
                mAdListener.onLoopMeBannerLoadFail(this, error);
            } else {
                Logging.out(LOG_TAG, "Warning: empty listener");
            }
            Logging.out(LOG_TAG, "Ad fails to load: " + error.getMessage());
        });
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
        mIsVideoFinished = true;
        switchToNormalMode();
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerVideoDidReachEnd(this);
        }
        Logging.out(LOG_TAG, "Video did reach end");
    }

    @Override
    public void dismiss() {
        Logging.out(LOG_TAG, "Banner will be dismissed");
        if (isShowing() || isNoneState()) {
            if (mDisplayController instanceof DisplayControllerLoopMe) {
                ((DisplayControllerLoopMe) mDisplayController).dismiss();
            }
            if (mBannerView != null) {
                mBannerView.setVisibility(View.GONE);
                mBannerView.removeAllViews();
            }
            setReady(false);
            setAdState(Constants.AdState.NONE);
            destroyDisplayController();
            if (mAdListener != null) {
                mAdListener.onLoopMeBannerHide(this);
            }
            Log.d(LOG_TAG, "Ad disappeared from screen");
        } else {
            Log.d(LOG_TAG, "Can't dismiss ad, it's not displaying");
        }
    }
}
