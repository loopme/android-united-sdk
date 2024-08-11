package com.loopme.gdpr;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.loopme.request.AdvertisingIdClient;
import com.loopme.utils.ExecutorHelper;

/**
 * Created by katerina on 4/27/18.
 */
public class DntFetcher {
    private static final String LOG_TAG = DntFetcher.class.getSimpleName();

    public static void start(@NonNull Context ctx, @NonNull OnDntFetcherListener listener) {
        ExecutorHelper.getExecutor().submit(() -> new Handler(Looper.getMainLooper()).post(() -> {
            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx);
            listener.onDntFetched(adInfo.isLimitAdTrackingEnabled(), adInfo.getId());
        }));
    }

    public interface OnDntFetcherListener {
        void onDntFetched(boolean isLimited, String advId);
    }
}
