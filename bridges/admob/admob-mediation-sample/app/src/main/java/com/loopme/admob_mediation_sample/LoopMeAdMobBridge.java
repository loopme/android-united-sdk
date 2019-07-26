package com.loopme.admob_mediation_sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.loopme.IntegrationType;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;

public class LoopMeAdMobBridge implements CustomEventInterstitial {

    private static final String LOG_TAG = LoopMeAdMobBridge.class.getSimpleName();

    private LoopMeInterstitial mInterstitial;
    private final LoopMeListener mLoopMeListener = new LoopMeListener();
    private CustomEventInterstitialListener mListener;

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener customEventInterstitialListener,
                                      String s,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle o) {

        Log.d(LOG_TAG, "requestInterstitialAd");

        mListener = customEventInterstitialListener;

        if (context instanceof Activity) {
            mInterstitial = LoopMeInterstitial.getInstance(s, (Activity) context);
            mInterstitial.setListener(mLoopMeListener);
            mInterstitial.load(IntegrationType.ADMOB);
        } else {
            Log.i(LOG_TAG, "context should not be null and should be instance of Activity");
        }
    }

    @Override
    public void showInterstitial() {
        Log.d(LOG_TAG, "showInterstitial");
        if (mInterstitial != null && mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        mInterstitial.destroy();
    }

    @Override
    public void onPause() {
    }

    private class LoopMeListener implements LoopMeInterstitial.Listener {

        @Override
        public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
            mListener.onAdClicked();
        }

        @Override
        public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
        }

        @Override
        public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
            mListener.onAdClosed();
        }

        @Override
        public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
        }

        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial arg0,
                                                 LoopMeError arg1) {
            mListener.onAdFailedToLoad(0);
        }

        @Override
        public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
            mListener.onAdLoaded();
        }

        @Override
        public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
            mListener.onAdOpened();
        }

        @Override
        public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        }
    }
}