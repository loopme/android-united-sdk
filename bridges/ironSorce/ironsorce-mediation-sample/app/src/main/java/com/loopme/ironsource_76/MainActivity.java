package com.loopme.ironsource_76;

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
        Button rewardedButton = findViewById(R.id.rewarded);
        interstitialButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), InterstitialActivity.class)));
        rewardedButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RewardedVideoActivity.class)));
    }
}