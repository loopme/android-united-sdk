package com.loopme.gdpr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.loopme.utils.AnimationUtils;
import com.loopme.utils.ApiLevel;
import com.loopme.views.webclient.WebViewClientCompat;

/**
 * Created by katerina on 5/7/18.
 */

public class GdprWebView extends WebView {

    private static final String BRIDGE = "GdprBridge";
    private static final String POPUP = "://popup/";
    private static final String LOOPME_SCHEMA = "loopme";
    private static final String READY_EVENT = POPUP + "ready";
    private static final String CLOSE_EVENT = POPUP + "close";
    private static final String PRIVACY_PAGE = "privacy";

    private GdprBridge.OnCloseListener gdprBridgeCloseListener;
    private ProgressBar progressBar;

    public GdprWebView(Context context) {
        super(context);
        configure();
    }

    public GdprWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public GdprWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure();
    }

    public GdprWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        configure();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configure() {
        WebSettings webSettings = getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        // TODO.
        webSettings.setAppCacheEnabled(false);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        // TODO.
        setWebContentsDebuggingEnabled(true);
        configureChromeClient();
        configureWebClient();
        allowMixedContent();
    }

    private void configureWebClient() {
        setWebViewClient(new WebViewClientCompat() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                enableProgressBar(AnimationUtils.AnimationType.OUT);
            }

            @Override
            public boolean shouldOverrideUrlLoadingCompat(WebView view, String url) {
                if (url.equalsIgnoreCase(LOOPME_SCHEMA + CLOSE_EVENT)) {
                    onClose();
                    return true;
                }

                if (url.equalsIgnoreCase(LOOPME_SCHEMA + READY_EVENT))
                    return true;

                if (!TextUtils.isEmpty(url) && url.contains(PRIVACY_PAGE))
                    enableProgressBar(AnimationUtils.AnimationType.IN);

                return false;
            }
        });
    }

    private void configureChromeClient() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar != null)
                    progressBar.setProgress(newProgress);
            }
        });
    }

    private void allowMixedContent() {
        if (ApiLevel.isApi21AndHigher())
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
    }

    private void onClose() {
        if (gdprBridgeCloseListener != null)
            gdprBridgeCloseListener.onGdprClose();

        gdprBridgeCloseListener = null;
    }

    private void enableProgressBar(AnimationUtils.AnimationType type) {
        if (progressBar != null)
            progressBar.startAnimation(AnimationUtils.getAlphaAnimation(type, progressBar));
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setListener(GdprBridge.OnCloseListener gdprBridgeCloseListener) {
        this.gdprBridgeCloseListener = gdprBridgeCloseListener;
        addJavascriptInterface(new GdprBridge(this.gdprBridgeCloseListener), BRIDGE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_BACK &&
                canGoBack()) {
            goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    // TODO. Duplicate of MraidView/AdView.
    @Override
    public void destroy() {
        gdprBridgeCloseListener = null;

        ViewParent parent = getParent();
        if (parent != null)
            ((ViewGroup) parent).removeView(this);

        clearCache(true);
        clearHistory();

        setWebViewClient(null);
        setWebChromeClient(null);

        super.destroy();
    }
}