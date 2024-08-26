package com.loopme.xml.vast4;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    public List<Impression> getImpressions() { return impressionList; }
    public Error getError() { return error; }
    public ViewableImpression getViewableImpression() { return viewableImpression; }
    public boolean isFollowAdditionalWrappers() { return true; }
    public boolean isAllowMultipleAds() { return allowMultipleAds; }
    public boolean isFallbackOnNoAd() { return fallbackOnNoAd; }
    public VastAdTagUri getVastAdTagUri() { return vastAdTagUri; }
    public AdSystem getAdSystem() { return adSystem; }
    public Pricing getPricing() { return pricing; }
    public AdVerifications getAdVerifications() { return adVerifications; }
    public Extension getExtension() { return extension; }
    public Creatives getCreatives() { return creatives; }

    @NonNull
    public String getVastTagUrl() {
        return  (vastAdTagUri != null && !TextUtils.isEmpty(vastAdTagUri.getText())) ?
            vastAdTagUri.getText() : "";
    }

    @NonNull
    public List<Tracking> getCreativeTrackingList() {
        return creatives == null ? new ArrayList<>() : creatives.getTrackingList();
    }

    @NonNull
    public List<Verification> getVerificationList() {
        return adVerifications == null ? new ArrayList<>() : adVerifications.getVerificationList();
    }

    @NonNull
    public List<String> getVideoClicksList() {
        return creatives == null ? new ArrayList<>() : creatives.getVideoClicksList();
    }

    @NonNull
    public List<String> getCompanionTrackingEvents() {
        return creatives == null ? new ArrayList<>() : new ArrayList<>(creatives.getCompanionTrackingEvents());
    }

    @NonNull
    public List<String> getCompanionClickTrackingList() {
        return creatives == null ? new ArrayList<>() : new ArrayList<>(creatives.getCompanionClickTrackingList());
    }
}
