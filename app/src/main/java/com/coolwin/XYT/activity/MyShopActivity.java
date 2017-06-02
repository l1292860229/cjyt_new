package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.MyShopBinding;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyShopActivity extends BaseActivity {
    MyShopBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  =  DataBindingUtil.setContentView(this, R.layout.my_shop);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的商城");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
    }

    /**
     * 打开商城首页
     * @param view
     */
    public void openIndex(View view){
        startActivity(new Intent(this,MyIndexActivity.class));
    }

    /**
     * 打开我的订单管理
     * @param view
     */
    public void openDingDan(View view){
        startActivity(new Intent(this,MyDingDanActivity.class));
    }

    /**
     * 打开我的商品
     * @param view
     */
    public void openCommodity(View view){
        startActivity(new Intent(this,CommodityActivity.class));
    }
}
