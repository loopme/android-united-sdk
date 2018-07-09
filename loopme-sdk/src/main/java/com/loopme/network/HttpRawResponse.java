package com.loopme.network;

public class HttpRawResponse {

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
        this.mMessage = message;
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
