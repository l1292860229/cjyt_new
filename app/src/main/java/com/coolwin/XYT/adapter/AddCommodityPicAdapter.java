package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopIndexBinding;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class AddCommodityPicAdapter extends BaseAdapter<String>{
    private AdapterView.OnItemClickListener mOnItemClickListener = null;
    public AddCommodityPicAdapter(Context context, List<String> mList) {
        super(context);
        this.mList = mList;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShopIndexBinding oneBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.shop_index, parent, false);
        MyRecycleViewHolder vh = new MyRecycleViewHolder(oneBinding.getRoot());
        vh.setBinding(oneBinding);
        return vh;
    }
    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, final int position) {
        ViewDataBinding dataBinding = holder.getBinding();
        String string =  mList.get(position);
        ShopIndexBinding oneBinding = (ShopIndexBinding) dataBinding;
        //判断是否为空
        if(StringUtil.isNull(string)){
            Phoenix.with(oneBinding.pic1)
                    .setWidth(200)
                    .setHeight(200)
                    .load(R.drawable.smiley_add_btn);
            oneBinding.pic1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(null,v,position,0);
                }
            });
        }else{
            Phoenix.with(oneBinding.pic1)
                    .setWidth(200)
                    .setHeight(200)
                    .load(string);
            oneBinding.pic1.setOnClickListener(null);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}