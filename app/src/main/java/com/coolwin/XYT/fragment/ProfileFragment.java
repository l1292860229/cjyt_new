package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.EditProfileActivity;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.MyAlbumActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.CertificationActivity;
import com.coolwin.XYT.activity.SettingTabActivity;
import com.coolwin.XYT.databinding.FragmentProfileBinding;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;
import com.facebook.fresco.helper.Phoenix;
import com.tendcloud.tenddata.TCAgent;

public class ProfileFragment extends Fragment{
    public static final String ATTEST_TRUE = "1";
    public static final String ATTEST_FALSE= "0";
    public static final String ATTEST_ING= "2";
    Context context;
    FragmentProfileBinding binding;
    Login login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getActivity();
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        binding.setBehavior(this);
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
    }

    private void initView(){
        login = GetDataUtil.getLogin(context);
        if (login==null) {
            return;
        }
        binding.setName(login.nickname);
        String fxid=GetDataUtil.getUsername(context);
        if(StringUtil.isNull(fxid)){
            binding.setUsername("未设置");
        }else{
            binding.setUsername("鱼塘号:"+fxid);
        }
        Phoenix.with(binding.ivAvatar).load(login.headsmall);
        Phoenix.with().load();
        if (login.isattest==null || ATTEST_FALSE.equals(login.isattest)) {
            binding.isrenzhen.setVisibility(View.GONE);
        }else if(ATTEST_TRUE.equals(login.isattest)){
            binding.renzhen.setVisibility(View.GONE);
        }else if(ATTEST_ING.equals(login.isattest)){
            binding.isrenzhen.setVisibility(View.GONE);
            binding.renzhen.setText("审核中");
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
}
