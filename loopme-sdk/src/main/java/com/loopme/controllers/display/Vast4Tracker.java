package com.loopme.controllers.display;

import android.view.View;
import android.webkit.WebView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.MoatViewAbilityUtils;
import com.loopme.ad.AdParams;
import com.loopme.utils.StringUtils;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.webservice.ExecutorHelper;

import java.util.List;
import java.util.concurrent.Executor;

public class Vast4Tracker {
    private static final String LOG_TAG = Vast4Tracker.class.getSimpleName();
    public static final int IMPRESSION_TIME_NATIVE_VIDEO = 2000;
    private static final double FIFTY_PERCENTS = 0.5;

    private boolean mIsImpressionTracked;
    private Executor mExecutor;
    private AdParams mAdParams;
    private WebView mWebView;
    private View mAdView;

    public Vast4Tracker(AdParams adParams, WebView webView) {
        mAdParams = adParams;
        mWebView = webView;
        mExecutor = ExecutorHelper.getExecutor();

    }


    public void postViewableEvents(final int doneMillis) {
        if (isImpressionTimePassed(doneMillis) && !mIsImpressionTracked) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    postImpressionEvent();
                }
            });
            mIsImpressionTracked = true;
        }
    }

    private boolean isImpressionTimePassed(int doneMillis) {
        return doneMillis >= IMPRESSION_TIME_NATIVE_VIDEO;
    }

    private void postImpressionEvent() {
        if (isVideoVisible()) {
            postEventsByUrl(mAdParams.getVisibleImpressions());
        } else {
            postEventsByUrl(mAdParams.getNotVisibleImpressions());
        }
    }

    private void postEventsByUrl(List<String> impressions) {
        for (String impressionUrl : impressions) {
            VastVpaidEventTracker.trackVastEvent(impressionUrl, "");
        }
    }

    private boolean isVideoVisible() {
        MoatViewAbilityUtils.ViewAbilityInfo viewAbilityInfo = MoatViewAbilityUtils.calculateViewAbilityInfo(mAdView);
        Logging.out(LOG_TAG, "visibility: " + viewAbilityInfo.getVisibility());
        return viewAbilityInfo.getVisibility() > FIFTY_PERCENTS;
    }

    public void loadVerificationJavaScripts() {
        List<String> adVerificationJavaScriptUrlList = mAdParams.getAdVerificationJavaScriptUrlList();
        String html = StringUtils.generateHtml(adVerificationJavaScriptUrlList);
        Logging.out(LOG_TAG, "verification html " + html);
        mWebView.loadData(html, Constants.MIME_TYPE_TEXT_HTML, Constants.UTF_8);
    }

    public void setAdView(View adView) {
        this.mAdView = adView;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }
}
