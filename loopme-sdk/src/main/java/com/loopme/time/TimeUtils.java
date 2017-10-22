package com.loopme.time;

import android.text.TextUtils;

import com.loopme.utils.Utils;

/**
 * Created by katerina on 7/3/17.
 */

public class TimeUtils {

    private static final int PERCENT = 100;
    private static final int MILLIS_IN_SECONDS = 1000;
    private static final String PERCENT_SYMBOL = "%";

    public static int retrieveSkipTime(String skipTime, int duration){
        if (TextUtils.isEmpty(skipTime)) {
            return -1;
        } else {
            if (skipTime.contains(PERCENT_SYMBOL)) {
                return duration * Utils.parsePercent(skipTime) / PERCENT;
            } else {
                return Utils.parseDuration(skipTime) * MILLIS_IN_SECONDS;
            }
        }
    }
}
