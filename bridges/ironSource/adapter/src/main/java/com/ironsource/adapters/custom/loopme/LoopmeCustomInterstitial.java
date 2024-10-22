package com.ironsource.adapters.custom.loopme;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.loopme.IntegrationType;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;

@Keep
public class LoopmeCustomInterstitial extends BaseInterstitial<LoopmeCustomAdapter> {

    private static final String LOG_TAG = LoopmeCustomInterstitial.class.getSimpleName();

    private LoopMeInterstitial mInterstitial;

    private InterstitialAdListener mInterstitialListener;

    public LoopmeCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener interstitialAdListener) {
        try {
            this.mInterstitialListener = interstitialAdListener;
            String appkey = adData.getConfiguration().get("instancekey").toString();
            mInterstitial = LoopMeInterstitial.getInstance(appkey, activity);
            mInterstitial.setAutoLoading(false);
            mInterstitial.setListener(new LoopMeInterstitial.Listener() {
                @Override
                public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdClicked();
                    Log.d(LOG_TAG, "onLoopMeInterstitialClicked");
                }

                @Override
                public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
                    Log.d(LOG_TAG, "onLoopMeInterstitialExpired");
                }

                @Override
                public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdClosed();
                    Log.d(LOG_TAG, "onLoopMeInterstitialHide");
                }

                @Override
                public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
                    Log.d(LOG_TAG, "onLoopMeInterstitialLeaveApp");
                }

                @Override
                public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdLoadSuccess();
                    Log.d(LOG_TAG, "onLoopMeInterstitialLoadSuccess");
                }

                @Override
                public void onLoopMeInterstitialLoadFail(LoopMeInterstitial loopMeInterstitial, LoopMeError i) {
                    mInterstitialListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, i.getErrorCode(), i.getMessage());
                    Log.d(LOG_TAG, "onLoopMeInterstitialLoadFail " + i.getMessage() + " " + i.getErrorCode());
                }

                @Override
                public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdOpened();
                    mInterstitialListener.onAdShowSuccess();
                    Log.d(LOG_TAG, "onLoopMeInterstitialShow");
                }

                @Override
                public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
                    mInterstitialListener.onAdEnded();
                    Log.d(LOG_TAG, "onLoopMeInterstitialVideoDidReachEnd");
                }
            });
            mInterstitial.load(IntegrationType.NORMAL);
        } catch (Exception e) {
            interstitialAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, -1, e.getMessage());
        }
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull InterstitialAdListener interstitialAdListener) {
        if (isAdAvailable(adData)) {
            mInterstitial.show();
        } else {
            mInterstitialListener.onAdShowFailed(-2, "adShowFailed");
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return mInterstitial.isReady();
    }

}