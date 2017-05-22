package com.coolwin.XYT.Entity;


import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

public class BbsReplyInfo extends SNSMessage {
	private static final long serialVersionUID = -4274108350647182194L;
	public String id;
	public String bid;
    public String uid;
    public String nickname;
    public String headsmall;
    public String phone;
    public String job;
    public String company;
    public int power;
    public int delchat;
    public int deldynamic;
    public String image;//上传图片
    public String imgUrlS = "";			//小图URL
    public String imgUrlL = "";			//大图URL
    public int imgWidth;				//小图宽度
    public int imgHeight;				//小图高度

    public String voice;//声音
    public String voiceUrl = "";		//音频URL
    public int voicetime;//声音时间长度
    public int sampleRate = 8000;		//播放音频采样率

    public double mLat = 0;				//纬度
    public double mLng = 0;				//经度
    public String mAddress = "";		//地址

    public String content; //消息的文字内容

    public int typefile;//1-文字 2-图片 3-声音 4-位置
    public long time;//发送消息的时间,毫秒（服务器生成）
    public BbsReplyInfo(){}
    public BbsReplyInfo(String bid, String uid, String nickname, String headsmall){
        this.bid = bid;
        this.uid = uid;
        this.nickname= nickname;
        this.headsmall = headsmall;
    }
    public BbsReplyInfo(JSONObject json){
        try {
            if(!json.isNull("id")) {
                id = json.getString("id");
            }
            if(!json.isNull("bid")) {
                bid = json.getString("bid");
            }
            if(!json.isNull("uid")) {
                uid = json.getString("uid");
            }
            if(!json.isNull("nickname")) {
                nickname = json.getString("nickname");
            }
            if(!json.isNull("headsmall")) {
                headsmall = json.getString("headsmall");
            }
            if(!json.isNull("phone")) {
                phone = json.getString("phone");
            }
            if(!json.isNull("job")) {
                job = json.getString("job");
            }
            if(!json.isNull("company")) {
                company = json.getString("company");
            }
            if(!json.isNull("image")){
                String imageString = json.getString("image");
                if(imageString!=null && !imageString.equals("") && imageString.startsWith("{")){
                    this.image = imageString;
                    String imageStr = json.getString("image");
                    JSONObject image = new JSONObject(imageStr.replace("\\\"","\""));
                    imgUrlS = image.getString("urlsmall");
                    imgUrlL = image.getString("urllarge");
                    imgWidth = image.getInt("width");
                    imgHeight = image.getInt("height");
                }

            }
            if(!json.isNull("voice")){
                String voiceString = json.getString("voice");
                if(voiceString!=null && !voiceString.equals("") && voiceString.startsWith("{")){
                    this.voice = voiceString;
                    String voiceStr = json.getString("voice");
                    JSONObject voice = new JSONObject(voiceStr.replace("\\\"","\""));
                    if(!voice.isNull("time")){
                        voicetime = voice.getInt("time");
                    }
                    if(!voice.isNull("url")){
                        voiceUrl = voice.getString("url");
                    }
                }
            }
            if(!json.isNull("location")){
                String locationString = json.getString("location");
                if(locationString!=null && !locationString.equals("")){
                    String locationStr = json.getString("location");
                    JSONObject location = new JSONObject(locationStr.replace("\\\"","\""));
                    mLat = location.getDouble("lat");
                    mLng = location.getDouble("lng");
                    mAddress = location.getString("address");
                }

            }
            if(!json.isNull("content")){
                content = json.getString("content");
            }
            if(!json.isNull("typefile")){
                typefile = json.getInt("typefile");
            }
            if(!json.isNull("time")){
                time = json.getLong("time");
            }
            if(!json.isNull("power")){
                power = json.getInt("power");
            }
            if(!json.isNull("delchat")){
                delchat = json.getInt("delchat");
            }
            if(!json.isNull("deldynamic")){
                deldynamic = json.getInt("deldynamic");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
