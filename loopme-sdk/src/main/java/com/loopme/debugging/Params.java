package com.loopme.debugging;

public class Params {

    //common
    public static final String DEVICE_OS = "device_os";//const ["ios"|"android"]
    public static final String SDK_TYPE = "sdk_type";//const ["loopme"|"vast"]
    public static final String SDK_VERSION = "sdk_version";//example: "6.1.0"
    public static final String DEVICE_ID = "device_id";//example: "ae98582a-a9f2-4caa-8c20-f4e3ab37d001"
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

    //Live Debug
    public static final String DEBUG_LOGS = "debug_logs";// example: "ui: Debug.LoopMe.BaseAd: Start fetcher timeout timer, ui: Debug.LoopMe.FileUtils: In cache 2 file(s)..."
    public static final String APP_IDS = "app_ids";

    //Error Log
    public static final String ERROR_TYPE = "error_type";//["server"|"bad_asset"|"js"|"custom"]
    public static final String ERROR_MSG = "error_msg";//examples depends on error_type:
//            "server" -> "Timeout"
//            "server" -> "Server code 502"
//            "bad_asset" -> "Wrong encoding: https://i.loopme.me/fd192b26e6c548af.mp4"
//            "bad_asset" -> "File not found: https://i.loopme.me/abcd.jpg"
//            "js" -> "L is not defined"

}
