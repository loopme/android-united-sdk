package com.loopme.tester.testfairy;

import com.loopme.tester.BuildConfig;
import com.loopme.tester.Constants;
import com.loopme.tester.testfairy.model.TestFairyResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TestFairyServiceImpl {
    private final OnUpdateListener mListener;

    public TestFairyServiceImpl(OnUpdateListener listener) {
        mListener = listener;
    }

    public void checkUpdate() {
        Retrofit retrofit = getRetrofit(BuildConfig.TESTFAIRY_API_URL, BuildConfig.TESTFAIRY_API_LOGIN, BuildConfig.TESTFAIRY_API_KEY);
        Call<TestFairyResponse> call = retrofit.create(TestFairyService.class).getBuilds();
        call.enqueue(new Callback<TestFairyResponse>() {
            @Override
            public void onResponse(Call<TestFairyResponse> call, Response<TestFairyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isNeedUpdate()) {
                        onUpdateAvailable();
                    } else {
                        onUpdateNotAvailable();
                    }
                }
            }

            @Override
            public void onFailure(Call<TestFairyResponse> call, Throwable t) {
                onUpdateNotAvailable();
            }
        });
    }

    private void onUpdateAvailable() {
        if (mListener != null) {
            mListener.onUpdateAvailable();
        }
    }

    private void onUpdateNotAvailable() {
        if (mListener != null) {
            mListener.onUpdateNotAvailable();
        }
    }

    private static Retrofit getRetrofit(String url, String login, String key) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(login, key))
                .build();
        return new Retrofit
                .Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public interface OnUpdateListener {
        void onUpdateAvailable();

        void onUpdateNotAvailable();
    }
}
