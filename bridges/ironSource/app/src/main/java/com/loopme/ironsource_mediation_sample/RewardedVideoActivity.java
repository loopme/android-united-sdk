package com.loopme.ironsource_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ironsource.adapters.custom.loopme.LoopmeCustomAdapter;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoManualListener;
import com.loopme.LoopMeSdk;

public class RewardedVideoActivity extends Activity {
    private static final String appKey = "f7a719b9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        View loadButton = findViewById(R.id.load_button);
        loadButton.setEnabled(false);
        // Use this method in case if you Publisher is willing to ask GDPR consent with your own or
        // MoPub dialog AND don't want LoopMe consent dialog to be shown - pass GDPR consent to this method.
        //LoopMeAdapterConfiguration.setPublisherConsent(loopMeConf, true);

        LoopMeSdk.initialize(this, new LoopMeSdk.Configuration(), new LoopMeSdk.LoopMeSdkListener() {
            @Override
            public void onSdkInitializationSuccess() {
                Toast.makeText(RewardedVideoActivity.this, "Loopme has been initialized", Toast.LENGTH_LONG).show();
                loadButton.setEnabled(true);
            }

            @Override
            public void onSdkInitializationFail(int error, String message) {
                Toast.makeText(RewardedVideoActivity.this, "Loopme failed initialization", Toast.LENGTH_LONG).show();
            }
        });
        IronSource.setLevelPlayRewardedVideoManualListener(new LevelPlayRewardedVideoManualListener() {
            @Override
            public void onAdReady(AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdReady", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoadFailed(IronSourceError ironSourceError) {
                Toast.makeText(getApplicationContext(), "onRewardedLoadFailed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdShowFailed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdRewarded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdClosed", Toast.LENGTH_SHORT).show();
            }
        });

        IronSource.init(this, appKey, IronSource.AD_UNIT.REWARDED_VIDEO);
        IronSource.setAdaptersDebug(true);
        Toast.makeText(this, "IronSource has been initialized", Toast.LENGTH_SHORT).show();
    }

    public void onShowClicked(View view) {
        if (IronSource.isRewardedVideoAvailable())
            IronSource.showRewardedVideo();
    }

    public void onLoadClicked(View view) {
        LoopmeCustomAdapter.setWeakActivity(this);
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