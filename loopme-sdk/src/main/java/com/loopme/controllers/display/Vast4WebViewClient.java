package com.loopme.controllers.display;

import android.webkit.WebView;

import com.loopme.views.webclient.WebViewClientCompat;

public class Vast4WebViewClient extends WebViewClientCompat {
    private OnPageLoadedListener mPageLoadedListener;

    void setOnPageLoadedListener(OnPageLoadedListener listener) {
        mPageLoadedListener = listener;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mPageLoadedListener != null)
            mPageLoadedListener.onPageLoaded();
        super.onPageFinished(view, url);
    }

    public interface OnPageLoadedListener {
        void onPageLoaded();
    }
}
