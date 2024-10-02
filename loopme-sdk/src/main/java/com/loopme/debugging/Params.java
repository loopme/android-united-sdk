package com.loopme.debugging;

public class Params {

    //common
    public static final String DEVICE_OS = "device_os";//const ["ios"|"android"]
    public static final String SDK_TYPE = "sdk_type";//const ["loopme"|"vast"]
    public static final String MEDIATION_SDK_VERSION = "mediation_sdk_version";//example: "12.6.0"
    public static final String ADAPTER_VERSION = "adapter_version";//example: "0.0.1"
    public static final String MEDIATION = "mediation";//example: ironSource
    public static final String SDK_VERSION = "sdk_version";//example: "6.1.0"
    public static final String DEVICE_ID = "device_id";//example: "ae98582a-a9f2-4caa-8c20-f4e3ab37d001"
    public static final String DEVICE_OS_VERSION = "device_os_version";//example: 13
    public static final String DEVICE_MODEL = "device_model";//example: A201
    public static final String DEVICE_MANUFACTURER = "device_manufacturer";//example: Samsung
    public static final String PACKAGE_ID = "package";// example: "com.loopme.tester"
    public static final String APP_KEY = "app_key";// example: "c693a45a79"
    public static final String MSG = "msg";//const "sdk_debug" or "sdk_error"
    public static final String SDK_FEEDBACK = "SDK_FEEDBACK";
    public static final String EVENT_TYPE = "et";
    public static final String R = "r";
    public static final String ID = "id";
    public static final String SDK_READY = "sdk_ready";
    public static final String SDK_SHOW = "sdk_show";
    public static final String SDK_MISSED = "sdk_missed";
    public static final String PLACEMENT_TYPE = "placement";// example: banner
    public static final String CID = "cid";
    public static final String CRID = "crid";
    public static final String REQUEST_ID = "request_id";
    //Live Debug
    public static final String DEBUG_LOGS = "debug_logs";// example: "ui: Debug.LoopMe.BaseAd: Start fetcher timeout timer, ui: Debug.LoopMe.FileUtils: In cache 2 file(s)..."
    public static final String APP_IDS = "app_ids";

    //Error Log
    public static final String ERROR_TYPE = "error_type";//["server"|"bad_asset"|"js"|"custom"]
    public static final String ERROR_MSG = "error_msg";//examples depends on error_type:
    public static final String ERROR_URL = "url";
    public static final String ERROR_CONSOLE = "console";
    public static final String ERROR_CONSOLE_SOURCE_ID = "console_source_id";
    public static final String ERROR_CONSOLE_LEVEL = "console_error_level";
    public static final String ERROR_EXCEPTION = "error_exception";
    public static final String TIMEOUT = "timeout";
    public static final String STATUS = "status";
    public static final String REQUEST = "request";
//            "server" -> "Timeout"
//            "server" -> "Server code 502"
//            "bad_asset" -> "Wrong encoding: https://i.loopme.me/fd192b26e6c548af.mp4"
//            "bad_asset" -> "File not found: https://i.loopme.me/abcd.jpg"
//            "js" -> "L is not defined"

}
