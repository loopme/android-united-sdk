package com.loopme.request;

import android.content.Context;

import com.loopme.Logging;

public class AdvIdFetcher implements Runnable {

    private static final String LOG_TAG = AdvIdFetcher.class.getSimpleName();

    private final Context mContext;
    private final Listener mListener;

    private String mAdvertisingId;

    public interface Listener {
        void onComplete(String advId, boolean isLimited);
    }

    public AdvIdFetcher(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public void run() {
        mAdvertisingId = "";
        boolean isLimited = false;
        try {
            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
            mAdvertisingId = adInfo.getId();
            isLimited = adInfo.isLimitAdTrackingEnabled();
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Exception: " + e.getMessage());
        }

        if (mListener != null) {
            mListener.onComplete(mAdvertisingId, isLimited);
        }
    }
}
