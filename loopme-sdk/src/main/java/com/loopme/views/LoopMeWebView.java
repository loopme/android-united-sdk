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
        allowCookies();
    }

    public LoopMeWebView(Context context) {
        super(context);
        configureWebSettings();
        allowCookies();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setDebugConfig();
        Logging.out(LOG_TAG, "Encoding: " + getSettings().getDefaultTextEncodingName());
    }

    private void allowCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(this, true);
    }

    private void setDebugConfig() {
        if (BuildConfig.DEBUG || Constants.sDebugMode) {
            getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            clearCache(true);
            setWebContentsDebuggingEnabled(true);
        }
    }

    public void loadHtml(String html) {
        loadDataWithBaseURL(
                Constants.BASE_URL,
                html,
                Constants.MIME_TYPE_TEXT_HTML,
                Constants.UTF_8,
                null);
    }

    public void setDefaultWebChromeClient() {
        setWebChromeClient(new AdViewChromeClient());
    }

    // TODO. Refactor.
    @Override
    public void destroy() {
        tryRemoveFromParent();
        stopLoading();
        clearCache(true);
        clearHistory();
        setWebViewClient(null);
        setWebChromeClient(null);
        loadCommand("about:blank");
        super.destroy();
    }

    // TODO. Refactor.
    protected void destroyGracefully() {
        tryRemoveFromParent();

        clearCache(true);
        clearHistory();

        setWebViewClient(null);
        setWebChromeClient(null);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                LoopMeWebView.super.destroy();
            }
        }, com.loopme.om.OmidHelper.FINISH_AD_SESSION_DELAY_MILLIS);
    }

    private void tryRemoveFromParent() {
        ViewParent parent = getParent();
        if (parent != null)
            ((ViewGroup) parent).removeView(this);
    }

    public void setWebViewState(Constants.WebviewState webviewState) {
        if (mViewState != webviewState) {
            mViewState = webviewState;
            String command = BridgeCommandBuilder.webviewState(BridgeCommandBuilder.LOOPME_PREFIX, mViewState);
            Logging.out(LOG_TAG, "setWebViewState() : " + webviewState.name());
            loadCommand(command);
        }
    }

    protected void loadCommand(String command) {
        Logging.out(LOG_TAG, command);
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
