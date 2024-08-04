package com.loopme.xml.vast4;

import android.text.TextUtils;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.AdSystem;
import com.loopme.xml.Creatives;
import com.loopme.xml.Error;
import com.loopme.xml.Impression;
import com.loopme.xml.Tracking;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {

    @Attribute
    private boolean allowMultipleAds;

    @Attribute
    private boolean fallbackOnNoAd;

    @Tag("Impression")
    private List<Impression> impressionList;

    @Tag
    private VastAdTagUri vastAdTagUri;

    @Tag
    private AdSystem adSystem;

    @Tag
    private Pricing pricing;

    @Tag
    private Error error;

    @Tag
    private ViewableImpression viewableImpression;

    @Tag
    private AdVerifications adVerifications;

    @Tag
    private Extension extension;

    @Tag
    private Creatives creatives;

    public boolean isFollowAdditionalWrappers() { return true; }

    public boolean isAllowMultipleAds() {
        return allowMultipleAds;
    }

    public boolean isFallbackOnNoAd() {
        return fallbackOnNoAd;
    }

    public List<Impression> getImpressions() {
        return impressionList;
    }

    public VastAdTagUri getVastAdTagUri() {
        return vastAdTagUri;
    }

    public AdSystem getAdSystem() {
        return adSystem;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public Error getError() {
        return error;
    }

    public ViewableImpression getViewableImpression() {
        return viewableImpression;
    }

    public AdVerifications getAdVerifications() {
        return adVerifications;
    }

    public Extension getExtension() {
        return extension;
    }

    public Creatives getCreatives() {
        return creatives;
    }

    public String getVastTagUrl() {
        return (vastAdTagUri != null && !TextUtils.isEmpty(vastAdTagUri.getText())) ?
            vastAdTagUri.getText() : "";
    }

    public List<Tracking> getCreativeTrackingList() {
        return creatives != null ? creatives.getTrackingList() : new ArrayList<>();
    }

    public List<Verification> getVerificationList() {
        return adVerifications == null ? new ArrayList<>() : adVerifications.getVerificationList();
    }

    public List<String> getVideoClicksList() {
        return creatives != null ? creatives.getVideoClicksList() : new ArrayList<>();
    }

    public List<String> getCompanionTrackingEvents() {
        List<String> trackingEvents = new ArrayList<>();
        if (creatives != null) {
            trackingEvents.addAll(creatives.getCompanionTrackingEvents());
        }
        return trackingEvents;
    }

    public List<String> getCompanionClickTrackingList() {
        List<String> clickTrackingList = new ArrayList<>();
        if (creatives != null) {
            clickTrackingList.addAll(creatives.getCompanionClickTrackingList());
        }
        return clickTrackingList;
    }
}
