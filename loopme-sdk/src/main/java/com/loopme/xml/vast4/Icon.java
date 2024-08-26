package com.loopme.xml.vast4;


import com.loopme.parser.xml.Tag;
import com.loopme.xml.StaticResource;

public class Icon {
    @Tag
    private StaticResource staticResource;
    @Tag
    private IFrameResource iFrameResource;
    @Tag
    private HTMLResource htmlResource;
    @Tag
    private IconClicks iconClicks;
    @Tag
    private IconViewTracking iconViewTracking;

    public StaticResource getStaticResource() { return staticResource; }
    public IFrameResource getiFrameResource() { return iFrameResource; }
    public HTMLResource getHtmlResource() { return htmlResource; }
    public IconClicks getIconClicks() { return iconClicks; }
    public IconViewTracking getIconViewTracking() { return iconViewTracking; }
}
