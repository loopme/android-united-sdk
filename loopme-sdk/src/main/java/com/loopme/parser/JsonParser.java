package com.loopme.parser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by katerina on 5/29/17.
 */

public class JsonParser {

    protected static String getString(JSONObject data, String name) {
        try {
            return data.isNull(name) ? null : data.getString(name);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return "";
    }

    protected static String getString(JSONObject data, String name, String defaultValue) {
        if (data.has(name)) {
            try {
                return data.isNull(name) ? null : data.getString(name);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        } else {
            return defaultValue;
        }
        return "";
    }

}
