package com.loopme.mopub_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

public class InterstitialActivity extends Activity implements
        MoPubInterstitial.InterstitialAdListener {

    private MoPubInterstitial mInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
    }

    public void onShowClicked(View view) {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    public void onLoadClicked(View view) {
        mInterstitial = new MoPubInterstitial(this, BuildConfig.AD_UNIT_ID_INTERSTITIAL);
        mInterstitial.setInterstitialAdListener(this);
        mInterstitial.load();
    }

    @Override
    protected void onDestroy() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        Toast.makeText(getApplicationContext(), "onInterstitialLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        Toast.makeText(getApplicationContext(), "onInterstitialFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
        Toast.makeText(getApplicationContext(), "onInterstitialShown", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
        Toast.makeText(getApplicationContext(), "onInterstitialClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        Toast.makeText(getApplicationContext(), "onInterstitialDismissed", Toast.LENGTH_SHORT).show();
    }
}