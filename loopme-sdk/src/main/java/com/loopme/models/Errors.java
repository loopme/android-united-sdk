package com.loopme.models;

import com.loopme.Constants;
import com.loopme.common.LoopMeError;

public class Errors {
    public static final String ERROR_MESSAGE_NETWORK_OPERATION = "Network operation timeout.";
    public static final String ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE = "Server's response is broken.";
    public static final String ERROR_MESSAGE_RESPONSE_SYNTAX_ERROR = "Syntax error in response.";

    public static final LoopMeError DOWNLOAD_ERROR = new LoopMeError(
            100, "DOWNLOAD_ERROR");

    public static final LoopMeError NOT_SUPPORTED_ANDROID_VERSION_ERROR = new LoopMeError(
            101, "Not supported Android version. Expected Android 4.4+");

    public static final LoopMeError NO_CONNECTION = new LoopMeError(
            102, "No connection");

    public static final LoopMeError BROKEN_RESPONSE = new LoopMeError(
            103, "Broken response");

    public static final LoopMeError HTML_LOADING_ERROR = new LoopMeError(
            104, "Html loading error");

    public static final LoopMeError NO_VALID_ADS_FOUND = new LoopMeError(
            105, "No valid ads found (Video, VPAID js, HTML does not exist in response)");

    public static final LoopMeError AD_PROCESSING_TIMEOUT = new LoopMeError(
            106, "Ad processing timeout");

    public static final LoopMeError ERROR_BUILD_REQUEST_URL = new LoopMeError(
            107, "Error during building ad request url");

    public static final LoopMeError REQUEST_TIMEOUT = new LoopMeError(
            108, "Request timeout");

    public static final LoopMeError NO_CONTENT = new LoopMeError(
            109, "No content");

    public static final LoopMeError REQUEST_PROBLEM = new LoopMeError(
            109, "Http request problem");

    public static final LoopMeError PARSE_JSON_ERROR = new LoopMeError(
            110, "Exception during json parse");

    public static final LoopMeError WRONG_AD_FORMAT = new LoopMeError(
            111, "Wrong Ad format: ");

    public static final LoopMeError AD_ALREADY_LOADING_OR_SHOWING = new LoopMeError(
            112, "Ad already loading or showing");

    public static final LoopMeError INCORRECT_INTEGRATION = new LoopMeError(
            113, "Incorrect integration");

    public static final LoopMeError NO_ERROR = new LoopMeError(
            115, "OK");

    public static final LoopMeError PARSE_VAST_ERROR = new LoopMeError(
            116, "Parse VAST response failed");

    public static final LoopMeError MOBILE_NETWORK_CACHE_ERROR = new LoopMeError(
            "Mobile network. Video will not be cached");

    public static final LoopMeError CACHE_ERROR = new LoopMeError(
            "File not cached");

    public static final LoopMeError SPECIFIC_WEBVIEW_ERROR = new LoopMeError(
            117, "Ad received specific URL loopme://webview/fail");

    public static final LoopMeError FAILED_TO_PROCESS_AD = new LoopMeError(
            118, "Failed to process ad");

    public static final LoopMeError ERROR_DURING_VIDEO_LOADING = new LoopMeError(
            120, "Error during video loading");

    public static final LoopMeError MOBILE_NETWORK_ERROR = new LoopMeError(
            "Mobile network. Video will not be cached");

    public static final LoopMeError PARSING_ERROR = new LoopMeError(
            "Parsing error");

    public static final LoopMeError SERVER_ERROR = new LoopMeError(
            "Server doesn't answer. Please, try later");

    public static final LoopMeError BAD_ASSET = new LoopMeError(
            "Bad asset", Constants.ErrorType.BAD_ASSET);

    public static final LoopMeError SYNTAX_ERROR_IN_RESPONSE = new LoopMeError(
            100, "Syntax error in response", Constants.ErrorType.SERVER);

    public static final LoopMeError AD_LOAD_ERROR = new LoopMeError(
            121, "Ad load failed", Constants.ErrorType.CUSTOM);

    // region DO_NOT_TRACK

    public static final LoopMeError NO_ADS_FOUND = new LoopMeError(
            204, "No ads found", Constants.ErrorType.DO_NOT_TRACK);

    public static final LoopMeError JS_LOADING_TIMEOUT = new LoopMeError(
            1001, "Js loading timeout", Constants.ErrorType.DO_NOT_TRACK);

    // endregion

    // region VPAID

    public static final LoopMeError VPAID_FILE_NOT_FOUND = new LoopMeError(
            401, "File not found", Constants.ErrorType.VPAID);

    public static final LoopMeError GENERAL_VPAID_ERROR = new LoopMeError(
            901, "General vpaid error", Constants.ErrorType.VPAID);

    public static final LoopMeError UNUSUAL_VIDEO_FORMAT = new LoopMeError(
            1000, "Player tries to play unusual video format.", Constants.ErrorType.VPAID);

    // endregion

    // region VAST

    public static final LoopMeError SYNTAX_ERROR_IN_XML = new LoopMeError(
            100, "Syntax error in xml", Constants.ErrorType.VAST);

    public static final LoopMeError SCHEMA_VALIDATION_ERROR = new LoopMeError(
            101, "VAST schema validation error", Constants.ErrorType.VAST);

    public static final LoopMeError RESPONSE_VERSION_NOT_SUPPORTED = new LoopMeError(
            102, "VAST version of response not supported", Constants.ErrorType.VAST);

    public static final LoopMeError VAST_BAD_ASSET = new LoopMeError(
            200, "Bad asset", Constants.ErrorType.VAST);

