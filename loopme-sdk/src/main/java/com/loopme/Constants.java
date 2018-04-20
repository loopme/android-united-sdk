package com.loopme;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.utils.Utils;

public class Constants {
    public static final int DEFAULT_WIDTH = 350;
    public static final int DEFAULT_HEIGHT = 250;
    public static final String UTF_8 = "UTF-8";
    public static final String MIME_TYPE_TEXT_HTML = "text/html";
    public static final String MRAID_ANDROID_ASSET = "file:///android_asset/";
    public static final String EXTRA_URL = "extraUrl";
    public static final String ADS = "ads";
    public static final String VIEWABLE = "VIEWABLE";
    public static final String NOT_VIEWABLE = "NOT_VIEWABLE";
    public static final String VIEW_UNDETERMINED = "VIEW_UNDETERMINED";
    public static final String WEBM_FORMAT_EXT = "webm";
    public static final String LOOPME_SDK_TYPE = "loopme";
    public static final String ADNROID_DEVICE_OS = "android";
    public static final String SDK_DEBUG_MSG = "sdk_debug";
    public static final String SDK_ERROR_MSG = "sdk_error";
    public static final String BAD_SERVERS_CODE = "Bad servers response code ";
    public static final String AD_ID_TAG = "adIdTag";
    public static final int MAX_FAIL_COUNT = 5;
    public static final int ONE_MINUTE_IN_MILLIS = 1000 * 60;
    public static final int SLEEP_TIME = ONE_MINUTE_IN_MILLIS * 15;
    public static final int DEFAULT_AD_ID = -1;
    public static final long ONE_SECOND_IN_MILLIS = 1000;
    public static final int AUTO_LOADING_ABSENCE = -1;
    public static final int BUTTON_SIZE = Utils.convertDpToPixel(40);

    public static final int DEFAULT_BANNER_WIDTH = 350;
    public static final int DEFAULT_BANNER_HEIGHT = 250;
    public static final long DESTROY_TIME_DELAY = 200;

    public static boolean sDebugMode = true;

    private Constants() {
    }

    public static final String HTTPS_SCHEME = "https";
    public static final String HTTP_PROTOCOL = "http";
    public static final String MP4_FORMAT = ".mp4";
    public static final String MP4_FORMAT_EXT = "mp4";
    public static final String FORMAT_TAG = "format";
    public static final String BANNER_TAG = "banner";
    public static final String UNKNOWN_NAME = "unknown";
    public static final String VIDEO_FOLDER = "LoopMeAds";
    public static final String EVENT_VIDEO_25 = "VIDEO_25";
    public static final String EVENT_VIDEO_50 = "VIDEO_50";
    public static final String EVENT_VIDEO_75 = "VIDEO_75";
    public static final String VIEWER_TOKEN = "viewer_token";
    public static final String ORIENTATION_PORT = "portrait";
    public static final String ORIENTATION_LAND = "landscape";
    public static final String INTERSTITIAL_TAG = "interstitial";
    public static final String EXTRAS_CUSTOM_CLOSE = "customClose";
    public static final String CACHED_LOG_FILE_NAME = "events_log.txt";
    public static final String CLICK_INTENT = "com.loopme.CLICK_INTENT";
    public static final String BASE_URL = HTTPS_SCHEME + "://loopme.me/api/ortb/ads";

    public static final String OPEN_RTB_URL = "https://loopme.me/api/ortb/";
    public static final String LOOPME_PREFERENCES = "LOOPME_PREFERENCES";
    public static final String DESTROY_INTENT = "com.loopme.DESTROY_INTENT";
    public static final String MRAID_NEED_CLOSE_BUTTON = "com.loopme.MRAID_NEED_CLOSE_BUTTON";
    public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String ERROR_URL = HTTPS_SCHEME + "://track.loopme.me/api/errors";
    public static final String OLD_TRACK_FEEDBACK_URL = "https://loopme.me/api/v2/events";

    public static final String TYPE_VPAID = "VPAID";
    public static final String TYPE_VAST = "VAST";
    public static final String TYPE_APPLICATION_JAVASCRIPT = "application/javascript";

