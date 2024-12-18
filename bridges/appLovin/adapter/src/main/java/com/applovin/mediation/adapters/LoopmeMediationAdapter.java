package com.applovin.mediation.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
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
import com.applovin.mediation.adapters.loopme.BuildConfig;
import com.applovin.sdk.AppLovinSdk;
import com.loopme.IntegrationType;
import com.loopme.LoopMeBanner;
import com.loopme.LoopMeInterstitial;
import com.loopme.LoopMeSdk;
import com.loopme.common.LoopMeError;
import com.loopme.debugging.Params;
import com.loopme.models.Errors;
import com.loopme.tracker.partners.LoopMeTracker;


/**
 * This is a mediation adapter for the LoopMe Unified SDK
 */
@Keep
public class LoopmeMediationAdapter
        extends MediationAdapterBase
        implements MaxInterstitialAdapter, MaxAdViewAdapter, MaxRewardedAdapter {

    private static final String LOG_TAG = LoopmeMediationAdapter.class.getSimpleName();
    private static InitializationStatus initializationStatus;
    private LoopMeBanner mBanner;

    // Interstitial
    private LoopMeInterstitial mInterstitial;
    private final LoopMeInterstitialListener mLoopMeInterstitialListener = new LoopMeInterstitialListener();
    private MaxInterstitialAdapterListener mInterstitialListener;

    // Rewarded
    private LoopMeInterstitial mRewarded;
    private final LoopMeRewardedListener mLoopMeRewardedListener = new LoopMeRewardedListener();
    private MaxRewardedAdapterListener mRewardedListener;
    private boolean isAlreadyRewarded = false;


    public LoopmeMediationAdapter(final AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters maxAdapterInitializationParameters, @Nullable Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d(LOG_TAG, "initialization");
        if (LoopMeSdk.isInitialized()) {
            initializationStatus = InitializationStatus.INITIALIZED_SUCCESS;
            onCompletionListener.onCompletion(initializationStatus, LOG_TAG);
            return;
        }
        initializationStatus = InitializationStatus.INITIALIZING;
        onCompletionListener.onCompletion(initializationStatus, LOG_TAG);

        new Handler(Looper.getMainLooper()).post(() -> {
            LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();
            loopMeConf.setMediation("applovin");
            loopMeConf.setAdapterVersion(getAdapterVersion());
            loopMeConf.setMediationSdkVersion(getMediationSdkVersion());
            LoopMeSdk.initialize(getContext(activity), loopMeConf, new LoopMeSdk.LoopMeSdkListener() {
                @Override
                public void onSdkInitializationSuccess() {
                    initializationStatus = InitializationStatus.INITIALIZED_SUCCESS;
                    onCompletionListener.onCompletion(initializationStatus, LOG_TAG);
                }

                @Override
                public void onSdkInitializationFail(int error, String message) {
                    initializationStatus = InitializationStatus.INITIALIZED_FAILURE;
                    onCompletionListener.onCompletion(initializationStatus, LOG_TAG);
                }
            });
        });
    }

    @Override
    public String getSdkVersion() {
        return LoopMeSdk.getVersion();
    }

    @Override
    public String getAdapterVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public String getMediationSdkVersion() {
        return AppLovinSdk.VERSION;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
        if (mRewarded != null) {
            mRewarded.destroy();
        }
    }

    @Override
    public void loadAdViewAd(MaxAdapterResponseParameters maxAdapterResponseParameters, MaxAdFormat maxAdFormat, @Nullable Activity activity, MaxAdViewAdapterListener maxAdViewAdapterListener) {
        Log.d(LOG_TAG, "load banner ad");
        String placementId = maxAdapterResponseParameters.getThirdPartyAdPlacementId();

        if (activity == null ) {
            LoopMeTracker.post(Errors.MISSING_ACTIVITY.addParam(Params.ID, placementId));
            maxAdViewAdapterListener.onAdViewAdLoadFailed(MaxAdapterError.MISSING_ACTIVITY);
            return;
        }

        try {
            mBanner = LoopMeBanner.getInstance(placementId, activity);
            mBanner.setSize(
                maxAdFormat.getSize().getWidth(),
                maxAdFormat.getSize().getHeight()
            );
            FrameLayout container = new FrameLayout(activity);

            container.setLayoutParams(
                new FrameLayout.LayoutParams(
                    maxAdFormat.getSize().getWidth(),
                    maxAdFormat.getSize().getHeight()
                )
            );
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
            activity.runOnUiThread(() -> mBanner.load());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            LoopMeTracker.post(Errors.ADAPTER_ERROR.addParam(Params.ID, placementId));
            maxAdViewAdapterListener.onAdViewAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, @Nullable Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "load interstitial ad");

        String appkey = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        if (activity == null ) {
            LoopMeTracker.post(Errors.MISSING_ACTIVITY.addParam(Params.ID, appkey));
            maxInterstitialAdapterListener.onInterstitialAdLoadFailed(MaxAdapterError.MISSING_ACTIVITY);
            return;
        }
        try {
            mInterstitialListener = maxInterstitialAdapterListener;
            mInterstitial = LoopMeInterstitial.getInstance(appkey, activity);
            mInterstitial.setListener(mLoopMeInterstitialListener);
            mInterstitial.setAutoLoading(false);
            activity.runOnUiThread(() -> mInterstitial.load());
        } catch (Exception e) {
            LoopMeTracker.post(Errors.ADAPTER_ERROR.addParam(Params.ID, appkey));
            maxInterstitialAdapterListener.onInterstitialAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, @Nullable Activity activity, MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.d(LOG_TAG, "load rewarded ad");
        String appkey = maxAdapterResponseParameters.getThirdPartyAdPlacementId();
        if (activity == null ) {
            LoopMeTracker.post(Errors.MISSING_ACTIVITY.addParam(Params.ID, appkey));
            maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.MISSING_ACTIVITY);
            return;
        }
        try {
            mRewardedListener = maxRewardedAdapterListener;
            mRewarded = LoopMeInterstitial.getInstance(appkey, activity, true);
            mRewarded.setListener(mLoopMeRewardedListener);
            mRewarded.setAutoLoading(false);
            activity.runOnUiThread(() -> mRewarded.load(IntegrationType.NORMAL));
        } catch (Exception e) {
            LoopMeTracker.post(Errors.ADAPTER_ERROR.addParam(Params.ID, appkey));
            maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, ViewGroup viewGroup, Lifecycle lifecycle, @Nullable Activity activity, MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }
    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, @Nullable MaxInterstitialAdapterListener maxInterstitialAdapterListener) {
        Log.d(LOG_TAG, "showInterstitial");
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }

    @Override
    public void showRewardedAd(MaxAdapterResponseParameters maxAdapterResponseParameters, Activity activity, @Nullable MaxRewardedAdapterListener maxRewardedAdapterListener) {
        Log.d(LOG_TAG, "showRewarded");
        try {
            if (mRewarded != null && mRewarded.isReady()) {
                mRewarded.show();
            }
        } catch (Exception e) {
            maxRewardedAdapterListener.onRewardedAdLoadFailed(MaxAdapterError.INTERNAL_ERROR);
        }
    }

    private Context getContext(@Nullable final Activity activity) {
        // activity can be null starting from applovin 11.1.0, also `getApplicationContext()` was introduced in 11.1.0
        return (activity != null) ? activity.getApplicationContext() : getApplicationContext();
    }

    private class LoopMeInterstitialListener implements LoopMeInterstitial.Listener {
        @Override
        public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
            mInterstitialListener.onInterstitialAdClicked();
        }
        @Override
        public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
            mInterstitialListener.onInterstitialAdHidden();
        }
        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial arg0, LoopMeError arg1) {
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
        public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) { }
        @Override
        public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) { }
        @Override
        public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) { }
    }

    private class LoopMeRewardedListener implements LoopMeInterstitial.Listener {
        @Override
        public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
            mRewardedListener.onRewardedAdClicked();
        }
        @Override
        public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
            if (!isAlreadyRewarded) {
                mRewardedListener.onUserRewarded(getReward());
                isAlreadyRewarded = true;
            }
            mRewardedListener.onRewardedAdHidden();
        }
        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial arg0, LoopMeError arg1) {
            Log.d(LOG_TAG, "loopme's ad loading failed");
            mRewardedListener.onRewardedAdLoadFailed(new MaxAdapterError(arg1.getErrorCode()));
        }
        @Override
        public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
            Log.d(LOG_TAG, "loopme's ad loaded");
            isAlreadyRewarded = false;
            mRewardedListener.onRewardedAdLoaded();
        }
        @Override
        public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
            mRewardedListener.onRewardedAdDisplayed();
        }

        @Override
        public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) { }
        @Override
        public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) { }
        @Override
        public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
            if (!isAlreadyRewarded) {
                mRewardedListener.onUserRewarded(getReward());
                isAlreadyRewarded = true;
            }
        }
    }
}
