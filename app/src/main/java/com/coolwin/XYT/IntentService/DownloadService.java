package com.coolwin.XYT.IntentService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.coolwin.XYT.R;
import com.coolwin.XYT.constant.UrlConstants;
import com.coolwin.XYT.download.Download;
import com.coolwin.XYT.download.DownloadAPI;
import com.coolwin.XYT.download.DownloadProgressListener;
import com.coolwin.XYT.util.FileUtil;

import java.io.File;

import io.reactivex.functions.Action;

/**
 * Created by dell on 2017/5/26.
 */

public class DownloadService extends IntentService {
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private String apkUrl = "http://download.fir.im/v2/app/install/5818acbcca87a836f50014af?download_token=a01301d7f6f8f4957643c3fcfe5ba6ff";
    public DownloadService() {
        super("DownloadService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        download();
    }
    private void download() {
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Download download = new Download();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                int progress = (int) ((bytesRead * 100) / contentLength);
                download.setProgress(progress);
                sendNotification(download);
            }
        };
        File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "file.apk");
        new DownloadAPI(UrlConstants.BASEURL,listener).downloadAPK(apkUrl, outputFile, new Action() {
            @Override
            public void run() throws Exception {
                downloadCompleted();
            }
        });
    }
    private void sendNotification(Download download) {
        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                FileUtil.getFormatSize(download.getCurrentFileSize()) + "/" +
                        FileUtil.getFormatSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }
    private void downloadCompleted() {
        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());
    }
    private void sendIntent(Download download) {
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }
}
