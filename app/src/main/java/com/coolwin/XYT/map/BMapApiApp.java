package com.coolwin.XYT.map;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.coolwin.XYT.Entity.CountryList;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.util.FrescoImageLoader;
import com.facebook.fresco.helper.Phoenix;
import com.tendcloud.tenddata.TCAgent;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * http://lbsyun.baidu.com/apiconsole/key
 * 百度地图key
 * @author dongli
 *
 */
public class BMapApiApp extends MultiDexApplication {
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
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        BMapManager.init();
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
        //图片选择器
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.BLACK)
                .setTitleBarTextColor(Color.WHITE)
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(mInstance,  new FrescoImageLoader(mInstance), theme)
                .build();
        GalleryFinal.init(coreConfig);
        //网络图片加载
        Phoenix.init(mInstance);
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}