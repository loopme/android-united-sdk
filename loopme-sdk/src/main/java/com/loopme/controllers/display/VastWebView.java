package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopme.views.webclient.AdViewChromeClient;

public class VastWebView extends WebView {

    private Vast4Tracker.OnAdVerificationListener mListener;

    public VastWebView(Context context, Vast4Tracker.OnAdVerificationListener listener) {
        super(context);
        mListener = listener;
        configure();
        allowCookies();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configure() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(Color.TRANSPARENT);
        setWebContentsDebuggingEnabled(true);
        setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        setWebChromeClient(new AdViewChromeClient(new AdViewChromeClient.OnErrorFromJsCallback() {
            @Override
            public void onErrorFromJs(String message) {
                onVerificationJsFailed(message);
            }
        }));

        setWebViewClient(new Vast4WebViewClient(mListener));
    }

    private void onVerificationJsFailed(String message) {
        if (mListener != null) {
            mListener.onVerificationJsFailed(message);
        }
    }

    private void allowCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
    }

    public void destroy() {
        clearCache(true);
        setWebChromeClient(null);
        setWebViewClient(null);
        loadUrl("about:blank");
    }
}
