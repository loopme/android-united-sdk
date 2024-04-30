package com.applovin.mediation.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.sdk.AppLovinSdk;
import com.loopme.BuildConfig;
import com.loopme.IntegrationType;
import com.loopme.LoopMeBanner;
import com.loopme.LoopMeInterstitial;
import com.loopme.LoopMeSdk;
import com.loopme.common.LoopMeError;


/**
 * This is a mediation adapter for the LoopMe Unified SDK
 */
@Keep
public class LoopmeMediationAdapter
        extends MediationAdapterBase
        implements MaxInterstitialAdapter, MaxAdViewAdapter, MaxRewardedAdapter {

    private static final String LOG_TAG = LoopmeMediationAdapter.class.getSimpleName();
    private static       InitializationStatus initializationStatus;
    private LoopMeBanner mBanner;


    private LoopMeInterstitial mInterstitial;
    private final LoopMeListener mLoopMeListener = new LoopMeListener();
    private MaxInterstitialAdapterListener mInterstitialListener;

    public LoopmeMediationAdapter(final AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d("loopmeAdapter", "initialization");
        LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();
        if (LoopMeSdk.isInitialized()) {
            onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_SUCCESS, LoopmeMediationAdapter.class.getSimpleName());
            return;
        } else {
            onCompletionListener.onCompletion(InitializationStatus.INITIALIZING, LoopmeMediationAdapter.class.getSimpleName());
        }
        LoopMeSdk.initialize(activity, loopMeConf, new LoopMeSdk.LoopMeSdkListener() {

            @Override
            public void onSdkInitializationSuccess() {
                onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_SUCCESS, LoopmeMediationAdapter.class.getSimpleName());
                initializationStatus = InitializationStatus.INITIALIZED_SUCCESS;
            }

            @Override
            public void onSdkInitializationFail(int error, String message) {
                onCompletionListener.onCompletion(InitializationStatus.INITIALIZED_FAILURE, LoopmeMediationAdapter.class.getSimpleName());
                initializationStatus = InitializationStatus.INITIALIZED_FAILURE;
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
        activity.runOnUiThread(() -> mInterstitial.load());
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
        return BuildConfig.VERSION_NAME;
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

        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();

        if (activity == null )
        {
            MaxAdapterError error = new MaxAdapterError( -5601, "Missing Activity");

        }

        try {
            mBanner = LoopMeBanner.getInstance(placementId, activity);
            FrameLayout container = new FrameLayout(activity);
            mBanner.bindView(container);
            mBanner.setAutoLoading(false);
            mBanner.setListener(new LoopMeBanner.Listener() {
                @Override
                public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                    maxAdViewAdapterListener.onAdViewAdLoaded(mBanner.getBannerView());
                    Log.d(LOG_TAG, "onLoopMeBannerLoadSuccess");
                    mBanner.show();
                }

                @Override
                public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
                    Log.d(LOG_TAG, "onLoopMeBannerLoadFail");
                    maxAdViewAdapterListener.onAdViewAdLoadFailed(MaxAdapterError.NO_FILL);
                }

                @Override
                public void onLoopMeBannerShow(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerShow");
                    maxAdViewAdapterListener.onAdViewAdDisplayed();
                }

                @Override
                public void onLoopMeBannerHide(LoopMeBanner banner) {
                    maxAdViewAdapterListener.onAdViewAdHidden();
                }

                @Override
                public void onLoopMeBannerClicked(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerClicked");
                    maxAdViewAdapterListener.onAdViewAdClicked();
                }

                @Override
                public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerLeaveApp");
                }

                @Override
                public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerVideoDidReachEnd");
                }

                @Override
                public void onLoopMeBannerExpired(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerExpired");
                }
            });
            mBanner.load();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            maxAdViewAdapterListener.onAdViewAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        try {
            mInterstitial = LoopMeInterstitial.getInstance(placementId, activity);
            mInterstitial.setAutoLoading(false);
            mInterstitial.load(IntegrationType.NORMAL);
        } catch (Exception e) {
            maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }

    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        try {
            mInterstitial = LoopMeInterstitial.getInstance(placementId, activity);
            mInterstitial.show();
        } catch (Exception e) {
            maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }


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