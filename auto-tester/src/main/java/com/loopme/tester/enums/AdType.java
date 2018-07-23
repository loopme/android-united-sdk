package com.loopme.tester.enums;

import android.text.TextUtils;

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

    public static AdType fromString(String adType) {
        if (!TextUtils.isEmpty(adType) && adType.equalsIgnoreCase(BANNER.name())) {
            return BANNER;
        } else {
            return INTERSTITIAL;
        }
    }
}
