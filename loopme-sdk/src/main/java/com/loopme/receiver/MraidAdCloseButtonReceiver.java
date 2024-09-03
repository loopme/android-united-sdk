package com.loopme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.loopme.Constants;

/**
 * Created by vynnykiakiv on 4/5/17.
 */

public class MraidAdCloseButtonReceiver extends BroadcastReceiver {

    public final Listener mListener;

    public interface Listener {
        void onCloseButtonVisibilityChanged(boolean customCloseButton);
    }

    public MraidAdCloseButtonReceiver(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener == null || intent.getExtras() == null) {
            return;
        }
        mListener.onCloseButtonVisibilityChanged(
            intent.getExtras().getBoolean(Constants.EXTRAS_CUSTOM_CLOSE)
        );
    }
}
