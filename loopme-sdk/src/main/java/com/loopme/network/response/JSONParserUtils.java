package com.loopme.network.response;

import androidx.arch.core.util.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParserUtils {
    private static final String LOG_TAG = JSONParserUtils.class.getSimpleName();

    public static <T> List<T> parseList(JSONObject jsonObject, String param, Function<Object, T> converter) {
        JSONArray jsonArray = jsonObject.optJSONArray(param);
        if (jsonArray == null) return new ArrayList<>();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.opt(i);
            if (value == null) continue;
            list.add(converter.apply(value));
        }
        return list;
    }

    public static List<String> parseStrings(JSONObject jsonObject, String param) {
        return parseList(jsonObject, param, json -> (String)json);
    }
}
