package com.admob.mediation.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;

@Keep
public class LoopMeRewardedListener implements LoopMeInterstitial.Listener, MediationRewardedAd {

    private static final String LOG_TAG = LoopMeRewardedListener.class.getSimpleName();
    /** Configuration for requesting the interstitial ad from the third-party network. */
    private final MediationRewardedAdConfiguration mediationRewardedAdConfiguration;

    /** Callback for interstitial ad events. */
    private MediationRewardedAdCallback rewardedAdCallback;
    /** Callback that fires on loading success or failure. */
    private final MediationAdLoadCallback<
        MediationRewardedAd,
        MediationRewardedAdCallback
    > mediationAdLoadCallback;

    private LoopMeInterstitial rewardedAd;

    public LoopMeRewardedListener(
        MediationRewardedAdConfiguration adConfiguration,
        MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback
    ) {
        Log.d(LOG_TAG, "LoopMeRewardedListener");
        Log.d(LOG_TAG, "adConfiguration: " + adConfiguration.toString());
        this.mediationRewardedAdConfiguration = adConfiguration;
        this.mediationAdLoadCallback = callback;
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        rewardedAdCallback = mediationAdLoadCallback.onSuccess(this);
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        AdError adError = new AdError(error.getErrorCode(), error.getMessage(), "loopme.com");
        mediationAdLoadCallback.onFailure(adError);
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
        rewardedAdCallback.onAdOpened();
        rewardedAdCallback.onVideoStart();
        rewardedAdCallback.reportAdImpression();
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        rewardedAdCallback.onAdClosed();
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
        rewardedAdCallback.reportAdClicked();
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {

    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        rewardedAdCallback.onVideoComplete();
        // TODO: Replace with real reward values
        RewardItem rewardItem = new RewardItem() {
            @NonNull
            @Override
            public String getType() {
                return "reward";
            }

            @Override
            public int getAmount() {
                return 0;
            }
        };
        rewardedAdCallback.onUserEarnedReward(rewardItem);
    }

    /** Loads the interstitial ad from the third-party ad network. */
    public void loadAd() {
        String serverParameter = mediationRewardedAdConfiguration
            .getServerParameters()
            .getString(MediationConfiguration.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        // TODO: Take appkey from AdMob waterfall setup
        String appkey = "";
        Activity activity = (Activity) mediationRewardedAdConfiguration.getContext();
        rewardedAd = LoopMeInterstitial.getInstance(appkey, activity);
        rewardedAd.setListener(this);
        rewardedAd.setAutoLoading(false);
        activity.runOnUiThread(() -> rewardedAd.load());
    }

    @Override
    public void showAd(@NonNull Context context) {
        Log.d(LOG_TAG, "showAd");
        if (rewardedAd != null) {
            rewardedAd.show();
        }
    }
}
