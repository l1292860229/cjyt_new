package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by dell on 2017/5/9.
 */

public class BranchSearchRests {
    public List<RedpacketInfo> BranchSearchRests;
    public int TotalCount;
    public String RetCode;
    public String RetMsg;
    public class RedpacketInfo{
        public String id;
        public String FullAddress;
        public double Latitude;
        public double Longitude;
        public String Name;

    }
    public static com.coolwin.XYT.Entity.BranchSearchRests getInfo(String json) {
        try {
            return JSONObject.parseObject(json, com.coolwin.XYT.Entity.BranchSearchRests.class);//toJavaObject(JSONObject.parseObject(json),
            //Card.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getInfo(com.coolwin.XYT.Entity.BranchSearchRests info) {
        String json = JSONObject.toJSON(info).toString();
        return json;
    }
}
