package com.loopme;

import android.app.Activity;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.loopme.gdpr.GdprChecker;
import com.loopme.om.OmidHelper;
import com.loopme.utils.ApiLevel;

/**
 * Created by katerina on 4/27/18.
 */

public final class LoopMeSdk {
    private LoopMeSdk() {
    }

    private static final String LOG_TAG = LoopMeSdk.class.getSimpleName();

    public static final int ERROR_NONE = -1;
    /**
     * Android is under api level 21 (Lollipop 5.0).
     */
    public static final int ERROR_INCOMPATIBLE_ANDROID_API_LEVEL = 0;
    /**
     * Open Measurement SDK failed to initialize.
     */
    public static final int ERROR_OMID_FAILED_TO_INITIALIZE = 1;

    private static LoopMeSdkListener loopMeSdkInitListener;

    public interface LoopMeSdkListener {
        void onSdkInitializationSuccess();

        void onSdkInitializationFail(int error, String message);
    }

    public static class Configuration {

        private GdprChecker.PublisherConsent publisherConsent;

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
    }

    @MainThread
    public static void initialize(@NonNull Activity activity,
                                  @NonNull Configuration configuration,
                                  @NonNull LoopMeSdkListener loopMeSdkInitListener) {

        if (Looper.getMainLooper() != Looper.myLooper())
            throw new IllegalStateException("Must be called on the main thread");

        if (!ApiLevel.isApi21AndHigher()) {
            loopMeSdkInitListener.onSdkInitializationFail(
                    ERROR_INCOMPATIBLE_ANDROID_API_LEVEL, "");
            return;
        }

        if (isInitialized()) {
            loopMeSdkInitListener.onSdkInitializationSuccess();
            return;
        }

        LoopMeSdk.loopMeSdkInitListener = loopMeSdkInitListener;

        // Omid init.
        OmidHelper.tryInitOmidAsync(
                activity.getApplicationContext(),
                createOmidSdkInitListener());

        // Gdpr.
        GdprChecker.start(activity,
                configuration.getPublisherConsent(),
                createGdprCheckerListener());
    }

    public static boolean isInitialized() {
        return OmidHelper.sdkInitialized() && GdprChecker.checkedAtLeastOnce();
    }

    private static OmidHelper.SDKInitListener createOmidSdkInitListener() {
        return new OmidHelper.SDKInitListener() {
            @Override
            public void onReady() {
                checkInitStatus(ERROR_NONE, "");
            }

            @Override
            public void onError(String error) {
                checkInitStatus(ERROR_OMID_FAILED_TO_INITIALIZE, error);
            }
        };
    }

    private static GdprChecker.Listener createGdprCheckerListener() {
        return new GdprChecker.Listener() {
            @Override
            public void onGdprChecked() {
                checkInitStatus(ERROR_NONE, "");
            }
        };
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
}