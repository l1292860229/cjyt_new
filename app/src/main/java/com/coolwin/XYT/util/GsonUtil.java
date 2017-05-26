package com.coolwin.XYT.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/1/27.
 */

public class GsonUtil {
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Object.class, new IntegerDefault0Adapter())
            .create();
    static{
        gson = new GsonBuilder()
                .registerTypeAdapter(Object.class, new IntegerDefault0Adapter())
                .create();
    }
    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    public static <T> T parseJsonWithGsonObject(String jsonData, Type typeOfT) {
        T result = gson.fromJson(jsonData, typeOfT);
        return result;
    }

    public  static String objectToJson(Object object) {
        return gson.toJson(object);
    }
    public static class IntegerDefault0Adapter implements JsonSerializer<Object>, JsonDeserializer<Object> {
        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {
                    return null;
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            try {
                return json.getAsJsonObject();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
