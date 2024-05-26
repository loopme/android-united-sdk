package com.loopme.om;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.iab.omid.library.loopme.Omid;
import com.iab.omid.library.loopme.ScriptInjector;
import com.iab.omid.library.loopme.adsession.AdSession;
import com.iab.omid.library.loopme.adsession.AdSessionConfiguration;
import com.iab.omid.library.loopme.adsession.AdSessionContext;
import com.iab.omid.library.loopme.adsession.CreativeType;
import com.iab.omid.library.loopme.adsession.ImpressionType;
import com.iab.omid.library.loopme.adsession.Owner;
import com.iab.omid.library.loopme.adsession.Partner;
import com.iab.omid.library.loopme.adsession.VerificationScriptResource;
import com.loopme.BuildConfig;
import com.loopme.Logging;
import com.loopme.network.HttpRawResponse;
import com.loopme.network.HttpUtils;
import com.loopme.utils.ExecutorHelper;

import java.net.HttpURLConnection;
import java.util.List;

public final class OmidHelper {

    private static final String LOG_TAG = OmidHelper.class.getSimpleName();

    private static final String CONTENT_URL = null;
    private static final String CUSTOM_REFERENCE_DATA = "";

    public static final int FINISH_AD_SESSION_DELAY_MILLIS = 1000;

    private static Partner partner;

    public static String getPartnerName() {
        return partner == null ? "" : partner.getName();
    }

    public static String getPartnerVersion() {
        return partner == null ? "" : partner.getVersion();
    }

    private static String omSDKJavaScript;

    private static boolean initialized;

    private OmidHelper() {
    }

    public interface SDKInitListener {
        void onReady();

        void onError(String error);
    }

    public static boolean sdkInitialized() {
        return initialized;
    }

    @MainThread
    public static void tryInitOmidAsync(@NonNull Context applicationContext,
                                        @NonNull SDKInitListener sdkInitListener) {

        if (sdkInitialized()) {
            sdkInitListener.onReady();
            return;
        }

        // 1. Omid activation.
        try {
            Omid.activate(applicationContext.getApplicationContext());
        } catch (Exception e) {
            String err = e.toString();
            Logging.out(LOG_TAG, err);
            sdkInitListener.onError(err);
            return;
        }

        // 2. Integration identification.
        if (partner == null) {
            try {
                partner = Partner.createPartner(
                        BuildConfig.OM_SDK_PARTNER, BuildConfig.VERSION_NAME);
            } catch (Exception e) {
                String err = e.toString();
                Logging.out(LOG_TAG, err);
                sdkInitListener.onError(err);
                return;
            }
        }

        // 3. Fetching of OM SDK JS library.
        if (TextUtils.isEmpty(omSDKJavaScript)) {
            tryDownloadOMSDKJavaScriptAsync(sdkInitListener);
        } else {
            initialized = true;
            sdkInitListener.onReady();
        }
    }

    // TODO. Prevent parallel calls or implement "initAsync" method for LoopMe SDK.
    private static void tryDownloadOMSDKJavaScriptAsync(final SDKInitListener sdkInitListener) {
        ExecutorHelper.getExecutor().submit(() -> {
            final HttpRawResponse response = HttpUtils.doRequest(
                    BuildConfig.OM_SDK_JS_URL,
                    HttpUtils.Method.GET,
                    null);

            final byte[] rawBody = response.getBody();

            // Main thread callback.
            new Handler(Looper.getMainLooper()).post(() -> {
                if (response.getCode() != HttpURLConnection.HTTP_OK ||
                        rawBody == null ||
                        rawBody.length == 0) {
                    sdkInitListener.onError("OM SDK javascript download failed");
                    return;
                }

                omSDKJavaScript = new String(rawBody);

                initialized = true;
                sdkInitListener.onReady();
            });
        });
    }

    // TODO. Remove init call.
    public static void createNativeVideoAdSessionAsync(
            Context applicationContext,
            final List<VerificationScriptResource> verificationScriptResourceList,
            final AdSessionListener listener) {

        tryInitOmidAsync(
                applicationContext,
                new SDKInitListener() {
                    @Override
                    public void onReady() {
                        AdSession adSession = createAdSession(verificationScriptResourceList);
                        if (adSession == null)
                            listener.onError("Couldn't create adSession");
                        else
                            listener.onReady(adSession);
                    }

                    @Override
                    public void onError(String error) {
                        listener.onError(error);
                    }
                });
    }

    public static AdSession createWebDisplayAdSession(WebView webView) {
        return createAdSession(webView);
    }

    // TODO. Remove init call.
    public static void injectScriptContentIntoHtmlAsync(
            Context applicationContext,
            final String html,
            final ScriptInjectListener listener) {

        tryInitOmidAsync(
                applicationContext,
                new SDKInitListener() {
                    @Override
                    public void onReady() {
                        try {
                            listener.onReady(
                                    ScriptInjector.injectScriptContentIntoHtml(
                                            omSDKJavaScript,
                                            html));

                        } catch (Exception e) {
                            String err = e.toString();
                            Logging.out(LOG_TAG, err);
                            listener.onError(err);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        listener.onError(error);
                    }
                });
    }

    private static AdSession createAdSession(WebView webView) {
        try {
            return createAdSession(
                    CreativeType.HTML_DISPLAY,
                    Owner.NONE,
                    AdSessionContext.createHtmlAdSessionContext(
                            partner,
                            webView,
                            CONTENT_URL,
                            CUSTOM_REFERENCE_DATA));
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
        return null;
    }

    private static AdSession createAdSession(List<VerificationScriptResource> resources) {
        try {
            return createAdSession(
                    CreativeType.VIDEO,
                    Owner.NATIVE,
                    AdSessionContext.createNativeAdSessionContext(
                            partner,
                            omSDKJavaScript,
                            resources,
                            CONTENT_URL,
                            CUSTOM_REFERENCE_DATA));
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
        return null;
    }

    private static AdSession createAdSession(
            CreativeType creativeType,
            Owner mediaEventsOwner,
            AdSessionContext adSessionContext
    ) {
        AdSessionConfiguration adSessionConfiguration =
                AdSessionConfiguration.createAdSessionConfiguration(
                        creativeType,
                        ImpressionType.BEGIN_TO_RENDER,
                        Owner.NATIVE,
                        mediaEventsOwner,
                        false);

        return AdSession.createAdSession(
                adSessionConfiguration,
                adSessionContext);
    }

    public interface AdSessionListener {
        void onReady(AdSession adSession);

        void onError(String error);
    }

    public interface ScriptInjectListener {
        void onReady(String html);

        void onError(String error);
    }
}