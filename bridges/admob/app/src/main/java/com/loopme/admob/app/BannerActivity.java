package com.loopme.admob.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

// TODO: Implement by following https://developers.google.com/ad-manager/mobile-ads-sdk/android/banner
public class BannerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
    }

    protected void onDestroy() {
        super.onDestroy();
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