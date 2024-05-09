package com.loopme.request;

public class RequestConstants {
    public static final String ANDROID_OS = "android";
    public static final String IAB25_3 = "IAB25-3";
    public static final String IAB25 = "IAB25";
    public static final String IAB26 = "IAB26";
    public static final String[] BCAT = new String[]{ IAB25_3, IAB25, IAB26 };
    public static final String LOOPME_SDK = "LOOPME_SDK";
    public static final String VIDEO_MP4 = "video/mp4";
    public static final String[] MIME_TYPES = new String[]{ VIDEO_MP4 };
    public static final int DEFAULT_MAX_DURATION = 999;
    public static final int BOXING_DEFAULT = 1;
    public static final int MAX_TIME_TO_SUBMIT_BID = 700;
    public static final int SECURE_IMPRESSION = 1;
    public static final int MAX_BITRATE_DEFAULT_VALUE = 1024;
    public static final int MIN_BITRATE_DEFAULT_VALUE = 5;
    public static final int SEQUENCE_DEFAULT_VALUE = 1;
    public static final int START_DELAY_DEFAULT_VALUE = 1;
    public static final int FRAMEWORK_VIPAID_2_0 = 2;
    public static final int FRAMEWORK_MRAID_2 = 5;
    public static final int FRAMEWORK_OMID_1 = 7;
    public static final int[] API_ALL = new int[]{ FRAMEWORK_MRAID_2, FRAMEWORK_VIPAID_2_0, FRAMEWORK_OMID_1 };
    public static final int[] API_HTML = new int[]{ FRAMEWORK_MRAID_2, FRAMEWORK_OMID_1 };
    public static final int[] API_VIDEO = new int[]{ FRAMEWORK_VIPAID_2_0, FRAMEWORK_OMID_1 };
    public static final int DELIVERY_METHOD_PROGRESSIVE = 2;
    public static final int[] DELIVERY_METHODS = new int[]{ DELIVERY_METHOD_PROGRESSIVE };
    public static final int PROTOCOLS_VAST_2_0 = 2;
    public static final int PROTOCOLS_VAST_3_0 = 3;
    public static final int PROTOCOLS_VAST_4_0 = 7;
    public static final int PROTOCOLS_VAST_4_0_WRAPPER = 8;
    public static final int[] PROTOCOLS = new int[]{
        PROTOCOLS_VAST_2_0, PROTOCOLS_VAST_3_0, PROTOCOLS_VAST_4_0, PROTOCOLS_VAST_4_0_WRAPPER
    };
    public static final int LINEAR_IN_STREAM = 1;
    public static final float BID_FLOOR_DEFAULT_VALUE = 0;
    public static final int DEVICE_TYPE_PHONE = 4;
    public static final int DEVICE_TYPE_TABLET = 5;
    public static final int PLUGIN = -1;
    // TODO: Describe the meaning of the following constants
    public static final int[] BATTERY_INFO = new int[]{3, 8};
    // TODO: Describe the meaning of the following constants
    public static final int[] EXPANDABLE_DIRECTION_FULLSCREEN = new int[]{5};
    public static final int JS_SUPPORTED = 1;
}
