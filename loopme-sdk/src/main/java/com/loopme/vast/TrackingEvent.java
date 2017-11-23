package com.loopme.vast;

public class TrackingEvent {
    public String url;
    public int timeMillis;

    public TrackingEvent(String url) {
        this.url = url;
    }

    public TrackingEvent(String url, int timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackingEvent event = (TrackingEvent) o;

        if (timeMillis != event.timeMillis) return false;
        return url.equals(event.url);

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + timeMillis;
        return result;
    }

}
