package com.loopme.tracker.interfaces;

import com.loopme.tracker.constants.Event;

public interface Tracker {
    void track(Event event, Object... args);
}
