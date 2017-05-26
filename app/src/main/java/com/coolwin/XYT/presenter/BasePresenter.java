package com.coolwin.XYT.presenter;

import android.app.Activity;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.constant.UrlConstants;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;

import java.io.File;

import cn.finalteam.toolsfinal.io.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dell on 2017/5/24.
 */

public class BasePresenter<T> {
    static Retrofit  retrofit = new Retrofit.Builder()
            .baseUrl(UrlConstants.BASEURL)
            .client(new OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    Login login;
    Activity context;
    public T mView;
    public void init(Activity activity,T t){
        this.context = activity;
        login = GetDataUtil.getLogin(context);
        mView = t;
    }

    /**
     * Retrofit 2.0 将文件路径转化为MultipartBody.Part
     * @param path
     * @param key
     * @return
     */
    public MultipartBody.Part getMultipartBodyFilePathPart(String path, String key){
        if (StringUtil.isNull(path)) {
            return null;
        }
        File file = FileUtils.getFile(path);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return  MultipartBody.Part.createFormData(key, file.getName(), requestFile);
    }

    /**
     * Retrofit 2.0  上传文件需要将文件转化为MultipartBody.Part
     * @param file
     * @param key
     * @return
     */
    public MultipartBody.Part getMultipartBodyPart( File file,String key){
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return  MultipartBody.Part.createFormData(key, file.getName(), requestFile);
    }

    /**
     *  Retrofit 2.0  如果需要上传文件需将普通参数
     *  转化为RequestBody
     * @param param
     * @return
     */
    public RequestBody getMultipartRequestBodyPart( String param){
        return  RequestBody.create(
                MediaType.parse("multipart/form-data"), param);
    }
}
