package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.Order;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.GoodAdapter;
import com.coolwin.XYT.databinding.OrderInfoBinding;
import com.coolwin.XYT.interfaceview.UIMyOrderInfo;
import com.coolwin.XYT.presenter.MyOrderInfoPresenter;
import com.coolwin.XYT.util.UIUtil;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyOrderInfoActivity extends BaseActivity<MyOrderInfoPresenter> implements UIMyOrderInfo {
    public static final String DATA="data";
    public static final String NODO="0";
    public static final String COMPLETE="4";
    public static final String SHIP="3";
    public static final String CANCEL="5";
    OrderInfoBinding binding;
    Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.order_info);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的订单详细");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        order = (Order) getIntent().getSerializableExtra(DATA);
        if(order==null){
            UIUtil.showMessage(context,"该订单不存在");
            this.finish();
        }
        initData();
    }
    void initData(){
        binding.setOrdercode("订单号:"+order.code);
        binding.setOrderstatus(order.status);
        binding.setOrderpay(order.ispay.equals("0")?"未支付":"已支付");
        binding.setOrderall("共"+order.goods.size()+"件商品,总计￥"+order.amount+"元");
        binding.setOrderuser(order.name);
        binding.setOrdertel(order.tel);
        binding.setOrderaddress(order.address);
        binding.goodslayout.setLayoutManager(new LinearLayoutManager(context));
        binding.goodslayout.setAdapter(new GoodAdapter(context,order.goods));
    }
    /**
     * 不处理
     * @param view
     */
    public void noDo(View view){
        mPresenter.setData(NODO,order.code);
    }
    /**
     * 完成
     * @param view
     */
    public void complete(View view){
        mPresenter.setData(COMPLETE,order.code);
    }
    /**
     * 发货
     * @param view
     */
    public void ship(View view){
        mPresenter.setData(SHIP,order.code);
    }
    /**
     * 取消
     * @param view
     */
    public void cancel(View view){
        mPresenter.setData(CANCEL,order.code);
    }
    @Override
    public void showLoading() {
        super.showLoading("提交中...");
    }

    @Override
    public void setData(String value) {
        UIUtil.showMessage(context,"修改成功");
        order.status = value;
        binding.setOrderstatus(order.status);
    }
}
