package com.coolwin.XYT.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.AppDataStatistics;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.ShopIndexFragmentAdapter;
import com.coolwin.XYT.databinding.ShopIndexFragmentBinding;
import com.coolwin.XYT.databinding.ShopIndexHeadBinding;
import com.coolwin.XYT.interfaceview.UIShopIndexFrament;
import com.coolwin.XYT.presenter.ShopIndexPresenter;
import com.facebook.fresco.helper.Phoenix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/6/30.
 */

public class ShopIndexFragment extends BaseFragment<ShopIndexPresenter> implements UIShopIndexFrament {
    ShopIndexFragmentBinding binding;
    ShopIndexFragmentAdapter adapter;
    List<AppDataStatistics> mDataStatisticses = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.shop_index_fragment,container,false);
        binding.data.setLayoutManager(new GridLayoutManager(context,5));
        mPresenter.init();
        return binding.getRoot();
    }

    @Override
    public void init(List<AppDataStatistics> dataStatisticses) {
        mDataStatisticses = dataStatisticses;
        adapter = new ShopIndexFragmentAdapter(context,dataStatisticses.subList(2,dataStatisticses.size()));
        binding.data.setAdapter(adapter);
        initHead(dataStatisticses.subList(0,2));
    }
    public void initHead(List<AppDataStatistics> dataStatisticses){
        ShopIndexHeadBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.shop_index_head,null,false);
        binding.setTotalSales(dataStatisticses.get(0).key);
        binding.totalsalesvalue.showNumberWithAnimation(((float) dataStatisticses.get(0).value/20),(float) dataStatisticses.get(0).value);
        binding.setTotalRecharge(dataStatisticses.get(1).key);
        binding.setTotalRechargeValue(dataStatisticses.get(1).value+"");
        adapter.setHeaderView(binding.getRoot());
        adapter.notifyDataSetChanged();
    }
    @Override
    public void showLoading() {
        if(mDataStatisticses.size()==0){
            Phoenix.with(binding.loading)
                    .load(R.drawable.timg);
        }
    }

    @Override
    public void hideLoading() {
        binding.loading.setVisibility(View.GONE);
    }
}
