package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CompoundButton;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.CaptureActivity;
import com.coolwin.XYT.Entity.constant.Constants;
import com.coolwin.XYT.IntentService.DownloadService;
import com.coolwin.XYT.MyBbsListActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.SendBbsActivity;
import com.coolwin.XYT.databinding.ActivityMainBinding;
import com.coolwin.XYT.fragment.ChatFragment;
import com.coolwin.XYT.fragment.FriensLoopFragment;
import com.coolwin.XYT.fragment.MyShopFragment;
import com.coolwin.XYT.fragment.ProfileFragment;
import com.coolwin.XYT.fragment.ShopMemberFragment;
import com.coolwin.XYT.fragment.ShopIndexFragment;
import com.coolwin.XYT.interfaceview.UISettingTab;
import com.coolwin.XYT.presenter.SettingTabPresenter;
import com.coolwin.XYT.service.SnsService;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.library.view.BelowView;

import java.util.Map;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;


public class MainActivity extends BaseActivity<SettingTabPresenter> implements UISettingTab {
	private Fragment[] fragments;
	public static String lbs="";
	ActivityMainBinding binding;
	BelowView blv;
	ChatFragment chatFragment;
	FriensLoopFragment friensLoopFragment;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(GetDataUtil.getLogin(context) == null || GetDataUtil.getLogin(context).nickname==null){
			startActivity(new Intent(context, LoginActivity.class));
			this.finish();
			return;
		}else {
			loginXMPP();
			//检测更新
			checkUpgrade();
		}
		RxBus.get().register(this);
		setActionBarLayout();
		//添加的弹出框
		blv = new BelowView(this, R.layout.addxml);
		//第一个按钮选中
		binding.btnIndex.setSelected(true);
		tempView = binding.btnIndex;
		chatFragment =  new ChatFragment();
		friensLoopFragment = new FriensLoopFragment();
		fragments = new Fragment[]{new ShopIndexFragment(),new ShopMemberFragment(),new MyShopFragment(),friensLoopFragment, new ProfileFragment()};
		if (!fragments[0].isAdded()) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
					.show(fragments[0]).commit();
		}
		binding.titleLayout.layoutTitle.setVisibility(View.GONE);
	}
	public void setActionBarLayout(){
		binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
		binding.setBehavior(this);
		binding.titleLayout.title.setText(getResources().getString(R.string.app_name));
		binding.titleLayout.setBehavior(this);
	}
	/**
	 * 连接到xmpp
	 */
	private void loginXMPP(){
		Intent intent = new Intent(getBaseContext(), SnsService.class);
		startService(intent);
	}
	private int index,currentTabIndex=0;
	private View tempView;
	//底部标签的切换
	public void onTabClicked(View view) {
		if(tempView!=null){
			tempView.setSelected(false);
		}
		view.setSelected(true);
		tempView = view;
		binding.titleLayout.title.setVisibility(View.VISIBLE);
		binding.titleLayout.tglloop.setVisibility(View.GONE);
		binding.titleLayout.layoutTitle.setVisibility(View.VISIBLE);
		switch (view.getId()) {
			case R.id.btn_index:
				binding.titleLayout.layoutTitle.setVisibility(View.GONE);
				index = 0;
				break;
			case R.id.btn_userinfo:
				index = 1;
				break;
			case R.id.btn_xcx:
				index = 2;
				break;
			case R.id.btn_container_supper:
				//如果是动态分类就隐藏标题并显示切换按钮
				binding.titleLayout.title.setVisibility(View.GONE);
				binding.titleLayout.tglloop.setVisibility(View.VISIBLE);
				binding.titleLayout.tglloop.setBackgroundResource(R.drawable.selector_donttai_toggle);
				binding.titleLayout.tglloop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							fragments[index] = chatFragment;
						}else{
							fragments[index] = friensLoopFragment;
						}
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.fragment_container, fragments[index])
								.show(fragments[index]).commit();
					}
				});
				index = 3;
				break;
			case R.id.btn_profile:
				index = 4;
				break;
		}
		showView();
	}
	public void showView(){
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		currentTabIndex = index;
	}
	private void checkUpgrade(){
		mPresenter.getNewVersion(UIUtil.getAppVersionName(context),false);
	}

	@Override
	public void right_btn(View view) {
		blv.showBelowView(view, true, 0, 0);
		View v =  blv.getBelowView();
		//我的鱼塘
		v.findViewById(R.id.my_chat_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent myintent = new Intent(context, MyBbsListActivity.class);
				myintent.putExtra("type","1");
				startActivity(myintent);
				blv.dismissBelowView();
			}
		});
		//创建鱼塘
		v.findViewById(R.id.create_chat_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent addIntent = new Intent();
				addIntent.setClass(context, SendBbsActivity.class);
				addIntent.putExtra("type",1);
				startActivity(addIntent);
				blv.dismissBelowView();
			}
		});
		//扫一扫
		v.findViewById(R.id.sao_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(context, CaptureActivity.class));
				blv.dismissBelowView();
			}
		});
	}

	@Override
	public void downNewVersion(final Map<String, String> map) {
		AbDialogUtil.showAlertDialog(context, "软件更新提示", map.get("description"), new AbAlertDialogFragment.AbDialogOnClickListener() {
			@Override
			public void onPositiveClick() {
				Intent intent = new Intent(context, DownloadService.class);
				intent.putExtra("apkUrl",map.get("url"));
				startService(intent);
				UIUtil.showMessage(context,"正在下载");
			}
			@Override
			public void onNegativeClick() {
			}
		});
	}
	/**
	 *  事件线的来源
	 *  1.SettingTabActivity->openNoLogin 来源
	 */
	@Subscribe(code = Constants.MAIN,
			threadMode = ThreadMode.MAIN)
	public void receive1002(){
		startActivity(new Intent(context, LoginActivity.class));
		this.finish();
	}
	@Override
	protected void onDestroy() {
		RxBus.get().unRegister(this);
		super.onDestroy();
	}
}