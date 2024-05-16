package com.loopme.bridges.vpaid;

public interface VpaidBridge {

    void prepare();
    void startAd();
    void stopAd();
    void pauseAd();
    void resumeAd();
    void getAdSkippableState();
    void resizeAd(int width, int height, String fullscreen);
}
