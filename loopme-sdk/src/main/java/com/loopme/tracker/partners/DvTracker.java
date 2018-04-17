//package com.loopme.tracker.partners;
//
//
//import com.doubleverify.dvsdk.DVSDK;
//import com.loopme.BuildConfig;
//import com.loopme.Logging;
//import com.loopme.ad.LoopMeAd;
//import com.loopme.tracker.interfaces.Tracker;
//import com.loopme.tracker.constants.AdType;
//import com.loopme.tracker.constants.TrackerType;
//import com.loopme.tracker.constants.Event;
//
//public class DvTracker implements Tracker {
//
//    private static final String LOG_TAG = DvTracker.class.getSimpleName();
//
//    public DvTracker(AdType adType) {
//
//    }
//
//    public static void startSdk(LoopMeAd loopMeAd) {
//        if (loopMeAd == null) {
//            Logging.out(LOG_TAG, "Activity should not be null");
//            return;
//        }
//        DVSDK.init(loopMeAd.getContext(), BuildConfig.DV_TOKEN);
//        Logging.out(LOG_TAG, "Sdk started: " + TrackerType.DV.name());
//    }
//
//    @Override
//    public void track(Event event, Object... args) {
//        // TODO: 8/15/17
//    }
//}
