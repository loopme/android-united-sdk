package com.loopme.video360.strategy.display;

import android.opengl.GLSurfaceView;
import android.view.View;

import com.loopme.video360.strategy.IModeStrategy;

import java.util.List;

public abstract class AbsDisplayStrategy implements IModeStrategy, IDisplayMode {
    private final List<GLSurfaceView> mGLSurfaceViewList;

    public AbsDisplayStrategy(List<GLSurfaceView> glSurfaceViewList) {
        this.mGLSurfaceViewList = glSurfaceViewList;
    }

    protected List<GLSurfaceView> getGLSurfaceViewList() {
        return mGLSurfaceViewList;
    }

    void setVisibleSize(int max){
        int i = 0;
        for (GLSurfaceView surfaceView : getGLSurfaceViewList()){
            if (i < max) {
                surfaceView.setVisibility(View.VISIBLE);
            } else {
                surfaceView.setVisibility(View.GONE);
            }
            i++;
        }
    }
}
