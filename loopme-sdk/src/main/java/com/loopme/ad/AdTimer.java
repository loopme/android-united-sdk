package com.loopme.ad;

import android.os.CountDownTimer;

public class AdTimer extends CountDownTimer {

    private static final int COUNTDOWN_INTERVAL = 1000 * 60;

    private Listener mListener;

    public interface Listener {
        void onTimeout();
    }

    public AdTimer(long millisInFuture, Listener listener) {
        super(millisInFuture, COUNTDOWN_INTERVAL);
        mListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
    }

    @Override
    public void onFinish() {
        if (mListener != null) {
            mListener.onTimeout();
        }
    }
}
