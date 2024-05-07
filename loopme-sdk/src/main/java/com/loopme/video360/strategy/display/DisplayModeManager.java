package com.loopme.video360.strategy.display;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.loopme.video360.strategy.ModeManager;

import java.util.List;

public class DisplayModeManager extends ModeManager<AbsDisplayStrategy> implements IDisplayMode {

    private final List<GLSurfaceView> mGLSurfaceViews;

    public DisplayModeManager(List<GLSurfaceView> glSurfaceViews) {
        super();
        this.mGLSurfaceViews = glSurfaceViews;
    }

    @Override
    public void switchMode(Context context) {
        switchMode(context);
    }

    @Override
    protected AbsDisplayStrategy createStrategy() {
        return new NormalStrategy(mGLSurfaceViews);
    }

    @Override
    public int getVisibleSize() {
        return getStrategy().getVisibleSize();
    }

}
