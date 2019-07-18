package com.loopme.om;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;

import com.iab.omid.library.loopme.Omid;
import com.iab.omid.library.loopme.ScriptInjector;
import com.iab.omid.library.loopme.adsession.AdSession;
import com.iab.omid.library.loopme.adsession.AdSessionConfiguration;
import com.iab.omid.library.loopme.adsession.AdSessionContext;
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

    public static final int FINISH_AD_SESSION_DELAY_MILLIS = 1000;

    private static Partner partner;
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
            if (!Omid.isActive() &&
                    !Omid.activateWithOmidApiVersion(
                            Omid.getVersion(),
                            applicationContext.getApplicationContext())) {

                String err = "Failed to activate omid";
                Logging.out(LOG_TAG, err);
                sdkInitListener.onError(err);
                return;
            }
        } catch (Exception e) {
            String err = e.toString();
            Logging.out(LOG_TAG, err);
            sdkInitListener.onError(e.toString());
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

    // TODO. Prevent parallel calls or implement "initAsync" method for LoopMe SDK
    // TODO. and put om.js download operation there.
    private static void tryDownloadOMSDKJavaScriptAsync(final SDKInitListener sdkInitListener) {
        ExecutorHelper.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                final HttpRawResponse response = HttpUtils.doRequest(
                        BuildConfig.OM_SDK_JS_URL,
                        HttpUtils.Method.GET,
                        null);

                final byte[] rawBody = response.getBody();

                // Main thread callback.
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        if (response.getCode() != HttpURLConnection.HTTP_OK ||
                                rawBody == null ||
                                rawBody.length == 0) {
                            sdkInitListener.onError("OM SDK javascript download failed");
                            return;
                        }

                        omSDKJavaScript = new String(rawBody);

                        initialized = true;
                        sdkInitListener.onReady();
                    }
                });
            }
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
                        AdSession adSession = createAdSession(
                                false,
                                null,
                                verificationScriptResourceList,
                                Owner.NATIVE,
                                Owner.NATIVE,
                                false);

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

    public static AdSession createWebDisplayAdSession(WebView adView) {
        return createAdSession(
                true,
                adView,
                null,
                Owner.NATIVE,
                null,
                false);
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

    // TODO. Refactor.
    private static AdSession createAdSession(
            boolean webAd,
            WebView adView,
            List<VerificationScriptResource> verificationScriptResourceList,
            Owner impressionOwner,
            Owner videoEventsOwner,
            boolean isolateVerificationScripts) {

        try {
            String customReferenceData = "";
            AdSessionContext adSessionContext =
                    webAd
                            ? AdSessionContext.createHtmlAdSessionContext(
                            partner,
                            adView,
                            customReferenceData)
                            : AdSessionContext.createNativeAdSessionContext(
                            partner,
                            omSDKJavaScript,
                            verificationScriptResourceList,
                            customReferenceData);

            AdSessionConfiguration adSessionConfiguration =
                    AdSessionConfiguration.createAdSessionConfiguration(
                            impressionOwner,
                            videoEventsOwner,
                            isolateVerificationScripts);

            return AdSession.createAdSession(
                    adSessionConfiguration,
                    adSessionContext);

        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }

        return null;
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