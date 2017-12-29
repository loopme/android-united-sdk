package com.loopme;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.time.TimersType;
import com.loopme.tracker.partners.LoopMeTracker;

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
    public static final String TEST_MPU_BANNER = "test_mpu";
    private Listener mAdListener;

    private volatile FrameLayout mBannerView;

    private boolean mIsVideoFinished;

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

    public LoopMeBannerGeneral(Activity activity, String appKey) {
        super(activity, appKey);
        Logging.out(LOG_TAG, "Start creating banner with app key: " + appKey);
    }

    public static LoopMeBannerGeneral getInstance(String appKey, Activity activity) {
        return new LoopMeBannerGeneral(activity, appKey);
    }

    @Override
    public void destroy() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
                LoopMeBannerGeneral.super.destroy();
            }
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
            LoopMeTracker.post("Bind view is null");
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
        if (isPrepared()) {
            showInternal();
            Logging.out(LOG_TAG, "Banner did start showing ad (native)");
        } else {
            LoopMeTracker.post("Banner is not ready");
        }
    }

    @Override
    public void show() {
        if (isPrepared()) {
            showInternal();
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

    private boolean isPrepared() {
        return isReady() && isViewBinded() && !isShowing();
    }

    private void showInternal() {
        setAdState(Constants.AdState.SHOWING);
        stopTimer(TimersType.EXPIRATION_TIMER);
        mContainerView = mBannerView;
        buildAdView();
        mBannerView.setVisibility(View.VISIBLE);
        onLoopMeBannerShow();
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
        if (isLoopMeBannerShowing() && isVideoNotFinished()) {
            DisplayControllerLoopMe displayControllerLoopMe = (DisplayControllerLoopMe) getDisplayController();
            if (displayControllerLoopMe.isMinimizedModeEnable()) {
                displayControllerLoopMe.switchToMinimizedMode();
            } else {
                pause();
            }
        }
    }

    private boolean isLoopMeBannerShowing() {
        return isLoopMeController() && isShowing();
    }

    private boolean isVideoNotFinished() {
        return !mIsVideoFinished;
    }

    private boolean isLoopMeController() {
        return getDisplayController() instanceof DisplayControllerLoopMe;
    }

    public void playbackFinishedWithError() {
        mIsVideoFinished = true;
    }

    public void switchToNormalMode() {
        if (isLoopMeAd() && getDisplayController() != null && isShowing()) {
            ((DisplayControllerLoopMe) getDisplayController()).switchToNormalMode();
        }
    }

    @Override
    public int getAdFormat() {
        return Constants.AdFormat.BANNER;
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
     * Triggered when banner ad failed to load ad content
     *
     * @param error - error of unsuccesful ad loading attempt
     */
    private void onLoopMeBannerLoadFail(final LoopMeError error) {
        stopTimer(TimersType.FETCHER_TIMER);
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLoadFail(this, error);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
        Logging.out(LOG_TAG, "Ad fails to load: " + error.getMessage());
    }

    /**
     * Triggered when the banner has successfully loaded the ad content
     */
    private void onLoopMeBannerSuccessLoad() {
        stopTimer(TimersType.FETCHER_TIMER);
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

    /**
     * Triggered when the banner ad appears on the screen
     */
    private void onLoopMeBannerShow() {
        mIsVideoFinished = false;
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerShow(this);
        }
        Logging.out(LOG_TAG, "Ad appeared on screen");
    }

    /**
     * Triggered when the banner ad disappears on the screen
     */
    private void onLoopMeBannerHide() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerHide(this);
        }
        Logging.out(LOG_TAG, "Ad disappeared from screen");
    }

    /**
     * Triggered when the user taps the banner ad and the banner is about to perform extra actions
     * Those actions may lead to displaying a modal browser or leaving your application.
     */
    void onLoopMeBannerClicked() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerClicked(this);
        }
        Logging.out(LOG_TAG, "Ad received click event");
    }

    /**
     * Triggered when your application is about to go to the background, initiated by the SDK.
     * This may happen in various ways, f.e if user wants open the SDK's browser web page in native browser or clicks
     * on `mailto:` links...
     */
    void onLoopMeBannerLeaveApp() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLeaveApp(LoopMeBannerGeneral.this);
        }
        Logging.out(LOG_TAG, "Leaving application");
    }

    /**
     * Triggered only when banner's video was played until the end.
     * It won't be sent if the video was skipped or the banner was dissmissed during the displaying process
     */
    private void onLoopMeBannerVideoDidReachEnd() {
        mIsVideoFinished = true;
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerVideoDidReachEnd(this);
        }
        Logging.out(LOG_TAG, "Video did reach end");
    }

    /**
     * Triggered when the banner's loaded ad content is expired.
     * Expiration happens when loaded ad content wasn't displayed during some period of time, approximately one hour.
     * Once the banner is presented on the screen, the expiration is no longer tracked and banner won't
     * receive this message
     */
    private void onLoopMeBannerExpired() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerExpired(this);
        }
        Logging.out(LOG_TAG, "Ad content is expired");
    }

    @Override
    public void onAdExpired() {
        onLoopMeBannerExpired();
    }

    @Override
    public void onAdLoadSuccess() {
        onLoopMeBannerSuccessLoad();
    }

    @Override
    public void onAdAlreadyLoaded() {
        if (mAdListener != null) {
            mAdListener.onLoopMeBannerLoadSuccess(this);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
    }

    @Override
    public void onAdLoadFail(final LoopMeError error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onLoopMeBannerLoadFail(error);
            }
        });
    }

    @Override
    public void onAdLeaveApp() {
        onLoopMeBannerLeaveApp();
    }

    @Override
    public void onAdClicked() {
        onLoopMeBannerClicked();
    }

    @Override
    public void onAdVideoDidReachEnd() {
        onLoopMeBannerVideoDidReachEnd();
    }

    @Override
    public void dismiss() {
        Logging.out(LOG_TAG, "Banner will be dismissed");
        if (isShowing() || isNoneState()) {
            dismissController();
            destroyBannerView();
            onLoopMeBannerHide();
        } else {
            Logging.out(LOG_TAG, "Can't dismiss ad, it's not displaying");
        }
    }

    private void dismissController() {
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).dismiss();
        }
    }

    private void destroyBannerView() {
        if (mBannerView != null) {
            mBannerView.setVisibility(View.GONE);
            mBannerView.removeAllViews();
        }
    }
}
