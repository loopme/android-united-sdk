package com.loopme;

import android.text.TextUtils;
import android.util.Log;

import com.loopme.debugging.LiveDebug;

public class Logging {

    private static final String PREFIX = "Debug.LoopMe.";

    private Logging() {
    }

    private static String createDebugTag(final String tag) {
        return PREFIX + tag;
    }

    private static void log(final String tag, final String text) {
        if (BuildConfig.DEBUG || Constants.sDebugMode)
            Log.i(tag, text);
    }

    /**
     * @param forceSave TODO. Refactor. For handling cases when sIsDebugOn isn't set yet.
     */
    public static void out(final String tag, final String text, boolean forceSave) {
        if (TextUtils.isEmpty(text))
            return;

        final String logTag = createDebugTag(tag);
        log(logTag, text);

        LiveDebug.handle(logTag, text, forceSave);
    }

    // TODO. Refactor. Duplicate code.
    public static void out(final String tag, final String text) {
        if (TextUtils.isEmpty(text))
            return;

        final String logTag = createDebugTag(tag);
        log(logTag, text);

        LiveDebug.handle(logTag, text, false);
    }

    public static void out(final String text) {
        out("", text);
    }

}
