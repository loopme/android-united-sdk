package com.loopme.xml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.AltText;
import com.loopme.xml.vast4.HTMLResource;
import com.loopme.xml.vast4.IFrameResource;

import java.util.ArrayList;
import java.util.List;

public class Companion {

    @Attribute
    private String id;
    @Attribute
    private int width;
    @Attribute
    private int height;
    @Attribute
    private int assetWidth;
    @Attribute
    private int assetHeight;
    @Attribute
    private int expandedWidth;
    @Attribute
    private int expandedHeight;
    @Attribute
    private String apiFramework;
    @Attribute
    private String adSlotId;
    @Attribute
    private String pxration;

    @Tag
    private StaticResource staticResource;
    @Tag
    private IFrameResource iFrameResource;
    @Tag
    private HTMLResource htmlResource;
    @Tag
    private TrackingEvents trackingEvents;
    @Tag
    private CompanionClickThrough companionClickThrough;
    @Tag("CompanionClickTracking")
    private List<CompanionClickTracking> companionClickTracking;
    @Tag
    private AdParameters adParameters;
    @Tag
    private AltText altText;

    public String getId() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public StaticResource getStaticResource() { return staticResource; }
    public IFrameResource getFrameResource() { return iFrameResource; }
    public AltText getAltText() { return altText; }
    public IFrameResource getIFrameResource() { return iFrameResource; }
    public HTMLResource getHtmlResource() { return htmlResource; }
    public AdParameters getAdParameters() { return adParameters; }
    public int getExpandedWidth() { return expandedWidth; }
    public int getExpandedHeight() { return expandedHeight; }
    public String getApiFramework() { return apiFramework; }
    public TrackingEvents getTrackingEvents() { return trackingEvents; }
    public CompanionClickThrough getCompanionClickThrough() { return companionClickThrough; }
    public List<CompanionClickTracking> getCompanionClickTracking() { return companionClickTracking; }
    public int getAssetWidth() { return assetWidth; }
    public int getAssetHeight() { return assetHeight; }
    public String getAdSlotId() { return adSlotId; }
    public String getPxration() { return pxration; }

    @NonNull
    public List<String> getCompanionViewEvents () {
        if (trackingEvents == null) return new ArrayList<>();
        List<String> events = new ArrayList<>();
        for (Tracking tracking : trackingEvents.getTrackingList()) {
            events.add(tracking.getText());
        }
        return events;
    }

    public String getCompanionRedirectUrl() {
        return (companionClickThrough != null && companionClickThrough.getText() != null) ?
            companionClickThrough.getText().trim() : "";
    }

    public List<String> getCompanionClickEvents () {
        if (companionClickTracking == null) return new ArrayList<>();
        List<String> clickEvents = new ArrayList<>();
        for (CompanionClickTracking tracking : companionClickTracking) {
            clickEvents.add(tracking.getText());
        }
        return clickEvents;
    }

    public List<String> getCompanionTrackingEvents() {
        List<String> trackingEventsList = new ArrayList<>();
        if (trackingEvents != null ) {
            trackingEventsList.addAll(trackingEvents.getCompanionTrackingEvents());
        }
        return trackingEventsList;
    }

    public List<String> getCompanionClickTrackingList() {
        List<String> clickTrackingList = new ArrayList<>();
        if (companionClickTracking != null) {
            for (CompanionClickTracking clickTracking : companionClickTracking) {
                if (clickTracking.hasText()) {
                    clickTrackingList.add(clickTracking.getText());
                }
            }
        }
        return clickTrackingList;
    }

    @NonNull
    private static List<Companion> getCompanionList(@NonNull List<Creative> creativeList) {
        for (Creative creative : creativeList) {
            boolean isCompanionAdsNull = creative == null ||
                creative.getCompanionAds() == null ||
                creative.getCompanionAds().getCompanionList() == null;
            if (!isCompanionAdsNull) {
                return creative.getCompanionAds().getCompanionList();
            }
        }
        return new ArrayList<>();
    }

    @Nullable
    public static Companion getCompanion(List<Creative> creatives) {
        List<Companion> companionList = getCompanionList(creatives);
        return (companionList.isEmpty() || companionList.get(0) == null) ? null : companionList.get(0);
    }

    public String getCompanionUrl() { return getStaticResource().getText().trim(); }
}
