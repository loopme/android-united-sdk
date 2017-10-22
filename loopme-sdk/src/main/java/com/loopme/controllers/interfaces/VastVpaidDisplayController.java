package com.loopme.controllers.interfaces;

public interface VastVpaidDisplayController {

    interface OnPreparedListener {
        void onPrepared();
    }

    void prepare(OnPreparedListener listener);

    void skipVideo();

    void closeSelf();
}
