package com.loopme;

import android.content.Intent;

import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.views.activity.BaseActivity;

public class AdUtils {

    private static final String LOG_TAG = AdUtils.class.getSimpleName();

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
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loopMeAd.getContext().startActivity(intent);
        }
    }

}
