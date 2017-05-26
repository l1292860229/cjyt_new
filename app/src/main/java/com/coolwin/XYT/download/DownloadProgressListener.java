package com.coolwin.XYT.download;

/**
 * Created by dell on 2017/5/26.
 */

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
