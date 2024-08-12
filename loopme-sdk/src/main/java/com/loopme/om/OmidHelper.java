package com.loopme.om;

import android.content.Context;
import android.webkit.WebView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.iab.omid.library.loopme.Omid;
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
import com.loopme.utils.FileUtils;

import java.util.List;

public final class OmidHelper {

    private static final String LOG_TAG = OmidHelper.class.getSimpleName();

    public static final int FINISH_AD_SESSION_DELAY_MILLIS = 1000;

    private static final String CONTENT_URL = null;
    private static final String CUSTOM_REFERENCE_DATA = "";

    private static Partner partner;
    public static String getPartnerName() { return partner == null ? "" : partner.getName(); }
    public static String getPartnerVersion() { return partner == null ? "" : partner.getVersion(); }

    private static String omSDKJavaScript;
    public static String getOmSDKJavaScript() { return omSDKJavaScript; }

    public static boolean isInitialized() { return isInitialized; }
    private static boolean isInitialized;

    private OmidHelper() { }

    @MainThread
    public static void init(@NonNull Context applicationContext) {
        if (isInitialized) {
            return;
        }

        try {
            // 1. Omid activation.
            Omid.activate(applicationContext.getApplicationContext());
            // 2. Integration identification.
            partner = Partner.createPartner(BuildConfig.OM_SDK_PARTNER, BuildConfig.VERSION_NAME);
            // 3. Fetching of OM SDK JS library.
            omSDKJavaScript = FileUtils.loadAssetFileAsString(applicationContext, "omsdk-v1.js");
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
            return;
        }

        isInitialized = true;
    }

    /**
     * AdSession.createAdSession: <a href="https://docs.iabtechlab.com/omsdk-1.4/android/com/iab/omid/library/adsession/AdSession.html#createAdSession-com.iab.omid.library.adsession.AdSessionConfiguration-com.iab.omid.library.adsession.AdSessionContext-">link</a>
     * AdSessionConfiguration.createAdSessionConfiguration: <a href="https://docs.iabtechlab.com/omsdk-1.4/android/com/iab/omid/library/adsession/AdSessionConfiguration.html#createAdSessionConfiguration-CreativeType-ImpressionType-Owner-Owner-boolean-">link</a>
     * AdSessionContext.createHtmlAdSessionContext: <a href="https://docs.iabtechlab.com/omsdk-1.4/android/com/iab/omid/library/adsession/AdSessionContext.html#createHtmlAdSessionContext-com.iab.omid.library.adsession.Partner-WebView-java.lang.String-java.lang.String-">link</a>
     * @param webView WebView
     * @return AdSession
     * @throws IllegalArgumentException Check links above for more details.
     * @throws IllegalStateException Check links above for more details.
     */
    public static AdSession createAdSessionHtml(WebView webView)
        throws IllegalArgumentException, IllegalStateException
    {
        return AdSession.createAdSession(
            AdSessionConfiguration.createAdSessionConfiguration(
                CreativeType.HTML_DISPLAY, ImpressionType.BEGIN_TO_RENDER, Owner.NATIVE, Owner.NONE, false
            ),
            AdSessionContext.createHtmlAdSessionContext(
                partner, webView, CONTENT_URL, CUSTOM_REFERENCE_DATA
            )
        );
    }

    /**
     * AdSession.createAdSession: <a href="https://docs.iabtechlab.com/omsdk-1.4/android/com/iab/omid/library/adsession/AdSession.html#createAdSession-com.iab.omid.library.adsession.AdSessionConfiguration-com.iab.omid.library.adsession.AdSessionContext-">link</a>
     * AdSessionConfiguration.createAdSessionConfiguration: <a href="https://docs.iabtechlab.com/omsdk-1.4/android/com/iab/omid/library/adsession/AdSessionConfiguration.html#createAdSessionConfiguration-CreativeType-ImpressionType-Owner-Owner-boolean-">link</a>
     * AdSessionContext.createNativeAdSessionContext: <a href="https://docs.iabtechlab.com/omsdk-1.4/android/com/iab/omid/library/adsession/AdSessionContext.html#createNativeAdSessionContext-com.iab.omid.library.adsession.Partner-java.lang.String-java.util.List-java.lang.String-java.lang.String-">link</a>
     * @param resources List<VerificationScriptResource>
     * @return AdSession
     * @throws IllegalArgumentException Check links above for more details.
     * @throws IllegalStateException Check links above for more details.
     */
    public static AdSession createAdSession(List<VerificationScriptResource> resources)
        throws IllegalArgumentException, IllegalStateException
    {
        return AdSession.createAdSession(
            AdSessionConfiguration.createAdSessionConfiguration(
                CreativeType.VIDEO, ImpressionType.BEGIN_TO_RENDER, Owner.NATIVE, Owner.NATIVE, false
            ),
            AdSessionContext.createNativeAdSessionContext(
                partner, omSDKJavaScript, resources, CONTENT_URL, CUSTOM_REFERENCE_DATA
            )
        );
    }
}
