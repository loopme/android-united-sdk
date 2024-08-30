package com.loopme;

import static com.loopme.Constants.UNKNOWN_MSG;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.loopme.debugging.Params;
import com.loopme.gdpr.GdprChecker;
import com.loopme.om.OmidHelper;
import com.loopme.utils.Utils;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

/**
 * Created by katerina on 4/27/18.
 */
public final class LoopMeSdk {
    private LoopMeSdk() { }

    private static final String LOG_TAG = LoopMeSdk.class.getSimpleName();

    private static final String LOOPME_SDK_VERSION = BuildConfig.VERSION_NAME;
    public static String getVersion() { return LOOPME_SDK_VERSION; }

    private static Configuration configuration;
    public static Configuration getConfiguration() { return configuration; }

    private static boolean isInitialized = false;
    public static boolean isInitialized() { return isInitialized; }

    public interface LoopMeSdkListener {
        void onSdkInitializationSuccess();
        void onSdkInitializationFail(int error, String message);
    }

    public static class Configuration {
        /**
         * Use this method in case if you Publisher is willing to ask GDPR consent with your own dialog,
         * pass GDPR consent to this method.
         */
        public void setPublisherConsent(GdprChecker.PublisherConsent consent) { publisherConsent = consent; }
        public GdprChecker.PublisherConsent getPublisherConsent() { return publisherConsent; }
        private GdprChecker.PublisherConsent publisherConsent;

        private String usPrivacy;
        public String getUsPrivacy() { return usPrivacy; }
        public void setUsPrivacy(String value) { usPrivacy = value; }

        private boolean coppa;
        public boolean getCoppa() { return coppa; }
        public void setCoppa(boolean value) { coppa = value; }

        private String mediation;
        public String getMediation() { return mediation != null ? mediation : UNKNOWN_MSG; }
        public void setMediation(String mediationName) { mediation = mediationName; }

        private String mediationSdkVersion;
        public String getMediationSdkVersion() { return mediationSdkVersion != null ? mediationSdkVersion : UNKNOWN_MSG; }
        public void setMediationSdkVersion(String version) { mediationSdkVersion = version; }

        private String adapterVersion;
        public String getAdapterVersion() { return adapterVersion != null ? adapterVersion : UNKNOWN_MSG; }
        public void setAdapterVersion(String version) { adapterVersion = version; }
    }

    @MainThread
    public static void initialize(
        @NonNull Context context, @NonNull Configuration config, @NonNull LoopMeSdkListener sdkInitListener
    ) {
        long start = System.currentTimeMillis();
        if (Looper.getMainLooper() != Looper.myLooper())
            throw new IllegalStateException("Must be called on the main thread");

        Utils.init(context);

        if (isInitialized) {
            sdkInitListener.onSdkInitializationSuccess();
            return;
        }
        isInitialized = true;

        configuration = config;
        if (configuration.getUsPrivacy() != null)
            IABPreferences.getInstance(context).setUSPrivacy(configuration.getUsPrivacy());
        IABPreferences.getInstance(context).setCoppa(configuration.getCoppa());

        // Omid init.
        long omidStart = System.currentTimeMillis();
        OmidHelper.init(context.getApplicationContext());
        long omidEnd = System.currentTimeMillis();

        // Gdpr.
        GdprChecker.start(context, configuration.getPublisherConsent());
        long end = System.currentTimeMillis();

        sdkInitListener.onSdkInitializationSuccess();

        if (omidEnd - omidStart > 100) {
            sendInitTimeAlert("OMID init time alert > 100ms", omidEnd - omidStart);
        }
        if (end - start > 100) {
            sendInitTimeAlert( "SDK init time alert > 100ms", end - start);
        }
    }

    private static void sendInitTimeAlert(String errorMessage, long initTime) {
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(Params.ERROR_TYPE, Constants.ErrorType.CUSTOM);
        errorInfo.put(Params.ERROR_MSG, errorMessage);
        errorInfo.put("timeout", String.valueOf(initTime));
        LoopMeTracker.post(errorInfo);
    }

}
