package com.loopme.tracker.constants;

public enum Event {
    REGISTER,
    LOADED,
    REGISTER_FRIENDLY_VIEW,
    RECORD_READY,
    IMPRESSION,
    PLAYING,
    PAUSED,
    CLICKED,
    FIRST_QUARTILE,
    MIDPOINT,
    THIRD_QUARTILE,
    COMPLETE,
    STOPPED,
    USER_CLOSE,
    CLOSE,
    SKIPPED,
    VOLUME_CHANGE,
    ENTERED_FULLSCREEN,
    EXITED_FULLSCREEN,
    EXPANDED_CHANGE,
    DURATION_CHANGED,
    END_SESSION,
    ERROR,
    INJECT_JS_WEB,
    INJECT_JS_VPAID,

    INIT,
    START,
    STOP,
    CHANGE_TARGET_VIEW,
    PREPARE,
    VIDEO_STARTED,
    NEW_ACTIVITY,

    //DV
    START_MEASURING,
    MUTE,
    VIDEO_COMPLETE,
    VIDEO_STOPPED,
    RESUMED,
    VIDEO_EVENT,
    LINEAR_CHANGED,
    USER_ACCEPT_INVITATION,
    USER_MINIMIZE
}
