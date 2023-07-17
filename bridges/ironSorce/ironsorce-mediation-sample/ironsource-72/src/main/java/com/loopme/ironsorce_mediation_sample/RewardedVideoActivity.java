package com.loopme.ironsorce_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ironsource.adapters.custom.loopme.LoopmeCustomAdapter;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.loopme.LoopMeSdk;
import com.loopme.ironsorce_72.R;

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
        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
            @Override
            public void onRewardedVideoAdOpened() {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAvailabilityChanged(boolean b) {
                Toast.makeText(getApplicationContext(), "rewarded availability - " + b, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdStarted() {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdStarted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdEnded() {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdEnded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdRewarded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
                Toast.makeText(getApplicationContext(), "onRewardedVideoAdShowFailed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClicked(Placement placement) {
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