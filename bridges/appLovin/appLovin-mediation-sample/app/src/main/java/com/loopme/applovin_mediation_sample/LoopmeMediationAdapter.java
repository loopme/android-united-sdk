package com.loopme.applovin_mediation_sample;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.sdk.AppLovinSdk;
import com.loopme.LoopMeBanner;
import com.loopme.LoopMeInterstitial;
import com.loopme.LoopMeSdk;
import com.loopme.common.LoopMeError;

@Keep
public class LoopmeMediationAdapter extends MediationAdapterBase implements MaxInterstitialAdapter, MaxAdViewAdapter {

    private static final String LOG_TAG = LoopmeMediationAdapter.class.getSimpleName();

    private LoopMeInterstitial mInterstitial;
    private LoopMeBanner mBanner;
    private final LoopMeListener mLoopMeListener = new LoopMeListener();
    private MaxInterstitialAdapterListener mInterstitialListener;
    private MaxAdViewAdapterListener mBannerListener;

    public LoopmeMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d("loopmeAdapter", "initialization");
        LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();
        if (LoopMeSdk.isInitialized()) {
            onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_SUCCESS, LoopmeMediationAdapter.class.getSimpleName());
            return;
        } else
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
        mInterstitialListener = maxInterstitialAdapterListener;
        mInterstitial = LoopMeInterstitial.getInstance(appkey, activity);
        mInterstitial.setListener(mLoopMeListener);
        mInterstitial.setAutoLoading(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInterstitial.load();
            }
        });
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

    @Override
    public void loadAdViewAd(MaxAdapterResponseParameters maxAdapterResponseParameters, MaxAdFormat maxAdFormat, Activity activity, MaxAdViewAdapterListener maxAdViewAdapterListener) {
        Log.d(LOG_TAG, "load banner ad");

        String appkey = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        mBannerListener = maxAdViewAdapterListener;

        mBanner = LoopMeBanner.getInstance(appkey, activity);
//        mBanner.bindView(); ??
    }

    private class LoopMeListener implements LoopMeInterstitial.Listener {

        @Override
        public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
            mInterstitialListener.onInterstitialAdClicked();
        }

        @Override
        public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
        }

        @Override
        public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
            mInterstitialListener.onInterstitialAdHidden();
        }

        @Override
        public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
        }

        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial arg0,
                                                 LoopMeError arg1) {
            Log.d(LOG_TAG, "loopme's ad loading failed");
            mInterstitialListener.onInterstitialAdLoadFailed(new MaxAdapterError(arg1.getErrorCode()));
        }

        @Override
        public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
            Log.d(LOG_TAG, "loopme's ad loaded");
            mInterstitialListener.onInterstitialAdLoaded();
        }

        @Override
        public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
            mInterstitialListener.onInterstitialAdDisplayed();
        }

        @Override
        public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        }
    }
}