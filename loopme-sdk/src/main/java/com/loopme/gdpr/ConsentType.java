package com.loopme.gdpr;

/**
 * Created by katerina on 4/27/18.
 */

public enum ConsentType {
    LOOPME("0"),
    PUBLISHER("1"),
    USER_RESTRICTED("2");

    private String mType;

    ConsentType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
