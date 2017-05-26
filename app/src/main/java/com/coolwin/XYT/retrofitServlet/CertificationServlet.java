package com.coolwin.XYT.retrofitServlet;

import com.coolwin.XYT.Entity.RetrofitResult;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by dell on 2017/5/24.
 */

public interface CertificationServlet {

    @Multipart
    @POST("user/api/userattest")
    Observable<RetrofitResult<Map<String,String>>> uploadUserAttext(@Part("uid") RequestBody uid,
                                                                    @Part MultipartBody.Part businesscard,
                                                                    @Part MultipartBody.Part signboard,
                                                                    @Part MultipartBody.Part idcard);
}
