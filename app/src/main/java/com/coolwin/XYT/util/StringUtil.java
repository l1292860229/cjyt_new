package com.coolwin.XYT.util;

import android.content.Context;

import com.coolwin.XYT.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/21.
 */

public class StringUtil {
    public static boolean isNull(String str){
        if(str==null || str.equals("")
                || str.toLowerCase().equals("null")
                || str.toLowerCase().equals("\"\"")|| str.toLowerCase().equals("''")){
            return true;
        }
        return false;
    }

//    /**
//     * 判断是否是正确的手机号码
//     * @param mobiles
//     * @return
//     */
//    public static boolean isMobileNum(Context c, String mobiles){
//        PhoneVerifior phoneVerifior  = new PhoneVerifior(c);
//        try {
//            return phoneVerifior.isValid(mobiles);
//        } catch (VerifiorException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    private static final int ONE_MINUTE = 60; // Seconds
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final int ONE_DAY = 24 * ONE_HOUR;
    public static String calculaterReleasedTime(Context context, Date date) {
        Date currentDate = new Date();
        long duration = (currentDate.getTime() - date.getTime()) / 1000; // Seconds
        try {
            if(isYeaterday(date, currentDate) ==0){
                SimpleDateFormat format =new SimpleDateFormat("MM月dd日"); //new SimpleDateFormat("HH:mm:ss");
                return format.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Not normal
        if (currentDate.before(date)) {
            if (Math.abs(duration) < ONE_MINUTE * 5) {//不足一分钟
                return context.getString(R.string.just_now);
            } else {
                return getDateString(context, date,
                        currentDate.getYear() != date.getYear());
            }
        }



        if (duration >= ONE_DAY) {
            return getDateString(context, date,
                    currentDate.getYear() != date.getYear());
			/*return getTime(time);*/
        }else if (duration >= ONE_HOUR) {
			/*return duration / ONE_HOUR + context.getString(R.string.hour)
					+ context.getString(R.string.before);*/
            SimpleDateFormat format =new SimpleDateFormat("HH:mm");
            return format.format(date);
            //return getTime(time,false);
        } else if (duration >= ONE_MINUTE) {
            return duration / ONE_MINUTE + context.getString(R.string.minutes_time)
                    + context.getString(R.string.before);
        } else {
            return duration + context.getString(R.string.second)
                    + context.getString(R.string.before);
        }
    }
    public static int isYeaterday(Date oldTime, Date newTime) throws ParseException {
        if(newTime==null){
            newTime=new Date();
        }
        //将下面的 理解成  yyyy-MM-dd 00：00：00 更好理解点
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = format.parse(todayStr);
        //昨天 86400000=24*60*60*1000 一天
        if((today.getTime()-oldTime.getTime())>0 && (today.getTime()-oldTime.getTime())<=86400000) {
            return 0;
        }
        else if((today.getTime()-oldTime.getTime())<=0){ //至少是今天
            return -1;
        }
        else{ //至少是前天
            return 1;
        }
    }
    public static String getDateString(Context context, Date date,
                                       boolean withYearString) {
        String timeString = "";
        SimpleDateFormat format = null;
        if (withYearString) {
            format = new SimpleDateFormat("yyyy.MM.dd");
        }else{
            format = new SimpleDateFormat("MM月dd日 ");
        }
        return format.format(date);
    }
}
