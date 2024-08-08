package com.loopme.time;

/**
 * Created by vynnykiakiv on 6/15/17.
 */

public enum TimersType {
    PREPARE_VPAID_JS_TIMER(2),
    EXPIRATION_TIMER(3),
    PREPARE_ASSETS_TIMER(5);

    private final int mId;
    TimersType(int id) { mId = id; }
    public int getId() { return mId; }
}
