package com.loopme.ironsource_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;

public class BannerActivity extends Activity {
    IronSourceBannerLayout banner;
    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        //banner = IronSource.createBanner(this, ISBannerSize.BANNER); // 320x50
        banner = IronSource.createBanner(this, ISBannerSize.RECTANGLE); // 300x250
        initIronSourceListeners();

        FrameLayout bannerContainer = findViewById(R.id.banner_container);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        );
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
        IronSource.loadBanner(banner);
    }

    public void onDestroy() {
        super.onDestroy();
        IronSource.destroyBanner(banner);
    }

    private void initIronSourceListeners() {
        banner.setLevelPlayBannerListener(new LevelPlayBannerListener() {
            @Override
            public void onAdLoaded(AdInfo adInfo) { toast("onAdLoaded"); }
            @Override
            public void onAdLoadFailed(IronSourceError error) { toast("onAdLoadFailed"); }
            @Override
            public void onAdClicked(AdInfo adInfo) { toast("onAdClicked"); }
            @Override
            public void onAdScreenPresented(AdInfo adInfo) { toast("onAdScreenPresented"); }
            @Override
            public void onAdScreenDismissed(AdInfo adInfo) { toast("onAdScreenDismissed"); }
            @Override
            public void onAdLeftApplication(AdInfo adInfo) { toast("onAdLeftApplication"); }
        });
    }
}