package com.loopme.ironsource_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ironsource.adapters.custom.loopme.LoopmeCustomAdapter;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.logger.IronSourceLogger;
import com.ironsource.mediationsdk.logger.LogListener;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.loopme.LoopMeSdk;

public class BannerActivity extends Activity {
    private static final String appKey = "f7a719b9";
    IronSourceBannerLayout banner;

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

        try {
            IronSource.init(this, appKey, IronSource.AD_UNIT.BANNER);
            IronSource.setAdaptersDebug(true);
            IronSource.setLogListener(new LogListener() {
                @Override
                public void onLog(IronSourceLogger.IronSourceTag tag, String message, int logLevel) {
                    Log.d("BannerActivity", "onLog");
                }
            });
        } catch (Exception e) {
            Log.d("BannerActivity", "onAdLoaded");
        }

        Toast.makeText(this, "IronSource has been initialized", Toast.LENGTH_SHORT).show();

        //banner = IronSource.createBanner(this, ISBannerSize.BANNER); // 320x50
        banner = IronSource.createBanner(this, ISBannerSize.RECTANGLE); // 300x250

        banner.setLevelPlayBannerListener(new LevelPlayBannerListener() {
            // Invoked each time a banner was loaded. Either on refresh, or manual load.
            //  AdInfo parameter includes information about the loaded ad
            @Override
            public void onAdLoaded(AdInfo adInfo) {
                Log.d("BannerActivity", "onAdLoaded");
            }

            // Invoked when the banner loading process has failed.
            //  This callback will be sent both for manual load and refreshed banner failures.
            @Override
            public void onAdLoadFailed(IronSourceError error) {
                Log.d("BannerActivity", "onAdLoadFailed");
            }

            // Invoked when end user clicks on the banner ad
            @Override
            public void onAdClicked(AdInfo adInfo) {
                Log.d("BannerActivity", "onAdClicked");
            }

            // Notifies the presentation of a full screen content following user click
            @Override
            public void onAdScreenPresented(AdInfo adInfo) {
                Log.d("BannerActivity", "onAdScreenPresented");
            }

            // Notifies the presented screen has been dismissed
            @Override
            public void onAdScreenDismissed(AdInfo adInfo) {
                Log.d("BannerActivity", "onAdScreenDismissed");
            }

            //Invoked when the user left the app
            @Override
            public void onAdLeftApplication(AdInfo adInfo) {
                Log.d("BannerActivity", "onAdLeftApplication");
            }
        });

        FrameLayout bannerContainer = findViewById(R.id.banner_container);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        bannerContainer.addView(banner, 0, layoutParams);
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

    public void onLoadClicked(View view) {
        LoopmeCustomAdapter.setWeakActivity(this);
        IronSource.loadBanner(banner);
    }
}