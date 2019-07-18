package com.loopme.views.webclient;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.utils.ApiLevel;

public class WebViewClientCompat extends WebViewClient {

    @Deprecated
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return !ApiLevel.isApi24AndHigher() &&
                shouldOverrideUrlLoadingCompat(
                        view,
                        url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return ApiLevel.isApi24AndHigher() &&
                shouldOverrideUrlLoadingCompat(
                        view,
                        request.getUrl().toString());
    }

    public boolean shouldOverrideUrlLoadingCompat(WebView webView, String url) {
        return false;
    }
}
