package com.loopme.video360.common;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.Surface;

public class VRUtil {

    private static final float[] mTmp = new float[16];

    public static void sensorRotationVector2Matrix(SensorEvent event, int rotation, float[] output) {
        float[] values = event.values;
        switch (rotation){
            case Surface.ROTATION_0://vert
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_X, SensorManager.AXIS_Z, output);
                break;

            case Surface.ROTATION_180: //vert
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Z, output);
                break;

            case Surface.ROTATION_90://land
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, output);
                break;

            case Surface.ROTATION_270://land
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_MINUS_Z, SensorManager.AXIS_X, output);
                break;
        }
    }

    public static void notNull(Object object, String error){
        if (object == null) throw new RuntimeException(error);
    }
}
