package com.loopme.gdpr;

import android.app.Activity;
import android.content.Context;

import com.loopme.IABPreferences;

import java.lang.ref.WeakReference;

/**
 * Created by katerina on 4/27/18.
 */

// TODO. WeakReference for listener field if used somewhere except LoopMeSdk.
public class GdprChecker implements DntFetcher.OnDntFetcherListener {
    private static final String LOG_TAG = GdprChecker.class.getSimpleName();
    private static GdprChecker instance;
    private static boolean checkedAtLeastOnce;
    private WeakReference<Activity> activity;
    private Context appContext;
    private PublisherConsent publisherConsent;
    private String advId;
    private Listener listener;
    private boolean destroyed;

    private GdprChecker(Activity activity, PublisherConsent publisherConsent, Listener listener) {
        this.activity = new WeakReference<>(activity);
        this.appContext = activity.getApplicationContext();
        this.publisherConsent = publisherConsent;
        this.listener = listener;
    }

    public static class PublisherConsent {
        private String consent;
        public PublisherConsent(String consent) {
            this.consent = consent;
        }
        public PublisherConsent(Boolean consent){
            setConsent(consent);
        }

        public String getConsent() {
            return consent;
        }
        /**
         * if you want to send TCF consent
         */
        public void setConsent(String consent) {
            this.consent = consent;
        }
        public void setConsent(boolean consent) {
            this.consent = consent ? "1" : "0";
        }
    }

    public interface Listener { void onGdprChecked(); }
    public static boolean checkedAtLeastOnce() {
        return checkedAtLeastOnce;
    }

    public static void start (Activity activity, PublisherConsent publisherConsent, Listener listener) {
        if (checkedAtLeastOnce()) {
            listener.onGdprChecked();
            return;
        }
        if (instance != null)
            instance.destroy();
        instance = new GdprChecker(activity, publisherConsent, listener);
        instance.check();
    }

    private void setGdprState(String isAccepted, ConsentType consentType) {
        if (destroyed)
            return;
        IABPreferences
                .getInstance(appContext)
                .setGdprState(isAccepted, consentType);
        checkedAtLeastOnce = true;
        // TODO. Ugly.
        Listener listener = this.listener;
        destroy();
        if (listener != null)
            listener.onGdprChecked();
    }

    private void setGdprState(boolean isAccepted, ConsentType consentType) {
        setGdprState(isAccepted ? "1" : "0", consentType);
    }

    private void destroy() {
        destroyed = true;
        instance = null;
        this.activity = new WeakReference<>(null);
        appContext = null;
        listener = null;
        publisherConsent = null;
    }

    private void check() {
        if (destroyed)
            return;

        if (IABPreferences.getInstance(appContext).isIabTcfCmpSdkPresent()) {
            setGdprState(true, ConsentType.PUBLISHER);
            return;
        }

        if (publisherConsent != null) {
            setGdprState(publisherConsent.getConsent(), ConsentType.PUBLISHER);
            return;
        }
        new DntFetcher(appContext, this).start();
    }

    @Override
    public void onDntFetched(boolean isLimited, String advId) {
        if (destroyed)
            return;

        this.advId = advId;

        if (isLimited) {
            setGdprState(false, ConsentType.USER_RESTRICTED);
        }
    }
}
