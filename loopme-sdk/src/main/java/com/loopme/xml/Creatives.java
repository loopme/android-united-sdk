package com.loopme.xml;

import androidx.annotation.NonNull;

import com.loopme.parser.xml.Tag;

import java.util.ArrayList;
import java.util.List;

public class Creatives {

    @Tag("Creative")
    private List<Creative> creativeList;

    public boolean hasCreativeList() { return creativeList != null && !creativeList.isEmpty(); }

    public List<Creative> getCreativeList() {
        return creativeList == null ? new ArrayList<>() : creativeList;
    }

    @NonNull
    public List<Tracking> getTrackingList() {
        if (creativeList == null) return new ArrayList<>();
        for (Creative creative : creativeList) {
            if (creative.getLinear() != null) {
                return creative.getLinear().getTrackingList();
            }
        }
        return new ArrayList<>();
    }

    public List<String> getVideoClicksList() {
        if (creativeList != null) {
            List<String> videoClicksList = new ArrayList<>();
            for (Creative creative : creativeList) {
                videoClicksList.addAll(creative.getVideoClicksList());
            }
            return videoClicksList;
        }
        return new ArrayList<>();
    }

    public List<String> getCompanionTrackingEvents() {
        List<String> trackingEvents = new ArrayList<>();
        if (creativeList != null) {
            for (Creative creative : creativeList) {
                trackingEvents.addAll(creative.getCompanionTrackingEvents());
            }
        }
        return trackingEvents;
    }

    public List<String> getCompanionClickTrackingList() {
        List<String> clickTrackingList = new ArrayList<>();
        if (creativeList != null) {
            for (Creative creative : creativeList) {
                clickTrackingList.addAll(creative.getCompanionClickTrackingList());
            }
        }
        return clickTrackingList;
    }
}
