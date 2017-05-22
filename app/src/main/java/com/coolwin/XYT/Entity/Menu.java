package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

/**
 * Created by yf on 2017/1/11.
 */

public class Menu {
    private String title;
    private String url;
    private String imageurl;
    private int count;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Menu(){}
    public Menu(String title, String url){
        this.title = title;
        this.url = url;
    }
    public Menu(String title, String url, int count){
        this.title = title;
        this.url = url;
        this.count = count;
    }
    public Menu(JSONObject json){
        try {
            if(!json.isNull("title")) {
                title = json.getString("title");
            }
            if(!json.isNull("url")) {
                url = json.getString("url");
            }
            if(!json.isNull("imageurl")) {
                imageurl = json.getString("imageurl");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
