package com.loopme.views.webclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.Constants;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.Logging;
import com.loopme.utils.Utils;

import java.net.URISyntaxException;

/**
 * Custom WebViewClient for AdBrowserWebView which handles different url schemes.
 * Has listener to communicate with buttons on AdBrowserLayout.
 */
public class AdBrowserWebViewClient extends WebViewClient {

    private static final String LOG_TAG = AdBrowserWebViewClient.class.getSimpleName();
    private static final String HEADER_PLAIN_TEXT = "plain/text";

    private static final String TEL_SCHEME = "tel";
    private static final String GEO_SCHEME = "geo";
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    private static final String INTENT_SCHEME = "intent";
    private static final String MARKET_SCHEME = "market";
    private static final String MAILTO_SCHEME = "mailto";
    private static final String YOUTUBE_SCHEME = "vnd.youtube";

    private static final String GEO_HOST = "maps.google.com";
    private static final String MARKET_HOST = "play.google.com";
    private static final String YOUTUBE_HOST1 = "www.youtube.com";
    private static final String YOUTUBE_HOST2 = "m.youtube.com";

    private static final String ID = "id";
    private static final String EMPTY_STRING = "";
    private static final String MAIL_TO = "mailto:";

    private Listener mListener;
    private Context mContext;


    public AdBrowserWebViewClient(Context context, Listener listener) {
        if (listener != null && context != null) {
            mContext = context;
            mListener = listener;
        } else {
            Logging.out(LOG_TAG, "Error: Listener or context is null");
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logging.out(LOG_TAG, "shouldOverrideUrlLoading url=" + url);

        Uri uri = parseUri(url);
        if (uri == null) {
            LoopMeTracker.post("Wrong redirect (" + url + ")");
            return false;
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (TextUtils.isEmpty(scheme)) {
            LoopMeTracker.post("Wrong redirect (" + url + ")");
            return false;
        }

        if (scheme.equalsIgnoreCase(TEL_SCHEME)) {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            resolveAndStartActivity(intent);

        } else if (scheme.equalsIgnoreCase(MAILTO_SCHEME)) {
            Intent intent = createMailToIntent(url, uri);
            resolveAndStartActivity(intent);

        } else if (scheme.equalsIgnoreCase(GEO_SCHEME)) {
            Intent searchAddress = new Intent(Intent.ACTION_VIEW, uri);
            resolveAndStartActivity(searchAddress);

        } else if (scheme.equalsIgnoreCase(YOUTUBE_SCHEME)) {
            leaveApp(url);

        } else if (scheme.equalsIgnoreCase(HTTP_SCHEME) || scheme.equalsIgnoreCase(HTTPS_SCHEME)) {
            return checkHost(url, host);

        } else if (scheme.equalsIgnoreCase(INTENT_SCHEME)) {
            handleIntentScheme(url);

        } else if (scheme.equalsIgnoreCase(MARKET_SCHEME)) {
            handleMarketScheme(url);

        } else {
            return true;
        }

        return true;
    }

    private Uri parseUri(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                return Uri.parse(url);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Intent createMailToIntent(String url, Uri uri) {
        if (!TextUtils.isEmpty(url) && uri != null) {
            url = url.replaceFirst(MAIL_TO, EMPTY_STRING);
            url = url.trim();
            Intent intent = new Intent(Intent.ACTION_SEND, uri);
            intent.setType(HEADER_PLAIN_TEXT).putExtra(Intent.EXTRA_EMAIL, new String[]{url});
            return intent;
        } else {
            return new Intent();
        }
    }

    /**
     * Checks host
     *
     * @param url  - full url
     * @param host - host from url
     * @return true - if param host equals with geo, market or youtube host
     * false - otherwise
     */
    private boolean checkHost(String url, String host) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(host)) {
            return false;
        }
        if (host.equalsIgnoreCase(GEO_HOST)) {
            Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            resolveAndStartActivity(searchAddress);
        } else if (host.equalsIgnoreCase(MARKET_HOST)
                || host.equalsIgnoreCase(YOUTUBE_HOST1)
                || host.equalsIgnoreCase(YOUTUBE_HOST2)) {
            leaveApp(url);
        } else {
            return false;
        }
        return true;
    }

    private void handleMarketScheme(String url) {
        try {
            Intent intent = Intent.parseUri(url, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!resolveAndStartActivity(intent)) {
                Uri uri = Uri.parse(url);
                String id = uri.getQueryParameter(ID);
                url = Constants.PLAY_STORE_URL + id;
                leaveApp(url);
            }
            onMarketOpen();
        } catch (Exception e) {
            e.printStackTrace();
            onReceiveError();
            LoopMeTracker.post("Wrong redirect (" + url + ")");
        }
    }

    private void handleIntentScheme(String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!resolveAndStartActivity(intent)) {
                url = Constants.PLAY_STORE_URL + intent.getPackage();
                leaveApp(url);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            onReceiveError();
            LoopMeTracker.post("Wrong redirect (" + url + ")");
        }
    }

    private void leaveApp(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        resolveAndStartActivity(intent);
        onLeaveApp();
    }

    private boolean resolveAndStartActivity(Intent intent) {
        if (Utils.isActivityResolved(intent, mContext)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else {
            onReceiveError();
        }
        return false;
    }

    private void startActivity(Intent intent) {
        if (mContext != null) {
            mContext.startActivity(intent);
        }
    }

    @Override
    public final void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        onPageStarted();
    }

    @Override
    public final void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        onPageFinished(view.canGoBack());
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        onReceiveError();
        LoopMeTracker.post("Wrong redirect " + failingUrl + " (" + description + ")");
        Logging.out(LOG_TAG, "onReceivedError: " + description);
    }

    private void onReceiveError() {
        if (mListener != null) {
            mListener.onReceiveError();
        }
    }

    private void onPageFinished(boolean canGoBack) {
        if (mListener != null) {
            mListener.onPageFinished(canGoBack);
        }
    }

    private void onPageStarted() {
        if (mListener != null) {
            mListener.onPageStarted();
        }
    }

    private void onLeaveApp() {
        if (mListener != null) {
            mListener.onLeaveApp();
        }
    }

    private void onMarketOpen() {
        if (mListener != null) {
            mListener.onMarketVisit();
        }
    }

    public interface Listener {
        void onPageStarted();

        void onPageFinished(boolean canGoBack);

        void onReceiveError();

        void onLeaveApp();

        void onMarketVisit();
    }

}
