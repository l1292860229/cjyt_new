package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by yf on 2016/12/14.
 */

public class Bbs implements Serializable {
    public String id;
    public String uid;
    public String title;
    public String content;
    public String headsmall;
    public int status;
    public int type;
    public int isTop;
    public int isFine;
    public int speakStatus;
    public long time;
    public String money;
    public int isVisitors;
    public int isjoin;
    public int isclosefriedloop;
    public int delchat;
    public int deldynamic;
    public int power;
    public int peopleCount=1;
    public int replyCount;
    public int getmsg;
    public String uheadsmall;
    public Bbs(){}
    public Bbs(JSONObject json){
        try {
            if(!json.isNull("id")) {
                id = json.getString("id");
            }
            if(!json.isNull("uid")) {
                uid = json.getString("uid");
            }
            if(!json.isNull("uheadsmall")) {
                uheadsmall = json.getString("uheadsmall");
            }
            if(!json.isNull("title")) {
                title = json.getString("title");
            }
            if(!json.isNull("content")) {
                content = json.getString("content");
            }
            if(!json.isNull("headsmall")) {
                headsmall = json.getString("headsmall");
            }
            if(!json.isNull("status")) {
                status = json.getInt("status");
            }
            if(!json.isNull("type")) {
                type = json.getInt("type");
            }
            if(!json.isNull("isTop")) {
                isTop = json.getInt("isTop");
            }
            if(!json.isNull("isFine")) {
                isFine = json.getInt("isFine");
            }
            if(!json.isNull("speakStatus")) {
                speakStatus = json.getInt("speakStatus");
            }
            if(!json.isNull("time")) {
                time = json.getLong("time");
            }
            if(!json.isNull("money")) {
                money = json.getString("money");
            }
            if(!json.isNull("isjoin")) {
                isjoin = json.getInt("isjoin");
            }
            if(!json.isNull("replyCount")) {
                replyCount = json.getInt("replyCount");
            }
            if(!json.isNull("peopleCount")) {
                peopleCount = json.getInt("peopleCount");
            }
            if(!json.isNull("isvisitors")) {
                isVisitors = json.getInt("isvisitors");
            }
            if(!json.isNull("delchat")) {
                delchat = json.getInt("delchat");
            }
            if(!json.isNull("deldynamic")) {
                deldynamic = json.getInt("deldynamic");
            }
            if(!json.isNull("power")) {
                power = json.getInt("power");
            }
            if(!json.isNull("isclosefriendloop")) {
                isclosefriedloop = json.getInt("isclosefriendloop");
            }
            if(!json.isNull("getmsg")) {
                getmsg = json.getInt("getmsg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Bbs{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", headsmall='" + headsmall + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", isTop=" + isTop +
                ", isFine=" + isFine +
                ", speakStatus=" + speakStatus +
                ", time=" + time +
                ", money='" + money + '\'' +
                ", isVisitors=" + isVisitors +
                ", isjoin=" + isjoin +
                ", isclosefriedloop=" + isclosefriedloop +
                ", delchat=" + delchat +
                ", deldynamic=" + deldynamic +
                ", power=" + power +
                ", peopleCount=" + peopleCount +
                ", replyCount=" + replyCount +
                '}';
    }
}
