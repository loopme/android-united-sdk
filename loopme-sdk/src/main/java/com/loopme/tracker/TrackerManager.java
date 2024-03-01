package com.loopme.tracker;

import android.text.TextUtils;

import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.constants.Partner;
import com.loopme.tracker.partners.DvTracker;
import com.loopme.tracker.partners.ias.IasTracker;
import com.loopme.tracker.partners.moat.MoatTracker;

import java.util.ArrayList;
import java.util.List;

public class TrackerManager {

    private final List<Tracker> mTrackers = new ArrayList<>();
    private List<String> mTrackersNamesList = new ArrayList<>();
    private LoopMeAd mLoopMeAd;

    public TrackerManager(LoopMeAd loopmeAd) {
        if (loopmeAd != null) {
            mLoopMeAd = loopmeAd;
            mTrackersNamesList.addAll(mLoopMeAd.getAdParams().getTrackers());
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
                tracker = initTracker(Partner.MOAT, adType);
            } else if (isIas(name)) {
                tracker = initTracker(Partner.IAS, adType);
            } else if (isDv(name)) {
                tracker = initTracker(Partner.DV, adType);
            }
            if (tracker != null) {
                mTrackers.add(tracker);
            }
        }
    }

    public void track(Event event, Object... args) {
        for (Tracker tracker : mTrackers) {
            tracker.track(event, args);
        }
    }

    private Tracker initTracker(Partner partner, AdType adType) {
        Tracker tracker = null;
        switch (partner) {
            case MOAT: {
                tracker = new MoatTracker(mLoopMeAd, adType);
                break;
            }
            case IAS: {
                tracker = new IasTracker(mLoopMeAd, adType);
                break;
            }
            case DV: {
                tracker = new DvTracker();
                break;
            }
        }
        return tracker;
    }

    private boolean isDv(String name) {
        return !TextUtils.isEmpty(name) && name.equalsIgnoreCase(Partner.DV.name());
    }

    private boolean isIas(String name) {
        return !TextUtils.isEmpty(name) && name.equalsIgnoreCase(Partner.IAS.name());
    }

    private boolean isMoat(String name) {
        return !TextUtils.isEmpty(name) && name.equalsIgnoreCase(Partner.MOAT.name());
    }
}
