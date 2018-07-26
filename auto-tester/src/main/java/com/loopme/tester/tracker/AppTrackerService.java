package com.loopme.tester.tracker;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AppTrackerService {
    @GET("?et=TRACKING&pt=sp&f=0")
    retrofit2.Call<String> trackEvent(@Query("id") String id, @Query("event_name") String eventName);
}
