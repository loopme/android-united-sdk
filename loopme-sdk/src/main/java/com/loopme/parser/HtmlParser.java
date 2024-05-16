package com.loopme.parser;


import android.os.Build;

import com.loopme.Constants;
import com.loopme.Logging;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class HtmlParser {

    private static final String LOG_TAG = HtmlParser.class.getSimpleName();
    private static final String WIDTH = "WIDTH";
    private static final String HEIGHT = "HEIGHT";
    private static final String MACROS = "macros";
    private static final String EMPTY_STRING = "";
    private final String mHtml;
    private String mJsonScript;

    public HtmlParser(String html) {
        this.mHtml = html;
        try {
            mJsonScript = mHtml.substring(mHtml.indexOf("{"), mHtml.lastIndexOf("}") + 1);
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Not appropriate ad type or response format, dimensions are taken by default ");
        }
    }

    public String getObject(String name) {
        try {
            JSONObject jsonObject = new JSONObject(mJsonScript);
            String param = jsonObject.getJSONObject(MACROS).getString(name);
            return decode(param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    @Deprecated
    public static String decode(String source) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
            URLDecoder.decode(source, StandardCharsets.UTF_8) : URLDecoder.decode(source);
    }

    public int getAdWidth() {
        try {
            return Integer.parseInt(getObject(WIDTH));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Not appropriate ad type, ad width by default");
        }
        return Constants.DEFAULT_BANNER_WIDTH;
    }

    public int getAdHeight() {
        try {
            return Integer.parseInt(getObject(HEIGHT));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Not appropriate ad type, ad height by default");
        }
        return Constants.DEFAULT_BANNER_HEIGHT;
    }
}
