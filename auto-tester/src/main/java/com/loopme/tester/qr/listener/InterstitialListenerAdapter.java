package com.loopme.tester.qr.listener;

import com.loopme.LoopMeInterstitial;

public abstract class InterstitialListenerAdapter implements LoopMeInterstitial.Listener {

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
    }
}
