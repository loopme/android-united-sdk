package com.loopme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loopme.Constants;


/**
 * Created by vynnykiakiv on 4/5/17.
 */

public class MraidAdCloseButtonReceiver extends BroadcastReceiver {

    public MraidAdCloseButtonListener mListener;

    public MraidAdCloseButtonReceiver(MraidAdCloseButtonListener listener) {
        this.mListener = listener;
    }

    public interface MraidAdCloseButtonListener {
        void onCloseButtonVisibilityChanged(boolean customCloseButton);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null && intent.getExtras() != null) {
            boolean customCloseButton = intent.getExtras().getBoolean(Constants.EXTRAS_CUSTOM_CLOSE);
            mListener.onCloseButtonVisibilityChanged(customCloseButton);
        }
    }
}
