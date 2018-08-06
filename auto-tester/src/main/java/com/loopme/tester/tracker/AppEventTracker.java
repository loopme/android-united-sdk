package com.loopme.tester.tracker;


import android.support.annotation.NonNull;

public class AppEventTracker {
    private static AppEventTracker sInstance;
    private final AppTrackerServiceImpl mService;

    private AppEventTracker() {
        mService = new AppTrackerServiceImpl();
    }

    public static AppEventTracker getInstance() {
        if (sInstance == null) {
            sInstance = new AppEventTracker();
        }
        return sInstance;
    }

    public void track(@NonNull Event event) {
        mService.trackEvent(event.getEventName());
    }

    public void trackFirstLaunch(boolean isFirstLaunch) {
        if (isFirstLaunch) {
            mService.trackEvent(AppEventTracker.Event.FIRST_LAUNCH.getEventName());
        }
    }

    public enum Event {
        FIRST_LAUNCH("APP_WAS_OPENED"),
        QR_SCANNER_LAUNCHED("QR_CODE_OPENED"),
        QR_SUCCESS("QR_AD_SHOWN"),
        QR_FAIL("QR_AD_UNSUCCESSFUL"),
        QR_AD_WATCH_AGAIN("QR_WATCH_AGAIN");

        private String mEventName;

        public String getEventName() {
            return mEventName;
        }

        Event(String eventName) {
            mEventName = eventName;
        }
    }
}
