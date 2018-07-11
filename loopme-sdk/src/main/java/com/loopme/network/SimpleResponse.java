package com.loopme.network;

public class SimpleResponse {
    private int mCode;
    private String mMessage;

    public boolean isSuccessful() {
        return mCode == 200;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public void setCode(int code) {
        this.mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
