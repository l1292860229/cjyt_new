package com.coolwin.XYT.map;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.coolwin.XYT.Entity.CountryList;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.tendcloud.tenddata.TCAgent;

/**
 * http://lbsyun.baidu.com/apiconsole/key
 * 百度地图key
 * @author dongli
 *
 */
public class BMapApiApp extends Application {
    private Bitmap bitmap;
    private static BMapApiApp mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    private static CountryList mCountryList = null;
   

   //public static final String strKey = "6VINDcwKFAHfjGlVwbqUmVxx"; //调试版
//  public static final String strKey = "FhpSpL7ZajWdt6M2nOo0rvQ3"; //发布版
//  public static final String strKey = "lcLbmRFo8jtyUcgMSIsPZs2jz9d2crjz"; //LYP测试
    public static final String strKey = "ZCeG9RBDpV6IOodybpi5WirgTw5gLj9C"; //as测试
    
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    	IMCommon.verifyNetwork(mInstance);
        initEngineManager(this);
        if(IMCommon.getUserId(mInstance)!=null && !IMCommon.getUserId(mInstance).equals("")){
        	 new Thread(){
             	public void run() {
             		try {
             			mCountryList = IMCommon.getIMInfo()
             					.getCityAndContryUser();
             		} catch (IMException e) {
             			e.printStackTrace();
             		}
             	};
             }.start();
        }
        TCAgent.LOG_ON=true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this, "8515B59DA24F44929E1E36742FC175F2", "pre.im");
    }
    
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }
        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(BMapApiApp.getInstance().getApplicationContext(),
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }
    
    public static BMapApiApp getInstance() {
        return mInstance;
    }
    
    public static CountryList getContryList(){
    	return mCountryList;
    }
    
    public static void setContryList(CountryList contryList){
    	mCountryList = contryList;
    }
   
    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            //非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(BMapApiApp.getInstance().getApplicationContext(),
                        "请在 BMapApiApp.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                BMapApiApp.getInstance().m_bKeyRight = false;
            }
            else{
                BMapApiApp.getInstance().m_bKeyRight = true;
                //Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), 
                //        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}