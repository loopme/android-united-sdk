package com.loopme.time;

import android.text.TextUtils;

import com.loopme.utils.Utils;

/**
 * Created by katerina on 7/3/17.
 */

public class TimeUtils {

    public static int retrieveSkipTime(String skipTime, int duration){
        if (TextUtils.isEmpty(skipTime)) {
            return -1;
        }
        return skipTime.contains("%") ?
            duration * Utils.parsePercent(skipTime) / 100 : Utils.parseDuration(skipTime) * 1000;
    }
}
