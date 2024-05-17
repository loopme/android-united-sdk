package com.loopme;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by katerina on 6/30/17.
 */

public class SensorManagerExtension {

    private SensorManager mSensorManager;
    private float mCurrentAcceleration;
    private float mLastAcceleration;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            mLastAcceleration = mCurrentAcceleration;
            mCurrentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

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
