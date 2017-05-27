package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.FeedbackPageBinding;
import com.coolwin.XYT.interfaceview.UIFeedBack;
import com.coolwin.XYT.presenter.FeedBackPresenter;

/**
 * 意见反馈
 *
 */
public class FeedBackActivity extends BaseActivity<FeedBackPresenter> implements UIFeedBack {
	FeedbackPageBinding binding;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this,R.layout.feedback_page);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		initComponent();
	}

	/*
	 * 实例化控件
	 */
	private void initComponent(){
		binding.titleLayout.title.setText("意见反馈");
		binding.titleLayout.leftIcon.setImageResource(R.drawable.back_icon);
		binding.titleLayout.rightBtn.setImageResource(R.drawable.send_map_btn);
		binding.titleLayout.rightBtn.setVisibility(View.VISIBLE);
	}

	@Override
	public void right_btn(View view) {
		mPresenter.sendFeedBack(binding.content.getText().toString());
	}
	@Override
	public void showLoading() {
		super.showLoading("提交中...");
	}

	@Override
	public void closeFeedBack() {
		this.finish();
	}
}
