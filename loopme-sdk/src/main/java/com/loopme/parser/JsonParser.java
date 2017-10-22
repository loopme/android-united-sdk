package com.loopme.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    protected static boolean getBoolean(JSONObject data, String name) {
        try {
            return data.getBoolean(name);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    protected static boolean getBoolean(JSONObject data, String name, boolean defaultValue) {
        if (data.has(name) && !data.isNull(name)) {
            try {
                return data.getBoolean(name);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        } else {
            return defaultValue;
        }
        return false;
    }

    protected static int getInt(JSONObject data, String name) {
        try {
            return data.getInt(name);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    protected static int getInt(JSONObject data, String name, int defaultValue) {
        if (data.has(name) && !data.isNull(name)) {
            try {
                return data.getInt(name);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        } else {
            return defaultValue;
        }
        return 0;
    }

    protected static long getLong(JSONObject data, String name) {
        try {
            return data.getLong(name);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    protected static long getLong(JSONObject data, String name, long defaultValue) {
        if (data.has(name) && !data.isNull(name)) {
            try {
                return data.getLong(name);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        } else {
            return defaultValue;
        }
        return 0;
    }

    protected static double getDouble(JSONObject data, String name) {
        try {
            return data.getDouble(name);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return 0.0;
    }

    protected static double getDouble(JSONObject data, String name, double defaultValue) {
        if (data.has(name) && !data.isNull(name)) {
            try {
                return data.getDouble(name);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        } else {
            return defaultValue;
        }
        return 0.0;
    }

    protected float getFloat(JSONObject data, String name) {
        try {
            return (float) data.getDouble(name);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return 0.0f;
    }

    protected static float getFloat(JSONObject data, String name, float defaultValue) {
        if (data.has(name) && !data.isNull(name)) {
            try {
                return (float) data.getDouble(name);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }
        } else {
            return defaultValue;
        }
        return 0.0f;
    }

    protected static JSONObject getJSONObject(JSONObject data, String name) {
        try {
            if (data.isNull(name)) {
                throw new JSONException("CONTENT_TYPE_APP_JSON data object must not be null: " + name);
            } else {
                return data.getJSONObject(name);
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return new JSONObject();
    }

    protected static JSONObject getJSONObject(JSONArray data, int index) {
        try {
            return data.getJSONObject(index);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return new JSONObject();
    }

    protected static JSONArray getJSONArray(JSONObject data, String name) {
        try {
            if (data.isNull(name)) {
                throw new JSONException("CONTENT_TYPE_APP_JSON data array must not be null: " + name);
            } else {
                return data.getJSONArray(name);
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return new JSONArray();
    }

    protected static List<String> getArray(JSONObject data, String name) {
        List<String> resultList = new ArrayList<>();
        try {
            if (data.isNull(name)) {
                throw new JSONException("CONTENT_TYPE_APP_JSON data array must not be null: " + name);
            } else {
                JSONArray jsonArray = data.getJSONArray(name);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        resultList.add(jsonArray.getString(i));
                    }
                }
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return resultList;
    }
}
