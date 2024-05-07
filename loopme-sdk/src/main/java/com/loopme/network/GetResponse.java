package com.loopme.network;

public class GetResponse<T> extends SimpleResponse {
    private T mBody;
    @Override
    public boolean isSuccessful() {
        return super.isSuccessful() && getBody() != null;
    }
    public T getBody() {
        return mBody;
    }
    public void setBody(T mResponseBody) {
        this.mBody = mResponseBody;
    }
}
