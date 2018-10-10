package com.loopme.tester.qr.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.SurfaceView;

import com.loopme.tester.utils.UiUtils;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class QReader {
    private QREader mQReader;
    private SurfaceView mSurfaceView;
    private QRDataListener mListener;
    private Context mContext;
    private boolean mIsInited;

    public QReader(@NonNull SurfaceView surfaceView, QRDataListener listener) {
        mContext = surfaceView.getContext();
        mSurfaceView = surfaceView;
        mListener = listener;
    }

    public void resume() {
        if (!mIsInited) {
            init();
        } else {
            if (mQReader != null && !mQReader.isCameraRunning()) {
                mQReader.start();
            }
        }
    }

    public void pause() {
        if (mIsInited) {
            if (mQReader != null) {
                mQReader.stop();
            }
        }
    }

    public void destroy() {
        mContext = null;
        if (mQReader != null) {
            mQReader.releaseAndCleanup();
        }
    }

    private void init() {
        mIsInited = true;
        mQReader = new QREader.Builder(mContext, mSurfaceView, mListener)
                .facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(UiUtils.getScreenWidth(mContext))
                .width(UiUtils.getScreenHeight(mContext))
                .build();
        mQReader.initAndStart(mSurfaceView);
    }
}
