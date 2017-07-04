package com.coolwin.XYT.adapter;

import android.content.Context;

import com.coolwin.XYT.Entity.Order;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.GoodsItemBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;

import java.util.List;

/**
 * Created by dell on 2017/7/3.
 */

public class GoodAdapter extends BaseAdapter<Order.Goods> {
    public GoodAdapter(Context context, List<Order.Goods> mList) {
        super(context, mList);
    }
    @Override
    public int getLayoutId() {
        return  R.layout.goods_item;
    }
    @Override
    protected void bindData(MyRecycleViewHolder holder, int position) {
        Order.Goods goods = mList.get(position);
        GoodsItemBinding binding = (GoodsItemBinding) holder.getBinding();
        binding.setGoodname(goods.title);
        binding.setGoodcount("x"+goods.num);
    }
}
