package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.AboutPageBinding;
import com.coolwin.XYT.global.FeatureFunction;

/**
 * 关于我们页面
 * @author dongli
 *
 */
public class AboutActivity extends BaseActivity {
	AboutPageBinding binding;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding =  DataBindingUtil.setContentView(this, R.layout.about_page);
		binding.titleLayout.setBehavior(this);
		context = this;
		initComponent();
	}
	
	/**
	 * 实例化控件
	 */
	private void initComponent(){
		binding.titleLayout.title.setText("关于我们");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
		binding.setVersion(context.getString(R.string.ochat_app_name) + FeatureFunction.getAppVersionName(AboutActivity.this));
		binding.setCopyright(context.getString(R.string.ochat_app_name) +"版权所有 深圳酷盈科技有限公司 ");
		binding.setWebsiteHint(" 官方网站 http://www.winchat.com.cn ");
	}
}
