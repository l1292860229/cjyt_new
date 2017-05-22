package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dell on 2017/5/10.
 */

public class RedpacketDetails implements Serializable {
    public String id;
    public String ishb;
    public String mymoney;
    public String hname;
    public String hpic;
    public String question;
    public String tips;
    public String q_openid;
    public int num;
    public int money;
    public ArrayList<OpenRedpacketUser> list;
    public static RedpacketDetails getInfo(String json) {
        try {
            return JSONObject.parseObject(json, RedpacketDetails.class);//toJavaObject(JSONObject.parseObject(json),
            //Card.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getInfo(RedpacketDetails info) {
        String json = JSONObject.toJSON(info).toString();
        return json;
    }
}
