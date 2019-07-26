package com.loopme.admob_mediation_sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.loopme.LoopMeSdk;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, OnInitializationCompleteListener, LoopMeSdk.LoopMeSdkListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String ADMOB_UNIT_ID = "YOUR_ADMOB_UNIT_ID";

    private InterstitialAd mInterstitialAd;
    private Button mLoadButton;
    private Button mShowButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLoopMe();
    }

    private void toast(String mess) {
        Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view == mLoadButton) {
            loadAd();
        } else if (view == mShowButton) {
            showAd();
        }
    }

    private void showAd() {
        mShowButton.setText("Interstitial Not Ready");
        mShowButton.setEnabled(false);

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
        }
    }

    private void loadAd() {
        mShowButton.setText("Loading Interstitial...");
        mShowButton.setEnabled(false);
        AdRequest.Builder builder = new AdRequest.Builder();
        mInterstitialAd.loadAd(builder.build());
    }

    private AdListener mAdListener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            toast("onAdClosed");
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            toast("onAdFailedToLoad");

            mShowButton.setText("Failed to Receive Ad");
            mShowButton.setEnabled(false);
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            toast("onAdLeftApplication");
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            toast("onAdOpened");
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            toast("onAdLoaded");

            mShowButton.setText("Show Interstitial");
            mShowButton.setEnabled(true);
        }
    };

    private void initializeLoopMe() {
        LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();

        // Use this method in case if you Publisher is willing to ask GDPR consent
        // with your own or AdMob dialog
        // AND don't want LoopMe consent dialog to be shown,
        // pass GDPR consent to this method.
        //loopMeConf.setPublisherConsent(new GdprChecker.PublisherConsent(publisherConsentResult));

        LoopMeSdk.initialize(this, loopMeConf, this);
    }

    // LoopMe.
    @Override
    public void onSdkInitializationSuccess() {
        initializeAdMob();
    }

    // LoopMe.
    @Override
    public void onSdkInitializationFail(int error, String message) {
        initializeAdMob();
    }

    private void initializeAdMob() {
        MobileAds.initialize(this, this);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(ADMOB_UNIT_ID);
        mInterstitialAd.setAdListener(mAdListener);
    }

    // AdMob.
    @Override
    public void onInitializationComplete(InitializationStatus initializationStatus) {
        initButtons();
    }

    private void initButtons() {
        mLoadButton = findViewById(R.id.load_button);
        mLoadButton.setEnabled(true);

        mShowButton = findViewById(R.id.show_button);
        mShowButton.setText("Interstitial Not Ready");
        mShowButton.setEnabled(false);

        mLoadButton.setOnClickListener(this);
        mShowButton.setOnClickListener(this);
    }
}
