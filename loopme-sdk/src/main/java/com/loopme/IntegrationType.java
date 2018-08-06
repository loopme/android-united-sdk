package com.loopme;

/**
 * Created by katerina on 8/28/17.
 */

public enum IntegrationType {
    NORMAL(0, "normal"),
    MOPUB(1, "mopub"),
    ADMOB(2, "admob"),
    FYBER(3, "fyber"),
    UNITY(4, "unity"),
    ADOBE_AIR(5, "adobe_air"),
    CORONA(6, "corona"),
    ADMOST(7, "admost"),
    AMR(8, "amr");

    private int mId;
    private String mType;

    IntegrationType(int id, String type) {
        this.mId = id;
        this.mType = type;
    }

    public int getId() {
        return mId;
    }

    public String getType() {
        return mType;
    }
}

