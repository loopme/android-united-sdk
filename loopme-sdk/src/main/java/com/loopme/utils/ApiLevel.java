package com.loopme.utils;

import android.os.Build;

import androidx.annotation.ChecksSdkIntAtLeast;

public class ApiLevel {

    private ApiLevel() {
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isApi21AndHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
    public static boolean isApi24AndHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    public static boolean isApi26AndHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
