package com.ironsource.adapters.custom.loopme;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.Keep;
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.loopme.LoopMeSdk;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Keep
public class LoopmeCustomAdapter extends BaseAdapter {

    private static final String KEY_PUBLISHER_CONSENT =
            LoopmeCustomAdapter.class.getSimpleName() + ".KEY_PUBLISHER_CONSENT";

    private static WeakReference<Activity> activityWeakReference =
            new WeakReference<>(null);

    private static String loopmeAppkey;


    @Override
    public void init(AdData adData, Context context, NetworkInitializationListener networkInitializationListener) {
        Activity activity = activityWeakReference.get();
        setWeakActivity(null);

        if (LoopMeSdk.isInitialized()) {
            onNetworkInitializationFinished(networkInitializationListener, null, true);
            return;
        }

        if (activity == null) {
            onNetworkInitializationFinished(networkInitializationListener, null, false);
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        tryInitializeLoopMeSdk(networkInitializationListener, latch, activity);

        try {
            latch.await();
        } catch (InterruptedException e) {
            onNetworkInitializationFinished(networkInitializationListener, null, false);
        }
    }

    @Override
    public String getNetworkSDKVersion() {
        return LoopMeSdk.getVersion();
    }

    @Override
    public String getAdapterVersion() {
        return "7.1.0.0";
    }

    private static void tryInitializeLoopMeSdk(
            NetworkInitializationListener networkInitializationListener,
            CountDownLatch latch,
            Activity activity) {

        activity.runOnUiThread(() -> {
            try {
                LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();

                LoopMeSdk.initialize(
                        activity,
                        loopMeConf,
                        new LoopMeSdk.LoopMeSdkListener() {
                            @Override
                            public void onSdkInitializationSuccess() {
                                onNetworkInitializationFinished(networkInitializationListener, latch, true);
                            }

                            @Override
                            public void onSdkInitializationFail(int error, String message) {
                                onNetworkInitializationFinished(networkInitializationListener, latch, false);
                            }
                        });
            } catch (Exception ex) {
                onNetworkInitializationFinished(networkInitializationListener, latch, false);
            }
        });
    }

    private static void onNetworkInitializationFinished(
            NetworkInitializationListener networkInitializationListener,
            CountDownLatch latch,
            boolean success) {

        if (networkInitializationListener != null)
            networkInitializationListener.onInitSuccess();

        if (latch != null)
            latch.countDown();
    }

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

    public static String getLoopmeAppkey() {
        return loopmeAppkey;
    }

    public static void setLoopmeAppkey(String loopmeAppkey) {
        LoopmeCustomAdapter.loopmeAppkey = loopmeAppkey;
    }
}