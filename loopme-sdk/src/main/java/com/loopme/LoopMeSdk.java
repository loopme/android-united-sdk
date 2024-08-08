package com.loopme;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.loopme.gdpr.GdprChecker;
import com.loopme.om.OmidHelper;

/**
 * Created by katerina on 4/27/18.
 */
public final class LoopMeSdk {
    private LoopMeSdk() { }

    private static final String LOG_TAG = LoopMeSdk.class.getSimpleName();
    public static final int ERROR_NONE = -1;
    public static final int ERROR_OMID_FAILED_TO_INITIALIZE = 1;
    private static final String LOOPME_SDK_VERSION = BuildConfig.VERSION_NAME;
    private static LoopMeSdkListener loopMeSdkInitListener;
    private static Configuration configuration;

    public interface LoopMeSdkListener {
        void onSdkInitializationSuccess();
        void onSdkInitializationFail(int error, String message);
    }

    public static class Configuration {
        private GdprChecker.PublisherConsent publisherConsent;
        private String californiaConsumerPrivacy;
        private boolean coppa;
        private String mediation;
        private String mediationSdkVersion;
        private String adapterVersion;

        public GdprChecker.PublisherConsent getPublisherConsent() {
            return publisherConsent;
        }

        /**
         * Use this method in case if you Publisher is willing to ask GDPR consent with your own dialog,
         * pass GDPR consent to this method.
         */
        public void setPublisherConsent(GdprChecker.PublisherConsent publisherConsent) {
            this.publisherConsent = publisherConsent;
        }

        public String getUsPrivacy() {
            return californiaConsumerPrivacy;
        }
        public void setUsPrivacy(String californiaConsumerPrivacy) {
            this.californiaConsumerPrivacy = californiaConsumerPrivacy;
        }

        public boolean getCoppa() {
            return coppa;
        }
        public void setCoppa(boolean coppa) {
            this.coppa = coppa;
        }

        public String getMediation() {
            return mediation;
        }
        public void setMediation(String mediation) {
            this.mediation = mediation;
        }

        public String getMediationSdkVersion() {
            return mediationSdkVersion;
        }
        public void setMediationSdkVersion(String mediationSdkVersion) {
            this.mediationSdkVersion = mediationSdkVersion;
        }

        public String getAdapterVersion() {
            return adapterVersion;
        }
        public void setAdapterVersion(String adapterVersion) {
            this.adapterVersion = adapterVersion;
        }
    }

    @MainThread
    public static void initialize(@NonNull Context context,
                                  @NonNull Configuration config,
                                  @NonNull LoopMeSdkListener sdkInitListener) {

        if (Looper.getMainLooper() != Looper.myLooper())
            throw new IllegalStateException("Must be called on the main thread");

        if (isInitialized()) {
            sdkInitListener.onSdkInitializationSuccess();
            return;
        }

        if (configuration.getUsPrivacy() != null)
            IABPreferences.getInstance(context).setUSPrivacy(configuration.getUsPrivacy());
        IABPreferences.getInstance(context).setCoppa(configuration.getCoppa());

        configuration = config;
        loopMeSdkInitListener = sdkInitListener;

        OmidHelper.SDKInitListener omidListener = new OmidHelper.SDKInitListener() {
            @Override
            public void onReady() {
                checkInitStatus(ERROR_NONE, "");
            }

            @Override
            public void onError(String error) {
                checkInitStatus(ERROR_OMID_FAILED_TO_INITIALIZE, error);
            }
        };
        // Omid init.
        OmidHelper.tryInitOmidAsync(context.getApplicationContext(), omidListener);

        // Gdpr.
        GdprChecker.Listener gdprCheckerListener = () -> checkInitStatus(ERROR_NONE, "");
        GdprChecker.start(context, config.getPublisherConsent(), gdprCheckerListener);
    }

    public static boolean isInitialized() {
        return OmidHelper.sdkInitialized();
    }

    private static void checkInitStatus(int errorCode, String errorMessage) {
        if (loopMeSdkInitListener == null)
            return;

        boolean hasError = errorCode != ERROR_NONE;

        if (!hasError && !isInitialized())
            return;

        LoopMeSdkListener listener = loopMeSdkInitListener;
        loopMeSdkInitListener = null;

        if (hasError) {
            Logging.out(LOG_TAG, errorMessage, true);
            listener.onSdkInitializationFail(errorCode, errorMessage);
        } else {
            listener.onSdkInitializationSuccess();
        }
    }

    public static String getVersion(){
        return LOOPME_SDK_VERSION;
    }

    public static String getMediation() {
        return configuration != null ? configuration.getMediation() : "unknown";
    }

    public static String getMediationSdkVersion() {
        return configuration != null ? configuration.getMediationSdkVersion() : null;
    }

    public static String getAdapterVersion() {
        return configuration != null ? configuration.getAdapterVersion() : null;
    }
}
