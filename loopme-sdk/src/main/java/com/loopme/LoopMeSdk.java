package com.loopme;

import android.app.Activity;
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

    public interface LoopMeSdkListener {
        void onSdkInitializationSuccess();
        void onSdkInitializationFail(int error, String message);
    }

    public static class Configuration {
        private GdprChecker.PublisherConsent publisherConsent;
        private String californiaConsumerPrivacy;
        private boolean coppa;
        public GdprChecker.PublisherConsent getPublisherConsent() {
            return publisherConsent;
        }
        public String getUsPrivacy() { return californiaConsumerPrivacy; }
        public void setUsPrivacy(String californiaConsumerPrivacy){
            this.californiaConsumerPrivacy = californiaConsumerPrivacy;
        }
        /**
         * Use this method in case if you Publisher is willing to ask GDPR consent with your own dialog,
         * pass GDPR consent to this method.
         */
        public void setPublisherConsent(GdprChecker.PublisherConsent publisherConsent) {
            this.publisherConsent = publisherConsent;
        }
        public boolean getCoppa() {
            return coppa;
        }
        public void setCoppa(boolean coppa) {
            this.coppa = coppa;
        }
    }

    @MainThread
    public static void initialize(@NonNull Activity activity,
                                  @NonNull Configuration configuration,
                                  @NonNull LoopMeSdkListener loopMeSdkInitListener) {

        if (Looper.getMainLooper() != Looper.myLooper())
            throw new IllegalStateException("Must be called on the main thread");

        if (isInitialized()) {
            loopMeSdkInitListener.onSdkInitializationSuccess();
            return;
        }

        if (configuration.getUsPrivacy() != null)
            IABPreferences.getInstance(activity).setUSPrivacy(configuration.getUsPrivacy());
        IABPreferences.getInstance(activity).setCoppa(configuration.getCoppa());

        LoopMeSdk.loopMeSdkInitListener = loopMeSdkInitListener;

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
        OmidHelper.tryInitOmidAsync(activity.getApplicationContext(), omidListener);

        // Gdpr.
        GdprChecker.Listener gdprCheckerListener = () -> checkInitStatus(ERROR_NONE, "");
        GdprChecker.start(activity, configuration.getPublisherConsent(), gdprCheckerListener);
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
}