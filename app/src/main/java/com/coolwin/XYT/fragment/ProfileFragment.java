package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Menu;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.MyAlbumActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.CertificationActivity;
import com.coolwin.XYT.activity.EditProfileActivity;
import com.coolwin.XYT.activity.SettingTabActivity;
import com.coolwin.XYT.adapter.ShopMenuAdapter;
import com.coolwin.XYT.databinding.FragmentProfileBinding;
import com.coolwin.XYT.databinding.ShopMenuBinding;
import com.coolwin.XYT.interfaceview.UIShopMenu;
import com.coolwin.XYT.presenter.ShopMenuPresenter;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;
import com.facebook.fresco.helper.Phoenix;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment<ShopMenuPresenter> implements UIShopMenu{
    public static final String ATTEST_TRUE = "1";
    public static final String ATTEST_FALSE= "0";
    public static final String ATTEST_ING= "2";
    Login login;
    ShopMenuBinding binding;
    ShopMenuAdapter shopMenuAdapter;
    List<Menu> menus = new ArrayList<>();
    FragmentProfileBinding fragmentProfileBinding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.shop_menu,container,false);
        binding.listlayout.setLayoutManager(new LinearLayoutManager(context));
        shopMenuAdapter = new ShopMenuAdapter(context,menus);
        binding.listlayout.setAdapter(shopMenuAdapter);
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile,container,false);
        fragmentProfileBinding.setBehavior(this);
        shopMenuAdapter.setHeaderView(fragmentProfileBinding.getRoot());
        TCAgent.onPageStart(context, "ProfileFragment");
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mPresenter.getData(UrlConstants.BASEURL+"user/api/getmenu");
    }

    private void initView(){
        login = GetDataUtil.getLogin(context);
        if (login==null) {
            return;
        }
        fragmentProfileBinding.setName(login.nickname);
        String fxid=GetDataUtil.getUsername(context);
        if(StringUtil.isNull(fxid)){
            fragmentProfileBinding.setUsername("未设置");
        }else{
            fragmentProfileBinding.setUsername("鱼塘号:"+fxid);
        }
        Phoenix.with(fragmentProfileBinding.ivAvatar).load(login.headsmall);
        if (login.isattest==null || ATTEST_FALSE.equals(login.isattest)) {
            fragmentProfileBinding.isrenzhen.setVisibility(View.GONE);
        }else if(ATTEST_TRUE.equals(login.isattest)){
            fragmentProfileBinding.renzhen.setVisibility(View.GONE);
        }else if(ATTEST_ING.equals(login.isattest)){
            fragmentProfileBinding.isrenzhen.setVisibility(View.GONE);
            fragmentProfileBinding.renzhen.setText("审核中");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开个人认证
     * @param view
     */
    public void openCertification(View view){
        if(!ATTEST_FALSE.equals(login.isattest)){
           return;
        }
        startActivity(new Intent(context, CertificationActivity.class));
    }
    /**
     * 打开修改个人资料
     * @param view
     */
    public void openEditProfile(View view){
        startActivity(new Intent(context, EditProfileActivity.class));
    }

    /**
     * 打开我的小鱼圈
     * @param view
     */
    public void openXiangce(View view){
        startActivity(new Intent(context, MyAlbumActivity.class));
    }

    /**
     * 打开设置
     * @param view
     */
    public void openSetting(View view){
        startActivity(new Intent(context, SettingTabActivity.class));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        TCAgent.onPageEnd(context, "ProfileFragment");
    }

    @Override
    public void init(List<Menu> menus) {
        this.menus = menus;
        shopMenuAdapter.setData(this.menus);
        shopMenuAdapter.notifyDataSetChanged();
    }
}
