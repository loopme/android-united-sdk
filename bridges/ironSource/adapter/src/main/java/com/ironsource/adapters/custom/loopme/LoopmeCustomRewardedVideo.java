package com.ironsource.adapters.custom.loopme;

import android.app.Activity;

import androidx.annotation.Keep;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.loopme.IntegrationType;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;

import org.jetbrains.annotations.NotNull;

@Keep
public class LoopmeCustomRewardedVideo extends BaseRewardedVideo<LoopmeCustomAdapter> {

    private static final String LOG_TAG = LoopmeCustomRewardedVideo.class.getSimpleName();

    private LoopMeInterstitial mRewarded;

    private RewardedVideoAdListener rewardedVideoAdListener;

    public LoopmeCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(AdData adData, Activity activity, @NotNull RewardedVideoAdListener rewardedVideoAdListener) {
        try {
            this.rewardedVideoAdListener = rewardedVideoAdListener;
            String appkey = adData.getConfiguration().get("instancekey").toString();
            mRewarded = LoopMeInterstitial.getInstance(appkey, activity, true);
            mRewarded.setAutoLoading(false);
            mRewarded.setListener(new LoopMeInterstitial.Listener() {
                @Override
                public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdClicked();
                }

                @Override
                public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
                }

                @Override
                public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdClosed();
                }

                @Override
                public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
                }

                @Override
                public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdLoadSuccess();
                }

                @Override
                public void onLoopMeInterstitialLoadFail(LoopMeInterstitial loopMeInterstitial, LoopMeError i) {
                    rewardedVideoAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, i.getErrorCode(), i.getMessage());
                }

                @Override
                public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdShowSuccess();
                    rewardedVideoAdListener.onAdVisible();
                }

                @Override
                public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
                    rewardedVideoAdListener.onAdEnded();
                    rewardedVideoAdListener.onAdRewarded();
                }
            });
            mRewarded.load(IntegrationType.NORMAL);
        } catch (Exception e) {
            rewardedVideoAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, -1, e.getMessage());
        }
    }

    @Override
    public void showAd(AdData adData, RewardedVideoAdListener rewardedVideoAdListener) {
        if (isAdAvailable(adData)) {
            mRewarded.show();
        } else {
            rewardedVideoAdListener.onAdShowFailed(-2, "adShowFailed");
        }
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        return mRewarded.isReady();
    }

}