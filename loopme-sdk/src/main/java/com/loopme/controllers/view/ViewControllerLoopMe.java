package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.utils.Utils;
import com.loopme.views.AdView;

public class ViewControllerLoopMe implements TextureView.SurfaceTextureListener, IViewController {

    private static final String LOG_TAG = ViewControllerLoopMe.class.getSimpleName();
    private static final int CHILD_INDEX_0 = 0;
    private static final int CHILD_INDEX_1 = 1;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mResizeWidth;
    private int mResizeHeight;

    private Surface mSurface;
    private final Callback mCallback;
    private TextureView mTextureView;
    private Constants.StretchOption mStretch = Constants.StretchOption.NONE;

    public ViewControllerLoopMe(Callback callback) { mCallback = callback; }

    @Override
    public void buildVideoAdView(Context context, ViewGroup bannerView, AdView adView) {
        if (context == null || bannerView == null || adView == null) {
            return;
        }

        mTextureView = new TextureView(context);
        mTextureView.setSurfaceTextureListener(this);
        bannerView.addView(mTextureView, CHILD_INDEX_0);

        adView.setBackgroundColor(Color.TRANSPARENT);
        adView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        clearView(adView);
        bannerView.addView(adView, CHILD_INDEX_1);
        bannerView.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void rebuildView(ViewGroup bannerView, AdView adView, Constants.DisplayMode displayMode) {
        Logging.out(LOG_TAG, "rebuildView");
        if (bannerView == null || adView == null || mTextureView == null) {
            return;
        }

        clearView(mTextureView);
        clearView(adView);
        bannerView.addView(mTextureView, CHILD_INDEX_0);
        bannerView.setBackgroundColor(Color.BLACK);
        if (displayMode == Constants.DisplayMode.NORMAL) {
            bannerView.addView(adView, CHILD_INDEX_1);
        }
    }

    private void clearView(View view) {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void setViewSize(int width, int height) {
        Logging.out(LOG_TAG, "setViewSize " + width + " : " + height);
        mResizeWidth = width;
        mResizeHeight = height;
    }

    @Override
    public void setVideoSize(int width, int height) {
        Logging.out(LOG_TAG, "setVideoSize " + width + " : " + height);
        mVideoWidth = width;
        mVideoHeight = height;
    }

    @Override
    public void setStretchParam(Constants.StretchOption stretchParam) {
        Logging.out(LOG_TAG, "setStretchParam");
        mStretch = stretchParam;
    }

    @Override
    public void handleTouchEvent(MotionEvent event) { }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        if (mCallback != null) {
            mCallback.onSurfaceTextureAvailable(surface);
        }
        Logging.out(LOG_TAG, "resizeVideo()");
        boolean isNewParamsValid = mResizeWidth != 0 ||
            mResizeHeight != 0 ||
            mVideoWidth != 0 ||
            mVideoHeight != 0 ||
            mTextureView != null;
        if (isNewParamsValid && mTextureView != null) {
            FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
            mTextureView.setLayoutParams(Utils.calculateNewLayoutParams(
                oldParams, mVideoWidth, mVideoHeight, mResizeWidth, mResizeHeight, mStretch
            ));
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mSurface = null;
        if (mCallback != null) {
            mCallback.onSurfaceTextureDestroyed();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }
    @Override
    public void onPause() { }
    @Override
    public void onResume() { }
    @Override
    public void onDestroy() { }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }

    public interface Callback {
        void onSurfaceTextureAvailable(SurfaceTexture surface);
        void onSurfaceTextureDestroyed();
    }
}
