package com.admob.mediation.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.loopme.LoopMeBanner;
import com.loopme.common.LoopMeError;

@Keep
public class LoopMeBannerListener implements LoopMeBanner.Listener, MediationBannerAd {

    private static final String LOG_TAG = LoopMeBannerListener.class.getSimpleName();
    private LoopMeBanner bannerAd;

    /** Configuration for requesting the banner ad from the third-party network. */
    private final MediationBannerAdConfiguration mediationBannerAdConfiguration;

    /** Callback that fires on loading success or failure. */
    private final MediationAdLoadCallback<
        MediationBannerAd,
        MediationBannerAdCallback
    > mediationAdLoadCallback;

    /** Callback for banner ad events. */
    private MediationBannerAdCallback bannerAdCallback;
    public LoopMeBannerListener(
            MediationBannerAdConfiguration adConfiguration,
            MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback
    ) {
        Log.d(LOG_TAG, "LoopMeBannerListener");
        Log.d(LOG_TAG, "adConfiguration: " + adConfiguration.toString());
        this.mediationBannerAdConfiguration = adConfiguration;
        this.mediationAdLoadCallback = callback;
    }

    @Override
    public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
        bannerAdCallback = mediationAdLoadCallback.onSuccess(this);
        bannerAd.show();
    }

    @Override
    public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
        AdError adError = new AdError(error.getErrorCode(), error.getMessage(), "loopme.com");
        mediationAdLoadCallback.onFailure(adError);
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner banner) {
        bannerAdCallback.onAdOpened();
        bannerAdCallback.reportAdImpression();
    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner banner) {
        bannerAdCallback.onAdClosed();
    }

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner banner) {
        bannerAdCallback.onAdOpened();
        bannerAdCallback.reportAdClicked();
    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
        bannerAdCallback.onAdLeftApplication();
    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {

    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner banner) {

    }

    public void loadAd() {

        String appKey = mediationBannerAdConfiguration
                .getServerParameters()
                .getString("app_key");
        Activity activity = (Activity) mediationBannerAdConfiguration.getContext();
        bannerAd = LoopMeBanner.getInstance(appKey, activity);
        FrameLayout container = new FrameLayout(activity);
        bannerAd.bindView(container);
        bannerAd.setAutoLoading(false);
        bannerAd.setListener(this);
        activity.runOnUiThread(() -> bannerAd.load());
    }

    @NonNull
    @Override
    public View getView() {
        return bannerAd.getBannerView();
    }
}
