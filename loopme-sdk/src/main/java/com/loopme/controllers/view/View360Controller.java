package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ShiftedValues;
import com.loopme.utils.Utils;
import com.loopme.video360.MDVRLibrary;
import com.loopme.views.AdView;

import java.util.List;

public class View360Controller implements IViewController {

    private static final String LOG_TAG = View360Controller.class.getSimpleName();

    private static final String ACCEL = "Accelerometer";
    private static final String GYRO = "Gyroscope";

    private GLSurfaceView mGLSurfaceView;
    private Callback mCallback;
    private MDVRLibrary mVRLibrary;

    public interface Callback {
        void onSurfaceReady(Surface surface);

        void onEvent(String event);
    }

    public View360Controller(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void setViewSize(int w, int h) {
    }

    @Override
    public void setVideoSize(int w, int h) {
    }

    @Override
    public void buildVideoAdView(Context context, ViewGroup bannerView, AdView adView, ShiftedValues shiftedValues, Constants.AdFormat adFormat) {
        mGLSurfaceView = new GLSurfaceView(context);
        bannerView.addView(mGLSurfaceView, 0);

        if (adView != null) {
            adView.setBackgroundColor(Color.TRANSPARENT);
            adView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }
            if (adFormat == Constants.AdFormat.INTERSTITIAL) {
                FrameLayout.LayoutParams shiftedInterstitialParams = Utils.generateShiftedParams(Utils.getScreenWidth(), Utils.getScreenHeight(), shiftedValues);
                adView.setLayoutParams(shiftedInterstitialParams);
            }
            bannerView.addView(adView, 1);
        }
    }

    @Override
    public void initVRLibrary(Context context) {
        if (mVRLibrary == null) {
            Logging.out(LOG_TAG, "initVRLibrary");
            mVRLibrary = MDVRLibrary.with(context)
                    .video(new MDVRLibrary.IOnSurfaceReadyCallback() {
                        @Override
                        public void onSurfaceReady(Surface surface) {
                            if (mCallback != null) {
                                mCallback.onSurfaceReady(surface);
                            }
                        }
                    })
                    .build(mGLSurfaceView);

            checkIsAccelGyroPresented(context);
            mVRLibrary.setEventCallback(mCallback);
        }
    }

    private void checkIsAccelGyroPresented(Context context) {
        SensorManager mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null) {
            return;
        }
        List<Sensor> mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : mSensorList) {
            if (s.getName().contains(ACCEL)) {
                mVRLibrary.setAccelSupported(true);
            }
            if (s.getName().contains(GYRO)) {
                mVRLibrary.setGyroSupported(true);
            }
        }
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        return mVRLibrary != null && mVRLibrary.handleTouchEvent(event);
    }

    @Override
    public void onResume() {
        if (mVRLibrary != null && mGLSurfaceView != null && mVRLibrary.isPaused()) {
            Logging.out(LOG_TAG, "VRLibrary resume");
            mVRLibrary.onResume(mGLSurfaceView.getContext());
        }
    }

    @Override
    public void onPause() {
        if (mVRLibrary != null && mGLSurfaceView != null && !mVRLibrary.isPaused()) {
            Logging.out(LOG_TAG, "VRLibrary pause");
            mVRLibrary.onPause(mGLSurfaceView.getContext());
        }
    }

    @Override
    public void onDestroy() {
        if (mVRLibrary != null) {
            Logging.out(LOG_TAG, "VRLibrary onDestroy");
            mVRLibrary.onDestroy();
            mVRLibrary = null;
        }
    }

    @Override
    public void rebuildView(ViewGroup bannerView, AdView adView, Constants.DisplayMode displayMode) {
        Logging.out(LOG_TAG, "rebuildView");
        if (bannerView == null || adView == null || mGLSurfaceView == null) {
            return;
        }
        if (mGLSurfaceView.getParent() != null) {
            ((ViewGroup) mGLSurfaceView.getParent()).removeView(mGLSurfaceView);
        }
        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        bannerView.removeAllViews();
        bannerView.addView(mGLSurfaceView, 0);
        bannerView.addView(adView, 1);
    }

    @Override
    public void setStretchParam(Constants.StretchOption option) {
    }
}
