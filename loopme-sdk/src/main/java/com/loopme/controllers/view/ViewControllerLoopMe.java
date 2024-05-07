package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Build;
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


    public ViewControllerLoopMe(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void buildVideoAdView(Context context, ViewGroup bannerView, AdView adView) {
        if (context != null && bannerView != null && adView != null) {
            mTextureView = new TextureView(context);
            configureTextureView();
            configureAdView(adView);
            clearView(adView);

            bannerView.setBackgroundColor(Color.BLACK);
            bannerView.addView(mTextureView, CHILD_INDEX_0);
            bannerView.addView(adView, CHILD_INDEX_1);
        }
    }

    private void configureTextureView() {
        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(this);
            if (Build.VERSION.SDK_INT < 23) {
                mTextureView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private void configureAdView(AdView adView) {
        if (adView != null) {
            adView.setBackgroundColor(Color.TRANSPARENT);
            adView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public void rebuildView(ViewGroup bannerView, AdView adView, Constants.DisplayMode displayMode) {
        Logging.out(LOG_TAG, "rebuildView");
        if (bannerView != null && adView != null && mTextureView != null) {
            bannerView.setBackgroundColor(Color.BLACK);
            clearView(mTextureView);
            clearView(adView);

            bannerView.addView(mTextureView, CHILD_INDEX_0);
            if (displayMode == Constants.DisplayMode.NORMAL) {
                bannerView.addView(adView, CHILD_INDEX_1);
            }
        }
    }

    private void clearView(View view) {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void resizeVideo() {
        Logging.out(LOG_TAG, "resizeVideo()");
        if (areNewParamsValid()) {
            FrameLayout.LayoutParams params = createNewParams();
            mTextureView.setLayoutParams(params);
        }
    }

    private FrameLayout.LayoutParams createNewParams() {
        if (mTextureView != null) {
            FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mTextureView.getLayoutParams();
            return Utils.calculateNewLayoutParams(oldParams, mVideoWidth, mVideoHeight,
                    mResizeWidth, mResizeHeight, mStretch);
        } else {
            return new FrameLayout.LayoutParams(Utils.getScreenWidth(), Utils.getScreenHeight());
        }
    }

    private boolean areNewParamsValid() {
        return mResizeWidth != 0 || mResizeHeight != 0 || mVideoWidth != 0
                || mVideoHeight != 0 || mTextureView != null;
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
    public boolean handleTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        onSurfaceTextureAvailable(surface);
        resizeVideo();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        onSurfaceTextureDestroyed();
        return true;
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void initVRLibrary(Context context) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }


    public Surface getSurface() {
        return mSurface;
    }

    private void onSurfaceTextureAvailable(SurfaceTexture surface) {
        mSurface = new Surface(surface);
        if (mCallback != null) {
            mCallback.onSurfaceTextureAvailable(surface);
        }
    }

    private void onSurfaceTextureDestroyed() {
        mSurface = null;
        if (mCallback != null) {
            mCallback.onSurfaceTextureDestroyed();
        }
    }

    public interface Callback {
        void onSurfaceTextureAvailable(SurfaceTexture surface);

        void onSurfaceTextureDestroyed();
    }
}
