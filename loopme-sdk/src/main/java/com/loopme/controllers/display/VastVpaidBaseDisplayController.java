package com.loopme.controllers.display;

import android.content.Context;
import android.content.res.AssetManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.VastVpaidDisplayController;
import com.loopme.loaders.VastVpaidAssetsResolver;
import com.loopme.models.Errors;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.xml.Tracking;
import com.loopme.xml.TrackingEvents;
import com.loopme.xml.vast4.JavaScriptResource;
import com.loopme.xml.vast4.VerificationParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public abstract class VastVpaidBaseDisplayController extends BaseTrackableController
        implements VastVpaidDisplayController, Observer {

    private static final String LOG_TAG = VastVpaidBaseDisplayController.class.getSimpleName();
    // TODO. Move?
    private static final String VAST_VERIFICATION_API_FRAMEWORK_OMID = "omid";
    private static final String VAST_EVENT_VERIFICATION_NOT_EXECUTED = "verificationNotExecuted";
    protected static final int VAST_MACROS_REASON_NOT_SUPPORTED = 2;
    protected static final int VAST_MACROS_REASON_LOAD_ERROR = 3;

    protected String mVideoUri;
    protected String mImageUri;
    protected AdParams mAdParams;
    protected LoopMeAd mLoopMeAd;

    private final VastVpaidAssetsResolver mVastVpaidAssetsResolver;

    private boolean mIsVpaidEventTracked;
    private Timers mTimer;
    private Context mContext;
    private ViewableImpressionTracker viewableImpressionTracker;
    private VastVpaidEventTracker mEventTracker;
    private WebView webView;

    public VastVpaidBaseDisplayController(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mLoopMeAd = loopMeAd;
        mAdParams = loopMeAd.getAdParams();
        mContext = loopMeAd.getContext();
        mVastVpaidAssetsResolver = new VastVpaidAssetsResolver();
        mTimer = new Timers(this);
        LoopMeTracker.initVastErrorUrl(mAdParams.getErrorUrlList());
        mEventTracker = new VastVpaidEventTracker(mLoopMeAd.getAdParams().getTrackingEventsList());
    }

    public void postVideoEvent(String event, String addMessage) {
        if (mEventTracker != null) {
            mEventTracker.postEvent(event, addMessage);
        }
    }

    public void postVideoEvent(String eventsUrlOrType) {
        postVideoEvent(eventsUrlOrType, "");
        postVpaidDidReachEnd(eventsUrlOrType);
    }

    protected void postVideoClicks(String currentPosition) {
        for (String trackUrl : mAdParams.getVideoClicks()) {
            postVideoEvent(trackUrl, currentPosition);
        }
    }

    protected void postEndCardClicks() {
        for (String trackUrl : mAdParams.getEndCardClicks()) {
            postVideoEvent(trackUrl);
        }
    }

    protected void onEndCardAppears() {
        for (String trackUrl : mAdParams.getCompanionCreativeViewEvents()) {
            postVideoEvent(trackUrl);
        }
    }

    private void postVpaidDidReachEnd(String eventType) {
        if (!mIsVpaidEventTracked) {
            if (this instanceof DisplayControllerVpaid && TextUtils.equals(eventType, EventConstants.COMPLETE)) {
                onAdVideoDidReachEnd();
                mIsVpaidEventTracked = true;
            }
        }
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();

        // Start initializing OMID part.
        onTryCreateOmidTracker(getSupportedOmidVerificationData(mAdParams));

        startTimer(TimersType.PREPARE_ASSETS_TIMER);
        mVastVpaidAssetsResolver.resolve(
                mAdParams,
                mContext,
                createAssetsLoadListener());

        initTrackers();
    }

    protected void onTryCreateOmidTracker(Map<String, Verification> omidVerificationMap) { }

    protected void setVerificationView(View view) {
        if (viewableImpressionTracker != null) {
            viewableImpressionTracker.setAdView(view);
        }
    }

    protected void postViewableEvents(int doneMillis) {
        if (viewableImpressionTracker != null) {
            viewableImpressionTracker.postViewableEvents(doneMillis);
        }
    }

    protected boolean hasViewableImpression() {
        return mAdParams != null && mAdParams.hasVast4ViewableImpressions();
    }

    private VastVpaidAssetsResolver.OnAssetsLoaded createAssetsLoadListener() {
        return new VastVpaidAssetsResolver.OnAssetsLoaded() {
            @Override
            public void onAssetsLoaded(String videoFilePath, String endCardFilePath) {
                mVideoUri = videoFilePath;
                mImageUri = endCardFilePath;
                stopTimer(TimersType.PREPARE_ASSETS_TIMER);
                prepareAd();
            }

            @Override
            public void onError(LoopMeError info) {
                onInternalLoadFail(info);
                stopTimer(TimersType.PREPARE_ASSETS_TIMER);
            }

            @Override
            public void onPostWarning(LoopMeError error) {
                postWarning(error);
            }
        };
    }

    // TODO. Refactor.
    private static Map<String, Verification> getSupportedOmidVerificationData(AdParams adParams) {
        Map<String, Verification> omidVerificationMap = new HashMap<>();

        if (adParams == null)
            return omidVerificationMap;

        List<com.loopme.xml.vast4.Verification> verificationList = adParams.getVerificationList();
        if (verificationList == null)
            return omidVerificationMap;

        for (com.loopme.xml.vast4.Verification v : verificationList) {
            if (v == null)
                continue;

            String vendor = v.getVendor();
            if (TextUtils.isEmpty(vendor))
                continue;

            List<String> verificationNotExecutedEventUrlList =
                    getVerificationNotExecutedEventUrlList(v.getTrackingEvents());

            String omidJSUrl = pickOMIDJavaScriptResourceUrl(v.getJavaScriptResourceList());
            // TODO. Refactor. This piece of code isn't about retrieving data.
            // Verification vendor api/resource type isn't supported: OMID JS resource only.
            if (TextUtils.isEmpty(omidJSUrl)) {
                postVerificationNotExecutedEvent(
                        verificationNotExecutedEventUrlList,
                        VAST_MACROS_REASON_NOT_SUPPORTED);
                continue;
            }

            VerificationParameters vp = v.getVerificationParameters();

            omidVerificationMap.put(
                    vendor,
                    new Verification(
                            vendor,
                            omidJSUrl,
                            verificationNotExecutedEventUrlList,
                            vp == null ? null : vp.getText()));
        }

        return omidVerificationMap;
    }

    // TODO. Refactor.
    protected static void postVerificationNotExecutedEvent(
            List<String> verificationNotExecutedUrlList,
            int reason) {

        if (verificationNotExecutedUrlList == null)
            return;

        for (String url : verificationNotExecutedUrlList)
            VastVpaidEventTracker.trackVastEvent(
                    url,
                    String.valueOf(reason));
    }

    // TODO. Refactor.
    @NonNull
    private static List<String> getVerificationNotExecutedEventUrlList(TrackingEvents trackingEvents) {
        List<String> verificationNotExecutedUrlList = new ArrayList<>();

        if (trackingEvents == null)
            return verificationNotExecutedUrlList;

        List<Tracking> trackingList = trackingEvents.getTrackingList();
        if (trackingList == null)
            return verificationNotExecutedUrlList;

        for (Tracking t : trackingList) {
            if (t == null)
                continue;

            String event = t.getEvent();
            if (event == null || !event.equalsIgnoreCase(VAST_EVENT_VERIFICATION_NOT_EXECUTED))
                continue;

            String url = t.getText();
            if (TextUtils.isEmpty(url))
                continue;

            verificationNotExecutedUrlList.add(url);
        }

        return verificationNotExecutedUrlList;
    }

    // TODO. Refactor.
    private static String pickOMIDJavaScriptResourceUrl(List<JavaScriptResource> jsResourceList) {
        if (jsResourceList == null)
            return null;

        String browserOptionalJsUrl = null;

        for (JavaScriptResource jsr : jsResourceList) {
            if (jsr == null)
                continue;

            String api = jsr.getApiFramework();
            if (api == null || !api.equalsIgnoreCase(VAST_VERIFICATION_API_FRAMEWORK_OMID))
                continue;

            String jsUrl = jsr.getText();
            if (TextUtils.isEmpty(jsUrl))
                continue;

            if (jsr.getBrowserOptional())
                browserOptionalJsUrl = jsUrl;
            else
                return jsUrl;
        }

        return browserOptionalJsUrl;
    }


    private void postWarning(LoopMeError error) {
        if (mLoopMeAd != null) {
            mLoopMeAd.onSendPostWarning(error);
        }
    }

    private void prepareAd() {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                startTimer(TimersType.PREPARE_VPAID_JS_TIMER);
                prepare();
            }
        });
    }

    protected abstract WebView createWebView();

    protected void destroyWebView() {
        if (webView != null)
            webView.destroy();

        webView = null;
    }

    @Override
    public void prepare() {
        webView = createWebView();
        onAdRegisterView(mLoopMeAd.getContext(), webView);

        if (viewableImpressionTracker == null && hasViewableImpression())
            viewableImpressionTracker = new ViewableImpressionTracker(mLoopMeAd.getAdParams());
    }

    @Override
    public void closeSelf() {
        onAdUserCloseEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyTimers();
        breakAssetsLoading();
        LoopMeTracker.clear();
    }

    protected void onAdLoadSuccess() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.onAdLoadSuccess();
                }
            });
        }
    }

    protected void dismissAd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.dismiss();
                }
            });
        }
    }

    protected void onAdClicked() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.onAdClicked();
                }
            });
        }
    }

    protected void onAdVideoDidReachEnd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.onAdVideoDidReachEnd();
                }
            });
        }
    }

    protected AssetManager getAssetsManager() {
        return mContext.getAssets();
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof Timers && arg instanceof TimersType) {
            switch ((TimersType) arg) {
                case PREPARE_VPAID_JS_TIMER: {
                    onPrepareJsTimeout();
                    break;
                }
                case PREPARE_ASSETS_TIMER: {
                    onPrepareAssetsTimeout();
                    break;
                }
            }
        }
    }

    protected void onAdReady() {
        stopTimer(TimersType.PREPARE_VPAID_JS_TIMER);
        onAdLoadSuccess();
    }

    private void onPrepareAssetsTimeout() {
        stopTimer(TimersType.PREPARE_ASSETS_TIMER);
        breakAssetsLoading();
        onInternalLoadFail(Errors.TIMEOUT_ON_MEDIA_FILE_URI);
    }

    private void onPrepareJsTimeout() {
        stopTimer(TimersType.PREPARE_VPAID_JS_TIMER);
        Logging.out(LOG_TAG, "Js loading timeout");
        if (this instanceof DisplayControllerVpaid) {
            onInternalLoadFail(Errors.JS_LOADING_TIMEOUT);
        }
    }

    private void breakAssetsLoading() {
        if (mVastVpaidAssetsResolver != null) {
            mVastVpaidAssetsResolver.stop();
        }
    }

    private void destroyTimers() {
        if (mTimer != null) {
            mTimer.destroy();
            mTimer = null;
        }
    }

    @Override
    public boolean isFullScreen() {
        return mLoopMeAd != null && mLoopMeAd.isInterstitial();
    }

    @Override
    public WebView getWebView() {
        return webView;
    }

    private void startTimer(TimersType timersType) {
        if (mTimer != null) {
            mTimer.startTimer(timersType);
        }
    }

    private void stopTimer(TimersType timersType) {
        if (mTimer != null) {
            mTimer.stopTimer(timersType);
        }
    }

    protected static class Verification {
        private String vendor;
        private String javaScriptResourceUrl;
        private List<String> verificationNotExecutedUrlList;
        private String verificationParameters;

        public Verification(
                String vendor,
                String jsResourceUrl,
                List<String> verificationNotExecutedUrlList,
                String verificationParameters) {
            this.vendor = vendor;
            this.javaScriptResourceUrl = jsResourceUrl;
            this.verificationNotExecutedUrlList = verificationNotExecutedUrlList;
            this.verificationParameters = verificationParameters;
        }

        public String getVendor() {
            return vendor;
        }

        public String getJavaScriptResourceUrl() {
            return javaScriptResourceUrl;
        }

        public List<String> getVerificationNotExecutedUrlList() {
            return verificationNotExecutedUrlList;
        }

        public String getVerificationParameters() {
            return verificationParameters;
        }

    }
}