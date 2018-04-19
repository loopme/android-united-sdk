package com.loopme.mopubbridgedemo3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.loopme.LoopMeBannerView;
import com.mopub.mobileads.LoopMeMopubBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;

public class BannerSampleActivity extends Activity implements MoPubView.BannerAdListener {

    private MoPubView mBanner;
    private static final String AD_UNIT_ID = "b4dc5fb8636645c08017780f6c0f0b71";//Your mopub key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_activity_main);

        mBanner = (MoPubView) findViewById(R.id.mopub_banner_view);
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

    public void onHideClicked(View view){
        LoopMeMoPubBanner.destroy();
    }

    public void onLoadClicked(View view){
        mBanner.setAdUnitId(AD_UNIT_ID);
        mBanner.setBannerAdListener(this);
        mBanner.loadAd();

        LoopMeContainerView loopmeView = findViewById(R.id.loopme_banner_view);
        Map extras = new HashMap();
        extras.put("bannerView", loopmeView);
        mBanner.setLocalExtras(extras);
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
