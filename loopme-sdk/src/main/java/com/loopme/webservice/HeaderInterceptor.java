package com.loopme.webservice;

import com.loopme.BuildConfig;
import com.loopme.utils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String OPEN_RTB_VER = "x-openrtb-version";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request updatedRequest = originalRequest.newBuilder()
                .header(USER_AGENT_HEADER, Utils.sUserAgent)
                .header(OPEN_RTB_VER, BuildConfig.OPEN_RTB_VERSION)
                .build();
        return chain.proceed(updatedRequest);
    }
}
