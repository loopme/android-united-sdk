package com.loopme.tracker;

import android.util.Log;
import androidx.annotation.NonNull;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.Event;
import java.util.Arrays;

public class TrackerManager {

    private static final String LOG_TAG = TrackerManager.class.getSimpleName();
    private final LoopMeAd mLoopMeAd;
    public TrackerManager(@NonNull LoopMeAd loopmeAd) {
        mLoopMeAd = loopmeAd;
        Log.d(LOG_TAG, "TrackerManager for appkey: " + mLoopMeAd.getAppKey() + "(" + mLoopMeAd.getAdId() + ")");
    }

    public void track(Event event, Object... args) {
        Log.d(LOG_TAG, "Track " + mLoopMeAd.getAppKey() + "(" + mLoopMeAd + "): " + event + " with args: " + Arrays.toString(args));
    }
}
