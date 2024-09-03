package com.loopme.admob.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class RewardedVideoActivity extends Activity {
    private static final String LOG_TAG = RewardedVideoActivity.class.getSimpleName();
//    private static final String adUnitId = "/6499/example/rewarded";
    private static final String adUnitId = "ca-app-pub-3222206793588139/6342952349";
    private RewardedAd rewardedAd;
    AdManagerAdRequest adRequest;
    private Button mShowButton;
    private void toast(String message) {
        Toast.makeText(RewardedVideoActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);

        Button mLoadButton = findViewById(R.id.load_button);
        mLoadButton.setOnClickListener((view) -> loadAd());

        mShowButton = findViewById(R.id.show_button);
        mShowButton.setText("Rewarded Not Ready");
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener((view) -> showAd());

        adRequest = new AdManagerAdRequest.Builder().build();
    }
    private void showAd() {
        mShowButton.setText("Rewarded Not Ready");
        mShowButton.setEnabled(false);
        if (rewardedAd != null) {
            rewardedAd.show(RewardedVideoActivity.this, rewardedItem -> {
                Log.d(LOG_TAG, "User earned the reward.");
                int rewardAmount = rewardedItem.getAmount();
                String rewardType = rewardedItem.getType();
                toast("User earned the reward. Amount: " + rewardAmount + " " + rewardType);
            });
        }
    }

    private void loadAd() {
        mShowButton.setText("Loading Rewarded...");
        mShowButton.setEnabled(false);
        RewardedAd.load(this, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                String errorMessage = loadAdError.getMessage();
                mShowButton.setText(String.format("Failed To Receive Ad:\n[%s]", errorMessage));
                mShowButton.setEnabled(false);
                rewardedAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                AdapterResponseInfo adapterResponseInfo = ad.getResponseInfo().getLoadedAdapterResponseInfo();
                String appkey = (String) adapterResponseInfo.getCredentials().get("parameter");
                String adapterClassName = adapterResponseInfo.getAdapterClassName();
                mShowButton.setText(String.format("Show Rewarded \nAppKey: %s\nAdapter: %s", appkey, adapterClassName));
                mShowButton.setEnabled(true);
            }
        });
    }
}