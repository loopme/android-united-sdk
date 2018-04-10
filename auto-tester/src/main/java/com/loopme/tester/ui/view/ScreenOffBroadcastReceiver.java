package com.loopme.tester.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.loopme.Logging;

public class ScreenOffBroadcastReceiver extends BroadcastReceiver {

    private static final java.lang.String LOG_TAG = ScreenOffBroadcastReceiver.class.getSimpleName();
    private Context mContext;
    private OnScreenOffListener mListener;
    private boolean mIsUnregistered;

    public ScreenOffBroadcastReceiver(Context context, OnScreenOffListener listener) {
        mContext = context;
        mListener = listener;
        registerReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {
            mListener.onScreenOff();
        }
    }

    public void destroy() {
        if (mContext != null && !mIsUnregistered) {
            mContext.unregisterReceiver(this);
            mIsUnregistered = true;
        }
    }

    private void registerReceiver() {
        if (mContext != null) {
            IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            mContext.registerReceiver(this, screenOffFilter);
        } else {
            Logging.out(LOG_TAG, "Could not register screen off receiver, context is null");
        }
    }

    public interface OnScreenOffListener {
        void onScreenOff();
    }
}
