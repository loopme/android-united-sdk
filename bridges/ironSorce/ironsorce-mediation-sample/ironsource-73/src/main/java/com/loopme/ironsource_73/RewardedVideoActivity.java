package com.loopme.ironsource_73;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ironsource.adapters.custom.loopme.LoopmeCustomAdapter;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.loopme.LoopMeSdk;

public class RewardedVideoActivity extends Activity {
    private static final String appKey = "124e1d38d";
    private static final String loopmeAppKey = "e166087988";

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
        LoopmeCustomAdapter.setWeakActivity(this);
        LoopmeCustomAdapter.setLoopmeAppkey(loopmeAppKey);
        IronSource.init(this, appKey, IronSource.AD_UNIT.REWARDED_VIDEO);
        IronSource.setAdaptersDebug(true);

        Toast.makeText(this, "IronSource has been initialized", Toast.LENGTH_SHORT).show();
        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
            // Indicates that there's an available ad.
            // The adInfo object includes information about the ad that was loaded successfully
            // Use this callback instead of onRewardedVideoAvailabilityChanged(true)
            @Override
            public void onAdAvailable(AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedAdAvailable", Toast.LENGTH_SHORT).show();
            }

            // Indicates that no ads are available to be displayed
            // Use this callback instead of onRewardedVideoAvailabilityChanged(false)
            @Override
            public void onAdUnavailable() {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdUnavailable", Toast.LENGTH_SHORT).show();
            }

            // The Rewarded Video ad view has opened. Your activity will loose focus
            @Override
            public void onAdOpened(AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
            }

            // The Rewarded Video ad view is about to be closed. Your activity will regain its focus
            @Override
            public void onAdClosed(AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            // The user completed to watch the video, and should be rewarded.
            // The placement parameter will include the reward data.
            // When using server-to-server callbacks, you may ignore this event and wait for the ironSource server callback
            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdRewarded", Toast.LENGTH_SHORT).show();
            }

            // The rewarded video ad was failed to show
            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoShowFailed", Toast.LENGTH_SHORT).show();
            }

            // Invoked when the video ad was clicked.
            // This callback is not supported by all networks, and we recommend using it
            // only if it's supported by all networks you included in your build
            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdClicked", Toast.LENGTH_SHORT).show();
            }
        });
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