package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.Icons;

import java.util.ArrayList;
import java.util.List;

public class Linear {

    @Attribute
    private String skipoffset;

    @Tag
    private Duration duration;

    @Tag
    private TrackingEvents trackingEvents;

    @Tag
    private VideoClicks videoClicks;

    @Tag
    private MediaFiles mediaFiles;

    @Tag
    private AdParameters adParameters;

    @Tag
    private Icons icons;

    public Duration getDuration() {
        return duration;
    }

    public TrackingEvents getTrackingEvents() {
        return trackingEvents;
    }

    public VideoClicks getVideoClicks() {
        return videoClicks;
    }

    public MediaFiles getMediaFiles() {
        return mediaFiles;
    }

    public AdParameters getAdParameters() {
        return adParameters;
    }

    public String getSkipoffset() {
        return skipoffset;
    }

    public List<Tracking> getTrackingList() {
        if (trackingEvents != null) {
            return trackingEvents.getTrackingList();
        }
        return null;
    }

    public ArrayList<String> getVideoClicksList() {
        if (videoClicks != null) {
            return videoClicks.getClicksList();
        }
        return new ArrayList<>();
    }

    public String getOrientation() {
        MediaFile mediafile = getMediaFiles().getMediaFileList().get(0);
        return mediafile.getHeight() > mediafile.getWidth() ? "portrait" : "landscape";
    }
}
