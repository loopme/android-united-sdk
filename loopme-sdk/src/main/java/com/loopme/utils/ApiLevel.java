package com.loopme.utils;

import android.os.Build;

public class ApiLevel {

    private ApiLevel() {
    }

    public static boolean isApi21AndHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isApi24AndHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
}
