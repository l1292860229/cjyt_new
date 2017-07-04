package com.coolwin.XYT.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.coolwin.XYT.R;
import com.coolwin.XYT.global.IMCommon;

/**
 * 欢迎页面
 * @author dongli
 *
 */
public class WelcomeActivity extends Activity {

	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			//设置顶部状态栏颜色
			window.setStatusBarColor(Color.WHITE);
			//设置底部导航栏颜色(有的手机没有)
			window.setNavigationBarColor(getResources().getColor(R.color.white));
		}else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		mContext = this;
		DisplayMetrics metrics= new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		IMCommon.mScreenWidth =metrics.widthPixels;
		IMCommon.mScreenHeight = metrics.heightPixels;
		showMainpage();
	}
	public void showMainpage(){
//		SharedPreferences mPreferences = this.getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
//		final String firstopen = mPreferences.getString(IMCommon.FIRSTOPEN, "");
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				Intent intentt;
//				if (firstopen != null && !"".equals(firstopen)) {
					intentt = new Intent(WelcomeActivity.this,MainActivity.class);
//				}else{
//					intentt = new Intent(WelcomeActivity.this,LoginMainActivity.class);
//				}
			    WelcomeActivity.this.startActivity(intentt);
			    WelcomeActivity.this.finish();
			}
		}, 2000);
	 }
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.finish();
			System.exit(0);
		}
		return super.dispatchKeyEvent(event);
	}
	
}
