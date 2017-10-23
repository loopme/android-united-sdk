package com.loopme.tester.ads;

public interface Ad {

    void loadAd();

    void showAd();

    void dismissAd();

    void destroyAd();

    void onPause();

    void onResume();

    boolean isReady();

    boolean isShowing();
}
