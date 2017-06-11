package com.coolwin.XYT.webactivity;

import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by dell on 2017/4/14.
 */

public class MyWebChromeClient extends WebChromeClient {
    private TextView titleTextView;
    public MyWebChromeClient(TextView titleTextView){
        this.titleTextView = titleTextView;
    }
    @Override
    // 处理javascript中的alert
    public boolean onJsAlert(WebView view, String url, String message,
                             final JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    // 处理javascript中的confirm
    public boolean onJsConfirm(WebView view, String url,
                               String message, final JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    // 处理javascript中的prompt
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, final JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue,
                result);
    }

    // 设置网页加载的进度条
//    @Override
//    public void onProgressChanged(WebView view, int newProgress) {
//        progressBar.setProgress(newProgress);
//        super.onProgressChanged(view, newProgress);
//    }

    // 设置程序的Title
    @Override
    public void onReceivedTitle(WebView view, String title) {
        titleTextView.setText(title);
    }

}
