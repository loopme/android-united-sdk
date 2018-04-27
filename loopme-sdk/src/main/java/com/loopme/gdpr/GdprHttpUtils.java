package com.loopme.gdpr;

import android.support.annotation.NonNull;

import com.loopme.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprHttpUtils {

    private Callback mCallback;
    private static GdprHttpUtils mInstance;

    private GdprHttpUtils() {
    }

    public static GdprHttpUtils getInstance() {
        synchronized (GdprHttpUtils.class) {
            if (mInstance == null) {
                mInstance = new GdprHttpUtils();
            }
        }
        return mInstance;
    }

    public RemoteChecker setListener(Callback callback) {
        mCallback = callback;
        return new RemoteChecker();
    }

    private void onFail(String message) {
        if (mCallback != null) {
            mCallback.onFail(message);
        }
    }

    private void onSuccess(GdprResponse response) {
        if (mCallback != null) {
            mCallback.onSuccess(response);
        }
    }

    protected class RemoteChecker {
        private static final long CONNECT_TIMEOUT = 5;
        private static final long READ_TIMEOUT = 5;

        private RemoteChecker() {
        }

        public void checkNeedConsent(String advId) {
            getService().checkUserConsent(advId).enqueue(new retrofit2.Callback<GdprResponse>() {
                @Override
                public void onResponse(@NonNull Call<GdprResponse> call, @NonNull Response<GdprResponse> response) {
                    if (response.isSuccessful()) {
                        onSuccess(response.body());
                    } else {
                        onFail(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GdprResponse> call, @NonNull Throwable trouble) {
                    onFail(trouble.getMessage());
                }
            });
        }

        private LoopMeGdprService getService() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(JacksonConverterFactory.create())
                    .baseUrl(BuildConfig.LOOPME_GDPR_URL)
                    .client(client)
                    .build();
            return retrofit.create(LoopMeGdprService.class);
        }
    }

    public interface Callback {
        void onSuccess(GdprResponse response);

        void onFail(String message);
    }
}
