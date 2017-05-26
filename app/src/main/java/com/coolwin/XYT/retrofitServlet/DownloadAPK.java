package com.coolwin.XYT.retrofitServlet;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/5/26.
 */

public interface DownloadAPK {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
