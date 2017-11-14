package com.loopme.webservice;

import com.loopme.utils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {
    private static final String USER_AGENT_HEADER = "User-Agent";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request updatedRequest = originalRequest.newBuilder().header(USER_AGENT_HEADER, Utils.sUserAgent).build();
        return chain.proceed(updatedRequest);
    }
}
