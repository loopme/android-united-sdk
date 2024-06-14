package com.loopme.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.loopme.utils.ApiLevel;
import com.loopme.views.webclient.AdViewChromeClient;

public class LoopMeWebView extends WebView {
    private static final String LOG_TAG = LoopMeWebView.class.getSimpleName();
    protected Constants.WebviewState mViewState = Constants.WebviewState.CLOSED;

    public LoopMeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureWebSettings();
    }

    public LoopMeWebView(Context context) {
        super(context);
        configureWebSettings();
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

    public void loadHtml(String html) {
        loadDataWithBaseURL(
            Constants.BASE_URL,
            html,
            Constants.MIME_TYPE_TEXT_HTML,
            Constants.UTF_8,
            null
        );
    }

    public void setDefaultWebChromeClient() {
        setWebChromeClient(new AdViewChromeClient());
    }

    private void tryRemoveFromParent() {
        ViewParent parent = getParent();
        if (parent != null)
            ((ViewGroup) parent).removeView(this);
    }

    @Override
    public void destroy() {
        tryRemoveFromParent();
        stopLoading();
        clearCache(true);
        clearHistory();
        setWebViewClient(null);
        setWebChromeClient(null);
        loadCommand("about:blank");
        // This helps Omid to send sessionFinish js event when ad is about to be destroyed.
        new Handler(Looper.getMainLooper())
            .postDelayed(LoopMeWebView.super::destroy, com.loopme.om.OmidHelper.FINISH_AD_SESSION_DELAY_MILLIS);
    }

    public void setWebViewState(Constants.WebviewState webviewState) {
        if (mViewState == webviewState) {
            return;
        }
        mViewState = webviewState;
        loadCommand(BridgeCommandBuilder.webviewState(BridgeCommandBuilder.LOOPME_PREFIX, mViewState));
    }

    protected void loadCommand(String command) {
        loadUrl(command);
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
