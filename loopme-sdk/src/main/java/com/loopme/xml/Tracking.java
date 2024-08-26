package com.loopme.xml;

import android.text.TextUtils;
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

    public Tracking() { }

    public Tracking(String event, String offset, String text) {
        this.event = event;
        this.offset = offset;
        this.text = text;
    }

    public String getEvent() { return event; }
    public String getOffset() { return offset; }
    public String getText() { return text; }
    public boolean hasText() { return !TextUtils.isEmpty(text); }

    public boolean isProgressEvent() { return EventConstants.PROGRESS.equalsIgnoreCase(event); }
    public boolean isCreativeViewEvent() { return EventConstants.CREATIVE_VIEW.equalsIgnoreCase(event); }
    public boolean isStartEvent() { return EventConstants.START.equalsIgnoreCase(event); }
    public boolean isFirstQuartileEvent() { return EventConstants.FIRST_QUARTILE.equalsIgnoreCase(event); }
    public boolean isMidpointEvent() { return EventConstants.MIDPOINT.equalsIgnoreCase(event); }
    public boolean isThirdQuartileEvent() { return EventConstants.THIRD_QUARTILE.equalsIgnoreCase(event); }
    public boolean isTypeOf(String eventType) { return eventType.equalsIgnoreCase(event); }
}
