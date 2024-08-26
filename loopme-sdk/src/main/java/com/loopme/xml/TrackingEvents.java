package com.loopme.xml;

import androidx.annotation.NonNull;

import com.loopme.parser.xml.Tag;

import java.util.ArrayList;
import java.util.List;

public class TrackingEvents {

    @Tag("Tracking")
    private List<Tracking> trackingList;

    @NonNull
    public List<Tracking> getTrackingList() {
        return trackingList == null ? new ArrayList<>() : trackingList;
    }

    @NonNull
    public List<String> getCompanionTrackingEvents() {
        if (trackingList == null) return new ArrayList<>();
        List<String> trackingEventsList = new ArrayList<>();
        for (Tracking tracking : trackingList) {
            if (tracking.hasText()) trackingEventsList.add(tracking.getText());
        }
        return trackingEventsList;
    }
}
