package com.loopme.applovin.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.loopme.LoopMeSdk;

public class RewardedVideoActivity extends Activity implements MaxRewardedAdListener {
    private MaxRewardedAd rewardedAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);
        View loadButton = findViewById(R.id.load_button);
        loadButton.setEnabled(false);
        rewardedAd = MaxRewardedAd.getInstance("53df96fcbd095f70", this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }

    public void onLoadClicked(View view) {
        Toast.makeText(RewardedVideoActivity.this, "POP", Toast.LENGTH_LONG).show();

    }
    public void onShowClicked(View view) {
        if ( rewardedAd.isReady() )
        {
            Toast.makeText(RewardedVideoActivity.this, "SHOWING", Toast.LENGTH_LONG).show();

            rewardedAd.showAd();
        } else {
            Toast.makeText(RewardedVideoActivity.this, "NOT SHOWING", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) {
        Toast.makeText(RewardedVideoActivity.this, "onUserRewarded", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {
        Toast.makeText(RewardedVideoActivity.this, "onAdLoaded", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {
        Toast.makeText(RewardedVideoActivity.this, "onAdDisplayed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {
        Toast.makeText(RewardedVideoActivity.this, "onAdHidden", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAdClicked(@NonNull MaxAd maxAd) {
        Toast.makeText(RewardedVideoActivity.this, "onAdClicked", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
        Toast.makeText(RewardedVideoActivity.this, "onAdLoadFailed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
        Toast.makeText(RewardedVideoActivity.this, "onAdDisplayFailed", Toast.LENGTH_LONG).show();

    }
}