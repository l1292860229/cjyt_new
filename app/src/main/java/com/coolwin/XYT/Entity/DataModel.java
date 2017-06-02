package com.coolwin.XYT.Entity;

/**
 * Created by Administrator on 2017/5/31.
 */

public class DataModel  {
    public static  final int TYPE_ONETOONE=12;
    public static  final int TYPE_ONETOFOUR=3;
    public static final int TYPE_ONETOTHREE=4;
    public static final int TYPE_ONETOTWO=6;
    public static final int TYPE_TWOTOTHREE=8;
    public static final int TYPE_THREETOFOUR=9;
    public String imagepath;
    public String openurl;
    public int type;

    @Override
    public String toString() {
        return "DataModel{" +
                "imagepath='" + imagepath + '\'' +
                ", openurl='" + openurl + '\'' +
                ", type=" + type +
                '}';
    }
}
