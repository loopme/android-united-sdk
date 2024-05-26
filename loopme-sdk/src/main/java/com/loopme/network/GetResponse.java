package com.loopme.network;

public class GetResponse<T> {
    private T mBody;
    private int mCode;
    private String mMessage;
    public boolean isSuccessful() { return mCode == 200 && getBody() != null; }
    public T getBody() { return mBody; }
    public void setBody(T mResponseBody) { this.mBody = mResponseBody; }
    public String getMessage() { return mMessage; }
    public void setMessage(String message) { this.mMessage = message; }
    public void setCode(int code) { this.mCode = code; }
    public int getCode() { return mCode; }
}
