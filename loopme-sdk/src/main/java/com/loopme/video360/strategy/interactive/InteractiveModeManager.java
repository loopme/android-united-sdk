package com.loopme.video360.strategy.interactive;

import android.content.Context;
import android.view.MotionEvent;

import com.loopme.video360.MD360Director;
import com.loopme.video360.strategy.ModeManager;

import java.util.List;

public class InteractiveModeManager extends ModeManager<AbsInteractiveStrategy> implements IInteractiveMode {

    private List<MD360Director> mDirectorList;
    private boolean mIsResumed;

    public InteractiveModeManager(List<MD360Director> directorList) {
        super();
        this.mDirectorList = directorList;
    }

    @Override
    public void switchMode(Context context) {
        switchMode(context);
        if (mIsResumed) onResume(context);
    }

    @Override
    protected AbsInteractiveStrategy createStrategy() {
        return new MotionStrategy(mDirectorList);
    }

    @Override
    public void onResume(Context context) {
        mIsResumed = true;
        getStrategy().onResume(context);
    }

    @Override
    public void onPause(Context context) {
        mIsResumed = false;
        getStrategy().onPause(context);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        return getStrategy().handleTouchEvent(event);
    }
}
