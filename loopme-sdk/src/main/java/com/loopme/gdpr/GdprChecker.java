package com.loopme.gdpr;

import android.content.Context;

import com.loopme.IABPreferences;

/**
 * Created by katerina on 4/27/18.
 */

// TODO. WeakReference for listener field if used somewhere except LoopMeSdk.
public class GdprChecker {
    private static final String LOG_TAG = GdprChecker.class.getSimpleName();
    private static GdprChecker instance;
    private static boolean checkedAtLeastOnce;
    private Context appContext;
    private PublisherConsent publisherConsent;
    private boolean destroyed;

    private GdprChecker(Context context, PublisherConsent consent) {
        appContext = context;
        publisherConsent = consent;
    }

    public static class PublisherConsent {
        private String consent;
        public PublisherConsent(String pConsent) { consent = pConsent; }
        public PublisherConsent(Boolean pConsent){ setConsent(pConsent); }
        public void setConsent(String pConsent) { consent = pConsent; }
        public void setConsent(boolean pConsent) { consent = pConsent ? "1" : "0"; }
        public String getConsent() { return consent; }
    }

    public static void start (Context context, PublisherConsent publisherConsent) {
        if (checkedAtLeastOnce) return;
        if (instance != null) instance.destroy();
        instance = new GdprChecker(context.getApplicationContext(), publisherConsent);
        instance.check();
    }

    private void setGdprState(String isAccepted, ConsentType consentType) {
        if (destroyed) return;
        IABPreferences.getInstance(appContext).setGdprState(isAccepted, consentType);
        checkedAtLeastOnce = true;
        destroy();
    }

    private void setGdprState(boolean isAccepted, ConsentType consentType) {
        setGdprState(isAccepted ? "1" : "0", consentType);
    }

    private void destroy() {
        destroyed = true;
        instance = null;
        appContext = null;
        publisherConsent = null;
    }

    private void check() {
        if (destroyed) return;
        if (IABPreferences.getInstance(appContext).isIabTcfCmpSdkPresent())
            setGdprState(true, ConsentType.PUBLISHER);
        else if (publisherConsent != null)
            setGdprState(publisherConsent.getConsent(), ConsentType.PUBLISHER);

        DntFetcher.start(appContext, (isLimited, advId) -> {
            if (isLimited) setGdprState(false, ConsentType.USER_RESTRICTED);
        });
    }
}
