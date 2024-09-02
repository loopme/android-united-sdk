package com.loopme.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.loopme.Constants;

public class UiUtils {

    private static final String LOG_TAG = UiUtils.class.getSimpleName();

    public static void broadcastIntent(Context context, String intentAction) {
        broadcastIntent(context, intentAction, Constants.DEFAULT_AD_ID);
    }

    public static void broadcastIntent(Context context, String intentAction, int adId) {
        if (context == null || TextUtils.isEmpty(intentAction)) return;
        Intent intent = new Intent(intentAction);
        intent.setPackage(context.getPackageName());
        if (adId != Constants.DEFAULT_AD_ID) {
            intent.putExtra(Constants.AD_ID_TAG, adId);
        }
        context.sendBroadcast(intent);
    }
}
