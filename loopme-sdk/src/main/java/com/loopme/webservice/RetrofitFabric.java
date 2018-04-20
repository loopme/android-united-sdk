package com.loopme.webservice;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopme.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitFabric {

    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 60;

    private RetrofitFabric() {
    }

    public static Retrofit getRetrofit(Constants.RetrofitType type, String baseUrl) {
        switch (type) {
            case DOWNLOAD: {
                return getDownloadRetrofit(baseUrl);
            }
            default: {
                return getFetchRetrofit();
            }
        }
    }

    private static Retrofit getDownloadRetrofit(String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HeaderInterceptor())
                .build();
        return new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new ToStringConverterFactory())
                .client(okHttpClient)
                .build();
    }

    private static Retrofit getFetchRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HeaderInterceptor())
                .build();
        return new Retrofit.Builder()
                .baseUrl(Constants.OPEN_RTB_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(
                        new ObjectMapper()
                                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
                .build();
    }
}
