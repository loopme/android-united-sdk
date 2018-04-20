package com.loopme.tracker.constants;

public enum Event {

    REGISTER, //done
    LOADED,
    REGISTER_FRIENDLY_VIEW,//----
    RECORD_READY,//done
    IMPRESSION,//done
    PLAYING,//done
    PAUSED,//done
    CLICKED,//done,
    FIRST_QUARTILE, ////done
    MIDPOINT,////done
    THIRD_QUARTILE,////done
    COMPLETE,//done
    STOPPED,//done
    USER_CLOSE,//done
    CLOSE,
    SKIPPED, //----
    VOLUME_CHANGE,//done
    ENTERED_FULLSCREEN,//done
    EXITED_FULLSCREEN,//done
    EXPANDED_CHANGE,//done
    DURATION_CHANGED,//done
    END_SESSION,//done
    ERROR,//done
    INJECT_JS,


    //MOAT
    INIT,
    START,
    STOP,
    CHANGE_TARGET_VIEW,
    PREPARE,
    VIDEO_STARTED,

    //DV
    START_MEASURING,
    MUTE,
    VIDEO_COMPLETE,
    VIDEO_STOPPED,
    RESUMED,
    VIDEO_EVENT,
    LINEAR_CHANGED

}
