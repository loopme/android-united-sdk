package com.loopme.utils;

import android.os.Build;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.request.AES;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class StringUtils {
    private static final String ERROR_CODE = "[ERRORCODE]";
    private static final String JS_RESOURCE = "[JSRESOURCE]";
    private static final String PLAY_TIME = "[CONTENTPLAYHEAD]";
    private static final String TIMESTAMP_CODE_PATTERN = "[TIMESTAMP]";
    private static final String ISO_8601_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.mmm'Z'";

    public static String getEncryptedString(String name) {
        AES.setDefaultKey();
        AES.encrypt(name);
        return deleteLastCharacter(AES.getEncryptedString());
    }

    public static String getUrlEncodedString(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, Constants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Build.UNKNOWN;
    }

    public static boolean hasMp4Extension(String videoUrl) {
        if (TextUtils.isEmpty(videoUrl)) {
            return false;
        }
        String[] tokens = videoUrl.split("\\.");
        return TextUtils.equals(tokens[tokens.length - 1], Constants.MP4_FORMAT_EXT);
    }

    public static String setErrorCode(String vastErrorUrl, String vastErrorCode) {
        return replace(vastErrorUrl, ERROR_CODE, vastErrorCode);
    }

    public static String setMessage(String url, String addMessage) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        String completeUrl = url;
        if (url.contains(TIMESTAMP_CODE_PATTERN)) {
            completeUrl = setTimeStamp(url);
        } else if (url.contains(ERROR_CODE)) {
            completeUrl = setErrorCode(url, addMessage);
        } else if (url.contains(PLAY_TIME)) {
            completeUrl = setPlayTime(url, addMessage);
        }

        return completeUrl;
    }

    public static String generateHtml(List<String> jsScriptList) {
        String htmlStartTag = " <html><head></head><body>\n";
        String htmlJsScript = "<script type=\"text/javascript\" src=\"[JSRESOURCE]\"></script>\n";
        String htmlEndTag = "</body></html>";
        for (String jsLink : jsScriptList) {
            htmlStartTag = htmlStartTag + replace(htmlJsScript, JS_RESOURCE, jsLink);
        }

        return htmlStartTag + htmlEndTag;
    }

    private static String setPlayTime(String trackUrl, String currentPosition) {
        return replace(trackUrl, PLAY_TIME, currentPosition);
    }

    private static String setTimeStamp(String url) {
        return replace(url, TIMESTAMP_CODE_PATTERN, getTimeInIso8061Format());
    }

    private static String getTimeInIso8061Format() {
        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_8601_TIME_FORMAT);
        dateFormat.setTimeZone(tz);
        return dateFormat.format(new Date());
    }

    private static String replace(String baseString, String pattern, String contentToReplace) {
        if (!TextUtils.isEmpty(baseString)) {
            if (baseString.contains(pattern)) {
                return baseString.replace(pattern, contentToReplace);
            } else {
                return baseString;
            }
        } else {
            return "";
        }
    }

    private static String deleteLastCharacter(String encryptedString) {
        if (!TextUtils.isEmpty(encryptedString)) {
            return encryptedString.substring(0, encryptedString.length() - 1);
        }
        return "";
    }
}
