package com.loopme.parser;


import com.loopme.Constants;
import com.loopme.Logging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class HtmlParser {

    private static final String LOG_TAG = HtmlParser.class.getSimpleName();
    private static final String WIDTH = "WIDTH";
    private static final String HEIGHT = "HEIGHT";
    private static final String MACROS = "macros";
    private static final String EMPTY_STRING = "";
    private String mHtml;
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
            String decodedParam = decode(param);
            return decodedParam;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    @Deprecated
    public static String decode(String source) {
        try {
            return URLDecoder.decode(source, Constants.UTF_8);
        } catch (UnsupportedEncodingException var2) {
            return URLDecoder.decode(source);
        }
    }

    public int getAdWidth() {
        int widthInt = Constants.DEFAULT_BANNER_WIDTH;
        String width = getObject(WIDTH);
        try {
            widthInt = Integer.parseInt(width);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Not appropriate ad type, ad width by default");
        }
        return widthInt;
    }

    public int getAdHeight() {
        int heightInt = Constants.DEFAULT_BANNER_HEIGHT;
        String height = getObject(HEIGHT);
        try {
            heightInt = Integer.parseInt(height);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Not appropriate ad type, ad height by default");
        }
        return heightInt;
    }
}
