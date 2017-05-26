package com.coolwin.XYT.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by dell on 2017/5/26.
 */

public class DownloadProgressInterceptor implements Interceptor {
    private DownloadProgressListener listener;
    public DownloadProgressInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
