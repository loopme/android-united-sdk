package com.loopme.time;

/**
 * Created by vynnykiakiv on 6/15/17.
 */

public enum TimersType {
    FETCHER_TIMER(1),
    PREPARE_VPAID_JS_TIMER(2),
    EXPIRATION_TIMER(3),
    REQUEST_TIMER(4),
    PREPARE_ASSETS_TIMER(5),
    GDPR_PAGE_LOADED_TIMER(6);

    private int mId;

    TimersType(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }
}
