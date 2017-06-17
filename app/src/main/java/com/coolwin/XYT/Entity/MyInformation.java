package com.coolwin.XYT.Entity;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class MyInformation {
    private int id;
    private int uid;
    private String ypid;
    private String content;
    private String title;
    private double price;
    private List<Picture> picture;
    private String shopurl;
    private Video video;
    private String type;
    public class Video{
        public String url;
        public String time;
        public String image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public String getYpid() {
        return ypid;
    }
    public void setYpid(String ypid) {
        this.ypid = ypid;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public List<Picture> getPicture() {
        return picture;
    }

    public void setPicture(List<Picture> picture) {
        this.picture = picture;
    }

    public String getShopurl() {
        return shopurl;
    }

    public void setShopurl(String shopurl) {
        this.shopurl = shopurl;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "MyInformation{" +
                "id=" + id +
                ", uid=" + uid +
                ", ypid='" + ypid + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", picture=" + picture +
                ", shopurl='" + shopurl + '\'' +
                ", video=" + video +
                '}';
    }
}
