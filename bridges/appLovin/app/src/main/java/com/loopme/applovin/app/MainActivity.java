package com.loopme.applovin.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button interstitialButton = findViewById(R.id.interstitial);
        Button bannerButton = findViewById(R.id.banner);
        Button rewardedButton = findViewById(R.id.rewarded);
        interstitialButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), InterstitialActivity.class)));
        bannerButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), BannerActivity.class)));
        rewardedButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RewardedVideoActivity.class)));
    }
}