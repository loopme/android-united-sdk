package com.loopme.views.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import com.loopme.R;
import com.loopme.Constants;
import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.utils.Utils;
import com.loopme.Logging;
import com.loopme.views.webclient.AdBrowserWebViewClient;
import com.loopme.views.LoopMeWebView;

/**
 * Browser Activity. Starts when ad click happened.
 */
public final class AdBrowserActivity extends Activity implements OnClickListener {

    private static final String LOG_TAG = AdBrowserActivity.class.getSimpleName();
    private String mRedirectUrl;
    private boolean isNativeBrowserVisited;
    private View mProgressBar;
    private LoopMeAd mLoopMeAd;
    private ImageView mBackButton;
    private ImageView mCloseButton;
    private ImageView mNativeButton;
    private ImageView mRefreshButton;
    private LoopMeWebView mLoopMeWebView;

    @Override
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!isValidExtras()) {
            finish();
        }
        requestSystemFlags();
        setContentView(R.layout.ad_browser_layout);
        initViews();
        setButtonsListeners();
        restoreWebViewState(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNativeBrowserVisited) {
            finish();
        }
        Logging.out(LOG_TAG, "resume");
        setProgressVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        Logging.out(LOG_TAG, " onDestroy");
        destroyWebView();
        super.onDestroy();
    }

    private void initViews() {
        mBackButton = (ImageView) findViewById(R.id.ad_browser_back_button);
        mCloseButton = (ImageView) findViewById(R.id.ad_browser_close_button);
        mProgressBar = findViewById(R.id.ad_browser_progress_button);
        mNativeButton = (ImageView) findViewById(R.id.ad_browser_native_button);
        mRefreshButton = (ImageView) findViewById(R.id.ad_browser_refresh_button);
        mLoopMeWebView = (LoopMeWebView) findViewById(R.id.loopme_webview);
        configureWebView();
    }

    private void restoreWebViewState(Bundle bundle) {
        if (mLoopMeWebView != null) {
            if (bundle != null) {
                mLoopMeWebView.restoreState(bundle);
            } else {
                mLoopMeWebView.loadUrl(mRedirectUrl);
            }
        }
    }

    private void requestSystemFlags() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }

    private boolean isValidExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            mRedirectUrl = extras.getString(Constants.EXTRA_URL);
            mLoopMeAd = LoopMeAdHolder.getAd(getIntent());
            return mLoopMeAd != null && !TextUtils.isEmpty(mRedirectUrl);
        }
        return false;
    }

    private void destroyWebView() {
        if (mLoopMeWebView != null) {
            mLoopMeWebView.loadUrl("about:blank");
            mLoopMeWebView = null;
        }
    }

    private void configureWebView() {
        if (mLoopMeWebView != null) {
            AdBrowserWebViewClient.Listener webClientListener = initAdBrowserClientListener();
            AdBrowserWebViewClient client = new AdBrowserWebViewClient(this, webClientListener);
            mLoopMeWebView.setWebViewClient(client);
            WebView.setWebContentsDebuggingEnabled(true);
            mLoopMeWebView.getSettings().setBuiltInZoomControls(false);
        }
    }

    private void onBackButtonClicked() {
        if (webViewCanGoBack()) {
            setProgressVisibility(View.VISIBLE);
            webViewGoBack();
        }
    }

    private void onCloseButtonClicked() {
        finish();
    }

    private void onRefreshButtonClicked() {
        setProgressVisibility(View.VISIBLE);
        reloadWebView();
    }

    private void reloadWebView() {
        if (mLoopMeWebView != null) {
            mLoopMeWebView.reload();
        }
    }

    private void onNativeButtonClicked() {
        String url = getWebViewUrl();
        if (!TextUtils.isEmpty(url)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            launchActivity(browserIntent);
        }
    }

    private void launchActivity(Intent browserIntent) {
        if (Utils.isActivityResolved(browserIntent, this)) {
            startActivity(browserIntent);
            onAdLeaveApp();
            isNativeBrowserVisited = true;
        }
    }

    private String getWebViewUrl() {
        if (mLoopMeWebView != null) {
            return mLoopMeWebView.getUrl();
        } else {
            return null;
        }
    }

    private void webViewGoBack() {
        if (mLoopMeWebView != null) {
            mLoopMeWebView.goBack();
        }
    }

    private boolean webViewCanGoBack() {
        return mLoopMeWebView != null && mLoopMeWebView.canGoBack();
    }

    private void saveWebViewState(Bundle outState) {
        if (mLoopMeWebView != null) {
            mLoopMeWebView.saveState(outState);
        }
    }

    private void onPageLoaded(boolean canGoBack) {
        setProgressVisibility(View.INVISIBLE);
        if (canGoBack) {
            setBackButtonEnable(true);
        } else {
            setBackButtonEnable(false);
        }
    }

    private void setBackButtonEnable(boolean enable) {
        if (mBackButton != null) {
            mBackButton.setEnabled(enable);
        }
    }

    private void setProgressVisibility(int visibility) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(visibility);
        }
    }

    private void onAdLeaveApp() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdLeaveApp();
        }
    }

    private void setButtonsListeners() {
        mBackButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);
        mRefreshButton.setOnClickListener(this);
        mNativeButton.setOnClickListener(this);
    }

    private AdBrowserWebViewClient.Listener initAdBrowserClientListener() {
        return new AdBrowserWebViewClient.Listener() {

            @Override
            public void onReceiveError() {
                finish();
            }

            @Override
            public void onPageStarted() {
                setProgressVisibility(View.VISIBLE);
            }

            @Override
            @SuppressLint("NewApi")
            public void onPageFinished(boolean canGoBack) {
                onPageLoaded(canGoBack);
            }

            @Override
            public void onLeaveApp() {
                onAdLeaveApp();
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveWebViewState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webViewCanGoBack()) {
            webViewGoBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ad_browser_close_button) {
            onCloseButtonClicked();
        } else if (i == R.id.ad_browser_refresh_button) {
            onRefreshButtonClicked();
        } else if (i == R.id.ad_browser_native_button) {
            onNativeButtonClicked();
        } else if (i == R.id.ad_browser_back_button) {
            onBackButtonClicked();
        }
    }
}
