package com.coolwin.XYT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.widget.ItemLongClickedPopWindow;
import com.coolwin.XYT.widget.SelectPicPopupWindow;
import com.coolwin.XYT.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by yf on 2016/11/24.
 */

public class RedpacketWebViewActivity extends Activity implements View.OnClickListener {
    private WebView webView;
    private LinearLayout backLayout,closeLayout;
    private ImageView left_icon;
    private String mUrl;
    private GestureDetector gestureDetector;
    private int downX, downY;
    private Context mWebview;
    private String imgurl;
    private SelectPicPopupWindow menuWindow;
    private RelativeLayout mTitleLayout;
    private TextView mTitleView;
    private ImageView mMoreBtn;
    private boolean isShowImagePath=true;
    private String ImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebview = RedpacketWebViewActivity.this;
        setContentView(R.layout.activity_webview);
        webView = (WebView) findViewById(R.id.webView);
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
                RedpacketWebViewActivity.this.finish();
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }else{
                    RedpacketWebViewActivity.this.finish();
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
        menuWindow = new SelectPicPopupWindow(RedpacketWebViewActivity.this, itemsOnClick);
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
    private void initWebView(){
        //允许加载js脚本,这个很重要,当初为了这个弄了半天
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                WebView.HitTestResult hit = view.getHitTestResult();
                int hitType = hit.getType();
                if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE) {//点击超链接
                    //这里执行自定义的操作
                    view.loadUrl(url);
                    mUrl = url;
                    return true;//返回true浏览器不再执行默认的操作
                } else if (hitType == 0) {//重定向时hitType为0
                    return false;//不捕获302重定向
                } else {
                    return false;
                }

            }
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                super.onPageFinished(view, url);
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
                mTitleView.setText(title);
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
    }
    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            if(html.contains("成功")){
                Intent intent = new Intent();
                intent.putExtra("url","http://shop.wei20.cn/repacket/wishmb/openrepacket.shtml");
                //intent.putExtra("title",greetings.getText().toString());
                setResult(RESULT_OK,intent);
                RedpacketWebViewActivity.this.finish();
            }
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
                showMenuWindow(mTitleLayout);
                break;
            default:
                break;
        }
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
                default:
                    break;
            }
            if (menuWindow != null && menuWindow.isShowing()) {
                menuWindow.dismiss();
                menuWindow = null;
            }
        }
    };
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
                        intent.putExtra("title", mTitleView.getText().toString());
                        intent.putExtra("imagePath", ImagePath);
                        switch (whichButton) {
                            case 0:
                                intent.putExtra("type", SendMessageToWX.Req.WXSceneSession);
                                break;
                            case 1:
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
            RedpacketWebViewActivity.this.finish();
            return true;
        }
        return false;
    }
}
