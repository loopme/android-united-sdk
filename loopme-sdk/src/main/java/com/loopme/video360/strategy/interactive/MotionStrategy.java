package com.loopme.video360.strategy.interactive;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.loopme.video360.MD360Director;
import com.loopme.video360.common.VRUtil;
import com.loopme.video360.strategy.interactive.AbsInteractiveStrategy;

import java.util.List;

public class MotionStrategy extends AbsInteractiveStrategy implements SensorEventListener {

    private static final String TAG = "MotionStrategy";
    private int mDeviceRotation;
    private float[] mSensorMatrix = new float[16];
    private boolean mRegistered = false;

    public MotionStrategy(List<MD360Director> directorList) {
        super(directorList);
    }

    @Override
    public void onResume(Context context) {
        registerSensor(context);
    }

    @Override
    public void onPause(Context context) {
        unregisterSensor(context);
    }

    @Override
    public boolean handleTouchEvent(MotionEvent event) {
        boolean handled = false;
        for (MD360Director director : getDirectorList()) {
            handled |= director.handleTouchEvent(event);
        }
        return handled;
    }

    @Override
    public void on(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            mDeviceRotation = wm.getDefaultDisplay().getRotation();
        }
    }

    @Override
    public void off(Context context) {
        unregisterSensor(context);
    }

    protected void registerSensor(Context context) {
        if (mRegistered) return;

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = null;
        if (mSensorManager != null) {
            sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        if (sensor == null) {
            Log.e(TAG, "TYPE_ROTATION_VECTOR sensor not support!");
            return;
        }

        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

        mRegistered = true;
    }

    protected void unregisterSensor(Context context) {
        if (!mRegistered) return;

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        mRegistered = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy != 0) {
            int type = event.sensor.getType();
            switch (type) {
                case Sensor.TYPE_ROTATION_VECTOR:
                    VRUtil.sensorRotationVector2Matrix(event, mDeviceRotation, mSensorMatrix);

                    float[] orientation = new float[3];
                    SensorManager.getOrientation(mSensorMatrix, orientation);

                    for (MD360Director director : getDirectorList()) {
                        director.updateSensorInfo(orientation);
                    }
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
