package com.loopme.controllers.interfaces;

import android.widget.FrameLayout;

public interface LoopMeDisplayController {
    void onBuildMraidView(FrameLayout containerView);
    void onRebuildView(FrameLayout containerView);
}
