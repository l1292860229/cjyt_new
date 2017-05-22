package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dell on 2017/5/10.
 */

public class GetRedpacketMoney implements Serializable {
    public String id;
    public String msg;
    public String money;
    public double Latitude;
    public double Longitude;
    public String Name;
    public ArrayList<OpenRedpacketUser> list;
    public static GetRedpacketMoney getInfo(String json) {
        try {
            return JSONObject.parseObject(json, GetRedpacketMoney.class);//toJavaObject(JSONObject.parseObject(json),
            //Card.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getInfo(GetRedpacketMoney info) {
        String json = JSONObject.toJSON(info).toString();
        return json;
    }
}
