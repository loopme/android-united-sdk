package com.loopme.applovin.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

public class InterstitialActivity extends AppCompatActivity implements MaxAdListener {
    private static final String LOG_TAG = InterstitialActivity.class.getSimpleName();
    // private static final String APPLOVIN_UNIT_ID = "114e4121c286d22a";
    private static final String APPLOVIN_UNIT_ID = "9dd28ead1f9f70a5";
    private MaxInterstitialAd interstitialAd;
    private Button mShowButton;
    private void toast(String message) {
        Toast.makeText(InterstitialActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        Button mLoadButton = findViewById(R.id.load_button);
        mLoadButton.setEnabled(true);
        mLoadButton.setOnClickListener((view) -> loadAd());

        mShowButton = findViewById(R.id.show_button);
        mShowButton.setText("Interstitial Not Ready");
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener((view) -> showAd());
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
        interstitialAd.loadAd();
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        mShowButton.setText(String.format("Show Interstitial [%s]", ad.getNetworkName()));
        mShowButton.setEnabled(true);
    }
    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
        mShowButton.setText(String.format("Failed to Receive Ad:\n%s", error.getMessage()));
        mShowButton.setEnabled(false);
        interstitialAd = null;
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd ad) { toast("Interstitial Displayed"); }
    @Override
    public void onAdHidden(@NonNull MaxAd ad) { toast("Interstitial Hidden"); }
    @Override
    public void onAdClicked(@NonNull MaxAd ad) { toast("Interstitial Clicked"); }
    @Override
    public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) { toast("Interstitial Failed to Display"); }
}
