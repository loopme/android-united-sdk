package com.ironsource.adapters.custom.loopme;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.loopme.LoopMeSdk;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Keep
public class LoopmeCustomAdapter extends BaseAdapter {

    private static final String KEY_PUBLISHER_CONSENT =
        LoopmeCustomAdapter.class.getSimpleName() + ".KEY_PUBLISHER_CONSENT";

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, NetworkInitializationListener networkInitializationListener) {
        if (LoopMeSdk.isInitialized()) {
            onNetworkInitializationFinished(networkInitializationListener, null, true);
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        tryInitializeLoopMeSdk(networkInitializationListener, latch, context);

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

    @NonNull
    @Override
    public String getAdapterVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getMediationSdkVersion() {
        return IronSourceUtils.getSDKVersion();
    }

    private static void tryInitializeLoopMeSdk(
        NetworkInitializationListener networkInitializationListener,
        CountDownLatch latch,
        Context context
    ) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                LoopMeSdk.Configuration loopMeConf = new LoopMeSdk.Configuration();
                loopMeConf.setMediation("ironsource");

                LoopmeCustomAdapter adapterInstance = new LoopmeCustomAdapter();
                loopMeConf.setAdapterVersion(adapterInstance.getAdapterVersion());

                loopMeConf.setMediationSdkVersion(getMediationSdkVersion());

                LoopMeSdk.initialize(
                    context,
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

        if (networkInitializationListener != null) {
            if (success) {
                networkInitializationListener.onInitSuccess();
            } else {
                networkInitializationListener.onInitFailed(-1, null);
            }
        }

        if (latch != null)
            latch.countDown();
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