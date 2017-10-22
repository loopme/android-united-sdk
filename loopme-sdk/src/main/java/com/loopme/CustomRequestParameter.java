package com.loopme;

public class CustomRequestParameter {

    private String mParamName;
    private String mParamValue;

    public CustomRequestParameter(String name, String value) {
        mParamName = name;
        mParamValue = value;
    }

    public String getParamName() {
        return mParamName;
    }

    public String getParamValue() {
        return mParamValue;
    }
}
