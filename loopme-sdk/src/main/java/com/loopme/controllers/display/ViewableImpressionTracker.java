package com.loopme.controllers.display;

import android.view.View;

import com.loopme.ViewAbilityUtils;
import com.loopme.ad.AdParams;
import com.loopme.utils.ExecutorHelper;
import com.loopme.vast.VastVpaidEventTracker;

import java.util.List;

class ViewableImpressionTracker {
    static final int IMPRESSION_TIME_NATIVE_VIDEO = 2000;

    private boolean mIsImpressionTracked;
    private final AdParams mAdParams;
    private View mAdView;

    ViewableImpressionTracker(AdParams adParams) { mAdParams = adParams; }

    void postViewableEvents(final int doneMillis) {
        if (mIsImpressionTracked || doneMillis < IMPRESSION_TIME_NATIVE_VIDEO)
            return;
        ExecutorHelper.getExecutor().execute(() -> {
            ViewAbilityUtils.ViewAbilityInfo viewAbilityInfo =
                ViewAbilityUtils.calculateViewAbilityInfo(mAdView);
            List<String> impressions = viewAbilityInfo.getVisibility() > 0.5
                ? mAdParams.getVisibleImpressions() : mAdParams.getNotVisibleImpressions();
            for (String url : impressions)
                VastVpaidEventTracker.trackVastEvent(url, "");
        });
        mIsImpressionTracked = true;
    }

    void setAdView(View adView) { this.mAdView = adView; }
}
