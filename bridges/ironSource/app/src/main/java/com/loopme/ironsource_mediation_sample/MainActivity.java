package com.loopme.ironsource_mediation_sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoManualListener;

public class MainActivity extends AppCompatActivity {

    private static final String appKey = "124e1d38d";
    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private static boolean isIronSourceInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button interstitialButton = findViewById(R.id.interstitial);
        interstitialButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), InterstitialActivity.class)));
        interstitialButton.setEnabled(false);

        Button rewardedButton = findViewById(R.id.rewarded);
        rewardedButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RewardedVideoActivity.class)));
        rewardedButton.setEnabled(false);

        Button bannerButton = findViewById(R.id.banner);
        bannerButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), BannerActivity.class)));
        bannerButton.setEnabled(false);

        if (isIronSourceInitialized) {
            initButtons();
        } else {
            initIronSourceListeners();
            IronSource.init(this, appKey, () -> MainActivity.this.runOnUiThread(this::initButtons));
            IronSource.setAdaptersDebug(true);
        }
    }

    private void initButtons() {
        isIronSourceInitialized = true;
        Button interstitialButton = findViewById(R.id.interstitial);
        interstitialButton.setEnabled(true);

        Button bannerButton = findViewById(R.id.banner);
        bannerButton.setEnabled(true);

        Button rewardedButton = findViewById(R.id.rewarded);
        rewardedButton.setEnabled(true);
    }

    private void initIronSourceListeners() {
        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
            @Override
            public void onAdReady(AdInfo adInfo) { toast("onInterstitialAdReady"); }
            @Override
            public void onAdLoadFailed(IronSourceError ironSourceError) { toast("onInterstitialAdLoadFailed"); }
            @Override
            public void onAdOpened(AdInfo adInfo) { toast("onInterstitialAdOpened"); }
            @Override
            public void onAdShowSucceeded(AdInfo adInfo) { toast("onInterstitialAdShowSucceeded"); }
            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) { toast("onInterstitialAdShowFailed"); }
            @Override
            public void onAdClicked(AdInfo adInfo) { toast("onInterstitialAdClicked"); }
            @Override
            public void onAdClosed(AdInfo adInfo) { toast("onInterstitialAdClosed"); }
        });

        IronSource.setLevelPlayRewardedVideoManualListener(new LevelPlayRewardedVideoManualListener() {
            @Override
            public void onAdReady(AdInfo adInfo) { toast("onRewardedAdReady"); }
            @Override
            public void onAdLoadFailed(IronSourceError ironSourceError) { toast("onRewardedLoadFailed"); }
            @Override
            public void onAdOpened(AdInfo adInfo) { toast("onRewardedAdOpened"); }
            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) { toast("onRewardedAdShowFailed"); }
            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) { toast("onRewardedAdClicked"); }
            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) { toast("onRewardedAdRewarded"); }
            @Override
            public void onAdClosed(AdInfo adInfo) { toast("onRewardedAdClosed"); }
        });
    }
}