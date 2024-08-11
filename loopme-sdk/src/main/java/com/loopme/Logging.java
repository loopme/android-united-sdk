package com.loopme;

import android.text.TextUtils;
import android.util.Log;

import com.loopme.debugging.LiveDebug;

public class Logging {

    private static final String PREFIX = "Debug.LoopMe.";

    private Logging() { }

    /**
     * @param forceSave - if true, the log will be saved to the database
     */
    public static void out(final String tag, final String text, boolean forceSave) {
        Log.d(PREFIX + tag, text);
        if (!TextUtils.isEmpty(text)) LiveDebug.handle(PREFIX + tag, text, forceSave);
    }

    public static void out(final String tag, final String text) { out(tag, text, false); }
    public static void out(final String text) { out("", text); }

}
