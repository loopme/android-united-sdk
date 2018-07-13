package com.loopme.tester.testfairy;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


class AuthInterceptor implements Interceptor {
    private String mPassword;
    private String mLogin;

    public AuthInterceptor(String login, String password) {
        mLogin = login;
        mPassword = password;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String authorValue = encodeCredentialsForBasicAuthorization();
        Request request = chain
                .request()
                .newBuilder()
                .addHeader("Authorization", authorValue)
                .build();
        return chain.proceed(request);
    }


    private String encodeCredentialsForBasicAuthorization() {
        final String userAndPassword = mLogin + ":" + mPassword;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }
}
