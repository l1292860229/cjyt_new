package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.MyDingdanBinding;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyDingDanActivity extends BaseActivity {
    MyDingdanBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.my_dingdan);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的订单");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
    }
}
