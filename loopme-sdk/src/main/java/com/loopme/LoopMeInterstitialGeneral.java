package com.loopme;

import static com.loopme.Constants.DISMISS_AD_REASON.EXPIRED;
import static com.loopme.Constants.DISMISS_AD_REASON.HIDE;
import static com.loopme.Constants.DISMISS_AD_REASON.LOAD_FAIL;

import android.app.Activity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.utils.UiUtils;

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

    private boolean isRewarded = false;
    public void setRewarded(boolean rewarded) { isRewarded = rewarded; }
    public boolean isRewarded() { return isRewarded; }

    private transient Listener mAdListener;
    public void setListener(Listener listener) { mAdListener = listener; }
    public Listener getListener() { return mAdListener; }
    @Override
    public void removeListener() { mAdListener = null; }

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

    public void bindView(FrameLayout frameLayout) {
        super.bindView(frameLayout);
        buildAdView();
    }

    /**
     * Triggered when the interstitial ad appears on the screen
     */
    @Override
    public void show() {
        if (!isReady()) {
            Logging.out(LOG_TAG, "Interstitial is not ready");
            return;
        }
        if (isShowing()) {
            Logging.out(LOG_TAG, "Interstitial is already showing");
            return;
        }
        setAdState(Constants.AdState.SHOWING);
        stopTimer();
        AdUtils.startAdActivity(this);
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialShow(this);
        }
        Logging.out(LOG_TAG, "Interstitial will present fullscreen ad. App key: " + getAppKey());
    }

    @NonNull
    @Override
    public Constants.AdFormat getAdFormat() { return Constants.AdFormat.INTERSTITIAL; }

    @Override
    public Constants.PlacementType getPlacementType() {
        return isRewarded ? Constants.PlacementType.REWARDED : Constants.PlacementType.INTERSTITIAL;
    }

    @Override
    public AdSpotDimensions getAdSpotDimensions() { return AdSpotDimensions.getFullscreen(); }

    @Override
    public void onAdAlreadyLoaded() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialLoadSuccess(this);
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
            mAdListener.onLoopMeInterstitialLeaveApp(this);
        }
        Logging.out(LOG_TAG, "Leaving application");
    }

    /**
     * Triggered when the user taps the interstitial ad and the interstitial is about to perform extra actions
     * Those actions may lead to displaying a modal browser or leaving your application.
     */
    @Override
    public void onAdClicked() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialClicked(this);
        }
        Logging.out(LOG_TAG, "Ad received tap event");
    }

    /**
     * Triggered only when interstitial's video was played until the end.
     * It won't be sent if the video was skipped or the interstitial was dissmissed during the displaying process
     */
    @Override
    public void onAdVideoDidReachEnd() {
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialVideoDidReachEnd(this);
        }
        Logging.out(LOG_TAG, "Video reach end");
    }

    /**
     * Triggered when the interstitial has successfully loaded the ad content
     */
    @Override
    public void onAdLoadSuccess() {
        long currentTime = System.currentTimeMillis();
        long loadingTime = currentTime - mStartLoadingTime;

        setReady(true);
        setAdState(Constants.AdState.NONE);
        if (mAdListener != null) {
            mAdListener.onLoopMeInterstitialLoadSuccess(this);
        }
        Logging.out(LOG_TAG, "Ad successfully loaded (" + loadingTime + "ms)");
    }

    /**
     * Triggered when interstitial ad failed to load ad content
     * @param error - error of unSuccessful ad loading attempt
     */
    @Override
    public void onAdLoadFail(final LoopMeError error) { destroyAd(LOAD_FAIL, error); }

    /**
     * Triggered when the interstitial ad disappears on the screen
     */
    @Override
    public void dismiss() { destroyAd(HIDE, null); }

    /**
     * Triggered when the interstitial's loaded ad content is expired.
     * Expiration happens when loaded ad content wasn't displayed during some period of time, approximately one hour.
     * Once the interstitial is presented on the screen, the expiration is no longer tracked and interstitial won't
     * receive this message
     */
    @Override
    public void onAdExpired() { destroyAd(EXPIRED, null); }

    private void destroyAd(String reason, LoopMeError error) {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        runOnUiThread(() -> {
            UiUtils.broadcastIntent(getContext(), Constants.DESTROY_INTENT, getAdId());
            if (mAdListener == null) return;
            if (EXPIRED.equals(reason))
                mAdListener.onLoopMeInterstitialExpired(this);
            if (LOAD_FAIL.equals(reason))
                mAdListener.onLoopMeInterstitialLoadFail(this, error);
            if (HIDE.equals(reason))
                mAdListener.onLoopMeInterstitialHide(this);
        });
        Logging.out(
            LOG_TAG,
            "Ad " + reason + (error == null ? "" : " with error: " + error.getMessage())
        );
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