    public static final int RESPONSE_SUCCESS = 200;
    public static final int START_POSITION = 0;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int MILLIS_IN_SECOND = 1000;
    public static final int REQUEST_TIMEOUT = 1000 * 15; //15 seconds
    public static final int PREPARE_VAST_ASSET_TIMEOUT = 1000 * 10; //10 seconds
    public static final int PREPARE_VPAID_JS_TIMEOUT = 1000 * 10; //10 seconds
    public static final int SHRINK_MODE_KEEP_AFTER_FINISH_TIME = 1000;
    public static final int DEFAULT_EXPIRED_TIME = 1000 * 60 * 10;//10 minutes
    public static final long FETCH_TIMEOUT = 1000 * 60;//1 minute
    public static long CACHED_VIDEO_LIFE_TIME = 1000 * 60 * 60 * 32;//32 hours

    public static boolean USE_MOBILE_NETWORK_FOR_CACHING = false;
    public static String sCacheDirectory = "";
    public static final String EXTRAS_ALLOW_ORIENTATION_CHANGE = "allowOrientationChange";
    public static final String EXTRAS_FORCE_ORIENTATION = "forceOrientation";


    public enum AdFormat {
        BANNER,
        INTERSTITIAL,
        EXPANDABLE_BANNER;

        public static AdFormat fromInt(int format) {
            if (format == BANNER.ordinal()) {
                return BANNER;
            } else if (format == INTERSTITIAL.ordinal()) {
                return INTERSTITIAL;
            } else {
                return EXPANDABLE_BANNER;
            }
        }
    }

    public enum AdState {
        /**
         * Initial state of ad right after creation.
         * Can be also after onHide() notification or destroy().
         */
        NONE,

        /**
         * Ad currently in "loading" process.
         * Can be between trigger load() and onLoadSuccess(), onLoadFail() notifications or destroy().
         * While Ad in this state all other calling `load` methods will be ignored
         */
        LOADING,

        /**
         * Ad currently displays on screen.
         * Can be between trigger show() and onHide() notification or destroy()
         */
        SHOWING
    }

    public class ConnectionType {
        public static final int UNKNOWN = 0;
        public static final int ETHERNET = 1;
        public static final int WIFI = 2;
        public static final int MOBILE_UNKNOWN_GENERATION = 3;
        public static final int MOBILE_2G = 4;
        public static final int MOBILE_3G = 5;
        public static final int MOBILE_4G = 6;

        private ConnectionType() {
        }
    }

    public class DeviceType {
        public static final String PHONE = "PHONE";
        public static final String TABLET = "TABLET";
        public static final String UNKNOWN = "UNKNOWN";

        private DeviceType() {
        }
    }

    public enum DisplayMode {
        NORMAL,
        MINIMIZED,
        FULLSCREEN
    }

    public enum StretchOption {
        NONE,
        STRETCH,
        NO_STRETCH
    }

    private static final int IDLE_INTEGER = 0;
    private static final int READY_INTEGER = 1;
    private static final int PLAYING_INTEGER = 2;
    private static final int PAUSED_INTEGER = 3;
    private static final int COMPLETE_INTEGER = 4;
    private static final int BROKEN_INTEGER = 5;
    private static final int BUFFERING_INTEGER = 6;

    public enum VideoState {
        IDLE(IDLE_INTEGER, "IDLE"),
        READY(READY_INTEGER, "READY"),
        PLAYING(PLAYING_INTEGER, "PLAYING"),
        PAUSED(PAUSED_INTEGER, "PAUSED"),
        COMPLETE(COMPLETE_INTEGER, "COMPLETE"),
        BROKEN(BROKEN_INTEGER, "BROKEN"),
        BUFFERING(BUFFERING_INTEGER, "BUFFERING)");

        private int mId;
        private String mName;

        VideoState(int id, String name) {
            mId = id;
            mName = name;
        }

