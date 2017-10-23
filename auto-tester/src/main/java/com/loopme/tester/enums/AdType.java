package com.loopme.tester.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AdType {
    BANNER("banner"),
    INTERSTITIAL("interstitial");

    private String mType;

    AdType(String type) {
        this.mType = type;
    }

    @JsonValue
    public String getType() {
        return this.mType;
    }
}
