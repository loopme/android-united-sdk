package com.admob.mediation.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.utils.ExecutorHelper;

@Keep
public class LoopMeInterstitialListener implements LoopMeInterstitial.Listener, MediationInterstitialAd {

    private static final String LOG_TAG = LoopMeInterstitialListener.class.getSimpleName();
    /** Configuration for requesting the interstitial ad from the third-party network. */
    private final MediationInterstitialAdConfiguration mediationInterstitialAdConfiguration;

    /** Listener for interstitial ad events. */
    private MediationInterstitialAdCallback interstitialAdCallback;
    /** Listener that fires on loading success or failure. */
    private final MediationAdLoadCallback<
        MediationInterstitialAd,
        MediationInterstitialAdCallback
    > mediationAdLoadCallback;

    private LoopMeInterstitial interstitialAd;

    public LoopMeInterstitialListener(
        MediationInterstitialAdConfiguration adConfiguration,
        MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback
    ) {
        Log.d(LOG_TAG, "LoopMeInterstitialListener");
        Log.d(LOG_TAG, "adConfiguration: " + adConfiguration.toString());
        this.mediationInterstitialAdConfiguration = adConfiguration;
        this.mediationAdLoadCallback = callback;
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        interstitialAdCallback = mediationAdLoadCallback.onSuccess(this);
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        AdError adError = new AdError(error.getErrorCode(), error.getMessage(), "loopme.com");
        mediationAdLoadCallback.onFailure(adError);
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
        interstitialAdCallback.onAdOpened();
        interstitialAdCallback.reportAdImpression();
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        interstitialAdCallback.onAdClosed();
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
        interstitialAdCallback.reportAdClicked();
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
        interstitialAdCallback.onAdLeftApplication();
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {

    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
    }

    /** Loads the interstitial ad from the third-party ad network. */
    public void loadAd() {
        String appkey = mediationInterstitialAdConfiguration
            .getServerParameters()
            .getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        Activity activity = (Activity) mediationInterstitialAdConfiguration.getContext();
        interstitialAd = LoopMeInterstitial.getInstance(appkey, activity);
        interstitialAd.setListener(this);
        interstitialAd.setAutoLoading(false);
        ExecutorHelper.executeOnWorkerThread(() -> interstitialAd.load());
    }

    @Override
    public void showAd(@NonNull Context context) {
        Log.d(LOG_TAG, "showAd");
        if (interstitialAd != null) {
            interstitialAd.show();
        }
    }
}
