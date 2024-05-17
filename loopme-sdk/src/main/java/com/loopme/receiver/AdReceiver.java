package com.loopme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loopme.Constants;

public class AdReceiver extends BroadcastReceiver {

    private final Listener mListener;
    private final int mAdId;

    public interface Listener {
        void onDestroyBroadcast();
        void onClickBroadcast();
    }

    public AdReceiver(Listener listener, int adId) {
        mListener = listener;
        mAdId = adId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener == null) {
            return;
        }
        if (getAdId(intent) != mAdId) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equalsIgnoreCase(Constants.DESTROY_INTENT)) {
            mListener.onDestroyBroadcast();
        } else if (action.equalsIgnoreCase(Constants.CLICK_INTENT)) {
            mListener.onClickBroadcast();
        }
    }

    private int getAdId(Intent intent) {
        return (intent == null || intent.getExtras() == null) ?
            Constants.DEFAULT_AD_ID : intent.getExtras().getInt(Constants.AD_ID_TAG);
    }
}
