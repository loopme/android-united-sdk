package com.loopme.gdpr;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.loopme.request.AdvertisingIdClient;
import com.loopme.utils.ExecutorHelper;

/**
 * Created by katerina on 4/27/18.
 */

public class DntFetcher implements Runnable {
    private static final String LOG_TAG = DntFetcher.class.getSimpleName();
    private final Context context;
    private final OnDntFetcherListener onDntFetcherListener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void start() {
        ExecutorHelper.getExecutor().submit(this);
    }

    DntFetcher(Context context, OnDntFetcherListener onDntFetcherListener) {
        this.context = context;
        this.onDntFetcherListener = onDntFetcherListener;
    }

    @Override
    public void run() {
        final AdvertisingIdClient.AdInfo adInfo =
            AdvertisingIdClient.getAdvertisingIdInfo(context);
        mainHandler.post(() -> {
            if (onDntFetcherListener != null)
                onDntFetcherListener.onDntFetched(adInfo.isLimitAdTrackingEnabled(), adInfo.getId());
        });
    }

    public interface OnDntFetcherListener {
        void onDntFetched(boolean isLimited, String advId);
    }
}
