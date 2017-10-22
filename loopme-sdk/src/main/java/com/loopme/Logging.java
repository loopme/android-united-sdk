package com.loopme;

import android.text.TextUtils;
import android.util.Log;

import com.loopme.debugging.LiveDebug;

public class Logging {

    private static final String PREFIX = "Debug.LoopMe.";

    private Logging() {
    }

    public static void out(String tag, final String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        final String logTag = PREFIX + tag;
        if (BuildConfig.DEBUG_MODE || Constants.sDebugMode) {
            Log.i(logTag, text);
        }
        LiveDebug.handle(logTag, text);
    }

    public static void out(final String text) {
        out("", text);
    }

}
