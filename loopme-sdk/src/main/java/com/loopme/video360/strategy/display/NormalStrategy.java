package com.loopme.video360.strategy.display;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.loopme.video360.strategy.display.AbsDisplayStrategy;

import java.util.List;

public class NormalStrategy extends AbsDisplayStrategy {

    public NormalStrategy(List<GLSurfaceView> glSurfaceViewList) {
        super(glSurfaceViewList);
    }

    @Override
    public void on(Context context) {
        setVisibleSize(1);
    }

    @Override
    public void off(Context context) {}

    @Override
    public int getVisibleSize() {
        return 1;
    }
}
