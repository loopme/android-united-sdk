package com.loopme.tester.ads;

import android.app.Activity;

import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;

public class AdLoopMeInterstitial implements LoopMeInterstitial.Listener, Ad {

    private LoopMeInterstitial mInterstitial;

    private AdListener mAdListener;

    public AdLoopMeInterstitial(Activity context, String appKey, AdListener listener, boolean autoLoadingEnabled) {
        mInterstitial = LoopMeInterstitial.getInstance(appKey, context);
        mAdListener = listener;
        mInterstitial.setAutoLoading(autoLoadingEnabled);
    }

    @Override
    public void loadAd() {
        if (mInterstitial != null) {
            mInterstitial.setListener(this);
            mInterstitial.load();
        } else {
            mAdListener.onLoadFail("Interstitial is null");
        }
    }

    @Override
    public void showAd() {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    public void dismissAd() {
        if (mInterstitial != null) {
            mInterstitial.dismiss();
        }
    }

    @Override
    public void destroyAd() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public boolean isReady() {
        return mInterstitial != null && mInterstitial.isReady();
    }

    @Override
    public boolean isShowing() {
        return mInterstitial != null && mInterstitial.isShowing();
    }


    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        if (mAdListener != null) {
            mAdListener.onLoadSuccess();
        }
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        if (mAdListener != null) {
            mAdListener.onLoadFail(error.getMessage());
        }
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
        if (mAdListener != null) {
            mAdListener.onShow();
        }
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        if (mAdListener != null) {
            mAdListener.onHide();
        }
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {

    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {

    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
        if (mAdListener != null) {
            mAdListener.onExpired();
        }
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {

    }
}
