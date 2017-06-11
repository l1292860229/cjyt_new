package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopCommodityBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CommodityAdapter extends BaseAdapter<MyCommodity>{
    public AdapterView.OnItemLongClickListener onItemLongClickListener;
    public CommodityAdapter(Context context, List<MyCommodity> mList) {
        super(context);
        this.mList = mList;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShopCommodityBinding shopCommodityBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.shop_commodity, parent, false);
        MyRecycleViewHolder myRecycleViewHolder = new MyRecycleViewHolder(shopCommodityBinding.getRoot());
        myRecycleViewHolder.setBinding(shopCommodityBinding);
        return myRecycleViewHolder;
    }
    @Override
    public void onBindViewHolder(final MyRecycleViewHolder holder, int position) {
        ShopCommodityBinding shopCommodityBinding  = (ShopCommodityBinding) holder.getBinding();
        MyCommodity subjects = mList.get(position);
        shopCommodityBinding.setName(subjects.getTitle());
        shopCommodityBinding.setMoney(subjects.getPrice()+"元");
//        shopCommodityBinding.setDiscount(subjects.discount+"折");
        if (subjects.getPicture()!=null && subjects.getPicture().size()>0) {
            Phoenix.with(shopCommodityBinding.pic1)
                    .load(subjects.getPicture().get(0).smallUrl);
        }
        shopCommodityBinding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemLongClickListener!=null){
                    onItemLongClickListener.onItemLongClick(null,v,holder.getLayoutPosition(),holder.getLayoutPosition());
                }
                return false;
            }
        });
    }
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }
}

