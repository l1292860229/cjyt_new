package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.ab.view.sliding.AbSlidingTabView;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.MyDingdanBinding;
import com.coolwin.XYT.fragment.OrderFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyDingDanActivity extends BaseActivity {
    MyDingdanBinding binding;
    AbSlidingTabView mAbSlidingTabView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.my_dingdan);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的订单");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        mAbSlidingTabView = binding.mAbSlidingTabView;
        //缓存数量
        mAbSlidingTabView.getViewPager().setOffscreenPageLimit(1);
        //设置样式
        mAbSlidingTabView.setTabTextColor(Color.BLACK);
        mAbSlidingTabView.setTabSelectColor(Color.rgb(30, 168, 131));
        mAbSlidingTabView.setTabBackgroundResource(R.drawable.tab_bg);
        mAbSlidingTabView.setTabLayoutBackgroundResource(R.drawable.slide_top);
        List<String> tabTexts = new ArrayList<String>();
        tabTexts.add("所有");
        tabTexts.add("未处理");
        tabTexts.add("已完成");
        tabTexts.add("已发货");
        tabTexts.add("已取消");
        List<android.app.Fragment> mFragments = new ArrayList<>();
        mFragments.add(OrderFragment.newInstance(null));
        mFragments.add(OrderFragment.newInstance("0"));
        mFragments.add(OrderFragment.newInstance("4"));
        mFragments.add(OrderFragment.newInstance("3"));
        mFragments.add(OrderFragment.newInstance("5"));
        //增加一组
        mAbSlidingTabView.addItemViews(tabTexts, mFragments);
    }
}
