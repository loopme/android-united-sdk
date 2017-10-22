package com.loopme.tracker.viewability;

import android.text.TextUtils;

import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.interfaces.Tracker;
import com.loopme.tracker.partners.DvTracker;
import com.loopme.tracker.partners.IasTracker;
import com.loopme.tracker.partners.MoatTracker;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.TrackerType;

import java.util.ArrayList;
import java.util.List;

public class TrackerManager {

    private static final String LOG_TAG = TrackerManager.class.getSimpleName();
    private final List<Tracker> mTrackers = new ArrayList<>();
    private List<String> mTrackersNamesList = new ArrayList<>();
    private LoopMeAd mLoopMeAd;

    public TrackerManager(LoopMeAd loopmeAd) {
        if (loopmeAd != null) {
            mLoopMeAd = loopmeAd;
            setTrackersList(mLoopMeAd.getAdParams().getTrackers());
        }
    }

    public void startSdk() {
        for (String name : mTrackersNamesList) {
            if (isMoat(name)) {
                MoatTracker.startSdk(mLoopMeAd);
            } else if (isIas(name)) {
                IasTracker.startSdk(mLoopMeAd);
            } else if (isDv(name)) {
                DvTracker.startSdk(mLoopMeAd);
            }
        }
    }

    public void onInitTracker(AdType adType) {
        for (String name : mTrackersNamesList) {
            Tracker tracker = null;

            if (isMoat(name)) {
                tracker = initTracker(TrackerType.MOAT, adType);
            } else if (isIas(name)) {
                tracker = initTracker(TrackerType.IAS, adType);
            } else if (isDv(name)) {
                tracker = initTracker(TrackerType.DV, adType);
            }

            addTrackerToList(tracker);
        }
    }

    public void track(Event event, Object... args) {
        for (Tracker tracker : mTrackers) {
            tracker.track(event, args);
        }
    }

    private Tracker initTracker(TrackerType trackerType, AdType adType) {
        Tracker tracker = null;
        switch (trackerType) {
            case MOAT: {
                tracker = new MoatTracker(mLoopMeAd, adType);
                break;
            }
            case IAS: {
                tracker = new IasTracker(adType);
                break;
            }
            case DV: {
                tracker = new DvTracker(adType);
                break;
            }
        }
        return tracker;
    }

    private boolean isDv(String name) {
        return !TextUtils.isEmpty(name) && name.equalsIgnoreCase(TrackerType.DV.name());
    }

    private boolean isIas(String name) {
        return !TextUtils.isEmpty(name) && name.equalsIgnoreCase(TrackerType.IAS.name());
    }

    private boolean isMoat(String name) {
        return !TextUtils.isEmpty(name) && name.equalsIgnoreCase(TrackerType.MOAT.name());
    }

    private void addTrackerToList(Tracker tracker) {
        if (tracker != null) {
            mTrackers.add(tracker);
        }
    }

    private void setTrackersList(List<String> trackers) {
        if (trackers != null && !trackers.isEmpty()) {
            mTrackersNamesList = trackers;
        } else {
            Logging.out(LOG_TAG, "trackers list is null or empty");
        }
    }
}
