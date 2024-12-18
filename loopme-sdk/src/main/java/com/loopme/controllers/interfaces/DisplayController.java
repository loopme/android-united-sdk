package com.loopme.controllers.interfaces;

import androidx.annotation.Nullable;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.ad.LoopMeAd;

public interface DisplayController {
    void onStartLoad();
    void onBuildVideoAdView(FrameLayout frameLayout);
    void onRedirect(@Nullable String url, LoopMeAd loopMeAd);
    void onVolumeMute(boolean mute);
    void onPlay(int position);
    void onPause();
    void onResume();
    void onDestroy();
    boolean isFullScreen();
    WebView getWebView();
    int getOrientation();
}
