package com.loopme.tester.model;

/**
 * Created by katerina on 2/15/17.
 */

public class Response {

    private boolean mIsSuccess;
    private String mMessage;

    public Response() {
    }

    public Response(boolean isSuccess, String message) {
        this.mIsSuccess = isSuccess;
        this.mMessage = message;
    }

    public boolean isSuccess() {
        return mIsSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.mIsSuccess = isSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
}
