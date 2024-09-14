package com.loopme;

import com.loopme.ad.AdSpotDimensions;

public class CreativeParams {

    private final AdSpotDimensions dimension;
    private final String creativeData;

    public CreativeParams(AdSpotDimensions dimension, String creativeData) {
        this.dimension = dimension;
        this.creativeData = creativeData;
    }

    public AdSpotDimensions getDimension() { return dimension; }
    public String getViewMode() { return "'normal'"; }
    public int getDesiredBitrate() { return 720; }
    public String getCreativeData() { return "{'AdParameters':'" + creativeData + "'}"; }
    public String getEnvironmentVars() {
        return "{ slot: document.getElementById('loopme-slot'), " +
            "videoSlot: document.getElementById('loopme-videoslot'), " +
            "videoSlotCanAutoPlay: true }";
    }
}
