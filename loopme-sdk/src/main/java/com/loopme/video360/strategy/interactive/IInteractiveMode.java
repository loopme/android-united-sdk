package com.loopme.video360.strategy.interactive;

import android.content.Context;
import android.view.MotionEvent;

public interface IInteractiveMode {
    void onResume(Context context);
    void onPause(Context context);
    boolean handleTouchEvent(MotionEvent event);
}
