package com.loopme.applovin_mediation_sample;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.sdk.AppLovinSdk;
import com.loopme.IntegrationType;
import com.loopme.LoopMeInterstitial;
import com.loopme.LoopMeSdk;
import com.loopme.common.LoopMeError;

@Keep
public class LoopmeMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter {

    private static final String LOG_TAG = LoopmeMediationAdapter.class.getSimpleName();

    private LoopMeInterstitial mInterstitial;
    private final LoopMeListener mLoopMeListener = new LoopMeListener();
    private MaxInterstitialAdapterListener mListener;

    public LoopmeMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();
        if (LoopMeSdk.isInitialized())
            onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_SUCCESS, LoopmeMediationAdapter.class.getSimpleName());
        else
            onCompletionListener.onCompletion(InitializationStatus.INITIALIZING, LoopmeMediationAdapter.class.getSimpleName());
        LoopMeSdk.initialize(activity, loopMeConf, new LoopMeSdk.LoopMeSdkListener() {

            @Override
            public void onSdkInitializationSuccess() {
                onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_SUCCESS, LoopmeMediationAdapter.class.getSimpleName());
            }

            @Override
            public void onSdkInitializationFail(int error, String message) {
                onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_FAILURE, LoopmeMediationAdapter.class.getSimpleName());
            }
        });
    }


    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "load interstitial ad");

        String appkey = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        mListener = maxInterstitialAdapterListener;

        mInterstitial = LoopMeInterstitial.getInstance(appkey, activity);
        mInterstitial.setListener(mLoopMeListener);
        mInterstitial.load(IntegrationType.ADMOB);
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, ViewGroup viewGroup, Lifecycle lifecycle, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }

    @Override
    public String getSdkVersion() {
        return LoopMeSdk.getVersion();
    }

    @Override
    public String getAdapterVersion() {
        return LoopMeSdk.getVersion();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        mInterstitial.destroy();
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }

    private class LoopMeListener implements LoopMeInterstitial.Listener {

        @Override
        public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
            mListener.onInterstitialAdClicked();
        }

        @Override
        public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
        }

        @Override
        public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
            mListener.onInterstitialAdHidden();
        }

        @Override
        public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
        }

        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial arg0,
                                                 LoopMeError arg1) {
            mListener.onInterstitialAdLoadFailed(new MaxAdapterError(arg1.getErrorCode()));
        }

        @Override
        public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
            mListener.onInterstitialAdLoaded();
        }

        @Override
        public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
            mListener.onInterstitialAdDisplayed();
        }

        @Override
        public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        }
    }
}