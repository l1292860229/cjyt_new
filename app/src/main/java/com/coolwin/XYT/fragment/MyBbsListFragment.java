package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.Bbs2Adapter;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.widget.BbsSearchDialog;
import com.coolwin.XYT.widget.PullToRefreshLayout;
import com.coolwin.XYT.widget.PullableListView;

import java.util.ArrayList;
import java.util.List;

public class MyBbsListFragment extends Fragment implements View.OnClickListener {
	public final static String MSG_REFRESH_MOVIINF = "im_refresh_bbs_action";
	private PullableListView mListView;
	private RelativeLayout mTitleLayout;
	private Bbs2Adapter mAdapter;
	private DisplayMetrics mMetrics;
	private int mWdith;
	private List<Bbs> mBbsList = new ArrayList<Bbs>();
	private int mIsHideSearcBtn = 0;
	private String type="1";
	private boolean ismy = true;
	private ToggleButton title2TB;
	private int page=1;
	private PullToRefreshLayout mContainer;
	private View mView;
	private Activity mContext;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext =  this.getActivity();
		mMetrics= new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		mIsHideSearcBtn =mContext. getIntent().getIntExtra("hide", 0);
		mWdith = mMetrics.widthPixels;
		registerReceiver();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.bbs_chat_tab3, container, false);
		initCompent();
		return mView;
	}

	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(MSG_REFRESH_MOVIINF);
		mContext.registerReceiver(Receiver, filter);
	}
	/*
	 * 释放通知
	 */
	private BroadcastReceiver Receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if(action.equals(MSG_REFRESH_MOVIINF)){
				getGroupList(true);
			}
		}
	};
	/*
	 * 实例化控件
	 */
	private void initCompent(){
		mContainer = (PullToRefreshLayout) mView.findViewById(R.id.refresh_view);
		//mSearchBtn = (ImageView)findViewById(R.id.search_btn);
		mListView = (PullableListView) mView.findViewById(R.id.content_view);
		getGroupList(true);
//		mSearchBtn.setVisibility(View.VISIBLE);
//		mSearchBtn.setOnClickListener(this);
		mContainer.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				page=1;
				getRefreshorloading(true,pullToRefreshLayout);
			}

			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
				page++;
				getRefreshorloading(false,pullToRefreshLayout);
			}
		});
		ImageView imageView = new ImageView(mContext);
		imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 88));
		imageView.setImageResource(R.drawable.sousuo);
		imageView.setBackgroundColor(Color.rgb(239,239,244));
//		imageView.setBackgroundResource(R.color.white);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BbsSearchDialog dialog = new BbsSearchDialog(mContext,type,ismy,1);
				dialog.show();
			}
		});
		mListView.addHeaderView(imageView);
	}
	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.search_btn://搜索群
//			BbsSearchDialog dialog = new BbsSearchDialog(mContext,type,ismy);
//			dialog.show();
//			break;
//		case R.id.add_btn://添加群组
//			Login login = IMCommon.getLoginResult(mContext);
//			if (login.quId!=null &&login.kai6Id.equals(login.quId)) {
//				Intent intent = new Intent(mContext, SendBbsActivity.class);
//				intent.putExtra("type",type);
//				startActivity(intent);
//			}else if(type.equals("1")) {
//				Intent intent = new Intent(mContext, SendBbsActivity.class);
//				intent.putExtra("type",type);
//				startActivity(intent);
//			}else{
//				Toast.makeText(mContext,"你不是渠道商",Toast.LENGTH_LONG).show();
//			}
//			break;

		default:
			break;
		}
	}
	/*
	 * 获取分组数据
	 */
	private void getGroupList(final boolean isShowProgress){
		IMCommon.verifyNetwork(mContext);
		if (!IMCommon.getNetWorkState()) {
			//mContext.mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					if(isShowProgress){
						//IMCommon.sendMsg(mContext.mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,"数据加载中,请稍后...");
					}
					BbsList bbsList = IMCommon.getIMInfo().getBbsList(type,ismy,null,page);
					if(mBbsList!=null && mBbsList.size()>0){
						mBbsList.clear();
					}
					if(bbsList.mBbsList!=null && bbsList.mBbsList.size()>0){
						mBbsList.addAll(bbsList.mBbsList);
					
					}
					if(isShowProgress){
						//mContext.mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}
					mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LISTVIEW_DATA);
				} catch (IMException e) {
					e.printStackTrace();
					//IMCommon.sendMsg(mContext.mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							//mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					if(isShowProgress){
						//mContext.mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}
				}
			};
		}.start();
	};
	private void getRefreshorloading(final boolean refresh,final PullToRefreshLayout pullToRefreshLayout){
		IMCommon.verifyNetwork(mContext);
		if (!IMCommon.getNetWorkState()) {
			//mContext.mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					final BbsList bbsList = IMCommon.getIMInfo().getBbsList(type,ismy,null,page);
					if(mBbsList!=null && mBbsList.size()>0 && refresh){
						mBbsList.clear();
					}
					if(bbsList.mBbsList!=null && bbsList.mBbsList.size()>0){
						mBbsList.addAll(bbsList.mBbsList);
					}
					//更新ui
					mContext.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(refresh){
								pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
							}else{
								pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
								if(bbsList.mBbsList==null || bbsList.mBbsList.size()==0){
									Toast.makeText(mContext,"没有更多数据了", Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
					mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LISTVIEW_DATA);
				} catch (IMException e) {
					e.printStackTrace();
					//IMCommon.sendMsg(mContext.mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							//mContext.getResources().getString(e.getStatusCode()));
					if(refresh){
						pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
					}else{
						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	};
	@Override
	public void onDestroy() {
		mContext.unregisterReceiver(Receiver);
		super.onDestroy();
	}
	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				if(mAdapter!=null){
					mAdapter.setData(mBbsList);
					mAdapter.notifyDataSetChanged();
				}else{
					mAdapter = new Bbs2Adapter(mContext, mBbsList);
					mListView.setAdapter(mAdapter);
				}
				break;
			default:
				break;
			}
		}
		
	};
}
