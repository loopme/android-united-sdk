package com.loopme.mopub_mediation_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.LoopMeAdapterConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SdkInitializationListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> loopMeConf = new HashMap<>();

        // Use this method in case if you Publisher is willing to ask GDPR consent with your own or
        // MoPub dialog AND don't want LoopMe consent dialog to be shown - pass GDPR consent to this method.
        //LoopMeAdapterConfiguration.setPublisherConsent(loopMeConf, true);

        // Unfortunately, MoPub SDK v5.5.x doesn't pass Activity context to initializeNetwork method:
        // pass it here, before initializing MoPub SDK.
        LoopMeAdapterConfiguration.setWeakActivity(this);

        MoPub.initializeSdk(this,
                new SdkConfiguration.Builder(BuildConfig.AD_UNIT_ID_BANNER)
                        .withAdditionalNetwork(
                                LoopMeAdapterConfiguration.class.getName())
                        .withMediatedNetworkConfiguration(
                                LoopMeAdapterConfiguration.class.getName(), loopMeConf)
                        .withLogLevel(MoPubLog.LogLevel.INFO)
                        .build(),
                this);

        Toast.makeText(this, "Initializing MoPub...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitializationFinished() {
        Toast.makeText(this, "MoPub has been initialized", Toast.LENGTH_SHORT).show();
        enableButtons();
    }

    private void enableButtons() {
        findViewById(R.id.interstitial).setEnabled(true);
        findViewById(R.id.rewarded).setEnabled(true);
        findViewById(R.id.banner).setEnabled(true);
    }

    public void openActivity(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.interstitial:
                intent = new Intent(this, InterstitialActivity.class);
                break;
            case R.id.rewarded:
                intent = new Intent(this, RewardedVideoActivity.class);
                break;
            case R.id.banner:
                intent = new Intent(this, BannerActivity.class);
                break;
        }

        startActivity(intent);
    }
}