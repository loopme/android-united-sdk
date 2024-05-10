package com.loopme.admob.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;


public class InterstitialActivity extends AppCompatActivity {
    private static final String LOG_TAG = InterstitialActivity.class.getSimpleName();
    private AdManagerInterstitialAd mAdManagerInterstitialAd;
    AdManagerAdRequest adRequest;
    private Button mLoadButton;
    private Button mShowButton;
    private void toast(String message) {
        Toast.makeText(InterstitialActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        mLoadButton = findViewById(R.id.load_button);
        mLoadButton.setEnabled(true);
        mLoadButton.setOnClickListener((view) -> loadAd());

        mShowButton = findViewById(R.id.show_button);
        mShowButton.setText("Interstitial Not Ready");
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener((view) -> showAd());

        adRequest = new AdManagerAdRequest.Builder().build();
    }

    private void showAd() {
        mShowButton.setText("Interstitial Not Ready");
        mShowButton.setEnabled(false);
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd.show(InterstitialActivity.this);
        } else {
            Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
        }
    }

    private void loadAd() {
        mShowButton.setText("Loading Interstitial...");
        mShowButton.setEnabled(false);
        AdManagerInterstitialAd.load(this,"/6499/example/interstitial", adRequest,
            new AdManagerInterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                    // The mAdManagerInterstitialAd reference will be null until
                    // an ad is loaded.
                    mAdManagerInterstitialAd = interstitialAd;
                    String adUnitId = mAdManagerInterstitialAd.getAdUnitId();
                    mShowButton.setText(String.format("Show Interstitial [%s]", adUnitId));
                    mShowButton.setEnabled(true);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    String errorMessage = loadAdError.getMessage();
                    mShowButton.setText(String.format("Failed To Receive Ad:\n[%s]", errorMessage));
                    mShowButton.setEnabled(false);
                    mAdManagerInterstitialAd = null;
                }
            });
    }
}
