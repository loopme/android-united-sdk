package com.loopme.tester.tracker;

import android.support.annotation.NonNull;
import android.util.Log;

import com.loopme.tester.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppTrackerServiceImpl implements Callback<String> {
    private static final String LOG_TAG = AppTrackerServiceImpl.class.getSimpleName();
    private final AppTrackerService mService;

    public AppTrackerServiceImpl() {
        mService = getRetrofit(com.loopme.Constants.BASE_EVENT_URL).create(AppTrackerService.class);
    }

    public void trackEvent(String eventName) {
        mService.trackEvent(eventName).enqueue(this);
    }

    private static Retrofit getRetrofit(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
        return new Retrofit
                .Builder()
                .baseUrl(url)
                .addConverterFactory(new ToStringConverterFactory())
                .client(okHttpClient)
                .build();
    }

    @Override
    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
        Log.d(LOG_TAG, "Event posted success" + call.request().url().toString());
    }

    @Override
    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
        Log.d(LOG_TAG, "Fail to post event " + call.request().url().toString() + " " + t.getMessage());
    }
}
