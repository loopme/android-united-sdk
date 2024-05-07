package com.loopme.controllers.display;

import android.view.View;

import com.loopme.Logging;
import com.loopme.MoatViewAbilityUtils;
import com.loopme.ad.AdParams;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.utils.ExecutorHelper;

class ViewableImpressionTracker {
    private static final String LOG_TAG = ViewableImpressionTracker.class.getSimpleName();
    static final int IMPRESSION_TIME_NATIVE_VIDEO = 2000;
    private static final double FIFTY_PERCENTS = 0.5;

    private boolean mIsImpressionTracked;
    private final AdParams mAdParams;
    private View mAdView;

    ViewableImpressionTracker(AdParams adParams) {
        mAdParams = adParams;
    }

    void postViewableEvents(final int doneMillis) {
        if (mIsImpressionTracked || doneMillis < IMPRESSION_TIME_NATIVE_VIDEO)
            return;

        ExecutorHelper.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (String url :
                        isVideoVisible()
                                ? mAdParams.getVisibleImpressions()
                                : mAdParams.getNotVisibleImpressions())
                    VastVpaidEventTracker.trackVastEvent(url, "");
            }
        });

        mIsImpressionTracked = true;
    }

    private boolean isVideoVisible() {
        MoatViewAbilityUtils.ViewAbilityInfo viewAbilityInfo =
                MoatViewAbilityUtils.calculateViewAbilityInfo(mAdView);

        Logging.out(
                LOG_TAG,
                "visibility: " + viewAbilityInfo.getVisibility());

        return viewAbilityInfo.getVisibility() > FIFTY_PERCENTS;
    }

    void setAdView(View adView) {
        this.mAdView = adView;
    }
}
