package com.loopme.tester.ads;

import android.view.View;

import com.loopme.Logging;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

public class AdMopubBanner implements Ad, MoPubView.BannerAdListener {

    private MoPubView mMoPubView;
    private String mAdUnitId;

    private AdListener mAdListener;

    public AdMopubBanner(MoPubView view, String appId, AdListener listener) {
        mMoPubView = view;
        mAdUnitId = appId;
        mAdListener = listener;
    }

    @Override
    public void loadAd() {
        mMoPubView.setAdUnitId(mAdUnitId);
        mMoPubView.setBannerAdListener(this);
        mMoPubView.loadAd();
        mMoPubView.setVisibility(View.GONE);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public boolean isShowing() {
        return mMoPubView != null && mMoPubView.isShown();
    }

    @Override
    public void showAd() {
        mMoPubView.setVisibility(View.VISIBLE);
        if (mAdListener != null) {
            mAdListener.onShow();
        }
    }

    @Override
    public void dismissAd() {
        if (mMoPubView != null) {
            mMoPubView.destroy();
            Logging.out("Ad closed.");
        }
    }

    @Override
    public void destroyAd() {
        dismissAd();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onBannerLoaded(MoPubView moPubView) {
        if (mAdListener != null) {
            mAdListener.onLoadSuccess();
        }
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
        if (mAdListener != null) {
            mAdListener.onLoadFail(moPubErrorCode.toString());
        }
    }

    @Override
    public void onBannerClicked(MoPubView moPubView) {
    }

    @Override
    public void onBannerExpanded(MoPubView moPubView) {

    }

    @Override
    public void onBannerCollapsed(MoPubView moPubView) {

    }
}
