package com.loopme.applovin.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

public class BannerActivity
        extends Activity
        implements MaxAdViewAdListener {
    private MaxAdView bigAdView;
    private MaxAdView smallAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        bigAdView = findViewById(R.id.big_banner_ad_view);
        bigAdView.setListener(this);
        bigAdView.loadAd();

        smallAdView = findViewById(R.id.small_banner_ad_view);
        smallAdView.setListener(this);
        smallAdView.loadAd();
    }

    protected void onDestroy()
    {
        super.onDestroy();

        bigAdView.destroy();
        smallAdView.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onLoadClicked(View view) {

    }

    @Override
    public void onAdExpanded(@NonNull MaxAd maxAd) {

    }

    @Override
    public void onAdCollapsed(@NonNull MaxAd maxAd) {

    }

    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {

    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {

    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {

    }

    @Override
    public void onAdClicked(@NonNull MaxAd maxAd) {

    }

    @Override
    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {

    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {

    }
}