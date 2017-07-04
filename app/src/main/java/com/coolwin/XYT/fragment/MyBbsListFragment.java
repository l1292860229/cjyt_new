package com.coolwin.XYT.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.coolwin.XYT.databinding.ViewToolbarSearchBinding;
import com.coolwin.XYT.interfaceview.UIBbsListFragment;
import com.coolwin.XYT.presenter.BbsListFragmentPresenter;
import com.facebook.fresco.helper.Phoenix;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MyBbsListFragment extends BaseFragment<BbsListFragmentPresenter> implements UIBbsListFragment {
	BbsListFragmentBinding binding;
	private List<Bbs> mBbsList = new ArrayList<>();
	int page=1;
	MyBbsListFragmentAdapter adapter;
	MyBbsListFragmentAdapter searchAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.bbs_list_fragment, container, false);
		binding.setBehavior(this);
		binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
		adapter = new MyBbsListFragmentAdapter(context,mBbsList);
		binding.ivIndex.setAdapter(adapter);
		binding.ivLayout.setAutoLoadMore(true);
		//刷新加载的功能
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
		setActionBarLayout();
		mPresenter.getData(page,GetDataType.INIT);
		return binding.getRoot();
	}
	public void setActionBarLayout(){
		//添加头像搜索框
		ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 88));
		imageView.setImageResource(R.drawable.sousuo);
		imageView.setBackgroundColor(Color.rgb(239,239,244));
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewToolbarSearchBinding binding = DataBindingUtil.
						inflate(LayoutInflater.from(context), R.layout.view_toolbar_search, null, false);
				//点击搜索框,跳出搜索页面
				final Dialog toolbarSearchDialog = new Dialog(context,R.style.MaterialSearch);
				toolbarSearchDialog.setContentView(binding.getRoot());
				toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				toolbarSearchDialog.show();
				binding.imgToolBack.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						toolbarSearchDialog.dismiss();
					}
				});
				binding.listSearch.setVisibility(View.VISIBLE);
				binding.listSearch.setLayoutManager(new LinearLayoutManager(context));
				searchAdapter =  new MyBbsListFragmentAdapter(context, new ArrayList());
				binding.listSearch.setAdapter(searchAdapter);
				binding.edtToolSearch.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {}
					@Override
					public void afterTextChanged(Editable s) {
						mPresenter.searchData(s.toString().trim());
					}
				});
				searchAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Intent intent = new Intent(context, FriensLoopActivity.class);
						intent.putExtra("bbs", searchAdapter.getData().get(position));
						startActivity(intent);
						toolbarSearchDialog.dismiss();
					}
				});
			}
		});
		adapter.setHeaderView(imageView);
		adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, FriensLoopActivity.class);
				intent.putExtra("bbs", mBbsList.get(position-1));
				startActivity(intent);
			}
		});
	}
	@Override
	public void showLoading() {
		if(mBbsList.size()==0){
			Phoenix.with(binding.loading)
					.load(R.drawable.timg);
		}
	}

	@Override
	public void hideLoading() {
		binding.loading.setVisibility(View.GONE);
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
	@Override
	public void searchData(List<Bbs> bbses) {
		searchAdapter.setData(bbses);
		searchAdapter.notifyDataSetChanged();
	}
}
