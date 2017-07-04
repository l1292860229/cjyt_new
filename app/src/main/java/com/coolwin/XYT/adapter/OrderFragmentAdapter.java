package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.coolwin.XYT.Entity.Order;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.MyOrderInfoActivity;
import com.coolwin.XYT.databinding.OrderItemBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;

import java.util.List;

/**
 * Created by dell on 2017/7/3.
 */

public class OrderFragmentAdapter extends BaseAdapter<Order> {
    public OrderFragmentAdapter(Context context, List<Order> orders) {
        super(context);
        this.mList = orders;
    }

    @Override
    public int getLayoutId() {
        return R.layout.order_item;
    }

    @Override
    protected void bindData(MyRecycleViewHolder holder, int position) {
        final Order order = mList.get(position);
        final OrderItemBinding binding = (OrderItemBinding) holder.getBinding();
        binding.setOrdercode("订单号:"+order.code);
        binding.setOrderstatus(order.status);
        binding.setOrderpay(order.ispay.equals("0")?"未支付":"已支付");
        binding.setOrderall("共"+order.goods.size()+"件商品,总计￥"+order.amount+"元");
        binding.setOrderuser(order.name);
        binding.setOrdertel(order.tel);
        binding.setOrderaddress(order.address);
        binding.goodslayout.setLayoutManager(new LinearLayoutManager(context));
        binding.goodslayout.setAdapter(new GoodAdapter(context,order.goods));
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MyOrderInfoActivity.class);
                intent.putExtra(MyOrderInfoActivity.DATA,order);
                context.startActivity(intent);
            }
        });
    }
}
