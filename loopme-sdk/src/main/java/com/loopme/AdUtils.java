package com.loopme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.utils.Utils;
import com.loopme.views.activity.BaseActivity;

public class AdUtils {

    private static final String LOG_TAG = AdUtils.class.getSimpleName();

    private static boolean debugObstructionEnabled;

    public static void startAdActivity(LoopMeAd loopMeAd) {
        startAdActivity(loopMeAd, false);
    }

    public static void startAdActivity(LoopMeAd loopMeAd, boolean customClose) {
        if (loopMeAd != null) {
            LoopMeAdHolder.putAd(loopMeAd);
            Logging.out(LOG_TAG, "Starting Ad Activity");

            Intent intent = new Intent(loopMeAd.getContext(), BaseActivity.class);

            intent.putExtra(Constants.AD_ID_TAG, loopMeAd.getAdId());
            intent.putExtra(Constants.FORMAT_TAG, loopMeAd.getAdFormat().ordinal());
            intent.putExtra(Constants.EXTRAS_CUSTOM_CLOSE, customClose);
            intent.putExtra(Constants.EXTRAS_DEBUG_OBSTRUCTION_ENABLED, debugObstructionEnabled);

            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            loopMeAd.getContext().startActivity(intent);
        }
    }

    // https://developer.chrome.com/multidevice/android/customtabs
    public static boolean tryStartCustomTabs(Context context, String uri) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent
                    .Builder()
                    .enableUrlBarHiding()
                    .build();
            customTabsIntent.launchUrl(context, Uri.parse(uri));

            // TODO. Refactor - Why Refactor? What does it mean?
            return Utils.isActivityResolved(customTabsIntent.intent, context);

        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }

        return false;
    }

    // TODO. Move. Why? What does it mean?
    public static void setDebugObstructionEnabled(boolean debugOstructionEnabled) {
        AdUtils.debugObstructionEnabled = debugOstructionEnabled;
    }
}