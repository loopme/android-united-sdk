package com.loopme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.views.activity.BaseActivity;

public class AdUtils {

    private static final String LOG_TAG = AdUtils.class.getSimpleName();

    public static void startAdActivity(@NonNull LoopMeAd loopMeAd) {
        LoopMeAdHolder.putAd(loopMeAd);
        Logging.out(LOG_TAG, "Starting Ad Activity");

        Intent intent = new Intent(loopMeAd.getContext(), BaseActivity.class);
        intent.setPackage(loopMeAd.getContext().getPackageName());
        intent.putExtra(Constants.AD_ID_TAG, loopMeAd.getAdId());
        intent.putExtra(Constants.FORMAT_TAG, loopMeAd.getAdFormat().ordinal());
        intent.putExtra(Constants.EXTRAS_CUSTOM_CLOSE, false);
        intent.putExtra(Constants.EXTRAS_DEBUG_OBSTRUCTION_ENABLED, false);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        loopMeAd.getContext().startActivity(intent);
    }

    // https://developer.chrome.com/multidevice/android/customtabs
    public static boolean tryStartCustomTabs(Context context, String uri) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent
                .Builder()
                .enableUrlBarHiding()
                .build();
            boolean isActivityResolved = context
                .getPackageManager()
                .resolveActivity(customTabsIntent.intent, PackageManager.MATCH_DEFAULT_ONLY) != null;
            customTabsIntent.launchUrl(context, Uri.parse(uri));
            return isActivityResolved;
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Failed to start custom tabs: " + e.getMessage());
            return false;
        }
    }
}