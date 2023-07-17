package com.ironsource.adapters.custom.loopme;

import android.app.Activity;
import androidx.annotation.Keep;
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
    public void loadAd(AdData adData, Activity activity, InterstitialAdListener interstitialAdListener) {
        try {
            this.mInterstitialListener = interstitialAdListener;
            String appkey = ((LoopmeCustomAdapter) getNetworkAdapter()).getLoopmeAppkey();
            mInterstitial = LoopMeInterstitial.getInstance(appkey, activity);
            mInterstitial.setAutoLoading(false);
            mInterstitial.setListener(new LoopMeInterstitial.Listener() {
                @Override
                public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdClicked();
                }

                @Override
                public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
                }

                @Override
                public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
                }

                @Override
                public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
                }

                @Override
                public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdLoadSuccess();
                }

                @Override
                public void onLoopMeInterstitialLoadFail(LoopMeInterstitial loopMeInterstitial, LoopMeError i) {
                    mInterstitialListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, i.getErrorCode(), i.getMessage());
                }

                @Override
                public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
                    mInterstitialListener.onAdShowSuccess();
                }

                @Override
                public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
                }
            });
            mInterstitial.load(IntegrationType.NORMAL);
        } catch (Exception e) {
            interstitialAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, -1, e.getMessage());
        }
    }

    @Override
    public void showAd(AdData adData, InterstitialAdListener interstitialAdListener) {
        mInterstitial.show();
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        return mInterstitial.isReady();
    }

}