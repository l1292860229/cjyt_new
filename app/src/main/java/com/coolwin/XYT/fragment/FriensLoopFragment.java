package com.coolwin.XYT.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.FriendsLoopDetailActivity;
import com.coolwin.XYT.adapter.FriensLoopFragmentAdapter;
import com.coolwin.XYT.databinding.PublicFragmentBinding;
import com.coolwin.XYT.interfaceview.UIFriensLoopFragment;
import com.coolwin.XYT.presenter.FriensLoopFragmentPresenter;
import com.facebook.fresco.helper.Phoenix;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class FriensLoopFragment extends BaseFragment<FriensLoopFragmentPresenter>  implements UIFriensLoopFragment {
    PublicFragmentBinding binding;
    FriensLoopFragmentAdapter friensLoopFragmentAdapter;
    List<FriendsLoopItem> mFriendsLoopItems = new ArrayList<>();
    int page=1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding =  DataBindingUtil.inflate(inflater,R.layout.public_fragment,container,false);
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
		return binding.getRoot();
	}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getData(page, GetDataType.INIT);
        friensLoopFragmentAdapter  = new FriensLoopFragmentAdapter(context,mFriendsLoopItems);
    }

    @Override
    public void init(List<FriendsLoopItem> friendsLoopItems) {
        mFriendsLoopItems = friendsLoopItems;
        friensLoopFragmentAdapter.setData(mFriendsLoopItems);
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivIndex.setAdapter(friensLoopFragmentAdapter);
        friensLoopFragmentAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,FriendsLoopDetailActivity.class);
                intent.putExtra(FriendsLoopDetailActivity.DATA,mFriendsLoopItems.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void refreshSucess(List<FriendsLoopItem> friendsLoopItems) {
        binding.ivLayout.finishRefreshing();
        mFriendsLoopItems = friendsLoopItems;
        friensLoopFragmentAdapter.setData(mFriendsLoopItems);
        friensLoopFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void reloadMoreSucess(List<FriendsLoopItem> friendsLoopItems) {
        binding.ivLayout.finishLoadmore();
        mFriendsLoopItems.addAll(friendsLoopItems);
        friensLoopFragmentAdapter.setData(mFriendsLoopItems);
        friensLoopFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        Phoenix.with(binding.loading)
                .load(R.drawable.timg);
    }

    @Override
    public void hideLoading() {
        binding.loading.setVisibility(View.GONE);
    }
}
