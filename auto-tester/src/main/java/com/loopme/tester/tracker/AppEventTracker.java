package com.loopme.tester.tracker;


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

    public void track(Event event, Object... args) {
        switch (event) {
            case FIRST_LAUNCH: {
                trackFirstLaunch(args);
                break;
            }
            case QR_SCANNER_LAUNCHED: {
                qrScannerLaunched();
                break;
            }

            case QR_SUCCESS: {
                qrAdSuccess(args);
                break;
            }
            case QR_FAIL: {
                qrAdFail(args);
                break;
            }
            case QR_AD_WATCH_AGAIN: {
                qrAdWatchAgain(args);
                break;
            }
        }
    }

    private void trackFirstLaunch(Object... args) {
        if (args != null && args.length > 0 && args[0] instanceof Boolean) {
            boolean isFirstLaunch = (Boolean) args[0];
            if (isFirstLaunch) {
                mService.trackEvent("id777", Event.FIRST_LAUNCH.getEventName());
            }
        }
    }

    private void qrScannerLaunched() {
        mService.trackEvent("id777", Event.QR_SCANNER_LAUNCHED.getEventName());
    }

    private void qrAdWatchAgain(Object[] args) {
        mService.trackEvent("id777", Event.QR_AD_WATCH_AGAIN.getEventName());
    }

    private void qrAdFail(Object[] args) {
        mService.trackEvent("id777", Event.QR_FAIL.getEventName());
    }

    private void qrAdSuccess(Object[] args) {
        mService.trackEvent("id777", Event.QR_SUCCESS.getEventName());
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
