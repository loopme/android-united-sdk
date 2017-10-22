package com.loopme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loopme.Constants;

public class AdReceiver extends BroadcastReceiver {

    private Listener mListener;
    private int mAdId;

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
        if (getAdId(intent) == mAdId) {
            if (intent.getAction().equalsIgnoreCase(Constants.DESTROY_INTENT)) {
                mListener.onDestroyBroadcast();

            } else if (intent.getAction().equalsIgnoreCase(Constants.CLICK_INTENT)) {
                mListener.onClickBroadcast();
            }
        }
    }

    private int getAdId(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            return intent.getExtras().getInt(Constants.AD_ID_TAG);
        } else {
            return Constants.DEFAULT_AD_ID;
        }
    }
}
