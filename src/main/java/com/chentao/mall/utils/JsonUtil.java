package com.chentao.mall.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> String Object2JSON(T object) {
        String json = null;
        try {
            mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> Object JSON2Object(String json, T object) {
        Object o = null;
        try {
            o = mapper.readValue(json, object.getClass());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return o;
    }
}
