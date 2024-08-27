package com.loopme.controllers.display;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.loopme.ad.AdParams;
import com.loopme.ad.LoopMeAd;
import com.loopme.controllers.interfaces.VastVpaidDisplayController;
import com.loopme.tracker.constants.AdType;
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

public abstract class VastVpaidBaseDisplayController extends BaseTrackableController
        implements VastVpaidDisplayController {

    private static final String LOG_TAG = VastVpaidBaseDisplayController.class.getSimpleName();
    // TODO. Move?
    private static final String VAST_VERIFICATION_API_FRAMEWORK_OMID = "omid";
    private static final String VAST_EVENT_VERIFICATION_NOT_EXECUTED = "verificationNotExecuted";
    protected static final int VAST_MACROS_REASON_NOT_SUPPORTED = 2;
    protected static final int VAST_MACROS_REASON_LOAD_ERROR = 3;

    protected AdParams mAdParams;
    protected LoopMeAd mLoopMeAd;

    private boolean mIsVpaidEventTracked;

    protected final Context mContext;
    private ViewableImpressionTracker viewableImpressionTracker;
    private final VastVpaidEventTracker mEventTracker;
    private WebView webView;

    public VastVpaidBaseDisplayController(@NonNull LoopMeAd loopMeAd) {
        super(loopMeAd);
        mLoopMeAd = loopMeAd;
        mAdParams = loopMeAd.getAdParams();
        mContext = loopMeAd.getContext();
        LoopMeTracker.initVastErrorUrl(mAdParams.getErrorUrlList());
        mEventTracker = new VastVpaidEventTracker(mLoopMeAd.getAdParams().getTrackingEventsList());
    }

    public void postVideoEvent(String event, String addMessage) {
        if (mEventTracker != null) mEventTracker.postEvent(event, addMessage);
    }

    public void postVideoEvent(String eventsUrlOrType) {
        postVideoEvent(eventsUrlOrType, "");
        if (mIsVpaidEventTracked) {
            return;
        }
        if (this instanceof DisplayControllerVpaid && TextUtils.equals(eventsUrlOrType, EventConstants.COMPLETE)) {
            onAdVideoDidReachEnd();
            mIsVpaidEventTracked = true;
        }
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        // Start initializing OMID part.
        onTryCreateOmidTracker(getSupportedOmidVerificationData(mAdParams));
        // Init trackers
        boolean isNativeAd = mLoopMeAd.isVastAd() && !mLoopMeAd.isVpaidAd() && !mLoopMeAd.isMraidAd();
        onInitTracker(isNativeAd ? AdType.NATIVE : AdType.WEB);
        mLoopMeAd.runOnUiThread(this::prepare);
    }

    protected void onTryCreateOmidTracker(Map<String, Verification> omidVerificationMap) { }

    protected void setVerificationView(View view) {
        if (viewableImpressionTracker != null) viewableImpressionTracker.setAdView(view);
    }

    protected void postViewableEvents(int doneMillis) {
        if (viewableImpressionTracker != null) viewableImpressionTracker.postViewableEvents(doneMillis);
    }

    // TODO. Refactor.
    private static Map<String, Verification> getSupportedOmidVerificationData(AdParams adParams) {
        Map<String, Verification> omidVerificationMap = new HashMap<>();
        if (adParams == null) return omidVerificationMap;

        List<com.loopme.xml.vast4.Verification> verificationList = adParams.getVerificationList();
        if (verificationList == null) return omidVerificationMap;

        for (com.loopme.xml.vast4.Verification v : verificationList) {
            if (v == null) continue;
            String vendor = v.getVendor();
            if (TextUtils.isEmpty(vendor)) continue;
            List<String> verificationNotExecutedEventUrlList =
                getVerificationNotExecutedEventUrlList(v.getTrackingEvents());
            String omidJSUrl = pickOMIDJavaScriptResourceUrl(v.getJavaScriptResourceList());
            // TODO. Refactor. This piece of code isn't about retrieving data.
            // Verification vendor api/resource type isn't supported: OMID JS resource only.
            if (TextUtils.isEmpty(omidJSUrl)) {
                postVerificationNotExecutedEvent(
                    verificationNotExecutedEventUrlList, VAST_MACROS_REASON_NOT_SUPPORTED
                );
                continue;
            }
            VerificationParameters vp = v.getVerificationParameters();
            omidVerificationMap.put(
                vendor,
                new Verification(
                    vendor, omidJSUrl, verificationNotExecutedEventUrlList, vp == null ? null : vp.getText()
                )
            );
        }
        return omidVerificationMap;
    }

    // TODO. Refactor.
    protected static void postVerificationNotExecutedEvent(
        List<String> verificationNotExecutedUrlList, int reason
    ) {
        if (verificationNotExecutedUrlList == null) return;
        for (String url : verificationNotExecutedUrlList)
            VastVpaidEventTracker.trackVastEvent(url, String.valueOf(reason));
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
            if (t == null) continue;
            String event = t.getEvent();
            if (event == null || !event.equalsIgnoreCase(VAST_EVENT_VERIFICATION_NOT_EXECUTED)) continue;
            String url = t.getText();
            if (TextUtils.isEmpty(url)) continue;
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
            if (jsr == null) continue;
            String api = jsr.getApiFramework();
            if (api == null || !api.equalsIgnoreCase(VAST_VERIFICATION_API_FRAMEWORK_OMID)) continue;
            String jsUrl = jsr.getText();
            if (TextUtils.isEmpty(jsUrl)) continue;
            if (jsr.getBrowserOptional())
                browserOptionalJsUrl = jsUrl;
            else
                return jsUrl;
        }
        return browserOptionalJsUrl;
    }

    protected abstract WebView createWebView();

    protected void destroyWebView() {
        if (webView != null) webView.destroy();
        webView = null;
    }

    @Override
    public void prepare() {
        webView = createWebView();
        onAdRegisterView(mLoopMeAd.getContext(), webView);
        boolean hasViewableImpression = mAdParams != null && mAdParams.hasVast4ViewableImpressions();
        if (viewableImpressionTracker == null && hasViewableImpression)
            viewableImpressionTracker = new ViewableImpressionTracker(mLoopMeAd.getAdParams());
    }

    @Override
    public void closeSelf() { onAdUserCloseEvent(); }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LoopMeTracker.clear();
    }

    protected void dismissAd() {
        if (mLoopMeAd != null) mLoopMeAd.runOnUiThread(() -> mLoopMeAd.dismiss());
    }

    protected void onAdClicked() {
        if (mLoopMeAd != null) mLoopMeAd.runOnUiThread(() -> mLoopMeAd.onAdClicked());
    }

    protected void onAdVideoDidReachEnd() {
        if (mLoopMeAd != null) mLoopMeAd.runOnUiThread(() -> mLoopMeAd.onAdVideoDidReachEnd());
    }

    protected void onAdReady() {
        if (mLoopMeAd != null) mLoopMeAd.runOnUiThread(() -> mLoopMeAd.onAdLoadSuccess());
    }

    @Override
    public boolean isFullScreen() { return mLoopMeAd != null && mLoopMeAd.isInterstitial(); }

    @Override
    public WebView getWebView() { return webView; }

    protected static class Verification {
        private final String vendor;
        private final String javaScriptResourceUrl;
        private final List<String> verificationNotExecutedUrlList;
        private final String verificationParameters;

        public Verification(
            String vendor,
            String jsResourceUrl,
            List<String> verificationNotExecutedUrlList,
            String verificationParameters
        ) {
            this.vendor = vendor;
            this.javaScriptResourceUrl = jsResourceUrl;
            this.verificationNotExecutedUrlList = verificationNotExecutedUrlList;
            this.verificationParameters = verificationParameters;
        }

        public String getVendor() { return vendor; }
        public String getJavaScriptResourceUrl() { return javaScriptResourceUrl; }
        public String getVerificationParameters() { return verificationParameters; }

        public List<String> getVerificationNotExecutedUrlList() {
            return verificationNotExecutedUrlList;
        }
    }
}