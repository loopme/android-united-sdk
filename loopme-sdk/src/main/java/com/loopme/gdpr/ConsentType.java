package com.loopme.gdpr;

/**
 * Created by katerina on 4/27/18.
 */

public enum ConsentType {
    PUBLISHER(1),
    USER_RESTRICTED(2);

    private int mType;

    ConsentType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }
}
