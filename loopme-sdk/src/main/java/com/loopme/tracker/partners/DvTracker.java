package com.loopme.tracker.partners;


import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.Tracker;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.constants.Partner;

public class DvTracker implements Tracker {

    private static final String LOG_TAG = DvTracker.class.getSimpleName();

    public DvTracker() {

    }

    public static void startSdk(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            Logging.out(LOG_TAG, "Activity should not be null");
            return;
        }
        Logging.out(LOG_TAG, "Sdk started: " + Partner.DV.name());
    }

    @Override
    public void track(Event event, Object... args) {
    }
}
