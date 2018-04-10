package com.loopme.tester.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AdSdk {
    LOOPME("loopme"),
    MOPUB("mopub"),
    LMVPAID("lmvpaid");

    private String mName;

    AdSdk(String name) {
        this.mName = name;
    }

    @JsonValue
    public String getName() {
        return this.mName;
    }
}
