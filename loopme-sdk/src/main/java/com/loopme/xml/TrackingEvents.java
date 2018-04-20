package com.loopme.xml;

import com.loopme.parser.xml.Tag;

import java.util.ArrayList;
import java.util.List;

public class TrackingEvents {

    @Tag("Tracking")
    private List<Tracking> trackingList;

    public List<Tracking> getTrackingList() {
        return trackingList;
    }

    public List<String> getCompanionTrackingEvents() {
        List<String> trackingEventsList = new ArrayList<>();
        if (trackingList != null) {
            for (Tracking tracking : trackingList) {
                if (tracking.hasText()) {
                    trackingEventsList.add(tracking.getText());
                }
            }
        }
        return trackingEventsList;
    }
}
