package com.loopme.network.parser;

import com.loopme.Logging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseJSONParser {
    private static final String LOG_TAG = BaseJSONParser.class.getSimpleName();

    protected int getInt(JSONObject jsonObject, String param, boolean throwException) throws JSONException {
        try {
            return jsonObject.getInt(param);
        } catch (JSONException e) {
            Logging.out(LOG_TAG, "Parameter " + param + " is absent. Obj: " + jsonObject);
            if (throwException) {
                throw e;
            }
        }
        return 0;
    }


    protected String getString(JSONObject jsonObject, String param, boolean throwException) throws JSONException {
        try {
            return jsonObject.getString(param);
        } catch (JSONException e) {
            Logging.out(LOG_TAG, "Parameter " + param + " is absent. Obj: " + jsonObject);
            if (throwException) {
                throw e;
            }
        }
        return "";
    }

    protected List<String> parseStringsList(JSONObject jsonObject, String param) {
        List<String> measurePartners = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(param);
            for (int i = 0; i < jsonArray.length(); i++) {
                measurePartners.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Logging.out(LOG_TAG, "Parameter " + param + " is absent. Obj: " + jsonObject);
        }
        return measurePartners;
    }
}
