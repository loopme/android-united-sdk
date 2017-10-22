package com.loopme.xml;

import com.loopme.parser.xml.Tag;

import java.util.List;

public class TrackingEvents {

    @Tag("Tracking")
    private List<Tracking> trackingList;

    public List<Tracking> getTrackingList() {
        return trackingList;
    }
}
