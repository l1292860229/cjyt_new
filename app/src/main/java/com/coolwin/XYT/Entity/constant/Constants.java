package com.coolwin.XYT.Entity.constant;

/**
 * Created by Administrator on 2017/1/21.
 */

public class Constants {
    public static final String LOGIN_SHARED = "login_shared";
    public static final String LOGIN_RESULT = "login";

    public static final String REMENBER_SHARED = "remenber_shared";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";


    //activity 公共参数
    public static final String DATAPOSITION = "position";


    //事件总线每条线的编号 参数类型必须一致,必须是实现类,否则传送不到对应的线
    public static final int UPDATEMYINDEXPIC = 1000;//UpdatePicIndexActivity
    public static final int UPDATEMYINDEXPIC_PIC = 10001;//UpdatePicIndexActivity
    public static final int COMMODITY = 1001;//InformationActivity
    public static final int MAIN = 1002;//MainActivity
}
