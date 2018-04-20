package com.loopme.tester.ads;

import com.loopme.ad.LoopMeAd;

public interface Ad {

    void loadAd();

    void showAd();

    void dismissAd();

    void destroyAd();

    void onPause();

    void onResume();

    boolean isReady();

    boolean isShowing();

    void setPreferredAd(LoopMeAd.Type type);
}
