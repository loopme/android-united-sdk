package com.loopme.mopubbridgedemo3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

public class InterstitialSampleActivity extends Activity implements
        MoPubInterstitial.InterstitialAdListener {

    private MoPubInterstitial mInterstitial;
    private static final String AD_UNIT_ID = "5f1b7dca09ac479c91d4ce1e1c25fb35";//Your mopub key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    public void onShowClicked(View view){
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    public void onLoadClicked(View view){
        mInterstitial = new MoPubInterstitial(InterstitialSampleActivity.this, AD_UNIT_ID);
        mInterstitial.setInterstitialAdListener(InterstitialSampleActivity.this);
        mInterstitial.load();
    }

    public void onTestBannerClicked(View view){
        startActivity(new Intent(this, BannerSampleActivity.class));
    }

    @Override
    protected void onDestroy() {
        if(mInterstitial != null){
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