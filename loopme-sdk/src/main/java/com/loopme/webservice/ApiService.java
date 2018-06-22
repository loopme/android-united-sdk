package com.loopme.webservice;

import android.support.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.models.response.ResponseJsonModel;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by katerina on 7/24/17.
 */

public interface ApiService {

    String CONTENT_TYPE_DEFINITION = "Content-Type: application/json";
    String CACHE_CONTROL_DEFINITION = "Cache-Control: max-age=640000";

    @Streaming
    @Headers({CONTENT_TYPE_DEFINITION, CACHE_CONTROL_DEFINITION})
    @POST(Constants.ADS)
    Call<ResponseJsonModel> fetchAd(@Body RequestBody jsonObject);

    @Headers({CONTENT_TYPE_DEFINITION, CACHE_CONTROL_DEFINITION})
    @POST(".")
    Call<ResponseJsonModel> fetchAd();

    @GET
    @Streaming
    Call<String> downloadFile(@Url String fileUrl);

    @GET
    Call<String> postEvent(@Url String url,
                           @Nullable Map<String, String> headers,
                           @Nullable String request);
}

