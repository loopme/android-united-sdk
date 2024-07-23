package com.loopme.controllers.interfaces;

import android.widget.FrameLayout;

import com.loopme.Constants;

public interface LoopMeDisplayController {
    void onBuildStaticAdView(FrameLayout containerView);
    void onBuildMraidView(FrameLayout containerView);
    void onRebuildView(FrameLayout containerView);
}
