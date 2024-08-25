package com.loopme.xml;

import android.text.TextUtils;

import androidx.annotation.NonNull;

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

    public String getId() { return id; }
    public Linear getLinear() { return linear; }
    public CompanionAds getCompanionAds() { return companionAds; }
    public UniversalAdId getUniversalAdId() { return universalAdId; }
    public CreativeExtensions getCreativeExtensions() { return creativeExtensions; }
    public NonLinearAds getNonLinearAds() { return nonLinearAds; }
    public String getAdId() { return adId; }
    public String getApiFramework() { return apiFramework; }
    public String getSequence() { return sequence; }
    public boolean hasCompanionAds() { return companionAds != null; }

    public boolean isVpaid() {
        return !TextUtils.isEmpty(apiFramework) && apiFramework.equals(VPAID_API);
    }

    @NonNull
    public List<Tracking> getTrackingList() {
        return linear == null ? new ArrayList<>() : linear.getTrackingList();
    }

    @NonNull
    public ArrayList<String> getVideoClicksList() {
        return linear == null ? new ArrayList<>() : linear.getVideoClicksList();
    }

    public List<String> getCompanionTrackingEvents() {
        List<String> trackingEvents = new ArrayList<>();
        if (companionAds != null && companionAds.hasCompanionList()) {
            for (Companion companion : companionAds.getCompanionList()) {
                trackingEvents.addAll(companion.getCompanionTrackingEvents());
            }
        }
        return trackingEvents;
    }

    public List<String> getCompanionClickTrackingList() {
        List<String> clickTrackingList = new ArrayList<>();
        if (companionAds != null && companionAds.hasCompanionList()) {
            for (Companion companion : companionAds.getCompanionList()) {
                clickTrackingList.addAll(companion.getCompanionClickTrackingList());
            }
        }
        return clickTrackingList;
    }
}
