package com.loopme.vast;

public class TrackingEvent {
    public String url;
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
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + timeMillis;
        return result;
    }

}
