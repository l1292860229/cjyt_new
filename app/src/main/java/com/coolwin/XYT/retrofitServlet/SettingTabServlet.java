package com.coolwin.XYT.retrofitServlet;

import com.coolwin.XYT.Entity.RetrofitResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by dell on 2017/5/26.
 */

public interface SettingTabServlet {

    @POST("version/api/update")
    Observable<RetrofitResult<Map<String,String>>> getNewVersion(@Query("version") String version , @Query("os") String os);
}