    public static final LoopMeError PLAYER_DIFFERENT_LINEARITY_EXPECTED = new LoopMeError(
            201, "Media player expecting different linearity", Constants.ErrorType.VAST);

    public static final LoopMeError PLAYER_DIFFERENT_DURATION_EXPECTED = new LoopMeError(
            202, "Media player expecting different duration", Constants.ErrorType.VAST);

    public static final LoopMeError PLAYER_DIFFERENT_SIZE_EXPECTED = new LoopMeError(
            203, "Media player expecting different size", Constants.ErrorType.VAST);

    public static final LoopMeError AD_CATEGORY_REQUIRED = new LoopMeError(
            204, "Ad category was required but not provided", Constants.ErrorType.VAST);

    public static final LoopMeError INLINE_CATEGORY_VIOLATION = new LoopMeError(
            205, "Inline Category violates Wrapper BlockedAdCategories", Constants.ErrorType.VAST);

    public static final LoopMeError GENERAL_WRAPPER_ERROR = new LoopMeError(
            300, "General wrapper error", Constants.ErrorType.VAST);

    public static final LoopMeError TIMEOUT_OF_VAST_URI = new LoopMeError(
            301, "Timeout of VAST URI", Constants.ErrorType.VAST);

    public static final LoopMeError WRAPPER_LIMIT_REACHED = new LoopMeError(
            302, "Wrapper limit reached", Constants.ErrorType.VAST);

    public static final LoopMeError NO_VAST_RESPONSE_AFTER_WRAPPER = new LoopMeError(
            303, "No VAST response after wrapper", Constants.ErrorType.VAST);

    public static final LoopMeError INLINE_RESPONSE_DISPLAY_TIMEOUT = new LoopMeError(
            304, "Inline response display timeout", Constants.ErrorType.VAST);

    public static final LoopMeError GENERAL_LINEAR_ERROR = new LoopMeError(
            400, "General Linear error. Unable to display the Linear Ad", Constants.ErrorType.VAST);

    public static final LoopMeError LINEAR_FILE_URI_NOT_FOUND = new LoopMeError(
            401, "File not found. Unable to find Linear/MediaFile from URI", Constants.ErrorType.VAST);

    public static final LoopMeError TIMEOUT_ON_MEDIA_FILE_URI = new LoopMeError(
            402, "Timeout by loading media file", Constants.ErrorType.VAST);

    public static final LoopMeError VAST_COULD_NOT_FIND_SUPPORTED_FORMAT = new LoopMeError(
            403, "Could not find supported format", Constants.ErrorType.VAST);

    public static final LoopMeError PROBLEM_DISPLAYING_MEDIAFILE = new LoopMeError(
            405, "Problem displaying media file", Constants.ErrorType.VAST);

    public static final LoopMeError MEZZANINE_REQUIRED = new LoopMeError(
            406, "Mezzanine was required but not provided", Constants.ErrorType.VAST);

    public static final LoopMeError MEZZANINE_IS_DOWNLOADING_NOW = new LoopMeError(
            407, "Mezzanine is in the process of being downloaded", Constants.ErrorType.VAST);

    public static final LoopMeError CONDITIONAL_REJECTED = new LoopMeError(
            408, "Conditional ad rejected", Constants.ErrorType.VAST);

    public static final LoopMeError INTERACTIVE_NOT_EXECUTED = new LoopMeError(
            409, "Interactive unit was not executed", Constants.ErrorType.VAST);

    public static final LoopMeError VERIFICATION_UNIT_NOT_EXECUTED = new LoopMeError(
            410, "Verification unit was not executed", Constants.ErrorType.VAST);

    public static final LoopMeError MEZZANINE_FILE_TYPE_NOT_COMPLIANT = new LoopMeError(
            411, "Mezzanine file did not meet required specification", Constants.ErrorType.VAST);

    public static final LoopMeError NON_LINEAR_ERROR = new LoopMeError(
            500, "General NonLinearAds error", Constants.ErrorType.VAST);

    public static final LoopMeError NON_LINEAR_WRONG_DIMENSIONS = new LoopMeError(
            501, "NonLinearAd dimensions do not align with creative display area", Constants.ErrorType.VAST);

    public static final LoopMeError NON_LINEAR_FETCH_ERROR = new LoopMeError(
            502, "Unable to fetch NonLinearAds/NonLinear resource", Constants.ErrorType.VAST);

    public static final LoopMeError NON_LINEAR_SUPPORTED_RESOURCE_NOT_FOUND = new LoopMeError(
            503, "Couldn’t find NonLinear resource with supported type", Constants.ErrorType.VAST);

    public static final LoopMeError COMPANION_ERROR = new LoopMeError(
            600, "General companion error", Constants.ErrorType.VAST);

    public static final LoopMeError COMPANION_WRONG_DIMENSIONS = new LoopMeError(
            601, "Companion dimensions do not fit within Companion display area", Constants.ErrorType.VAST);

    public static final LoopMeError COMPANION_DISPLAY_ERROR = new LoopMeError(
            602, "Unable to display required Companion", Constants.ErrorType.VAST);

    public static final LoopMeError COMPANION_FETCH_ERROR = new LoopMeError(
            603, "Unable to fetch CompanionAds/Companion resource", Constants.ErrorType.VAST);

    public static final LoopMeError COMPANION_SUPPORTED_RESOURCE_NOT_FOUND = new LoopMeError(
            604, "Couldn’t find Companion resource with supported type", Constants.ErrorType.VAST);

    public static final LoopMeError VAST_UNDEFINED_ERROR = new LoopMeError(
            900, "Undefined error", Constants.ErrorType.VAST);


    // endregion
}