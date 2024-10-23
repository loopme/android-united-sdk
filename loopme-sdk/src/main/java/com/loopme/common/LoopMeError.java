package com.loopme.common;

import android.text.TextUtils;

import com.loopme.Constants;

import java.util.HashMap;

public class LoopMeError {

    private int mErrorCode;
    private String mErrorMessage;
    private String mErrorType = Constants.ErrorType.CUSTOM;
    private HashMap<String, String> params = new HashMap<>();

    public LoopMeError() { }

    public LoopMeError(int errorCode, String errorMessage) {
        this.mErrorCode = errorCode;
        this.mErrorMessage = errorMessage;
    }

    public LoopMeError(int errorCode, String errorMessage, String errorType) {
        this.mErrorCode = errorCode;
        this.mErrorMessage = errorMessage;
        this.mErrorType = errorType;
    }

    public LoopMeError(LoopMeError error) {
        if (error == null) {
            return;
        }
        mErrorCode = error.getErrorCode();
        mErrorMessage = error.getMessage();
        mErrorType = error.getErrorType();
        this.params = error.getParams() != null ? new HashMap<>(error.getParams()) : new HashMap<>();
    }

    public LoopMeError(String message) {
        this.mErrorMessage = message;
    }

    public LoopMeError(String message, String errorType) {
        this.mErrorMessage = message;
        this.mErrorType = errorType;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getMessage() {
        return this.mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
    }

    public void addToMessage(String additionalMessage) {
        if (!TextUtils.isEmpty(additionalMessage)) {
            mErrorMessage = mErrorMessage + " " + additionalMessage;
        }
    }

    public String getErrorType() {
        return mErrorType;
    }

    public void setErrorType(String mErrorType) {
        this.mErrorType = mErrorType;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public LoopMeError addParam(String field, String value) {
        params.put(field, value);
        return this;
    }

    public LoopMeError addParams(HashMap<String, String> params) {
        this.params.putAll(params);
        return this;
    }
}
