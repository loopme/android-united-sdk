package com.loopme.applovin_mediation_sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.loopme.LoopMeSdk;
import com.loopme.admob_mediation_sample.R;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, MaxAdListener, LoopMeSdk.LoopMeSdkListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String APPLOVIN_UNIT_ID = "00a4102a0500a881";

    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
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

        if (interstitialAd != null) {
            interstitialAd.showAd();
        } else {
            Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
        }
    }

    private void loadAd() {
        mShowButton.setText("Loading Interstitial...");
        mShowButton.setEnabled(false);
        interstitialAd = new MaxInterstitialAd(APPLOVIN_UNIT_ID, this);
        interstitialAd.setListener(this);
    }

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
        initializeAppLovin();
    }

    // LoopMe.
    @Override
    public void onSdkInitializationFail(int error, String message) {
        initializeAppLovin();
    }

    private void initializeAppLovin() {
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                initButtons();
            }
        });
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

    @Override
    public void onAdLoaded(MaxAd ad) {
        toast("onAdLoaded");

        interstitialAd = interstitialAd;
        mShowButton.setText("Show Interstitial");
        mShowButton.setEnabled(true);
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        toast("onAdFailedToLoad");

        mShowButton.setText("Failed to Receive Ad");
        mShowButton.setEnabled(false);
        interstitialAd = null;
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }
}
