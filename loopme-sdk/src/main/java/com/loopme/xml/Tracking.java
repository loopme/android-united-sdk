package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;
import com.loopme.tracker.constants.EventConstants;

public class Tracking {

    @Attribute
    private String event = "";

    @Attribute
    private String offset = "";

    @Text
    private String text = "";

    public Tracking() {
    }

    public Tracking(String event, String offset, String text) {
        this.event = event;
        this.offset = offset;
        this.text = text;
    }

    public String getEvent() {
        return event;
    }

    public String getOffset() {
        return offset;
    }

    public String getText() {
        return text;
    }

    public boolean isProgressEvent() {
        return event != null && event.equalsIgnoreCase(EventConstants.PROGRESS);
    }

    public boolean isCreativeViewEvent() {
        return event != null && event.equalsIgnoreCase(EventConstants.CREATIVE_VIEW);
    }

    public boolean isStartEvent() {
        return event != null && event.equalsIgnoreCase(EventConstants.START);
    }

    public boolean isFirstQuartileEvent() {
        return event != null && event.equalsIgnoreCase(EventConstants.FIRST_QUARTILE);
    }

    public boolean isMidpointEvent() {
        return event != null && event.equalsIgnoreCase(EventConstants.MIDPOINT);
    }

    public boolean isThirdQuartileEvent() {
        return event != null && event.equalsIgnoreCase(EventConstants.THIRD_QUARTILE);
    }

    public boolean isTypeOf(String eventType) {
        return event != null && event.equalsIgnoreCase(eventType);
    }
}
