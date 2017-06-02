package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopCommodityBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CommodityAdapter extends BaseAdapter<MyCommodity.Subjects>{
    public Context context;
    public CommodityAdapter(Context context, List<MyCommodity.Subjects> mList) {
        this.context = context;
        this.mList = mList;
    }
    public List<MyCommodity.Subjects> getData(){
        return mList;
    }
    public void setData(List<MyCommodity.Subjects> model) {
        mList = model;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShopCommodityBinding shopCommodityBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.shop_commodity, parent, false);
        MyRecycleViewHolder vh = new MyRecycleViewHolder(shopCommodityBinding.getRoot());
        vh.setBinding(shopCommodityBinding);
        return vh;
    }
    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, final int position) {
        ViewDataBinding dataBinding = holder.getBinding();
        MyCommodity.Subjects subjects = mList.get(position);
        ShopCommodityBinding shopCommodityBinding = (ShopCommodityBinding) dataBinding;
        shopCommodityBinding.setName(subjects.Name);
        shopCommodityBinding.setMoney(subjects.LowSellPrice+"元");
        shopCommodityBinding.setDiscount(subjects.discount+"折");
        Phoenix.with(shopCommodityBinding.pic1)
                .load(subjects.url);
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}

