package com.loopme.tester.ads;

import android.app.Activity;

import com.loopme.ad.LoopMeAd;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

public class AdMopubInterstitial implements MoPubInterstitial.InterstitialAdListener, Ad {

    private MoPubInterstitial mInterstitial;
    private String mAdUnitId;

    private AdListener mAdListener;

    public AdMopubInterstitial(Activity context, String appId, AdListener listener) {
        mInterstitial = new MoPubInterstitial(context, appId);
        mInterstitial.setInterstitialAdListener(this);
        mAdListener = listener;
        mAdUnitId = appId;
    }

    @Override
    public boolean isReady() {
        return mInterstitial != null && mInterstitial.isReady();
    }

    @Override
    public boolean isShowing() {
        return false;
    }

    @Override
    public void setPreferredAd(LoopMeAd.Type type) {
    }

    @Override
    public void loadAd() {
        mInterstitial.load();
    }

    @Override
    public void showAd() {
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }

    @Override
    public void dismissAd() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
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
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        if (mAdListener != null) {
            mAdListener.onLoadSuccess();
        }
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        if (mAdListener != null) {
            mAdListener.onLoadFail(moPubErrorCode.toString());
        }
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {
        if (mAdListener != null) {
            mAdListener.onShow();
        }
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        if (mAdListener != null) {
            mAdListener.onHide();
        }
    }
}
