package com.loopme;

import android.app.Activity;
import android.widget.FrameLayout;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.time.TimersType;
import com.loopme.utils.UiUtils;
import com.loopme.utils.Utils;


/**
 * The `LoopMeInterstitial` class provides the facilities to display a full-screen ad
 * during natural transition points in your application.
 * <p>
 * It is recommended to implement `LoopMeInterstitialVV.Listener`
 * to stay informed about ad state changes,
 * such as when an ad has been loaded or has failed to load its content, when video ad has been watched completely,
 * when an ad has been presented or dismissed from the screen, and when an ad has expired or received a tap.
 */
public class LoopMeInterstitialGeneral extends LoopMeAd {

    private static final String LOG_TAG = LoopMeInterstitialGeneral.class.getSimpleName();
    public static final String TEST_PORT_INTERSTITIAL = "test_interstitial_p";
    public static final String TEST_LAND_INTERSTITIAL = "test_interstitial_l";

    private transient Listener mAdListener;

    public LoopMeInterstitialGeneral(Activity activity, String appKey) {
        super(activity, appKey);
        Logging.out(LOG_TAG, "Start creating interstitial with app key: " + appKey);
    }

    public static LoopMeInterstitialGeneral getInstance(String appKey, Activity activity) {
        return new LoopMeInterstitialGeneral(activity, appKey);
    }

    @Override
    public void destroy() {
        if (isShowing()) {
            dismiss();
        } else {
            Logging.out(LOG_TAG, "Can't dismiss ad, it's not displaying");
        }
        super.destroy();
    }

    public void setListener(Listener listener) {
        mAdListener = listener;
    }

    public Listener getListener() {
        return mAdListener;
    }

    @Override
    public void removeListener() {
        mAdListener = null;
    }

    public void bindView(FrameLayout frameLayout) {
        super.bindView(frameLayout);
        buildAdView();
    }

    @Override
    public void show() {
        if (isReady()) {
            if (!isShowing()) {
                setAdState(Constants.AdState.SHOWING);
                stopTimer(TimersType.EXPIRATION_TIMER);
                AdUtils.startAdActivity(this);
                onLoopMeInterstitialShow();
                Logging.out(LOG_TAG, "Interstitial will present fullscreen ad. App key: " + getAppKey());
            }
        } else {
            Logging.out(LOG_TAG, "Interstitial is not ready");
        }
    }

    @Override
    public int getAdFormat() {
        return Constants.AdFormat.INTERSTITIAL;
    }

    @Override
    public AdSpotDimensions getAdSpotDimensions() {
        return new AdSpotDimensions(Utils.getScreenWidth(), Utils.getScreenHeight());
    }

    /**
     * Triggered when the interstitial has successfully loaded the ad content
     */
    private void onLoopMeInterstitialLoadSuccess() {
        stopTimer(TimersType.FETCHER_TIMER);
        long currentTime = System.currentTimeMillis();
        long loadingTime = currentTime - mStartLoadingTime;

        setReady(true);
        setAdState(Constants.AdState.NONE);
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialLoadSuccess(this);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
        Logging.out(LOG_TAG, "Ad successfully loaded (" + loadingTime + "ms)");
    }

    /**
     * Triggered when interstitial ad failed to load ad content
     *
     * @param error - error of unSuccessful ad loading attempt
     */
    private void onLoopMeInterstitialLoadFail(final LoopMeError error) {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        stopTimer(TimersType.FETCHER_TIMER);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialLoadFail(this, error);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
        Logging.out(LOG_TAG, "Ad fails to load: " + error.getMessage());
    }

    /**
     * Triggered when the interstitial ad appears on the screen
     */
    private void onLoopMeInterstitialShow() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialShow(this);
        }
        Logging.out(LOG_TAG, "Ad appeared on screen");
    }

    /**
     * Triggered when the interstitial ad disappears on the screen
     */
    public void onLoopMeInterstitialHide() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialHide(this);
        }
        Logging.out(LOG_TAG, "Ad disappeared from screen");
    }

    /**
     * Triggered when the user taps the interstitial ad and the interstitial is about to perform extra actions
     * Those actions may lead to displaying a modal browser or leaving your application.
     */
    private void onLoopMeInterstitialClicked() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialClicked(this);
        }
        Logging.out(LOG_TAG, "Ad received tap event");
    }

    /**
     * Triggered when your application is about to go to the background, initiated by the SDK.
     * This may happen in various ways, f.e if user wants open the SDK's browser web page in native browser or clicks
     * on `mailto:` links...
     */
    private void onLoopMeInterstitialLeaveApp() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialLeaveApp(this);
        }
        Logging.out(LOG_TAG, "Leaving application");
    }

    /**
     * Triggered when the interstitial's loaded ad content is expired.
     * Expiration happens when loaded ad content wasn't displayed during some period of time, approximately one hour.
     * Once the interstitial is presented on the screen, the expiration is no longer tracked and interstitial won't
     * receive this message
     */
    private void onLoopMeInterstitialExpired() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialExpired(this);
        }
        Logging.out(LOG_TAG, "Ads content expired");
    }

    /**
     * Triggered only when interstitial's video was played until the end.
     * It won't be sent if the video was skipped or the interstitial was dissmissed during the displaying process
     */
    private void onLoopMeInterstitialVideoDidReachEnd() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialVideoDidReachEnd(this);
        }
        Logging.out(LOG_TAG, "Video reach end");
    }

    @Override
    public void onAdExpired() {
        onLoopMeInterstitialExpired();
    }

    @Override
    public void onAdLoadSuccess() {
        onLoopMeInterstitialLoadSuccess();
    }

    @Override
    public void onAdAlreadyLoaded() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialLoadSuccess(this);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
    }

    @Override
    public void onAdLoadFail(final LoopMeError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onLoopMeInterstitialLoadFail(error);
            }
        });
    }

    @Override
    public void onAdLeaveApp() {
        onLoopMeInterstitialLeaveApp();
    }

    @Override
    public void onAdClicked() {
        onLoopMeInterstitialClicked();
    }

    @Override
    public void onAdVideoDidReachEnd() {
        onLoopMeInterstitialVideoDidReachEnd();
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            UiUtils.broadcastIntent(getContext(), Constants.DESTROY_INTENT, getAdId());
            onLoopMeInterstitialHide();
            Logging.out(LOG_TAG, "Dismiss ad");
        }
    }

    public interface Listener {

        void onLoopMeInterstitialLoadSuccess(LoopMeInterstitialGeneral interstitial);

        void onLoopMeInterstitialLoadFail(LoopMeInterstitialGeneral interstitial, LoopMeError error);

        void onLoopMeInterstitialShow(LoopMeInterstitialGeneral interstitial);

        void onLoopMeInterstitialHide(LoopMeInterstitialGeneral interstitial);

        void onLoopMeInterstitialClicked(LoopMeInterstitialGeneral interstitial);

        void onLoopMeInterstitialLeaveApp(LoopMeInterstitialGeneral interstitial);

        void onLoopMeInterstitialExpired(LoopMeInterstitialGeneral interstitial);

        void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitialGeneral interstitial);
    }

}