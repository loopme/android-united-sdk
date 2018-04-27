package com.loopme.gdpr;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by katerina on 4/27/18.
 */

public interface LoopMeGdprService {
    @GET("consent_check/")
    Call<GdprResponse> checkUserConsent(@Query("device_id") String advId);
}