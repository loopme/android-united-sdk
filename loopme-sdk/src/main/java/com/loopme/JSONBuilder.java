package com.loopme;

import android.util.Log;

import org.json.JSONObject;

public class JSONBuilder {
    private static final String LOG_TAG = JSONBuilder.class.getSimpleName();
    private final JSONObject jsonObject;
    public JSONBuilder() {
        jsonObject = new JSONObject();
    }

    public JSONBuilder put(String key, Object value) {
        // Don't put null values
        if (value == null) {
            return this;
        }
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
        }
        return this;
    }

    public JSONObject build() {
        return jsonObject;
    }
}
