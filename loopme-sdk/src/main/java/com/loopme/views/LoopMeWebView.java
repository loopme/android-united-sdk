package com.loopme.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.bridges.BridgeCommandBuilder;
import com.loopme.views.webclient.AdViewChromeClient;

public class LoopMeWebView extends WebView {
    private static final String LOG_TAG = LoopMeWebView.class.getSimpleName();
    private OnPageLoadedCallback mCallback;
    private static boolean sDeadlockCleared = false;
    protected Constants.WebviewState mViewState = Constants.WebviewState.CLOSED;

    public LoopMeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopMeWebView(Context context) {
        super(context);
        if (!sDeadlockCleared) {
            resolveWebViewDeadlock(getContext());
            sDeadlockCleared = true;
        }
        configureWebSettings();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setDebugConfig();
    }

    private void setDebugConfig() {
        if (BuildConfig.DEBUG_MODE || Constants.sDebugMode) {
            getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            clearCache(true);
            setWebContentsDebuggingEnabled(true);
        }
    }

    public void loadHtml(String html) {
        loadDataWithBaseURL(Constants.BASE_URL, html, Constants.MIME_TYPE_TEXT_HTML, Constants.UTF_8, null);
    }

    public void setDefaultWebChromeClient() {
        setWebChromeClient(new AdViewChromeClient());
    }

    public VpaidWebViewClient getVpaidWebViewClient() {
        return new VpaidWebViewClient();
    }

    private class VpaidWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            onPageLoaded();
            Logging.out(LOG_TAG, "Init webView done");
        }

    }

    private void onPageLoaded() {
        if (mCallback != null) {
            mCallback.onPageLoaded();
        }
    }

    public void setOnPageLoadedCallback(OnPageLoadedCallback callback) {
        mCallback = callback;
    }

    public interface OnPageLoadedCallback {
        void onPageLoaded();
    }

    public void destroy() {
        super.destroy();
        stopLoading();
        clearCache(true);
        clearHistory();
        setWebViewClient(null);
        setWebChromeClient(null);
        loadCommand("about:blank");
        removeChildes();
        removeAllViews();
    }

    private void removeChildes() {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeAllViews();
        }
    }

    protected void setWebViewState(String sdkPrefix, Constants.WebviewState webViewStatestate) {
        if (mViewState != webViewStatestate) {
            mViewState = webViewStatestate;
            String command = BridgeCommandBuilder.webviewState(sdkPrefix, mViewState);
            Logging.out(LOG_TAG, "setWebViewState() : " + webViewStatestate.name());
            loadCommand(command);
        }
    }

    protected void loadCommand(String command) {
        Logging.out(LOG_TAG, command);
        loadUrl(command);
    }

    private void resolveWebViewDeadlock(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            final WebView webView = new WebView(context.getApplicationContext());
            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.loadDataWithBaseURL(null, "", "text/html", Constants.UTF_8, null);

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = 1;
            params.height = 1;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            params.format = PixelFormat.TRANSPARENT;
            params.gravity = Gravity.START | Gravity.TOP;

            final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(webView, params);
        }
    }
}
