package com.coolwin.XYT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.coolwin.XYT.activity.LoginActivity;
import com.coolwin.XYT.activity.MainActivity;
import com.coolwin.XYT.activity.RegisterActivity;
import com.coolwin.XYT.global.GlobalParam;

/**
 * 欢迎页面
 * @author dongli
 *
 */
public class LoginMainActivity extends Activity {

	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_main);
		mContext = this;
		showMainpage();
	}
	public void showMainpage(){
		findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginMainActivity.this,LoginActivity.class);
				intent.putExtra("firstopen","1");
				LoginMainActivity.this.startActivityForResult(intent, GlobalParam.LOGIN_REQUEST);
			}
		});
		findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginMainActivity.this,RegisterActivity.class);
				LoginMainActivity.this.startActivity(intent);
			}
		});
	 }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case GlobalParam.LOGIN_REQUEST:
				if (resultCode == RESULT_OK) {// dl repair
					Intent intent = new Intent(LoginMainActivity.this,MainActivity.class);
					LoginMainActivity.this.startActivity(intent);
					LoginMainActivity.this.finish();
					return;
				}
				break;
		}
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
