package com.coolwin.XYT.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.coolwin.XYT.R;
import com.coolwin.XYT.global.Util;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import static android.R.attr.type;


/*
 * 微信登录，分享应用中必须有这个名字叫WXEntryActivity，并且必须在wxapi包名下，腾讯官方文档中有要求
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    public static final String APP_ID = "wxd5f5aa165bb85a47";// 微信开放平台申请到的app_id
    protected static final int THUMB_SIZE = 150;// 分享的图片大小
    private IWXAPI api;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        api.registerApp(APP_ID);
        try {
            api.handleIntent(getIntent(), this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String url = getIntent().getStringExtra("url");
        int type = getIntent().getIntExtra("type",0);
        String title = getIntent().getStringExtra("title");
        String imagePath = getIntent().getStringExtra("imagePath");
        showProgressDialog(getResources().getString(R.string.add_more_loading));
        if (imagePath != null && !imagePath.equals("")) {
            shareLocalPic2Cir(imagePath);
        }else{
            shareUrl2Circle(url,type,title);
        }
    }
    private void shareLocalPic2Cir(String picPath) {
        //TODO 判断图片是否存在

        WXImageObject imageObject = new WXImageObject();
        imageObject.setImagePath(picPath);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;
        Bitmap bmp = BitmapFactory.decodeFile(picPath);
        Bitmap thumb = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        Req req = new Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        if (Req.WXSceneTimeline==type) {
            req.scene = Req.WXSceneTimeline;
        }else{
            req.scene = Req.WXSceneSession;
        }
        api.sendReq(req);
        WXEntryActivity.this.finish();
    }

    /**
     * @param url 要分享的链接
     */
    private void shareUrl2Circle(final String url, int type, String title) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = title;
        Req req = new Req();
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        //req.scene = Req.WXSceneTimeline;// 表示发送场景为朋友圈，这个代表分享到朋友圈
//      // req.scene = SendMessageToWX.Req.WXSceneSession;//表示发送场景为好友对话，这个代表分享给好友
//      // req.scene = SendMessageToWX.Req.WXSceneTimeline;// 表示发送场景为收藏，这个代表添加到微信收藏
        if (Req.WXSceneTimeline==type) {
            req.scene = Req.WXSceneTimeline;
        }else{
            req.scene = Req.WXSceneSession;
        }
        api.sendReq(req);
        WXEntryActivity.this.finish();
    }
    /**
     * 构造一个用于请求的唯一标识
     *
     * @param type 分享的内容类型
     * @return
     */
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }
    public void showProgressDialog(String msg){
        mProgressDialog = new ProgressDialog(WXEntryActivity.this);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }
    public void hideProgressDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    /**
     * 请求回调接口
     */
    @Override
    public void onReq(BaseReq req) { }

    /**
     * 请求响应回调接口
     */
    @Override
    public void onResp(BaseResp resp) {
        int code = resp.errCode;
        if (code == 0){
            //TODO 显示充值成功的页面和需要的操作
        }
        if (code == -1){
            //TODO 错误
        }
        if (code == -2){
            //TODO 用户取消
        }
        WXEntryActivity.this.finish();
    }
}
