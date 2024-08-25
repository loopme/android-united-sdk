package com.loopme.xml;


import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class MediaFile {

    @Attribute
    private String id;

    /**
     * streaming, progressive
     */
    @Attribute
    private String delivery;
    @Attribute
    private String type;
    @Attribute
    private int bitrate;
    @Attribute
    private int maxBitrate;
    @Attribute
    private int minBitrate;
    @Attribute
    private int width;
    @Attribute
    private int height;
    @Attribute
    private boolean scalable;
    @Attribute
    private boolean maintainAspectRatio;
    @Attribute
    private String apiFramework;
    @Attribute
    private String codec;

    @Text
    private String text;

    public String getId() { return id; }
    public String getType() { return type; }
    public int getBitrate() { return bitrate; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getApiFramework() { return apiFramework; }
    public String getText() { return text; }
    public String getDelivery() { return delivery; }
    public boolean isScalable() { return scalable; }
    public boolean isMaintainAspectRatio() { return maintainAspectRatio; }
}
