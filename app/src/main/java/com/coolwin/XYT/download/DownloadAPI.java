package com.coolwin.XYT.download;

import android.support.annotation.NonNull;

import com.coolwin.XYT.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/5/26.
 */

public class DownloadAPI {
    public interface DownloadAPK {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }
    private static final String TAG = "DownloadAPI";
    private static final int DEFAULT_TIMEOUT = 15;
    public Retrofit retrofit;
    public DownloadAPI(String url, DownloadProgressListener listener) {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public void downloadAPK(@NonNull String url, final File file, Action action) {
        retrofit.create(DownloadAPK.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        FileUtil.writeFile(inputStream, file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {}
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, action);
    }


}