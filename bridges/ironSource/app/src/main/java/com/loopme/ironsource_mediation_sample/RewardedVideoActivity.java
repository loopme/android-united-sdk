package com.loopme.ironsource_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.ironsource.mediationsdk.IronSource;

public class RewardedVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        View loadButton = findViewById(R.id.load_button);
        loadButton.setEnabled(true);
    }

    public void onShowClicked(View view) {
        if (IronSource.isRewardedVideoAvailable())
            IronSource.showRewardedVideo();
    }

    public void onLoadClicked(View view) {
        IronSource.loadRewardedVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
}