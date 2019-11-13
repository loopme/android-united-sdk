package com.loopme.views.webclient;

import android.graphics.Bitmap;
import android.webkit.WebView;

import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.Logging;

/**
 * Custom WebViewClient for AdBrowserWebView which handles different url schemes.
 * Has listener to communicate with buttons on AdBrowserLayout.
 */
public class AdBrowserWebViewClient extends WebViewClientCompat {

    private static final String LOG_TAG = AdBrowserWebViewClient.class.getSimpleName();

    private Listener listener;

    public AdBrowserWebViewClient(Listener listener) {
        this.listener = listener;
    }

    @Override
    public final void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (listener != null)
            listener.onPageStarted();
    }

    @Override
    public final void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (listener != null)
            listener.onPageFinished(view.canGoBack());
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

        if (listener != null)
            listener.onReceivedError();

        Logging.out(LOG_TAG, "onReceivedError: " + description);
        LoopMeTracker.post("Wrong redirect " + failingUrl + " (" + description + ")");
    }

    @Override
    protected void onLeaveApp() {
        if (listener != null)
            listener.onLeaveApp();
    }

    @Override
    protected void onMarketVisit() {
        if (listener != null)
            listener.onMarketVisit();
    }

    public interface Listener {
        void onPageStarted();

        void onPageFinished(boolean canGoBack);

        void onReceivedError();

        void onLeaveApp();

        void onMarketVisit();
    }
}