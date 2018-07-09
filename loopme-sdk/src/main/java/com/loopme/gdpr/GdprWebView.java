package com.loopme.gdpr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.utils.AnimationUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by katerina on 5/7/18.
 */

public class GdprWebView extends WebView implements Observer {

    private static final String BRIDGE = "GdprBridge";
    private static final String LOOPME_SCHEMA = "loopme://popup/";
    private static final String READY_EVENT = "ready";
    private static final String CLOSE_EVENT = "close";
    private GdprViewListener mPageLoadedListener;
    private static final String PRIVACY_PAGE = "privacy";
    private ProgressBar mProgressBar;
    private Context mContext;
    private Timers mTimers;
    private Handler mHandler = new Handler((Looper.getMainLooper()));

    public GdprWebView(Context context) {
        super(context);
        configure(context);
    }

    public GdprWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure(context);
    }

    public GdprWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure(context);
    }

    public GdprWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        configure(context);
    }


    public void setListener(GdprViewListener pageLoadedListener) {
        mPageLoadedListener = pageLoadedListener;
        addJavascriptInterface(new GdprBridge(mPageLoadedListener), BRIDGE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configure(Context context) {
        mContext = context;

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
        configureChromeClient();
        configureWebClient();
        allowMixedContent();
    }

    private void configureWebClient() {
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                startGdprPageLoadedTimer();
                onPageLoaded();
                enableProgressBar(AnimationUtils.AnimationType.OUT);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equalsIgnoreCase(LOOPME_SCHEMA + CLOSE_EVENT)) {
                    onClose();
                }
                if (url.equalsIgnoreCase(LOOPME_SCHEMA + READY_EVENT)) {
                    stopGdprPageLoadedTimer();
                }
                if (!TextUtils.isEmpty(url) && url.contains(PRIVACY_PAGE)) {
                    enableProgressBar(AnimationUtils.AnimationType.IN);
                    return super.shouldOverrideUrlLoading(view, url);
                } else {
                    return true;
                }
            }
        });
    }

    private void configureChromeClient() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (mProgressBar != null) {
                    mProgressBar.setProgress(newProgress);
                }
            }
        });
    }

    private void startGdprPageLoadedTimer() {
        mTimers = new Timers(this);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTimers.startTimer(TimersType.REQUEST_TIMER);
            }
        });
    }

    protected void stopGdprPageLoadedTimer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTimers != null) {
                    mTimers.stopTimer(TimersType.GDPR_PAGE_LOADED_TIMER);
                }
            }
        });
    }

    private void onPageLoaded() {
        if (mPageLoadedListener != null) {
            mPageLoadedListener.onPageLoaded(mContext);
        }
    }

    private void onClose() {
        stopGdprPageLoadedTimer();
        if (mPageLoadedListener != null) {
            mPageLoadedListener.onClose();
        }

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
        loadUrl(url);
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

    @Override
    public void update(Observable observable, Object arg) {
        if (observable != null && observable instanceof Timers
                && arg != null && arg instanceof TimersType) {
            if ((arg == TimersType.GDPR_PAGE_LOADED_TIMER)) {
                onRequestTimeout();
            }
        }
    }

    private void onRequestTimeout() {
        onClose();
    }

    public interface GdprViewListener extends GdprBridge.OnCloseListener {
        void onPageLoaded(Context context);

        void onClose();
    }

}
