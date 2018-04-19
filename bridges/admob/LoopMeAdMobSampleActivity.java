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

    private static final String ADMOB_UNIT_ID = "ca-app-pub-3761402714494666/9717676232"; // YOUR AD_UNIT_ID
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
