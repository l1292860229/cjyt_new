package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by yf on 2017/3/22.
 */

public class UserMenu implements Serializable {
    public String id;
    public String uid;
    public String menuname;
    public String menuurl;
    public UserMenu(){}
    public UserMenu(String uid, String menuname, String menuurl){
        this.uid = uid;
        this.menuname = menuname;
        this.menuurl = menuurl;
    }
    public static UserMenu getInfo(String json) {
        try {
            return JSONObject.parseObject(json, UserMenu.class);//toJavaObject(JSONObject.parseObject(json),
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getInfo(UserMenu info) {
        String json = JSONObject.toJSON(info).toString();
        return json;
    }
}
