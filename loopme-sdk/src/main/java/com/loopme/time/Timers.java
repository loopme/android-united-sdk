package com.loopme.time;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdTimer;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by vynnykiakiv on 6/15/17.
 */

public class Timers extends Observable {
    private static final String LOG_TAG = Timers.class.getSimpleName();
    private final Map<TimersType, AdTimer> timersMap = new HashMap<>();

    public Timers(Observer observer) {
        addObserver(observer);
        initTimer(TimersType.REQUEST_TIMER, Constants.REQUEST_TIMEOUT);
        initTimer(TimersType.FETCHER_TIMER, Constants.FETCH_TIMEOUT);
        initTimer(TimersType.PREPARE_ASSETS_TIMER, Constants.PREPARE_VAST_ASSET_TIMEOUT);
        initTimer(TimersType.PREPARE_VPAID_JS_TIMER, Constants.PREPARE_VPAID_JS_TIMEOUT);
        initTimer(TimersType.GDPR_PAGE_LOADED_TIMER, Constants.GDPR_PAGE_READY_TIMEOUT);
    }

    private void initTimer(TimersType type, long duration) {
        timersMap.put(type, new AdTimer(duration, () -> notifyTimeout(type)));
    }

    public void setExpirationValidTime(int validExpirationTime) {
        // TODO: Why we need to init timer here?
        initTimer(TimersType.EXPIRATION_TIMER, validExpirationTime);
    }

    private void notifyTimeout(TimersType timer) {
        setChanged();
        notifyObservers(timer);
    }

    public void startTimer(TimersType type) {
        AdTimer timer = timersMap.get(type);
        if (timer != null) {
            timer.start();
            Logging.out(LOG_TAG, type.name() + " timer starts");
        }
    }

    public void stopTimer(TimersType type) {
        AdTimer timer = timersMap.get(type);
        if (timer != null) {
            timer.cancel();
            Logging.out(LOG_TAG, "Stop " + type.name() + " timer");
        }
    }

    public void destroy() {
        for (AdTimer timer : timersMap.values()) {
            if (timer != null) {
                timer.cancel();
            }
        }
        timersMap.clear();
    }
}
