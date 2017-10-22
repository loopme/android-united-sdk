package com.loopme.xml;

import android.text.TextUtils;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.CreativeExtensions;
import com.loopme.xml.vast4.NonLinearAds;
import com.loopme.xml.vast4.UniversalAdId;

import java.util.ArrayList;
import java.util.List;

public class Creative {

    private static final String VPAID_API = "VPAID";

    @Attribute
    private String id;

    @Attribute
    private String adId;

    @Attribute
    private String sequence;

    @Attribute
    private String apiFramework;

    @Tag
    private Linear linear;

    @Tag
    private CompanionAds companionAds;

    @Tag
    private UniversalAdId universalAdId;

    @Tag
    private CreativeExtensions creativeExtensions;

    @Tag
    private NonLinearAds nonLinearAds;

    public UniversalAdId getUniversalAdId() {
        return universalAdId;
    }

    public CreativeExtensions getCreativeExtensions() {
        return creativeExtensions;
    }

    public NonLinearAds getNonLinearAds() {
        return nonLinearAds;
    }

    public String getAdId() {
        return adId;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public String getId() {
        return id;
    }

    public String getSequence() {
        return sequence;
    }

    public Linear getLinear() {
        return linear;
    }

    public CompanionAds getCompanionAds() {
        return companionAds;
    }

    public boolean isVpaid() {
        return !TextUtils.isEmpty(apiFramework) && apiFramework.equals(VPAID_API);
    }


    public List<Tracking> getTrackingList() {
        if (linear != null) {
            return linear.getTrackingList();
        } else {
            return null;
        }
    }


    public ArrayList<String> getVideoClicksList() {
        if (linear != null) {
            return linear.getVideoClicksList();
        }
        return new ArrayList<>();
    }
}
