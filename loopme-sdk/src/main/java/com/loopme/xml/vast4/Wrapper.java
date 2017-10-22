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
    private boolean followAdditionalWrappers = true;

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

    public boolean isFollowAdditionalWrappers() {
        return followAdditionalWrappers;
    }

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
        if (vastAdTagUri != null && !TextUtils.isEmpty(vastAdTagUri.getText())) {
            return vastAdTagUri.getText();
        } else {
            return "";
        }
    }

    public List<Tracking> getCreativeTrackingList() {
        if (creatives != null) {
            return creatives.getTrackingList();
        }
        return null;
    }

    public List<String> getAdVerificationJavaScriptUrlList() {
        if (adVerifications != null) {
            return adVerifications.getJavaScriptResourceList();
        }
        return new ArrayList<>();
    }

    public List<String> getVideoClicksList() {
        if (creatives != null) {
            return creatives.getVideoClicksList();
        }
        return new ArrayList<>();
    }
}
