package com.loopme.applovin.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxMediatedNetworkInfo;
import com.applovin.mediation.adapters.LoopmeMediationAdapter;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = LoopmeMediationAdapter.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button interstitialButton = findViewById(R.id.interstitial);
        interstitialButton.setEnabled(false);
        interstitialButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), InterstitialActivity.class)));

        Button bannerButton = findViewById(R.id.banner);
        bannerButton.setEnabled(false);
        bannerButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), BannerActivity.class)));

        Button rewardedButton = findViewById(R.id.rewarded);
        rewardedButton.setEnabled(false);
        rewardedButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RewardedVideoActivity.class)));

        initializeAppLovin();
    }

    private void initializeAppLovin() {
        List<MaxMediatedNetworkInfo> networks = AppLovinSdk.getInstance(this).getAvailableMediatedNetworks();
        for (MaxMediatedNetworkInfo network : networks) {
            System.out.println(network.getAdapterClassName());
        }
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk(this, configuration -> {
            Log.d(LOG_TAG, "AppLovin SDK initialized");
            initButtons();
        });
    }

    private void initButtons() {
        Button interstitialButton = findViewById(R.id.interstitial);
        interstitialButton.setEnabled(true);

        Button bannerButton = findViewById(R.id.banner);
        bannerButton.setEnabled(true);

        Button rewardedButton = findViewById(R.id.rewarded);
        rewardedButton.setEnabled(true);
    }
}