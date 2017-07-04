package com.coolwin.XYT.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.Menu;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.Entity.enumentity.InformationType;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.FileListActivity;
import com.coolwin.XYT.activity.InformationActivity;
import com.coolwin.XYT.activity.MyDingDanActivity;
import com.coolwin.XYT.activity.MyIndexActivity;
import com.coolwin.XYT.adapter.ShopMenuAdapter;
import com.coolwin.XYT.databinding.MyShopBinding;
import com.coolwin.XYT.databinding.ShopMenuBinding;
import com.coolwin.XYT.interfaceview.UIShopMenu;
import com.coolwin.XYT.presenter.ShopMenuPresenter;

import java.util.ArrayList;
import java.util.List;

public class MyShopFragment extends BaseFragment<ShopMenuPresenter> implements UIShopMenu {
	ShopMenuBinding binding;
	ShopMenuAdapter shopMenuAdapter;
	List<Menu> menus = new ArrayList<>();
	@Override
	public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.shop_menu,container,false);
		binding.listlayout.setLayoutManager(new LinearLayoutManager(context));
		shopMenuAdapter = new ShopMenuAdapter(context,menus);
		binding.listlayout.setAdapter(shopMenuAdapter);
		MyShopBinding  myShopBinding = DataBindingUtil.inflate(inflater, R.layout.my_shop,container,false);
		myShopBinding.setBehavior(this);
		shopMenuAdapter.setHeaderView(myShopBinding.getRoot());
		return binding.getRoot();
	}

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getData(UrlConstants.BASEURL+"user/api/getmenu_my");
    }

    /**
	 * 打开商城首页
	 * @param view
	 */
	public void openIndex(View view){
		startActivity(new Intent(context,MyIndexActivity.class));
	}
	/**
	 * 打开我的订单管理
	 * @param view
	 */
	public void openDingDan(View view){
		startActivity(new Intent(context,MyDingDanActivity.class));
	}
	/**
	 * 打开文件列表
	 * @param view
	 */
	public void openFileList(View view){
		startActivity(new Intent(context,FileListActivity.class));
	}
	/**
	 * 打开我的商品
	 * @param view
	 */
	public void openCommodity(View view){
		Intent intent = new Intent(context,InformationActivity.class);
		intent.putExtra(InformationActivity.DATATYPE, InformationType.commodity);
		startActivity(intent);
	}
	/**
	 * 打开我的资讯
	 * @param view
	 */
	public void openInformation(View view){
		Intent intent = new Intent(context,InformationActivity.class);
		intent.putExtra(InformationActivity.DATATYPE, InformationType.Information);
		startActivity(intent);
	}

	@Override
	public void init(List<Menu> menus) {
		this.menus = menus;
		shopMenuAdapter.setData(this.menus);
		shopMenuAdapter.notifyDataSetChanged();
	}
}
