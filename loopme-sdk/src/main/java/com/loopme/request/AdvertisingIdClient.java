package com.loopme.request;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.loopme.Logging;

/**
 * Detect google advertising id
 */
public final class AdvertisingIdClient {

    private final static String LOG_TAG = AdvertisingIdClient.class.getSimpleName();

    private final static String DNT_AD_ID = "00000000-0000-0000-0000-000000000000";

    public static final class AdInfo {

        private String mAdvertisingId;
        private boolean mLimitAdTrackingEnabled;

        private AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {
            mAdvertisingId = advertisingId;
            mLimitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId() {
            return mAdvertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return mLimitAdTrackingEnabled;
        }
    }

    @NonNull
    public static AdInfo getAdvertisingIdInfo(Context context) {
        try {
            Context appContext = context.getApplicationContext();
            com.google.android.gms.ads.identifier.AdvertisingIdClient.Info result =
                    com.google.android.gms.ads.identifier.AdvertisingIdClient
                            .getAdvertisingIdInfo(
                                    appContext == null
                                            ? context
                                            : appContext);

            boolean dnt = result.isLimitAdTrackingEnabled();
            String id = dnt ? DNT_AD_ID : result.getId();

            if (TextUtils.isEmpty(id)) {
                id = "";
                Logging.out(LOG_TAG, "getId() returned empty id.", true);
            }

            return new AdInfo(id, dnt);

        } catch (GooglePlayServicesRepairableException e) {
            Logging.out(LOG_TAG,
                    e.toString() +
                            " MESSAGE: " +
                            e.getMessage() +
                            " CONNECTION_STATUS_CODE: " +
                            e.getConnectionStatusCode(),
                    true);
        } catch (GooglePlayServicesNotAvailableException e) {
            Logging.out(LOG_TAG,
                    e.toString() +
                            " MESSAGE: " +
                            e.getMessage() +
                            " ERROR_CODE: " +
                            e.errorCode,
                    true);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString(), true);
        }

        return new AdInfo("", false);
    }
}