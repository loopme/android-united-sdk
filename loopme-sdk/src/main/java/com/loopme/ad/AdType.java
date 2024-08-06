package com.loopme.ad;

import android.text.TextUtils;

/**
 * Created by katerina on 5/29/17.
 */

public enum AdType {
    HTML, VAST, MRAID, VPAID;

    public static AdType fromString(String adType) {
        if (TextUtils.isEmpty(adType)) return HTML;
        if (adType.equalsIgnoreCase(MRAID.name())) return MRAID;
        if (adType.equalsIgnoreCase(VPAID.name())) return VPAID;
        if (adType.equalsIgnoreCase(VAST.name())) return VAST;
        return HTML;
    }
}
