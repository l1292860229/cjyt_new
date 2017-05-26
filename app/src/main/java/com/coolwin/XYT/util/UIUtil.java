package com.coolwin.XYT.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import static com.tencent.smtt.sdk.TbsReaderView.TAG;

/**
 * Created by dell on 2017/5/23.
 */

public class UIUtil {
    private static Handler handler = new Handler();
    public static  void getImageFromCamera(Activity context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }
    public static void showMessage(final Context context, final String message){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 检查当前app版本
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 跟服务器上的版本进行比较
     * @param local
     * @param romote
     * @return
     */
    public static boolean compareVersion(String local, String romote){
        try {
            String loaclVersion[] = local.split("\\.");
            String romoteVersion[] = romote.split("\\.");
            int length = loaclVersion.length < romoteVersion.length ? loaclVersion.length : romoteVersion.length;
            for (int i = 0; i < length; i++) {
                int localversion  = Integer.valueOf(loaclVersion[i]);
                int romoteversion  = Integer.valueOf(romoteVersion[i]);
                if (localversion < romoteversion) {
                    return true;
                }
                else if (localversion > romoteversion) {
                    break;
                }
            }
        }catch (NumberFormatException e){
        }catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
