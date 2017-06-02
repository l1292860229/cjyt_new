package com.coolwin.XYT.presenter;

import android.app.Activity;
import android.util.Log;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;

import java.io.File;
import java.io.IOException;

import cn.finalteam.toolsfinal.io.FileUtils;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
            .client( new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build())
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
 class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration=endTime-startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        Log.d(TAG,"\n");
        Log.d(TAG,"----------Start----------------");
        Log.d(TAG, "| "+request.toString());
        String method=request.method();
        if("POST".equals(method)){
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                Log.d(TAG, "| RequestParams:{"+sb.toString()+"}");
            }
        }
        Log.d(TAG, "| Response:" + content);
        Log.d(TAG,"----------End:"+duration+"毫秒----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}
