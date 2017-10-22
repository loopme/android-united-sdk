package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.views.webclient.AdViewChromeClient;

public class VastWebView extends WebView {
    private OnFinishLoadListener mListener;

    public VastWebView(Context context, OnFinishLoadListener listener) {
        super(context);
        mListener = listener;
        configure();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configure() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setBackgroundColor(Color.TRANSPARENT);
        setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        setWebChromeClient(new AdViewChromeClient(new AdViewChromeClient.OnErrorFromJsCallback() {
            @Override
            public void onErrorFromJs(String message) {
                onJsError(message);
            }
        }));

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onFinishLoad();
            }
        });
    }

    public void destroy() {
        clearCache(true);
        setWebChromeClient(null);
        setWebViewClient(null);
        loadUrl("about:blank");
    }

    private void onFinishLoad() {
        if (mListener != null) {
            mListener.onFinishLoad();
        }
    }

    private void onJsError(String message) {
        if (mListener != null) {
            mListener.onJsError(message);
        }
    }

    public interface OnFinishLoadListener {
        void onFinishLoad();

        void onJsError(String message);
    }
}
