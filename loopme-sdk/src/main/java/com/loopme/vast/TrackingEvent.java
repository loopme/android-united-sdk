package com.loopme.vast;

import com.loopme.utils.Utils;
import com.loopme.xml.Tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrackingEvent {
    public final String url;
    public int timeMillis;

    public TrackingEvent(String url) {
        this.url = url;
    }

    public TrackingEvent(String url, int timeMillis) {
        this.timeMillis = timeMillis;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackingEvent that = (TrackingEvent) o;

        if (timeMillis != that.timeMillis) return false;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return 31 * url.hashCode() + timeMillis;
    }

    public static List<TrackingEvent> createProgressPoints(
        int duration,
        List<String> impressions,
        List<Tracking> trackingEvents,
        String skipTime
    ) {
        List<TrackingEvent> trackingEventsList = new ArrayList<>();

        for (String url : impressions) {
            trackingEventsList.add(new TrackingEvent(url));
        }

        for (Tracking tracking : trackingEvents) {
            if (tracking == null) {
                continue;
            }
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.isCreativeViewEvent()) {
                event.timeMillis = 0;
                trackingEventsList.add(event);
            }
            if (tracking.isStartEvent()) {
                event.timeMillis = 0;
                trackingEventsList.add(event);
            }
            if (tracking.isFirstQuartileEvent()) {
                event.timeMillis = duration / 4;
                trackingEventsList.add(event);
            }
            if (tracking.isMidpointEvent()) {
                event.timeMillis = duration / 2;
                trackingEventsList.add(event);
            }
            if (tracking.isThirdQuartileEvent()) {
                event.timeMillis = duration * 3 / 4;
                trackingEventsList.add(event);
            }
            if (tracking.isProgressEvent() && tracking.getOffset() != null) {
                event.timeMillis = tracking.getOffset().contains("%") ?
                        duration * Utils.parsePercent(skipTime) / 100 :
                        Utils.parseDuration(tracking.getOffset()) * 1000;
                trackingEventsList.add(event);
            }
        }
        return trackingEventsList;
    }

}
