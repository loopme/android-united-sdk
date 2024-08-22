package com.loopme.network;

public class HttpRawResponse {
    private int mCode;
    public void setCode(int code) { mCode = code; }
    public int getCode() { return mCode; }

    private byte[] mBody;
    public void setBody(byte[] body) { mBody = body; }
    public byte[] getBody() { return mBody; }

    private String mMessage = "";
    public void setMessage(String message) { mMessage = message; }
    public String getMessage() { return mMessage; }
}
