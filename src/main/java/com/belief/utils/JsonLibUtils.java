package com.belief.utils;

import net.sf.json.JSONObject;

public class JsonLibUtils {

    public static JSONObject fromJson(String json) {
        JSONObject jsonObject = JSONObject.fromObject(json);
        return jsonObject;
    }
}
