package com.lm.admobbridgetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class LoopMeAdMobSampleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ADMOB_UNIT_ID = "ca-app-pub-2365163122289590/4973472667"; // YOUR AD_UNIT_ID
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private InterstitialAd mInterstitialAd;
    private Button mLoadButton;
    private Button mShowButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(ADMOB_UNIT_ID);
        mInterstitialAd.setAdListener(mAdListener);

        initButtons();

//       For correct initialization AdMob's Consent SDK you should add the gradle dependency
//       compile 'com.google.android.ads.consent:consent-library:1.0.6'

        initGdprChecker();
    }

    private void initGdprChecker(){
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL("https://loopme.com/privacy/");
        } catch (MalformedURLException e) {
            e.printStackTrace();

        }

        form = new ConsentForm.Builder(this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        Logging.out("+++", "onConsentFormLoaded ");
                        form.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        Logging.out("+++", "onConsentFormOpened ");
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Logging.out("+++", "onConsentFormClosed " + consentStatus.name() + " userPrefersAdFree" + userPrefersAdFree);
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Logging.out("+++", "errorDescription " + errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();

        form.load();


        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA);
        consentInformation.addTestDevice("22BE2250B43518CCDA7DE426D04EE232");
        String[] publisherIds = {"pub-2365163122289590"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Logging.out("+++", "consentStatus " + consentStatus.name());
                Toast.makeText(LoopMeAdMobSampleActivity.this, "consentStatus " + consentStatus.name(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedToUpdateConsentInfo(String reason) {
                Logging.out("+++", "reason " + reason);
                Toast.makeText(LoopMeAdMobSampleActivity.this, "reason " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toast(String mess) {
        Toast.makeText(MainActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    private void initButtons() {
        mLoadButton = findViewById(R.id.load_button);
        mShowButton = findViewById(R.id.show_button);
        mShowButton.setText("Interstitial Not Ready");
        mShowButton.setEnabled(false);

        mLoadButton.setOnClickListener(this);
        mShowButton.setOnClickListener(this);
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
}
