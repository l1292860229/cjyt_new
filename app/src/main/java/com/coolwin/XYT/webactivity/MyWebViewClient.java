package com.coolwin.XYT.webactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/27.
 */

public class MyWebViewClient extends WebViewClient {
    private String mUrl;
    private Context context;
    private ImageView loadingImage;
    public static Map<String,String> offlineResources = new HashMap<String,String>();
    public String getmUrl() {
        return mUrl;
    }
    public MyWebViewClient(Context context,ImageView loadingImage){
        this.context = context;
        this.loadingImage = loadingImage;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        mUrl = url;
        if(url.startsWith("tel:")){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse(url);
            intent.setData(data);
            context.startActivity(intent);
            return true;
        }
        view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
        WebView.HitTestResult hit = view.getHitTestResult();
        int hitType = hit.getType();
        if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE) {//点击超链接
            //这里执行自定义的操作
            view.loadUrl(url);
            return true;//返回true浏览器不再执行默认的操作
        } else if (hitType == 0) {//重定向时hitType为0
            return false;//不捕获302重定向
        } else {
            return false;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        loadingImage.setVisibility(View.GONE);
        //读取网页上的内容
        view.loadUrl("javascript:window.messageToApp.showSource(document.getElementsByTagName('body')[0].innerHTML);");
        super.onPageFinished(view, url);
    }
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
        handler.proceed();  // 接受所有网站的证书
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        int lastSlash = url.lastIndexOf("/");
        int endindex = url.lastIndexOf("?");
        if(lastSlash != -1 && endindex>lastSlash) {
            String suffix = url.substring(lastSlash + 1,(endindex!=-1)?endindex:url.length());
            if(offlineResources.containsKey(suffix)) {
                String mimeType;
                if(suffix.endsWith(".js")) {
                    mimeType = "application/x-javascript";
                } else if(suffix.endsWith(".css")) {
                    mimeType = "text/css";
                }else if(suffix.endsWith(".jpg")) {
                    mimeType = "image/jpeg";
                }else if(suffix.endsWith(".png")) {
                    mimeType = "image/png";
                } else {
                    mimeType = "text/html";
                }
                try {
                    InputStream is = context.getAssets().open(offlineResources.get(suffix));
                    Log.e("shouldInterceptRequest", "use offline resource for: " + url);
                    return new WebResourceResponse(mimeType, "UTF-8", is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.shouldInterceptRequest(view, url);

    }
}
