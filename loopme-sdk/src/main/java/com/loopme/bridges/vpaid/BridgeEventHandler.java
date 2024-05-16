package com.loopme.bridges.vpaid;

import com.loopme.ad.LoopMeAd;

public interface BridgeEventHandler {

    void runOnUiThread(Runnable runnable);
    void callJsMethod(final String url);
    void onPrepared();
    void onAdSkipped();
    void onAdStopped();
    void setSkippableState(boolean skippable);
    void onRedirect(String url, LoopMeAd loopMeAd);
    void trackError(String message);
    void postEvent(String eventType);
    void postEvent(String eventType, int value);
    void onDurationChanged();
    void onAdLinearChange();
    void onAdVolumeChange();
    void onAdImpression();
    void resizeAd();
    void adStarted();
    void setVideoTime(int time);
}
