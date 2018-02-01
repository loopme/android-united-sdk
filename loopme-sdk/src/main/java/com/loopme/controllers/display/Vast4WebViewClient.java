package com.loopme.controllers.display;

import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.Logging;

import java.net.URI;
import java.net.URISyntaxException;

public class Vast4WebViewClient extends WebViewClient {
    private static final String LOG_TAG = Vast4WebViewClient.class.getSimpleName();
    private static final String VAST4 = "vast4";
    private static final String LOAD_FAIL = "jsLoadFail";
    private static final String LOAD_SUCCESS = "jsLoadSuccess";
    private Vast4Tracker.OnAdVerificationListener mVerificationListener;
    private OnPageLoadedListener mPageLoadedListener;

    public Vast4WebViewClient(Vast4Tracker.OnAdVerificationListener listener) {
        mVerificationListener = listener;
    }

    public void setOnPageLoadedListener(OnPageLoadedListener listener) {
        mPageLoadedListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logging.out(LOG_TAG, "Ad verification loading result: " + url);
        URI redirect = getURI(url);
        if (redirect != null) {
            String protocol = redirect.getScheme();
            if (TextUtils.equals(protocol, VAST4)) {
                String command = redirect.getHost();
                handleVast4Command(command, redirect);
            }
        } else {
            Logging.out(LOG_TAG, "received URI = null");
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        onPageLoaded();
        super.onPageFinished(view, url);
    }

    private void handleVast4Command(String command, URI redirect) {
        switch (command) {
            case LOAD_SUCCESS: {
                onVerificationJsLoaded();
                break;
            }
            case LOAD_FAIL: {
                String source = redirect.getPath();
                onVerificationJsFailed(source);
                break;
            }
        }
    }

    private URI getURI(String url) {
        URI redirect = null;
        try {
            redirect = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Logging.out(LOG_TAG, e.getMessage());
        }
        return redirect;
    }

    private void onVerificationJsLoaded() {
        if (mVerificationListener != null) {
            mVerificationListener.onVerificationJsLoaded();
        }
    }

    private void onVerificationJsFailed(String message) {
        if (mVerificationListener != null) {
            mVerificationListener.onVerificationJsFailed(message);
        }
    }

    private void onPageLoaded() {
        if (mPageLoadedListener != null) {
            mPageLoadedListener.onPageLoaded();
        }
    }

    public interface OnPageLoadedListener {
        void onPageLoaded();
    }
}
