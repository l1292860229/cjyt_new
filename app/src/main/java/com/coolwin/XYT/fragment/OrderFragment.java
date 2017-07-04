package com.coolwin.XYT.fragment;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.Order;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.OrderFragmentAdapter;
import com.coolwin.XYT.databinding.PublicFragmentBinding;
import com.coolwin.XYT.interfaceview.UIOrderFragment;
import com.coolwin.XYT.presenter.OrderFragmentPresenter;
import com.facebook.fresco.helper.Phoenix;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/28.
 */

public class OrderFragment extends Fragment implements UIOrderFragment {
    private Context context;
    private int page=1;
    private String orderType;
    private OrderFragmentPresenter presenter;
    PublicFragmentBinding binding;
    OrderFragmentAdapter adapter;
    private List<Order> mList = new ArrayList<>();
    public static final OrderFragment newInstance(String orderType){
        OrderFragment c = new OrderFragment();
        Bundle bdl = new Bundle();
        bdl.putString("orderType", orderType);
        c.setArguments(bdl);
        return c;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.public_fragment,container,false);
        Bundle bundle = getArguments();
        orderType = bundle.getString("orderType");
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                presenter.getData(page,orderType, GetDataType.REFRESH);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                presenter.getData(page,orderType, GetDataType.LOADMORE);
            }
        });
        presenter = new OrderFragmentPresenter();
        presenter.init(getActivity(),this);
        presenter.getData(page,orderType, GetDataType.INIT);
        adapter = new OrderFragmentAdapter(context,mList);
    }

    @Override
    public void showLoading() {
        if(mList.size()==0){
            Phoenix.with(binding.loading)
                    .load(R.drawable.timg);
        }
    }
    @Override
    public void hideLoading() {
        binding.loading.setVisibility(View.GONE);
    }

    @Override
    public void init(List<Order> userInfos) {
        this.mList = userInfos;
        adapter.setData(userInfos);
        binding.ivIndex.setAdapter(adapter);
    }

    @Override
    public void loadsuccess(List<Order> userInfos) {
        binding.ivLayout.finishLoadmore();
        this.mList.addAll(userInfos);
        adapter.setData(this.mList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshsuccess(List<Order> userInfos) {
        binding.ivLayout.finishRefreshing();
        this.mList = userInfos;
        adapter.setData(this.mList);
        adapter.notifyDataSetChanged();
    }
}
