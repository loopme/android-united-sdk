package com.loopme;

import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

public class Helpers {

    public static void init(LoopMeAd loopMeAd) {
        Utils.init(loopMeAd.getContext());
        LoopMeTracker.init(loopMeAd);
    }

    public static void reset() {
        Utils.reset();
    }
}
