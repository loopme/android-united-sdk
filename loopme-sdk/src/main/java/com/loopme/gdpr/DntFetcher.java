package com.loopme.gdpr;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.loopme.Logging;
import com.loopme.request.AdvertisingIdClient;
import com.loopme.utils.ExecutorHelper;

/**
 * Created by katerina on 4/27/18.
 */

public class DntFetcher implements Runnable {

    private static final String LOG_TAG = DntFetcher.class.getSimpleName();

    private final Activity mContext;
    private final OnDntFetcherListener mOnDntListener;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void start() {
        ExecutorHelper.getExecutor().submit(this);
    }


    public DntFetcher(Activity context, OnDntFetcherListener onDntFetcherListener) {
        mContext = context;
        mOnDntListener = onDntFetcherListener;
    }

    @Override
    public void run() {
        boolean isLimited = false;
        String advId = "";
        try {
            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
            isLimited = adInfo.isLimitAdTrackingEnabled();
            advId = adInfo.getId();
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Exception: " + e.getMessage());
        }
        onDntFetchedOnUiThread(isLimited, advId);
    }

    private void onDntFetchedOnUiThread(final boolean isLimited, final String advId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mOnDntListener != null) {
                    mOnDntListener.onDntFetched(isLimited, advId);
                }
            }
        });
    }

    public interface OnDntFetcherListener {
        void onDntFetched(boolean isLimited, String advId);
    }
}
