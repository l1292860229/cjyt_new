package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.NewUserBinding;
import com.coolwin.XYT.interfaceview.UIRegister;
import com.coolwin.XYT.presenter.RegisterPresenter;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;

/**
 * 新用户注册
 * @author dongli
 *
 */
public class RegisterActivity extends BaseActivity<RegisterPresenter> implements UIRegister {

	NewUserBinding binding;
	String mCode;
	int time=60;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this,R.layout.new_user);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		binding.titleLayout.title.setText("注册");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
	}

	public void sendCode(View view){
		String telephone = binding.telephone.getText().toString().trim();
		if(StringUtil.isNull(telephone)){
			UIUtil.showMessage(context,"电话不能为空");
			return;
		}
		mPresenter.sendCode(telephone);
		updateCode();
	}
	public void updateCode(){
		time--;
		binding.sendcode.setText(time+"秒");
		binding.sendcode.setEnabled(false);
		if(time<=0){
			time = 60;
			binding.sendcode.setText("获取验证码");
			binding.sendcode.setEnabled(true);
		}else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateCode();
						}
					});
				}
			}).start();
		}
	}
	public void  register(View view){
		String name = binding.name.getText().toString().trim();
		String tjr = binding.tjr.getText().toString().trim();
		String telephone = binding.telephone.getText().toString().trim();
		String code = binding.code.getText().toString().trim();
		String password = binding.password.getText().toString().trim();
		if(StringUtil.isNull(name)){
			UIUtil.showMessage(context,"姓名不能为空");
			return;
		}
		if(StringUtil.isNull(tjr)){
			UIUtil.showMessage(context,"推荐人不能为空");
			return;
		}
		if(StringUtil.isNull(telephone)){
			UIUtil.showMessage(context,"电话不能为空");
			return;
		}
		if(StringUtil.isNull(code)){
			UIUtil.showMessage(context,"验证码不能为空");
			return;
		}
		if(StringUtil.isNull(password)){
			UIUtil.showMessage(context,"密码不能为空");
			return;
		}
		if(!code.equals(mCode)){
			UIUtil.showMessage(context,"验证码不正确");
			return;
		}
		mPresenter.register(name,tjr,telephone,password);
	}

	@Override
	public void showLoading() {
		super.showLoading("提交中...");
	}

	@Override
	public void setCode(String code) {
		mCode = code;
	}
}