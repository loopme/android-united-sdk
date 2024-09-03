package com.admob.mediation.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationInterstitialAd;
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback;
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.loopme.BuildConfig;
import com.loopme.LoopMeSdk;

import java.util.List;

@Keep
public class LoopMeMediationAdapter extends Adapter {

    private static final String LOG_TAG = LoopMeMediationAdapter.class.getSimpleName();

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = LoopMeSdk.getVersion();
        String[] splits = versionString.split("\\.");

        if (splits.length >= 3) {
            int major = Integer.parseInt(splits[0]);
            int minor = Integer.parseInt(splits[1]);
            int micro = Integer.parseInt(splits[2]);
            return new VersionInfo(major, minor, micro);
        }
        return new VersionInfo(0, 0, 0);
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        String versionString = BuildConfig.VERSION_NAME;
        String[] splits = versionString.split("\\.");

        if (splits.length >= 3) {
            int major = Integer.parseInt(splits[0]);
            int minor = Integer.parseInt(splits[1]);
            int micro = Integer.parseInt(splits[2]);
            return new VersionInfo(major, minor, micro);
        }
        return new VersionInfo(0, 0, 0);
    }

    @Override
    public void initialize(
        @NonNull Context context,
        @NonNull InitializationCompleteCallback initializationCompleteCallback,
        @NonNull List<MediationConfiguration> list
    ) {
        Log.d(LOG_TAG, "initialization");
        if (LoopMeSdk.isInitialized()) {
            initializationCompleteCallback.onInitializationSucceeded();
            return;
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();
            loopMeConf.setMediation("admob");
            loopMeConf.setMediationSdkVersion(getMediationSdkVersion());
            loopMeConf.setAdapterVersion(getAdapterVersion());
            LoopMeSdk.initialize(context, loopMeConf, new LoopMeSdk.LoopMeSdkListener() {
                @Override
                public void onSdkInitializationSuccess() {
                    initializationCompleteCallback.onInitializationSucceeded();
                }

                @Override
                public void onSdkInitializationFail(int error, String message) {
                    initializationCompleteCallback.onInitializationFailed(message);
                }
            });
        });
    }

    public String getMediationSdkVersion() {
        return MobileAds.getVersion().toString();
    }
    public String getAdapterVersion() { return BuildConfig.VERSION_NAME; }

    @Override
    public void loadInterstitialAd(
        @NonNull MediationInterstitialAdConfiguration adConfiguration,
        @NonNull MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback> callback
    ) {
        LoopMeInterstitialListener interstitialLoader = new LoopMeInterstitialListener(adConfiguration, callback);
        interstitialLoader.loadAd();
    }

    @Override
    public void loadRewardedAd(
        @NonNull MediationRewardedAdConfiguration adConfiguration,
        @NonNull MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> callback
    ) {
        LoopMeRewardedListener rewardedLoader = new LoopMeRewardedListener(adConfiguration, callback);
        rewardedLoader.loadAd();
    }

    @Override
    public void loadBannerAd(
        @NonNull MediationBannerAdConfiguration adConfiguration,
        @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback
    ) {
        LoopMeBannerListener bannerLoader = new LoopMeBannerListener(adConfiguration, callback);
        bannerLoader.loadAd();
    }
}

