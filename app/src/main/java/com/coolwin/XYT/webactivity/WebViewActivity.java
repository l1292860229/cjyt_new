package com.coolwin.XYT.webactivity;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.BaseActivity;
import com.coolwin.XYT.databinding.ActivityWebviewBinding;
import com.facebook.fresco.helper.Phoenix;

/**
 * Created by yf on 2016/11/24.
 */

public class WebViewActivity extends BaseActivity {
    public static final String WEBURL = "url";
    ActivityWebviewBinding binding;
    public String mUrl;
    private GestureDetector gestureDetector;
    private int downX, downY;

    @Override
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        binding.titleLayout.setBehavior(this);
        mUrl = this.getIntent().getStringExtra(WEBURL);
        //WebView加载web资源
        binding.webView.loadUrl(mUrl);
        initWebView();
        setActionBarLayout();
        Phoenix.with(binding.webloading)
                .load(R.drawable.timg);
    }

    @Override
    public void left_btn2(View view) {
        this.finish();
    }

    @Override
    public void close(View view) {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            this.finish();
        }
    }

    public void setActionBarLayout() {
        binding.titleLayout.leftIcon.setImageResource(R.drawable.back_btn);
        binding.titleLayout.leftTitle.setText("返回");
        binding.titleLayout.leftTitle2.setText("关闭");
        binding.titleLayout.rightBtn.setImageResource(R.drawable.more_btn);
        binding.titleLayout.rightBtn.setVisibility(View.VISIBLE);
    }

    private void initWebView() {
        binding.webView.addJavascriptInterface(new InJavaScriptLocalObj(context), "messageToApp");
        binding.webView.setWebViewClient(new MyWebViewClient(context, binding.webloading));
        //获取webView触摸的xy
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                downX = (int) e.getX();
                downY = (int) e.getY();
            }
        });
        binding.webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result)
                    return false;
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;
                //final ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(mWebview, ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW, FeatureFunction.dip2px(mWebview, 120), FeatureFunction.dip2px(mWebview, 90));
                switch (type) {
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                        // Log.d(DEG_TAG, "超链接");
                        break;
                    case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项
//                        imgurl = result.getExtra();
//                        itemLongClickedPopWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, downX, downY + 10);
                        break;
                    default:
                        break;
                }
//                itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage)
//                        .setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                itemLongClickedPopWindow.dismiss();
//                                isShowImagePath = true;
//                                new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
//                            }
//                        });
//                //分享图片
//                itemLongClickedPopWindow.getView(R.id.item_longclicked_fenXiangImage)
//                        .setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                itemLongClickedPopWindow.dismiss();
//                                isShowImagePath = false;
//                                new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
//                            }
//                        });
                return true;
            }
        });
        binding.webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                binding.titleLayout.title.setText(title);
            }

        });
        binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //将图片调整到适合WebView的大小
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //设置图片自动加载
        binding.webView.getSettings().setLoadsImagesAutomatically(true);
        //允许加载js脚本,这个很重要,当初为了这个弄了半天
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //H5 localStorage支持
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        binding.webView.getSettings().setAppCachePath(appCachePath);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setAppCacheEnabled(true);
        binding.webView.getSettings().setDefaultTextEncodingName("gb2312");
    }

    @Override
    protected void onDestroy() {
        if (binding.webView != null) {
            binding.webView.stopLoading();
            binding.webView.removeAllViews();
            binding.webView.destroy();
        }
        super.onDestroy();
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.more_btn:
//                shareLayout.setVisibility(View.VISIBLE);
////                showMenuWindow(mTitleLayout);
//                return;
//            case R.id.cancel:
//                break;
//            case R.id.shanji:
//                Intent shanjiIntent = new Intent();
//                shanjiIntent.setClass(mWebview,SendMovingActivity.class);
//                shanjiIntent.putExtra("url",mUrl);
//                shanjiIntent.putExtra("title", urltitle);
//                if (IMCommon.getLoginResult(mWebview).userDj.equals("0")) {
//                    Toast.makeText(mWebview,"等级不够无法分享", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                startActivity(shanjiIntent);
//                break;
//            case R.id.friend:
//                getUrlImage();
//                Intent friendIntent = new Intent();
//                friendIntent.setClass(mWebview, ShareActivity.class);
//                friendIntent.putExtra("url",mUrl);
//                friendIntent.putExtra("title", urltitle);
//                friendIntent.putExtra("imagePath", ImagePath);
//                friendIntent.putExtra("shareurl_msg", new ShareUrl(mUrl,urltitle,ImagePath));
//                startActivity(friendIntent);
//                break;
//            case R.id.wechat:
//                Intent wechatIntent = new Intent();
//                wechatIntent.setClass(mWebview,WXEntryActivity.class);
//                wechatIntent.putExtra("url",mUrl);
//                wechatIntent.putExtra("title", urltitle);
//                wechatIntent.putExtra("imagePath", ImagePath);
//                wechatIntent.putExtra("type", SendMessageToWX.Req.WXSceneSession);
//                startActivity(wechatIntent);
//                break;
//            case R.id.wechatq:
//                Intent wechatqIntent = new Intent();
//                wechatqIntent.setClass(mWebview,WXEntryActivity.class);
//                wechatqIntent.putExtra("url",mUrl);
//                wechatqIntent.putExtra("title", urltitle);
//                wechatqIntent.putExtra("imagePath", ImagePath);
//                wechatqIntent.putExtra("type", SendMessageToWX.Req.WXSceneTimeline);
//                startActivity(wechatqIntent);
//                break;
//            case R.id.liulan:
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                intent.setData(Uri.parse(mUrl));
//                startActivity(intent);
//                break;
//            case  fuzhi:
//                ClipboardManager cm =(ClipboardManager) mWebview.getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setText(mUrl);
//                break;
//            default:
//                break;
//        }
//        shareLayout.setVisibility(View.GONE);
//    }
    //分享详细
    @Override
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (binding.webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
            binding.webView.goBack();
            return true;
        } else if (!binding.webView.canGoBack()) {
            this.finish();
            return true;
        }
        return false;
    }
}
