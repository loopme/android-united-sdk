package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.AltText;
import com.loopme.xml.vast4.HTMLResource;
import com.loopme.xml.vast4.IFrameResource;

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

    public IFrameResource getFrameResource() {
        return iFrameResource;
    }

    public AltText getAltText() {
        return altText;
    }

    public IFrameResource getIFrameResource() {
        return iFrameResource;
    }

    public HTMLResource getHtmlResource() {
        return htmlResource;
    }

    public AdParameters getAdParameters() {
        return adParameters;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getExpandedWidth() {
        return expandedWidth;
    }

    public int getExpandedHeight() {
        return expandedHeight;
    }

    public String getApiFramework() {
        return apiFramework;
    }

    public StaticResource getStaticResource() {
        return staticResource;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public CompanionClickThrough getCompanionClickThrough() {
        return companionClickThrough;
    }

    public List<CompanionClickTracking> getCompanionClickTracking() {
        return companionClickTracking;
    }

    public int getAssetWidth() {
        return assetWidth;
    }

    public int getAssetHeight() {
        return assetHeight;
    }

    public String getAdSlotId() {
        return adSlotId;
    }

    public String getPxration() {
        return pxration;
    }
}
