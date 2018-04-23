package com.loopme.controllers.interfaces;

import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.ad.LoopMeAd;
import com.loopme.models.Message;

public interface DisplayController {

    void onStartLoad();

    void onBuildVideoAdView(FrameLayout frameLayout);

    void onRedirect(@Nullable String url, LoopMeAd loopMeAd);

    void onVolumeMute(boolean mute);

    void onPlay(int position);

    void onPause();

    void onResume();

    void onDestroy();

    void onMessage(Message type, String message);

    boolean isFullScreen();

    WebView getWebView();

    void onAdShake();

    void setFullScreen(boolean isFullScreen);

    int getOrientation();
}
