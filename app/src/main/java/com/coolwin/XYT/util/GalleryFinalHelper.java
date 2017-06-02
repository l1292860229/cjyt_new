package com.coolwin.XYT.util;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;



/**
 * Created by dell on 2017/5/24.
 */

public class GalleryFinalHelper {
    /**
     * 打开相机
     * @param requestCode
     * @param isCrop
     * @param callback
     */
    public static void openCamera(int requestCode,boolean isCrop,GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableCrop(isCrop)//裁剪功能
                .setEnablePreview(true)//预览功能
                .setEnableEdit(isCrop)//开启编辑功能
                .build();
        GalleryFinal.openCamera(requestCode,config, callback);
    }

    /**
     * 相册的单选功能
     * @param requestCode
     * @param isCrop
     * @param isCamera
     * @param callback
     */
    public static void openGallerySingle(int requestCode,boolean isCrop,boolean isCamera,GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableCrop(isCrop)//裁剪功能
                .setEnablePreview(true)//预览功能
                .setEnableCamera(isCamera)//相机功能
                .setEnableEdit(isCrop)//开启编辑功能
                .build();
        GalleryFinal.openGallerySingle(requestCode,config, callback);
    }

    /**
     * 相册的多选功能
     * @param requestCode
     * @param isCrop
     * @param isCamera
     * @param callback
     */
    public static void openGalleryMuti(int requestCode,boolean isCrop,boolean isCamera,GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableCrop(isCrop)//裁剪功能
                .setEnableEdit(isCrop)//开启编辑功能
                .setEnablePreview(true)//预览功能
                .setEnableCamera(isCamera)//相机功能
                .setMutiSelectMaxSize(9)
                .build();
        GalleryFinal.openGalleryMuti(requestCode,config, callback);
    }
    /**
     * 相册的多选功能
     * @param requestCode
     * @param isCrop
     * @param isCamera
     * @param callback
     */
    public static void openGalleryMuti(int requestCode,boolean isCrop,boolean isCamera,int max,GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig config = new FunctionConfig.Builder()
                .setEnableCrop(isCrop)//裁剪功能
                .setEnableEdit(isCrop)//开启编辑功能
                .setEnablePreview(true)//预览功能
                .setEnableCamera(isCamera)//相机功能
                .setMutiSelectMaxSize(max)
                .build();
        GalleryFinal.openGalleryMuti(requestCode,config, callback);
    }
}
