package com.loopme.tester.testfairy;

import com.loopme.tester.testfairy.model.TestFairyResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TestFairyService {
    @GET(".")
    Call<TestFairyResponse> getBuilds();
}
