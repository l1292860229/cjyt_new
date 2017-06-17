package com.coolwin.XYT.activity;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.LoginBinding;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.interfaceview.UIPublic;
import com.coolwin.XYT.presenter.LoginPresenter;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;


/**
 * 登录
 * @author dongli
 *
 */
public class LoginActivity extends BaseActivity<LoginPresenter> implements UIPublic {

	LoginBinding binding;
//	boolean canClose=false;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.login);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		binding.titleLayout.title.setText("登录");
//		final String firstopen =getIntent().getStringExtra("firstopen");
//		if (firstopen != null && !"".equals(firstopen)) {
//			canClose = true;
//			ImageView leftbtn = binding.titleLayout.leftIcon;
//			leftbtn.setImageResource(R.drawable.back_icon);
//		}
		String username = GetDataUtil.getUsername(context);
		String password = GetDataUtil.getPassword(context);
		binding.username.setText(username);
		binding.password.setText(password);
	}

	/**
	 * 登录
	 * @param view
	 */
	public void login(View view){
		String username = binding.username.getText().toString().trim();
		String password = binding.password.getText().toString().trim();
		if (StringUtil.isNull(username)) {
			UIUtil.showMessage(context,"用户名不能为空");
			return;
		}
		if (StringUtil.isNull(password)) {
			UIUtil.showMessage(context,"密码不能为空");
			return;
		}
		mPresenter.login(username,password);
	}

	/**
	 * 注册
	 * @param view
	 */
	public void register(View view){
		startActivity(new Intent(context,RegisterActivity.class));
	}

	@Override
	public void close(View view) {
//		if(canClose){
//			super.close(view);
//		}
	}

	/**
	 * 键盘返回事件
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			setResult(GlobalParam.RESULT_EXIT);
			this.finish();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void showLoading() {
		super.showLoading("登录中...");
	}
}
