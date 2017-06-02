package com.coolwin.XYT.IntentService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.coolwin.XYT.R;
import com.coolwin.XYT.Entity.constant.UrlConstants;
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
    String apkUrl;
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        apkUrl = intent.getStringExtra("apkUrl");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("正在下载")
                .setContentText("下载中")
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
        final File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "xyt.apk");
        if (outputFile.exists()) {
            Intent intent = new Intent();
            // 设置目标应用安装包路径
            intent.setDataAndType(Uri.fromFile(outputFile),
                    "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            new DownloadAPI(UrlConstants.BASEURL,listener).downloadAPK(apkUrl, outputFile, new Action() {
                @Override
                public void run() throws Exception {
                    downloadCompleted(outputFile);
                }
            });
        }
    }

    private void sendNotification(Download download) {
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                FileUtil.getFormatSize(download.getCurrentFileSize()) + "/" +
                        FileUtil.getFormatSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void downloadCompleted(File outputFile) {
        Download download = new Download();
        download.setProgress(100);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("下载完成");
        notificationManager.notify(0, notificationBuilder.build());
        Intent intent = new Intent();
        // 设置目标应用安装包路径
        intent.setDataAndType(Uri.fromFile(outputFile),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }
}
