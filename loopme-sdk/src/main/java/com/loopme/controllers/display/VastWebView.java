package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopme.views.webclient.AdViewChromeClient;

public class VastWebView extends WebView {

    public VastWebView(Context context) {
        super(context);
        configure();
        allowCookies();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configure() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(Color.TRANSPARENT);
        // TODO.
        setWebContentsDebuggingEnabled(true);
        setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        setWebChromeClient(new AdViewChromeClient(message -> { }));
        setWebViewClient(new Vast4WebViewClient());
    }

    private void allowCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(this, true);
    }

    // TODO. Refactor. Duplicate of LoopMeWebView.
    public void destroy() {
        ViewParent parent = getParent();
        if (parent != null)
            ((ViewGroup)parent).removeView(this);

        clearCache(true);
        clearHistory();
        setWebViewClient(null);
        setWebChromeClient(null);
        loadUrl("about:blank");
        super.destroy();
    }
}
