package com.coolwin.XYT.util;

import android.content.Context;

/**
 * Created by Administrator on 2017/1/21.
 */

public class StringUtil {
    public static boolean isNull(String str){
        if(str==null || str.equals("") || str.toLowerCase().equals("null")){
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
}
