package com.loopme.applovin.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.loopme.LoopMeSdk;

public class BannerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        LoopMeSdk.initialize(this, new LoopMeSdk.Configuration(), new LoopMeSdk.LoopMeSdkListener() {
            @Override
            public void onSdkInitializationSuccess() {
                Toast.makeText(BannerActivity.this, "Loopme has been initialized", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSdkInitializationFail(int error, String message) {
                Toast.makeText(BannerActivity.this, "Loopme failed initialization", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onLoadClicked(View view) {

    }
}