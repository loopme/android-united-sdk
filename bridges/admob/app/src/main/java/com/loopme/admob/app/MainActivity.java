package com.loopme.admob.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private void toast(String message) {
        Log.d(LOG_TAG, message);
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

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

        MobileAds.initialize(this, initializationStatus -> {
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for (String adapterClass : statusMap.keySet()) {
                AdapterStatus status = statusMap.get(adapterClass);
                Log.d("MyApp", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status.getDescription(), status.getLatency()));
            }
            toast("AdMob SDK initialized\n" + initializationStatus);
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
