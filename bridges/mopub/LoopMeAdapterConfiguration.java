package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.LoopMeSdk;
import com.loopme.gdpr.GdprChecker;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

import java.lang.ref.WeakReference;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class LoopMeAdapterConfiguration extends BaseAdapterConfiguration {

    private static final String KEY_PUBLISHER_CONSENT =
            LoopMeAdapterConfiguration.class.getSimpleName() + ".KEY_PUBLISHER_CONSENT";

    private static WeakReference<Activity> activityWeakReference =
            new WeakReference<>(null);

    @NonNull
    @Override
    public String getAdapterVersion() {
        return "7.0.1.0";
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return "loopme";
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        return "7.0.1";
    }

    @Override
    public void initializeNetwork(
            @NonNull Context context,
            @Nullable Map<String, String> moPubConfiguration,
            @NonNull OnNetworkInitializationFinishedListener moPubListener) {

        Activity activity = activityWeakReference.get();
        setWeakActivity(null);

        if (LoopMeSdk.isInitialized()) {
            onNetworkInitializationFinished(moPubListener, null, true);
            return;
        }

        if (activity == null) {
            onNetworkInitializationFinished(moPubListener, null, false);
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        tryInitializeLoopMeSdk(moPubListener, moPubConfiguration, latch, activity);

        try {
            latch.await();
        } catch (InterruptedException e) {
            onNetworkInitializationFinished(moPubListener, null, false);
        }
    }

    private static void tryInitializeLoopMeSdk(
            OnNetworkInitializationFinishedListener moPubListener,
            Map<String, String> moPubConfiguration,
            CountDownLatch latch,
            Activity activity) {

        activity.runOnUiThread(() -> {
            try {
                LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();

                if (moPubConfiguration != null && moPubConfiguration.containsKey(KEY_PUBLISHER_CONSENT))
                    loopMeConf.setPublisherConsent(
                            new GdprChecker.PublisherConsent(
                                    Boolean.parseBoolean(
                                            moPubConfiguration.get(KEY_PUBLISHER_CONSENT))));

                LoopMeSdk.initialize(
                        activity,
                        loopMeConf,
                        new LoopMeSdk.LoopMeSdkListener() {
                            @Override
                            public void onSdkInitializationSuccess() {
                                onNetworkInitializationFinished(moPubListener, latch, true);
                            }

                            @Override
                            public void onSdkInitializationFail(int error, String message) {
                                onNetworkInitializationFinished(moPubListener, latch, false);
                            }
                        });
            } catch (Exception ex) {
                onNetworkInitializationFinished(moPubListener, latch, false);
            }
        });
    }

    private static void onNetworkInitializationFinished(
            OnNetworkInitializationFinishedListener moPubListener,
            CountDownLatch latch,
            boolean success) {

        if (moPubListener != null)
            moPubListener.onNetworkInitializationFinished(
                    LoopMeAdapterConfiguration.class,
                    success
                            ? MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS
                            : MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);

        if (latch != null)
            latch.countDown();
    }

    /**
     * Unfortunately, MoPub SDK v5.5.x doesn't pass Activity context to
     * {@link #initializeNetwork(Context, Map, OnNetworkInitializationFinishedListener)}.
     * Pass activity here before calling
     * {@link com.mopub.common.MoPub#initializeSdk(Context, SdkConfiguration, SdkInitializationListener)} }.
     */
    public static void setWeakActivity(Activity activity) {
        if (LoopMeSdk.isInitialized())
            return;

        activityWeakReference = new WeakReference<>(activity);
    }

    /**
     * Use this method in case if you Publisher is willing to ask GDPR consent with your own or
     * MoPub dialog AND don't want LoopMe consent dialog to be shown - pass GDPR consent to this method.
     */
    public static void setPublisherConsent(
            Map<String, String> moPubConfiguration,
            boolean consent) {

        moPubConfiguration.put(KEY_PUBLISHER_CONSENT, String.valueOf(consent));
    }
}