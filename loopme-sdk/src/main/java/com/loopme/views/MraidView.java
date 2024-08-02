package com.loopme.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.bridges.BridgeCommandBuilder;
import com.loopme.bridges.MraidBridge;
import com.loopme.controllers.MraidController;
import com.loopme.listener.AdReadyListener;
import com.loopme.utils.ApiLevel;
import com.loopme.views.webclient.AdViewChromeClient;


public class MraidView extends WebView {

    private String mCurrentMraidState;
    private Constants.WebviewState mViewState = Constants.WebviewState.CLOSED;

    private static final String LOG_TAG = MraidView.class.getSimpleName();

    public MraidView(Context context) {
        super(context);
        configureWebSettings();
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        setDefaultWebChromeClient();
    }

    public void loadHtml(String html) {
        loadDataWithBaseURL(
            Constants.BASE_URL,
            html,
            Constants.MIME_TYPE_TEXT_HTML,
            Constants.UTF_8,
            null
        );
    }

    public void setIsViewable(boolean isViewable) {
        loadUrl(BridgeCommandBuilder.mraidSetIsViewable(isViewable));
    }
    public void notifySizeChangeEvent(int width, int height) {
        loadUrl(BridgeCommandBuilder.mraidNotifySizeChangeEvent(width, height));
    }
    public void notifyReady() { loadUrl(BridgeCommandBuilder.mraidNotifyReady()); }
    public void notifyError() { loadUrl(BridgeCommandBuilder.mraidNotifyError()); }
    public void notifyStateChange() { loadUrl(BridgeCommandBuilder.mraidNotifyStateChange()); }
    public void onMraidCallComplete() { loadUrl(BridgeCommandBuilder.mraidNativeCallComplete()); }
    public void onLoopMeCallComplete() { loadUrl(BridgeCommandBuilder.isNativeCallFinished(true)); }

    public void setState(String state) {
        if (!TextUtils.equals(mCurrentMraidState, state)) {
            mCurrentMraidState = state;
            loadUrl(BridgeCommandBuilder.mraidSetState(state));
        }
    }

    public boolean isExpanded() {
        return TextUtils.equals(mCurrentMraidState, Constants.MraidState.EXPANDED);
    }

    public boolean isResized() {
        return TextUtils.equals(mCurrentMraidState, Constants.MraidState.RESIZED);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        if (BuildConfig.DEBUG || Constants.sDebugMode) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            clearCache(true);
            setWebContentsDebuggingEnabled(true);
        }
        Logging.out(LOG_TAG, "Encoding: " + getSettings().getDefaultTextEncodingName());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(this, true);
    }

    public void setDefaultWebChromeClient() { setWebChromeClient(new AdViewChromeClient()); }

    @Override
    public void destroy() {
        ViewParent parent = getParent();
        if (parent != null)
            ((ViewGroup) parent).removeView(this);
        stopLoading();
        clearCache(true);
        clearHistory();
        setWebViewClient(null);
        setWebChromeClient(null);
        loadUrl("about:blank");
        // This helps Omid to send sessionFinish js event when ad is about to be destroyed.
        new Handler(Looper.getMainLooper())
                .postDelayed(super::destroy, com.loopme.om.OmidHelper.FINISH_AD_SESSION_DELAY_MILLIS);
    }

    public void setWebViewState(Constants.WebviewState webviewState) {
        if (mViewState == webviewState) {
            return;
        }
        mViewState = webviewState;
        loadUrl(BridgeCommandBuilder.webviewState(mViewState));
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
        if (!ApiLevel.isApi26AndHigher())
            webChromeClient = client;
    }

    public WebChromeClient getWebChromeClientCompat() {
        return ApiLevel.isApi26AndHigher() ? getWebChromeClient() : webChromeClient;
    }

    private WebChromeClient webChromeClient;
}
