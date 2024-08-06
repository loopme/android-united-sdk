package com.loopme.network;

import android.util.Log;

public class HttpRawResponse {
    private static final String LOG_TAG = HttpRawResponse.class.getSimpleName();
    private int mCode;
    private String mMessage = "";
    private byte[] mBody;
    public void setCode(int code) {
        this.mCode = code;
    }
    public int getCode() {
        return mCode;
    }
    public void setMessage(String message) {
        mMessage = message;
        Log.d(LOG_TAG, "HttpRawResponse message: " + mMessage);
    }
    public String getMessage() {
        return mMessage;
    }
    public void setBody(byte[] body) {
        this.mBody = body;
    }
    public byte[] getBody() {
        return mBody;
    }
}
