package com.loopme.tester.qr.listener;

import com.loopme.LoopMeBanner;

public abstract class BannerListenerAdapter implements LoopMeBanner.Listener {

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner banner) {
    }
}
