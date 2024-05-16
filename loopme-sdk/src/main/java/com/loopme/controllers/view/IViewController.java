package com.loopme.controllers.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.loopme.Constants;
import com.loopme.views.AdView;

public interface IViewController {
    void setViewSize(int width, int height);
    void setVideoSize(int width, int height);
    void buildVideoAdView(Context context, ViewGroup viewGroup, AdView adView);
    void rebuildView(ViewGroup viewGroup, AdView adView, Constants.DisplayMode displayMode);
    void setStretchParam(Constants.StretchOption stretchOption);
    void onPause();
    void onResume();
    void onDestroy();
    boolean handleTouchEvent(MotionEvent event);
    void initVRLibrary(Context context);
}
