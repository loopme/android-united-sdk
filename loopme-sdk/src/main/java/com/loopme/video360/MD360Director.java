package com.loopme.video360;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.loopme.controllers.view.View360Controller;
import com.loopme.video360.common.VREvents;

import java.util.ArrayList;
import java.util.List;

public class MD360Director {

    private static final String LOG_TAG = MD360Director.class.getSimpleName();

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;

    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];

    private final float[] mMVMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    private float mEyeZ = 0f;
    private float mEyeX = 0f;
    private float mAngle = 0f;
    private float mRatio = 0f;
    private float mNear = 0f;
    private float mLookX = 0f;

    private final float[] mCurrentRotation = new float[16];
    private final float[] mAccumulatedRotation = new float[16];
    private final float[] mTemporaryMatrix = new float[16];

    private float mPreviousX;
    private float mPreviousY;

    private float mDeltaX;
    private float mDeltaY;

    private float mDeltaSensorX;
    private float mDeltaSensorY;
    private float mDeltaSensorZ;

    private boolean mWasSwipe;
    private boolean mHandledMotion;
    private int mCurrentViewQuarter = ViewQuarter.UNKNOWN;

    private boolean mGyroSupported;
    private boolean mAccelSupported;

    private View360Controller.Callback mCallback;
    private final List<String> mHandledEvents = new ArrayList<>();

    private MD360Director(Builder builder) {
        this.mEyeZ = builder.mEyeZ;
        this.mRatio = builder.mRatio;
        this.mNear = builder.mNear;
        this.mAngle = builder.mAngle;
        this.mEyeX = builder.mEyeX;
        this.mLookX = builder.mLookX;
        initCamera();
        initModel();
    }

    void setEventCallback(View360Controller.Callback callback) {
        mCallback = callback;
    }

    public boolean handleTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float deltaX = (x - mPreviousX) / sDensity * sDamping ;
                float deltaY = (y - mPreviousY) / sDensity * sDamping ;
                mDeltaX += deltaX;
                mDeltaY += deltaY;
                mWasSwipe = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mWasSwipe) {
                    mWasSwipe = false;
                    handleEvent(VREvents.SWIPE);
                }
                mHandledMotion = false;
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;

        } else {
            return false;
        }
    }

    private void updateCurentViewQuarter(int vq) {
        if (mCurrentViewQuarter != vq) {
            mCurrentViewQuarter = vq;
            String event = null;
            switch (vq) {
                case ViewQuarter.FRONT:
                    event = VREvents.FRONT;
                    break;

                case ViewQuarter.LEFT:
                    event = VREvents.LEFT;
                    break;

                case ViewQuarter.RIGHT:
                    event = VREvents.RIGHT;
                    break;

                case ViewQuarter.BACK:
                    event = VREvents.BACK;
                    break;
            }
            handleEvent(event);
        }
    }

    private void detectViewQuarter(float y) {
        if (y > 45 && y < 135) {
            updateCurentViewQuarter(ViewQuarter.RIGHT);

        } else if (Math.abs(y) > 135) {
            updateCurentViewQuarter(ViewQuarter.BACK);

        } else if (Math.abs(y) < 45) {
            updateCurentViewQuarter(ViewQuarter.FRONT);

        } else {
            updateCurentViewQuarter(ViewQuarter.LEFT);
        }
    }

    private void initCamera() {
        updateViewMatrix();
    }

    private void initModel(){
        Matrix.setIdentityM(mAccumulatedRotation, 0);
        updateModelRotate(mAngle);
    }

    public void shot(MD360Program program) {

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, -mDeltaY + mDeltaSensorZ, 1.0f, 0.0f, 0.0f);//vertical
        Matrix.rotateM(mCurrentRotation, 0, -mDeltaX + mDeltaSensorY, 0.0f, 1.0f, 0.0f);//horiz

        System.arraycopy(mCurrentRotation, 0, mAccumulatedRotation, 0, 16);
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        GLES20.glUniformMatrix4fv(program.getMVMatrixHandle(), 1, false, mMVMatrix, 0);
        GLES20.glUniformMatrix4fv(program.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
    }

    public void updateProjection(int width, int height){
        mRatio = width * 1.0f / height;
        updateProjectionNear(mNear);
    }

    private void updateViewMatrix() {
        final float eyeX = mEyeX;
        final float eyeY = 0.0f;
        final float eyeZ = mEyeZ;
        final float lookX = mLookX;
        final float lookY = 0.0f;
        final float lookZ = -1.0f;
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    protected void updateModelRotate(float a) {
        mAngle = a;
    }

    private void updateProjectionNear(float near){
        mNear = near;
        final float left = -mRatio/2;
        final float right = mRatio/2;
        final float bottom = -0.5f;
        final float top = 0.5f;
        final float far = 500;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, mNear, far);
    }

    private void handleEvent(String event) {
        if (mCallback != null) {
            if (!mHandledEvents.contains(event)) {
                mCallback.onEvent(event);
                mHandledEvents.add(event);
            }
        }
    }

    public void updateSensorInfo(float[] vector) {
        mDeltaSensorX = (float) Math.toDegrees(vector[2]);// -180 +180
        mDeltaSensorY = (float) Math.toDegrees(vector[0]);//-180 +180
        mDeltaSensorZ = (float) Math.toDegrees(vector[1]);

        if (!mHandledMotion) {
            if (mAccelSupported) {
                handleEvent(VREvents.ACCEL);
            }
            if (mGyroSupported) {
                handleEvent(VREvents.GYRO);
            }
            mHandledMotion = true;
        }
        detectViewQuarter(mDeltaSensorY);
    }

    public void setGyroSupported(boolean b) {
        mGyroSupported = b;
    }

    public void setAccelSupported(boolean b) {
        mAccelSupported = b;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private final float mEyeZ = 0f;
        private final float mAngle = 0;
        private final float mRatio = 1.5f;
        private final float mNear = 0.4f;
        private final float mEyeX = 0f;
        private final float mLookX = 0f;

        public MD360Director build(){
            return new MD360Director(this);
        }
    }
}
