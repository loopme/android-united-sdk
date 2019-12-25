package com.loopme.views.webclient;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.utils.ApiLevel;
import com.loopme.utils.Utils;

public class WebViewClientCompat extends WebViewClient {

    private static final String LOG_TAG = WebViewClientCompat.class.getSimpleName();

    private static final String TEL_SCHEME = "tel";
    private static final String GEO_SCHEME = "geo";
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    private static final String INTENT_SCHEME = "intent";
    private static final String MARKET_SCHEME = "market";
    private static final String YOUTUBE_SCHEME = "vnd.youtube";

    private static final String HEADER_PLAIN_TEXT = "plain/text";

    private static final String GEO_HOST = "maps.google.com";
    private static final String MARKET_HOST = "play.google.com";
    private static final String YOUTUBE_HOST1 = "www.youtube.com";
    private static final String YOUTUBE_HOST2 = "m.youtube.com";

    private static final String ID = "id";

    @Deprecated
    @Override
    public final boolean shouldOverrideUrlLoading(WebView view, String url) {
        return !ApiLevel.isApi24AndHigher() &&
                handleShouldOverrideUrlLoading(view, url);
    }

    @Override
    public final boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return ApiLevel.isApi24AndHigher() &&
                handleShouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    private boolean handleShouldOverrideUrlLoading(WebView webView, String url) {
        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception ex) {
            Logging.out(LOG_TAG, ex.toString());
        }

        Context context = webView.getContext();

        String scheme = uri == null ? "" : uri.getScheme();
        if (scheme == null) scheme = "";

        if (tryHandleUriSchemeActions(context, uri))
            // Abort current url loading process.
            return true;

        if (scheme.equalsIgnoreCase(HTTP_SCHEME) ||
                scheme.equalsIgnoreCase(HTTPS_SCHEME) ||
                canHandleCustomScheme(scheme))
            return shouldOverrideUrlLoadingCompat(webView, url);

        // Abort current url loading process.
        return true;
    }

    protected boolean canHandleCustomScheme(String scheme) {
        return false;
    }

    protected boolean shouldOverrideUrlLoadingCompat(WebView webView, String url) {
        return false;
    }

    private boolean tryHandleUriSchemeActions(Context context, Uri uri) {
        if (uri == null)
            return false;

        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme))
            return false;

        String url = uri.toString();
        String host = uri.getHost();

        if (scheme.equalsIgnoreCase(TEL_SCHEME))
            return tryStartTask(new Intent(Intent.ACTION_DIAL, uri), context);

        if (MailTo.isMailTo(url))
            return tryStartTask(createMailToIntent(url), context);

        if (scheme.equalsIgnoreCase(GEO_SCHEME))
            return tryStartTask(url, context);

        if (scheme.equalsIgnoreCase(YOUTUBE_SCHEME))
            return tryStartTask(url, context);

        if (scheme.equalsIgnoreCase(INTENT_SCHEME))
            return tryStartTaskFromIntentScheme(url, context);

        if (scheme.equalsIgnoreCase(MARKET_SCHEME))
            return tryStartTaskFromMarketScheme(url, context);

        if (scheme.equalsIgnoreCase(HTTP_SCHEME) || scheme.equalsIgnoreCase(HTTPS_SCHEME))
            return tryStartTaskFromHost(url, host, context);

        return false;
    }

    private static Intent createMailToIntent(String url) {
        MailTo mailTo = MailTo.parse(url);

        return new Intent(Intent.ACTION_SEND)
                .setType(HEADER_PLAIN_TEXT)
                .putExtra(Intent.EXTRA_EMAIL, mailTo.getTo().split(","))
                .putExtra(Intent.EXTRA_CC, mailTo.getCc())
                .putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject())
                .putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
    }

    private boolean tryStartTaskFromMarketScheme(String url, Context context) {
        try {
            boolean isTaskStarted = tryStartTask(Intent.parseUri(url, 0), context);

            if (!isTaskStarted)
                isTaskStarted = tryStartTask(
                        Constants.PLAY_STORE_URL + Uri.parse(url).getQueryParameter(ID),
                        context);

            if (isTaskStarted)
                onMarketVisit();

            return isTaskStarted;

        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean tryStartTaskFromIntentScheme(String url, Context context) {
        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (Exception ignored) {
            return false;
        }

        return tryStartTask(intent, context) ||
                tryStartTask(Constants.PLAY_STORE_URL + intent.getPackage(), context);
    }

    private boolean tryStartTaskFromHost(String url, String host, Context context) {
        if (TextUtils.isEmpty(host))
            return false;

        if (host.equalsIgnoreCase(GEO_HOST) ||
                host.equalsIgnoreCase(MARKET_HOST) ||
                host.equalsIgnoreCase(YOUTUBE_HOST1) ||
                host.equalsIgnoreCase(YOUTUBE_HOST2)) {
            return tryStartTask(url, context);
        }

        return false;
    }

    private boolean tryStartTask(String url, Context context) {
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        } catch (Exception ignored) {
            return false;
        }

        return tryStartTask(intent, context);
    }

    private boolean tryStartTask(Intent intent, Context context) {
        try {
            if (Utils.isActivityResolved(intent, context)) {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                onLeaveApp();
                return true;
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    protected void onMarketVisit() {
    }

    protected void onLeaveApp() {
    }
}
