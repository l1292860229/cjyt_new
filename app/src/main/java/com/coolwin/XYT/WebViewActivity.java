package com.coolwin.XYT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.ShareUrl;
import com.coolwin.XYT.apientity.UserInfo;
import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.widget.ItemLongClickedPopWindow;
import com.coolwin.XYT.widget.SelectPicPopupWindow;
import com.coolwin.XYT.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yf on 2016/11/24.
 */

public class WebViewActivity extends Activity implements View.OnClickListener {
    private WebView webView;
    private LinearLayout backLayout,closeLayout;
    private ImageView left_icon;
    private String mUrl,ImagePath,urltitle;
    private GestureDetector gestureDetector;
    private int downX, downY;
    private Context mWebview;
    private String imgurl;
    private SelectPicPopupWindow menuWindow;
    private RelativeLayout mTitleLayout;
    private TextView mTitleView;
    private ImageView mMoreBtn;
    private boolean isShowImagePath=true;
    private String mHtml;
    private ImageView webloading;
    public static Map<String,String> offlineResources = new HashMap<String,String>();
    RelativeLayout shareLayout;
    LinearLayout cancel;
    private TextView shanji,friend,wechat,wechatq,liulan,fuzhi;
    @Override
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebview = WebViewActivity.this;
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        webView = (WebView) findViewById(R.id.webView);
        shareLayout = (RelativeLayout) findViewById(R.id.share_layout);
        cancel= (LinearLayout) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        shanji= (TextView) findViewById(R.id.shanji);
        shanji.setOnClickListener(this);
        friend= (TextView) findViewById(R.id.friend);
        friend.setOnClickListener(this);
        wechat= (TextView) findViewById(R.id.wechat);
        wechat.setOnClickListener(this);
        wechatq= (TextView) findViewById(R.id.wechatq);
        wechatq.setOnClickListener(this);
        liulan= (TextView) findViewById(R.id.liulan);
        liulan.setOnClickListener(this);
        fuzhi= (TextView) findViewById(R.id.fuzhi);
        fuzhi.setOnClickListener(this);
        webloading = (ImageView) findViewById(R.id.webloading);
        backLayout = (LinearLayout) findViewById(R.id.left_btn);
        closeLayout = (LinearLayout) findViewById(R.id.left_btn2);
        left_icon = (ImageView) findViewById(R.id.left_icon);
        mUrl = this.getIntent().getStringExtra("url");
        //WebView加载web资源
        webView.loadUrl(mUrl);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        initWebView();
        left_icon.setImageResource(R.drawable.back_btn);
        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.this.finish();
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }else{
                    WebViewActivity.this.finish();
                }
            }
        });
        setActionBarLayout();
    }
    public void showMenuWindow(View view){
        if (menuWindow != null && menuWindow.isShowing()) {
            menuWindow.dismiss();
            menuWindow = null;
        }
        menuWindow = new SelectPicPopupWindow(WebViewActivity.this, itemsOnClick);
        // 显示窗口
        // 计算坐标的偏移量
        int xoffInPixels = menuWindow.getWidth() - view.getWidth()+10;
        menuWindow.showAsDropDown(view, -xoffInPixels, 0);
    }
    public void setActionBarLayout(){
        mTitleLayout = (RelativeLayout)findViewById(R.id.title_layout);
        mTitleView = (TextView)findViewById(R.id.title);
        ((TextView)findViewById(R.id.left_title)).setText("返回");
        ((TextView)findViewById(R.id.left_title2)).setText("关闭");
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        mMoreBtn.setVisibility(View.VISIBLE);
        mMoreBtn.setOnClickListener(this);
    }
    public void onC(View view){
        shareLayout.setVisibility(View.GONE);
    }
    private void initWebView(){
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "messageToApp");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mUrl = url;
//                if (url.startsWith("intent://")) {
//                    try {
//                        Uri uri = Uri.parse(url);
//                        Intent intent =new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }
                if(url.startsWith("tel:")){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse(url);
                    intent.setData(data);
                    startActivity(intent);
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
                webloading.setVisibility(View.GONE);
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
                            InputStream is = getAssets().open(offlineResources.get(suffix));
                            Log.e("shouldInterceptRequest", "use offline resource for: " + url);
                            return new WebResourceResponse(mimeType, "UTF-8", is);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);

            }
        });
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                downX = (int) e.getX();
                downY = (int) e.getY();
            }
        });
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;
                final ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(mWebview, ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW, FeatureFunction.dip2px(mWebview, 120), FeatureFunction.dip2px(mWebview, 90));
                switch (type) {
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        // Log.d(DEG_TAG, "超链接");
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
                        imgurl = result.getExtra();
                        itemLongClickedPopWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, downX, downY + 10);
                        break;
                    default:
                        break;
                }
                itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemLongClickedPopWindow.dismiss();
                                isShowImagePath = true;
                                new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
                            }
                        });
                //分享图片
                itemLongClickedPopWindow.getView(R.id.item_longclicked_fenXiangImage)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemLongClickedPopWindow.dismiss();
                                isShowImagePath = false;
                                new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
                            }
                        });
                return true;
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                urltitle= title;
                mTitleView.setText(urltitle);
            }

        });
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //将图片调整到适合WebView的大小
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //设置图片自动加载
        webView.getSettings().setLoadsImagesAutomatically(true);
        //允许加载js脚本,这个很重要,当初为了这个弄了半天
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        //H5 localStorage支持
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("gb2312");
    }
    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            mHtml = html;
        }
        @JavascriptInterface
        public String postMessage(String html) {
            try {
                JSONObject o= new JSONObject(html);
                String method = o.getString("method");
                String param="";
                if (o.has("param1")) {
                    param = o.getString("param1");
                }
                final String param1= param;
                if(method.equals("userDetail")){
                    int param2 = o.getInt("param2");
                    Intent userInfoIntent = new Intent();
                    userInfoIntent.setClass(mWebview, UserInfoActivity.class);
                    userInfoIntent.putExtra("type",2);
                    userInfoIntent.putExtra("o",param2);
                    userInfoIntent.putExtra("uid", param1);
                    startActivity(userInfoIntent);
                }else if(method.equals("home")){
                    Intent intent = new Intent(mWebview,MainActivity.class);
                    startActivity(intent);
                }else if(method.equals("indurstryCircle")){
                    Intent hanyeIntent = new Intent();
                    if(param1==null&&param1.equals("")){
                        hanyeIntent.putExtra("type","1");
                        hanyeIntent.setClass(mWebview, MyBbsListActivity.class);
                    }else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BbsList bl= IMCommon.getIMInfo().getBbs(param1);
                                    if(bl.mBbsList.size()!=0){
                                        Bbs bbs =bl.mBbsList.get(0);
                                        if(bbs.isjoin==1){
                                            Intent intent = new Intent(mWebview, BbsChatMainActivity.class);
                                            intent.putExtra("data", bbs);
                                            startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(mWebview, JoinBBSActivity.class);
                                            intent.putExtra("data", bbs);
                                            startActivity(intent);
                                        }
                                    }
                                } catch (IMException e) {
                                    e.printStackTrace();
                                } catch(Exception e){
                                }
                            }
                        }).start();
                    }
                    startActivity(hanyeIntent);
                }else if(method.equals("userInfo")){
                    SharedPreferences mPreferences= mWebview.getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
                    String fxid=mPreferences.getString(IMCommon.USERNAME, "");
                    String str = com.alibaba.fastjson.JSONObject.toJSON(new UserInfo(IMCommon.getLoginResult(mWebview),fxid)).toString();
                    return str;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onDestroy() {
        if(webView!=null){
            webView.stopLoading();
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_btn:
                shareLayout.setVisibility(View.VISIBLE);
//                showMenuWindow(mTitleLayout);
                return;
            case R.id.cancel:
                break;
            case R.id.shanji:
                Intent shanjiIntent = new Intent();
                shanjiIntent.setClass(mWebview,SendMovingActivity.class);
                shanjiIntent.putExtra("url",mUrl);
                shanjiIntent.putExtra("title", urltitle);
                if (IMCommon.getLoginResult(mWebview).userDj.equals("0")) {
                    Toast.makeText(mWebview,"等级不够无法分享", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(shanjiIntent);
                break;
            case R.id.friend:
                getUrlImage();
                Intent friendIntent = new Intent();
                friendIntent.setClass(mWebview, ShareActivity.class);
                friendIntent.putExtra("url",mUrl);
                friendIntent.putExtra("title", urltitle);
                friendIntent.putExtra("imagePath", ImagePath);
                friendIntent.putExtra("shareurl_msg", new ShareUrl(mUrl,urltitle,ImagePath));
                startActivity(friendIntent);
                break;
            case R.id.wechat:
                Intent wechatIntent = new Intent();
                wechatIntent.setClass(mWebview,WXEntryActivity.class);
                wechatIntent.putExtra("url",mUrl);
                wechatIntent.putExtra("title", urltitle);
                wechatIntent.putExtra("imagePath", ImagePath);
                wechatIntent.putExtra("type", SendMessageToWX.Req.WXSceneSession);
                startActivity(wechatIntent);
                break;
            case R.id.wechatq:
                Intent wechatqIntent = new Intent();
                wechatqIntent.setClass(mWebview,WXEntryActivity.class);
                wechatqIntent.putExtra("url",mUrl);
                wechatqIntent.putExtra("title", urltitle);
                wechatqIntent.putExtra("imagePath", ImagePath);
                wechatqIntent.putExtra("type", SendMessageToWX.Req.WXSceneTimeline);
                startActivity(wechatqIntent);
                break;
            case R.id.liulan:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(mUrl));
                startActivity(intent);
                break;
            case  R.id.fuzhi:
                ClipboardManager cm =(ClipboardManager) mWebview.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mUrl);
                break;
            default:
                break;
        }
        shareLayout.setVisibility(View.GONE);
    }

    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }
                Log.e("SaveImage","imgurl="+imgurl);
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/Download/" + new Date().getTime() + ext);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
                ImagePath = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                result = "保存失败！" + e.getLocalizedMessage();
            }
            Log.e("SaveImage","result="+result);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            if (isShowImagePath) {
                showToast(result);
            }else{
                showPromptDialog();
            }
        }
    }
    private void showToast(String result){
        Toast.makeText(mWebview, result, Toast.LENGTH_LONG).show();
    }
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fenxiang_layout:
                    showPromptDialog();
                    break;
                case R.id.waibuliulan:
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse(mUrl));
                    startActivity(intent);
                    break;
                case R.id.copyurl:
                    ClipboardManager cm =(ClipboardManager) mWebview.getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(mUrl);
                    break;
                default:
                    break;
            }
            if (menuWindow != null && menuWindow.isShowing()) {
                menuWindow.dismiss();
                menuWindow = null;
            }
        }
    };
    private void getUrlImage(){
        String patternStr = "((http://|https://|//)+[^\\s]*)+(jpeg|jpg|gif)";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher m = pattern.matcher(mHtml);
        Set<String> urlStrSet = new LinkedHashSet<String>();
        while(m.find()) {
            urlStrSet.add(m.group());
            if(urlStrSet.size()>3){
                break;
            }
        }
        List<String> urlStrList = new ArrayList<String>(urlStrSet);
        if(urlStrList.size()>=3){
            ImagePath = urlStrList.get(1);
        }else if(urlStrList.size()>0&&urlStrList.size()<3){
            ImagePath = urlStrList.get(0);
        }
        if(ImagePath!=null&&ImagePath.startsWith("//")){
            ImagePath = "http:"+ImagePath;
        }
    }
    //分享详细
    private void showPromptDialog(){
        String[] itemMenu  = mWebview.getResources().getStringArray(R.array.fenxian_item);
        MMAlert.showAlert(mWebview, "", itemMenu,
                null, new MMAlert.OnAlertSelectId() {
                    @Override
                    public void onClick(int whichButton) {
                        Intent intent = new Intent();
                        intent.setClass(mWebview,WXEntryActivity.class);
                        intent.putExtra("url",mUrl);
                        intent.putExtra("title", urltitle);
                        intent.putExtra("imagePath", ImagePath);
                        switch (whichButton) {
                            case 0:
                                if (IMCommon.getLoginResult(mWebview).userDj.equals("0")) {
                                    Toast.makeText(mWebview,"等级不够无法分享", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                intent.setClass(mWebview,SendMovingActivity.class);
                                break;
                            case 1:
                                getUrlImage();
                                intent.putExtra("imagePath", ImagePath);
                                intent.setClass(mWebview, ShareActivity.class);
                                intent.putExtra("shareurl_msg", new ShareUrl(mUrl,urltitle,ImagePath));
                                break;
                            case 2:
                                intent.putExtra("type", SendMessageToWX.Req.WXSceneSession);
                                break;
                            case 3:
                                intent.putExtra("type", SendMessageToWX.Req.WXSceneTimeline);
                                break;
                            default:
                                return;
                        }
                        startActivity(intent);
                    }
                });
    }
    // goBack()表示返回webView的上一页面
    @Override
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
            webView.goBack();
            return true;
        } else if (!webView.canGoBack()) {
            WebViewActivity.this.finish();
            return true;
        }
        return false;
    }
}
