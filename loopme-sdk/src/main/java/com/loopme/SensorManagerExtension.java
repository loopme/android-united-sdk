package com.loopme;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by katerina on 6/30/17.
 */

public class SensorManagerExtension {

    private SensorManager mSensorManager;
    private float mAcceleration;
    private float mCurrentAcceleration;
    private float mLastAcceleration;
    private OnLoopMeSensorListener mOnSensorListener;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            mLastAcceleration = mCurrentAcceleration;
            mCurrentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mCurrentAcceleration - mLastAcceleration;
            mAcceleration = mAcceleration * 0.9f + delta;
            if (delta > 5) {
                onAdShake();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void onAdShake() {

    }

    public SensorManagerExtension initSensor(Context context, OnLoopMeSensorListener listener) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAcceleration = 0.00f;
        mCurrentAcceleration = android.hardware.SensorManager.GRAVITY_EARTH;
        mLastAcceleration = android.hardware.SensorManager.GRAVITY_EARTH;
        return this;
    }

    public void pauseSensor(){
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    public void registerListener(){
        if (mSensorManager != null) {
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public interface OnLoopMeSensorListener{
        void onAdShake();
    }
}
