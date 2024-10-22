package com.ironsource.adapters.custom.loopme;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

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

    private boolean isAlreadyRewarded = false;

    public LoopmeCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NotNull RewardedVideoAdListener rewardedVideoAdListener) {
        try {
            isAlreadyRewarded = false;
            String appkey = adData.getConfiguration().get("instancekey").toString();
            mRewarded = LoopMeInterstitial.getInstance(appkey, activity, true);
            mRewarded.setAutoLoading(false);
            mRewarded.setListener(new LoopMeInterstitial.Listener() {
                @Override
                public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdClicked();
                    Log.d(LOG_TAG, "onLoopMeInterstitialClicked");
                }

                @Override
                public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
                    Log.d(LOG_TAG, "onLoopMeInterstitialExpired");
                }

                @Override
                public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
                    if (!isAlreadyRewarded) {
                        rewardedVideoAdListener.onAdRewarded();
                        isAlreadyRewarded = true;
                        Log.d(LOG_TAG, "onLoopMeInterstitialHide (Rewarded)");
                    }
                    rewardedVideoAdListener.onAdClosed();
                    Log.d(LOG_TAG, "onLoopMeInterstitialHide");
                }

                @Override
                public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
                    Log.d(LOG_TAG, "onLoopMeInterstitialLeaveApp");
                }

                @Override
                public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdLoadSuccess();
                    Log.d(LOG_TAG, "onLoopMeInterstitialLoadSuccess");
                }

                @Override
                public void onLoopMeInterstitialLoadFail(LoopMeInterstitial loopMeInterstitial, LoopMeError i) {
                    rewardedVideoAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, i.getErrorCode(), i.getMessage());
                    Log.d(LOG_TAG, "onLoopMeInterstitialLoadFail " + i.getMessage() + " " + i.getErrorCode());
                }

                @Override
                public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
                    rewardedVideoAdListener.onAdOpened();
                    rewardedVideoAdListener.onAdShowSuccess();
                    rewardedVideoAdListener.onAdVisible();
                    Log.d(LOG_TAG, "onLoopMeInterstitialShow");
                }

                @Override
                public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
                    if (!isAlreadyRewarded) {
                        rewardedVideoAdListener.onAdRewarded();
                        isAlreadyRewarded = true;
                        Log.d(LOG_TAG, "onLoopMeInterstitialVideoDidReachEnd (Rewarded)");
                    }
                    rewardedVideoAdListener.onAdEnded();
                    Log.d(LOG_TAG, "onLoopMeInterstitialVideoDidReachEnd");
                }
            });
            mRewarded.load(IntegrationType.NORMAL);
        } catch (Exception e) {
            rewardedVideoAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, -1, e.getMessage());
        }
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull RewardedVideoAdListener rewardedVideoAdListener) {
        if (isAdAvailable(adData)) {
            mRewarded.show();
        } else {
            rewardedVideoAdListener.onAdShowFailed(-2, "adShowFailed");
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return mRewarded.isReady();
    }

}