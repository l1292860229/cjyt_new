package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.CommodityAdapter;
import com.coolwin.XYT.databinding.CommodityBinding;
import com.coolwin.XYT.interfaceview.UICommodity;
import com.coolwin.XYT.presenter.CommodityPresenter;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CommodityActivity extends BaseActivity<CommodityPresenter>  implements UICommodity {
    CommodityBinding binding;
    CommodityAdapter commodityAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.commodity);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的商品");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("添加");
        binding.ivCommodity.setLayoutManager(new LinearLayoutManager(context));
        mPresenter.init();
    }
    @Override
    public void right_text(View view) {
        startActivity(new Intent(this,AddCommodityActivity.class));
    }
    @Override
    public void showLoading() {
    }
    @Override
    public void init(MyCommodity data) {
        commodityAdapter = new CommodityAdapter(context,data.subjects);
        binding.ivCommodity.setAdapter(commodityAdapter);
    }
}
