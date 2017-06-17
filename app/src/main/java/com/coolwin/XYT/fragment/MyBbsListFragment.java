package com.coolwin.XYT.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.FriensLoopActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyBbsListFragmentAdapter;
import com.coolwin.XYT.databinding.BbsListFragmentBinding;
import com.coolwin.XYT.interfaceview.UIBbsListFragment;
import com.coolwin.XYT.presenter.BbsListFragmentPresenter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MyBbsListFragment extends BaseFragment<BbsListFragmentPresenter> implements UIBbsListFragment {
	BbsListFragmentBinding binding;
	private List<Bbs> mBbsList = new ArrayList<>();
	int page=1;
	MyBbsListFragmentAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.bbs_list_fragment, container, false);
		binding.setBehavior(this);
		binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
		adapter = new MyBbsListFragmentAdapter(context,mBbsList);
		binding.ivIndex.setAdapter(adapter);
		binding.ivLayout.setAutoLoadMore(true);
		binding.ivLayout.setOnRefreshListener(new RefreshListenerAdapter() {
			@Override
			public void onRefresh(TwinklingRefreshLayout refreshLayout) {
				page = 1;
				mPresenter.getData(null,page, GetDataType.REFRESH);
			}

			@Override
			public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
				page++;
				mPresenter.getData(null,page, GetDataType.LOADMORE);
			}
		});
		mPresenter.getData(null,page,GetDataType.INIT);
		ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 88));
		imageView.setImageResource(R.drawable.sousuo);
		imageView.setBackgroundColor(Color.rgb(239,239,244));
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		adapter.setHeaderView(imageView);
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, FriensLoopActivity.class);
				intent.putExtra("bbs", mBbsList.get(position));
				startActivity(intent);
			}
		});
		return binding.getRoot();
	}

	@Override
	public void showLoading() {

	}

	@Override
	public void hideLoading() {

	}

	@Override
	public void init(List<Bbs> bbses) {
		mBbsList = bbses;
		adapter.setData(mBbsList);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void refreshSucess(List<Bbs> bbses) {
		binding.ivLayout.finishRefreshing();
		mBbsList = bbses;
		adapter.setData(mBbsList);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void reloadMoreSucess(List<Bbs> bbses) {
		binding.ivLayout.finishLoadmore();
		mBbsList.addAll(bbses);
		adapter.setData(mBbsList);
		adapter.notifyDataSetChanged();
	}
}
