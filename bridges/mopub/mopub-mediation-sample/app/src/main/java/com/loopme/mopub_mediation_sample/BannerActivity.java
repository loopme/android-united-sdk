package com.loopme.mopub_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mopub.mobileads.LoopMeMoPubBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;

public class BannerActivity extends Activity implements MoPubView.BannerAdListener {

    private MoPubView mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        mBanner = findViewById(R.id.mopub_banner_view);
    }

    @Override
    protected void onPause() {
        LoopMeMoPubBanner.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        LoopMeMoPubBanner.resume();
        super.onResume();
    }

    public void onHideClicked(View view) {
        LoopMeMoPubBanner.destroy();
    }

    public void onLoadClicked(View view) {
        FrameLayout loopMeView = findViewById(R.id.loopme_banner_view);
        Map<String, Object> extras = new HashMap<>();
        extras.put("bannerView", loopMeView);
        mBanner.setLocalExtras(extras);

        mBanner.setAdUnitId(BuildConfig.AD_UNIT_ID_BANNER);
        mBanner.setBannerAdListener(this);
        mBanner.loadAd();
    }

    @Override
    protected void onDestroy() {
        mBanner.destroy();
        LoopMeMoPubBanner.destroy();
        super.onDestroy();
    }

    private void toast(String mess) {
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoaded(MoPubView moPubView) {
        toast("onBannerLoaded");
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
        toast("onBannerFailed " + moPubErrorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView moPubView) {
        toast("onBannerClicked");
    }

    @Override
    public void onBannerExpanded(MoPubView moPubView) {
        toast("onBannerExpanded");
    }

    @Override
    public void onBannerCollapsed(MoPubView moPubView) {
        toast("onBannerCollapsed");
    }
}
