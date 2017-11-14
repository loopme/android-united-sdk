package com.loopme.models;

import com.loopme.Constants;
import com.loopme.common.LoopMeError;

public class Errors {

    public static final LoopMeError DOWNLOAD_ERROR = new LoopMeError(100, "DOWNLOAD_ERROR");
    public static final LoopMeError NOT_SUPPORTED_ANDROID_VERSION_ERROR = new LoopMeError(101, "Not supported Android version. Expected Android 4.4+");


    public static final LoopMeError NO_CONNECTION = new LoopMeError(102, "No connection");
    public static final LoopMeError BROKEN_RESPONSE = new LoopMeError(103, "Broken response");
    public static final LoopMeError HTML_LOADING_ERROR = new LoopMeError(104, "Html loading error");
    public static final LoopMeError NO_VALID_ADS_FOUND = new LoopMeError(105, "No valid ads found");
    public static final LoopMeError AD_PROCESSING_TIMEOUT = new LoopMeError(106, "Ad processing timeout");
    public static final LoopMeError ERROR_BUILD_REQUEST_URL = new LoopMeError(107, "Error during building ad request url");
    public static final LoopMeError REQUEST_TIMEOUT = new LoopMeError(108, "Request timeout");
    public static final LoopMeError NO_CONTENT = new LoopMeError(109, "No content");
    public static final LoopMeError REQUEST_PROBLEM = new LoopMeError(109, "Http request problem");
    public static final LoopMeError PARSE_JSON_ERROR = new LoopMeError(110, "Exception during json parse");
    public static final LoopMeError WRONG_AD_FORMAT = new LoopMeError(111, "Wrong Ad format: ");
    public static final LoopMeError AD_ALREADY_LOADING_OR_SHOWING = new LoopMeError(112, "Ad already loading or showing");
    public static final LoopMeError INCORRECT_INTEGRATION = new LoopMeError(113, "Incorrect integration");
    public static final LoopMeError NO_ADS_FOUND = new LoopMeError(204, "No ads found");
    public static final LoopMeError NO_ERROR = new LoopMeError(115, "OK");
    public static final LoopMeError PARSE_VAST_ERROR = new LoopMeError(116, "Parse VAST response failed");
    public static final LoopMeError MOBILE_NETWORK_CACHE_ERROR = new LoopMeError("Mobile network. Video will not be cached");

    public static final LoopMeError SPECIFIC_WEBVIEW_ERROR = new LoopMeError(117, "Ad received specific URL loopme://webview/fail");
    public static final LoopMeError FAILED_TO_PROCESS_AD = new LoopMeError(118, "Failed to process ad");
    public static final LoopMeError ERROR_DURING_VIDEO_LOADING = new LoopMeError(120, "Error during video loading");
    public static final LoopMeError MOBILE_NETWORK_ERROR = new LoopMeError("Mobile network. Video will not be cached");
    public static final LoopMeError PARSING_ERROR = new LoopMeError("Parsing error");
    public static final LoopMeError SERVER_ERROR = new LoopMeError("Server doesn't answer. Please, try later");
    public static final LoopMeError BAD_ASSET = new LoopMeError("Bad asset", Constants.ErrorType.BAD_ASSET);
    public static final LoopMeError SYNTAX_ERROR_IN_RESPONSE = new LoopMeError(100, "Syntax error in response", Constants.ErrorType.SERVER);

    public static final LoopMeError SYNTAX_ERROR_IN_XML = new LoopMeError(100, "Syntax error in xml", Constants.ErrorType.VAST);
    public static final LoopMeError VAST_BAD_ASSET = new LoopMeError(200, "Bad asset", Constants.ErrorType.VAST);
    public static final LoopMeError GENERAL_WRAPPER_ERROR = new LoopMeError(300, "General wrapper error.", Constants.ErrorType.VAST);
    public static final LoopMeError TIMEOUT_OF_VAST_URI = new LoopMeError(301, "Timeout of VAST URI.", Constants.ErrorType.VAST);
    public static final LoopMeError WRAPPER_LIMIT_REACHED = new LoopMeError(302, "Wrapper limit reached.", Constants.ErrorType.VAST);
    public static final LoopMeError NO_VAST_RESPONSE_AFTER_WRAPPER = new LoopMeError(303, "No VAST response after wrapper.", Constants.ErrorType.VAST);
    public static final LoopMeError VPAID_FILE_NOT_FOUND = new LoopMeError(401, "File not found", Constants.ErrorType.VPAID);
    public static final LoopMeError TIMEOUT_ON_MEDIA_FILE_URI = new LoopMeError(402, "Timeout by loading media file", Constants.ErrorType.VAST);
    public static final LoopMeError VAST_COULD_NOT_FIND_SUPPORTED_FORMAT = new LoopMeError(403, "Could not find supported format", Constants.ErrorType.VAST);
    public static final LoopMeError PROBLEM_DISPLAYING_MEDIAFILE = new LoopMeError(405, "Problem displaying media file.", Constants.ErrorType.VAST);
    public static final LoopMeError COMPANION_ERROR = new LoopMeError(600, "General companion error", Constants.ErrorType.VAST);
    public static final LoopMeError GENERAL_VPAID_ERROR = new LoopMeError(901, "General vpaid error", Constants.ErrorType.VPAID);
    public static final LoopMeError VERIFICATION_UNIT_NOT_EXECUTED = new LoopMeError(410, "Verification unit was not executed.", Constants.ErrorType.VAST);
    public static final LoopMeError UNUSUAL_VIDEO_FORMAT = new LoopMeError(1000, "Player tries to play unusual video format.", Constants.ErrorType.VPAID);

    public enum VastError {
        XML_PARSING(100),
        TRAFFICKING(200),
        WRAPPER(300),
        WRAPPER_TIMEOUT(301),
        WRAPPER_LIMIT(302),
        WRAPPER_NO_VAST(303),
        UNABLE_DISPLAY_LINEAR_AD(400),
        FILE_NOT_FOUND(401),
        TIMEOUT(402),
        MEDIA_FILE_NO_SUPPORTED_TYPE(403),
        MEDIA_FILE_UNSUPPORTED(405),
        COMPANION(600),
        UNDEFINED(900),
        VPAID(901);

        private int value;

        VastError(int value) {
            this.value = value;
        }

        public String getValue() {
            return String.valueOf(value);
        }
    }

}
