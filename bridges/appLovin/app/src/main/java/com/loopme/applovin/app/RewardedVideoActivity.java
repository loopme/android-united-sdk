package com.loopme.applovin.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.loopme.LoopMeSdk;

public class RewardedVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        View loadButton = findViewById(R.id.load_button);
        loadButton.setEnabled(false);
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
    }
}