package com.loopme.gdpr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loopme.utils.AnimationUtils;

/**
 * Created by katerina on 5/7/18.
 */

public class GdprView extends WebView {
    private static final String BRIDGE = "GdprBridge";
    private GdprViewListener mPageLoadedListener;
    private static final String PRIVACY_PAGE = "privacy";
    private ProgressBar mProgressBar;

    public GdprView(Context context) {
        super(context);
        configure();
    }

    public GdprView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure();
    }

    public GdprView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure();
    }

    public GdprView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        configure();
    }


    public void setListener(GdprViewListener pageLoadedListener) {
        mPageLoadedListener = pageLoadedListener;
        addJavascriptInterface(new GdprBridge(mPageLoadedListener), BRIDGE);
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
        webSettings.setAppCacheEnabled(false);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setWebContentsDebuggingEnabled(true);
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mPageLoadedListener != null) {
                    mPageLoadedListener.onPageLoaded(GdprView.this.getContext());
                }
                enableProgressBar(AnimationUtils.AnimationType.OUT);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.contains(PRIVACY_PAGE)) {
                    enableProgressBar(AnimationUtils.AnimationType.IN);
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    return true;
                }
            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mProgressBar != null) {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });
        allowMixedContent();
    }

    private void enableProgressBar(AnimationUtils.AnimationType type) {
        if (mProgressBar != null) {
            mProgressBar.startAnimation(AnimationUtils.getAlphaAnimation(type, mProgressBar));
        }
    }

    private void allowMixedContent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
    }

    public void loadPage(String url) {
        loadUrl(url, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    if (canGoBack()) {
                        goBack();
                    }
                    break;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public void setProgressbar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public interface GdprViewListener extends GdprBridge.OnAnswerListener {
        void onPageLoaded(Context context);
    }
}
