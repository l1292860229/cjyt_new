package com.coolwin.XYT.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.ShopMemberFragmentAdapter;
import com.coolwin.XYT.databinding.PublicFragmentBinding;
import com.coolwin.XYT.interfaceview.UIShopMemberFragment;
import com.coolwin.XYT.presenter.ShopMemberFragmentPresenter;
import com.facebook.fresco.helper.Phoenix;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/6/30.
 */

public class ShopMemberFragment extends BaseFragment<ShopMemberFragmentPresenter> implements UIShopMemberFragment {
    PublicFragmentBinding binding;
    int page=1;
    ShopMemberFragmentAdapter shopMemberFragmentAdapter;
    List<Login> mlogin  = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater, R.layout.public_fragment,container,false);
        binding.ivLayout.setAutoLoadMore(true);
        binding.ivLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                mPresenter.getData(page, GetDataType.REFRESH);
            }
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                mPresenter.getData(page, GetDataType.LOADMORE);
            }
        });
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getData(page, GetDataType.INIT);
        shopMemberFragmentAdapter = new ShopMemberFragmentAdapter(context,this.mlogin);
    }

    @Override
    public void init(List<Login> mlogin) {
        this.mlogin = mlogin;
        shopMemberFragmentAdapter.setData(this.mlogin);
        binding.ivIndex.setAdapter(shopMemberFragmentAdapter);
    }

    @Override
    public void refreshSucess(List<Login> mlogin) {
        binding.ivLayout.finishRefreshing();
        this.mlogin = mlogin;
        shopMemberFragmentAdapter.setData(this.mlogin);
        shopMemberFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void reloadMoreSucess(List<Login> mlogin) {
        binding.ivLayout.finishLoadmore();
        this.mlogin.addAll(mlogin);
        shopMemberFragmentAdapter.setData(this.mlogin);
        shopMemberFragmentAdapter.notifyDataSetChanged();
    }
    @Override
    public void showLoading() {
        if(mlogin.size()==0){
            Phoenix.with(binding.loading)
                    .load(R.drawable.timg);
        }
    }

    @Override
    public void hideLoading() {
        binding.loading.setVisibility(View.GONE);
    }
}