        public int getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public static String nameOf(int state) {
            switch (state) {
                case IDLE_INTEGER: {
                    return IDLE.getName();
                }
                case READY_INTEGER: {
                    return READY.getName();
                }
                case PLAYING_INTEGER: {
                    return PLAYING.getName();
                }
                case PAUSED_INTEGER: {
                    return PAUSED.getName();
                }
                case COMPLETE_INTEGER: {
                    return COMPLETE.getName();
                }
                case BROKEN_INTEGER: {
                    return BROKEN.getName();
                }
                case BUFFERING_INTEGER: {
                    return BUFFERING.getName();
                }
                default: {
                    return UNKNOWN_NAME;
                }
            }
        }
    }

    private static final int VISIBLE_INTEGER = 1;
    private static final int HIDDEN_INTEGER = 2;
    private static final int CLOSED_INTEGER = 3;

    public enum WebviewState {

        VISIBLE(VISIBLE_INTEGER, "VISIBLE"),
        HIDDEN(HIDDEN_INTEGER, "HIDDEN"),
        CLOSED(CLOSED_INTEGER, "UNKNOWN");

        private int mId;
        private String mName;

        WebviewState(int id, String name) {
            mId = id;
            mName = name;
        }

        public int getId() {
            return mId;
        }

        public String getName() {
            return mName;
        }

        public static String nameOf(int state) {
            switch (state) {
                case VISIBLE_INTEGER: {
                    return VISIBLE.getName();
                }
                case HIDDEN_INTEGER: {
                    return HIDDEN.getName();
                }
                case CLOSED_INTEGER: {
                    return CLOSED.getName();
                }
                default: {
                    return UNKNOWN_NAME;
                }
            }
        }
    }

    public class ErrorType {
        public static final String SERVER = "server";
        public static final String BAD_ASSET = "bad_asset";
        public static final String JS = "js";
        public static final String CUSTOM = "custom";
        public static final String VAST = "vast";
        public static final String VPAID = "vpaid";
        public static final String DO_NOT_TRACK = "do not track";
    }

    class Params {
        public static final String DEVICE_OS = "device_os";
        public static final String SDK_TYPE = "sdk_type";
        public static final String SDK_VERSION = "sdk_version";
        public static final String DEVICE_ID = "device_id";
        public static final String PACKAGE_ID = "package";
        public static final String APP_KEY = "app_key";
        public static final String MSG = "msg";
        public static final String DEBUG_LOGS = "debug_logs";
        public static final String ERROR_TYPE = "error_type";
        public static final String ERROR_MSG = "error_msg";
    }

    public class MraidState {
        public static final String DEFAULT = "default";
        public static final String LOADING = "loading";
        public static final String EXPANDED = "expanded";
        public static final String HIDDEN = "hidden";
        public static final String RESIZED = "resized";
    }

    public class VREvents {
        public static final String SWIPE = "SWIPE";
        public static final String GYRO = "GYRO";
        public static final String ACCEL = "ACCEL";
        public static final String FRONT = "FRONT";
        public static final String LEFT = "LEFT";
        public static final String RIGHT = "RIGHT";
        public static final String BACK = "BACK";
    }

    public class ViewQuarter {
        public static final int UNKNOWN = 0;
        public static final int FRONT = 1;
        public static final int LEFT = 2;
        public static final int RIGHT = 3;
        public static final int BACK = 4;
    }

    public static class Banner {
        public static final int MPU_BANNER_WIDTH = 300;
        public static final int MPU_BANNER_HEIGHT = 250;
        public static final int EXPAND_BANNER_WIDTH = 320;
        public static final int EXPAND_BANNER_HEIGHT = 50;
        public static final int SIZE_DISCREPANCY = 2;

        public static int[] EXPANDABLE_BANNER_SIZE = {EXPAND_BANNER_WIDTH, EXPAND_BANNER_HEIGHT};
        public static int[] MPU_BANNER_SIZE = {MPU_BANNER_WIDTH, MPU_BANNER_HEIGHT};
    }

    public enum RetrofitType {
        FETCH, DOWNLOAD
    }
}
