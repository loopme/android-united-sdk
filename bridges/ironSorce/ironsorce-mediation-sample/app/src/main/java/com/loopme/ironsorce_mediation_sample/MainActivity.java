package com.loopme.ironsorce_mediation_sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.ironsource.mediationsdk.IronSource;
import com.loopme.mopub_mediation_sample.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> loopMeConf = new HashMap<>();


        enableButtons();
    }

    private void enableButtons() {
        findViewById(R.id.interstitial).setEnabled(true);
        findViewById(R.id.rewarded).setEnabled(true);
        findViewById(R.id.banner).setEnabled(false);
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
        }

        startActivity(intent);
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