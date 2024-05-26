package com.loopme.applovin.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

public class RewardedVideoActivity extends Activity implements MaxRewardedAdListener {
    private static final String LOG_TAG = RewardedVideoActivity.class.getSimpleName();
    private static final String APPLOVIN_UNIT_ID = "53df96fcbd095f70";
    private MaxRewardedAd rewardedAd;
    private Button mLoadButton;
    private Button mShowButton;
    private void toast(String message) {
        Toast.makeText(RewardedVideoActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);

        mLoadButton = findViewById(R.id.load_button);
        mLoadButton.setOnClickListener((view) -> loadAd());

        mShowButton = findViewById(R.id.show_button);
        mShowButton.setText("Rewarded Not Ready");
        mShowButton.setEnabled(false);
        mShowButton.setOnClickListener((view) -> showAd());
    }
    private void showAd() {
        mShowButton.setText("Rewarded Not Ready");
        mShowButton.setEnabled(false);
        if (rewardedAd != null) {
            rewardedAd.showAd();
        }
    }

    private void loadAd() {
        mShowButton.setText("Loading Rewarded...");
        mShowButton.setEnabled(false);
        rewardedAd = MaxRewardedAd.getInstance(APPLOVIN_UNIT_ID, this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }

    @Override
    public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) { toast("onUserRewarded"); }
    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {
        mShowButton.setText(String.format("Show Rewarded [%s]", maxAd.getNetworkName()));
        mShowButton.setEnabled(true);
    }
    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) { toast("onAdDisplayed"); }
    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) { toast("onAdHidden"); }
    @Override
    public void onAdClicked(@NonNull MaxAd maxAd) { toast("onAdClicked"); }
    @Override
    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
        mShowButton.setText(String.format("Failed to Receive Ad:\n%s", maxError.getMessage()));
        mShowButton.setEnabled(false);
        rewardedAd = null;
    }
    @Override
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) { toast("onAdDisplayFailed"); }
}