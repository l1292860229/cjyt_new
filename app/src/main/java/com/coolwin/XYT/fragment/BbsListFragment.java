package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.R;
import com.coolwin.XYT.SendBbsActivity;
import com.coolwin.XYT.adapter.BbsAdapter;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.widget.BbsSearchDialog;
import com.coolwin.XYT.widget.CustomProgressDialog;
import com.coolwin.XYT.widget.PullToRefreshLayout;
import com.coolwin.XYT.widget.PullableListView;

import java.util.ArrayList;
import java.util.List;

import static com.coolwin.XYT.BaseActivity.BASE_HIDE_PROGRESS_DIALOG;
import static com.coolwin.XYT.BaseActivity.BASE_MSG_NETWORK_ERROR;
import static com.coolwin.XYT.BaseActivity.BASE_MSG_TIMEOUT_ERROR;
import static com.coolwin.XYT.BaseActivity.BASE_SHOW_PROGRESS_DIALOG;

public class BbsListFragment extends Fragment implements View.OnClickListener {
	public final static String MSG_REFRESH_MOVIINF = "im_refresh_bbs_action";
	private PullableListView mListView;
	private BbsAdapter mAdapter;
	private DisplayMetrics mMetrics;
	private int mWdith;
	private List<Bbs> mBbsList = new ArrayList<Bbs>();
	private int mIsHideSearcBtn = 0;
	private String type="1";
	private boolean ismy = false;
	private int page=1;
	private PullToRefreshLayout mContainer;
	private View mView;
	private Activity mContext;
	private TextView all,my;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		mMetrics= new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		mWdith = mMetrics.widthPixels;
		registerReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.bbs_chat_tab, container, false);
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
		all = (TextView)  mView.findViewById(R.id.all);
		my = (TextView)  mView.findViewById(R.id.my);
		all.setSelected(true);
		all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				all.setSelected(true);
				my.setSelected(false);
				ismy=false;
				page = 1;
				mAdapter=null;
				getGroupList(true);
			}
		});
		my.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				all.setSelected(false);
				my.setSelected(true);
				ismy=true;
				page = 1;
				mAdapter=null;
				getGroupList(true);
			}
		});
		mView.findViewById(R.id.search_btn).setOnClickListener(this);
		mView.findViewById(R.id.add_btn).setOnClickListener(this);
		mContainer = (PullToRefreshLayout) mView.findViewById(R.id.refresh_view);
		//mLeftBtn.setOnClickListener(this);
		mListView = (PullableListView) mView.findViewById(R.id.content_view);
		mListView.setDividerHeight(40);
		getGroupList(true);
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
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Bbs bbs = mBbsList.get(position);
				if(bbs.isVisitors==1){
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					intent.putExtra("isvisitors", true);
					mContext.startActivity(intent);
					return;
				}
				if(bbs.isjoin==0){
					showModifybgDialog(bbs);
				}else if(bbs.isjoin==1){
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					mContext.startActivity(intent);
				}else if(bbs.isjoin==2){
					Toast.makeText(mContext,"你的申请已经提交,等待管理员审核", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			break;
		case R.id.search_btn://搜索群
			BbsSearchDialog dialog = new BbsSearchDialog(mContext,type,ismy);
			dialog.show();
			break;
		case R.id.add_btn://添加群组
			Login login = IMCommon.getLoginResult(mContext);
			if (login.quId!=null &&login.kai6Id.equals(login.quId)) {
				Intent intent = new Intent(mContext, SendBbsActivity.class);
				intent.putExtra("type",type);
				startActivity(intent);
			}else if(type.equals("1")) {
				Intent intent = new Intent(mContext, SendBbsActivity.class);
				intent.putExtra("type",type);
				startActivity(intent);
			}else{
				Toast.makeText(mContext,"你不是渠道商", Toast.LENGTH_LONG).show();
			}
			break;

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
			mHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					if(isShowProgress){
						IMCommon.sendMsg(mHandler, BASE_SHOW_PROGRESS_DIALOG,"数据加载中,请稍后...");
					}
					BbsList bbsList = IMCommon.getIMInfo().getBbsList(type,ismy,null,page);
					if(mBbsList!=null && mBbsList.size()>0){
						mBbsList.clear();
					}
					if(bbsList.mBbsList!=null && bbsList.mBbsList.size()>0){
						mBbsList.addAll(bbsList.mBbsList);
					
					}
					if(isShowProgress){
						mHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}
					mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LISTVIEW_DATA);
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					if(isShowProgress){
						mHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}
				}
			};
		}.start();
	};
	private void getRefreshorloading(final boolean refresh,final PullToRefreshLayout pullToRefreshLayout){
		IMCommon.verifyNetwork(mContext);
		if (!IMCommon.getNetWorkState()) {
			mHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
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
					IMCommon.sendMsg(mHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
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
					mAdapter = new BbsAdapter(mContext, mBbsList);
					mListView.setAdapter(mAdapter);
				}
				break;
			case BASE_MSG_NETWORK_ERROR://网络连接错误
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
				hideProgressDialog();
				break;
			case BASE_SHOW_PROGRESS_DIALOG://显示提示框
				String dialogMsg = (String)msg.obj;
				showProgressDialog(dialogMsg);
				break;
			case BASE_HIDE_PROGRESS_DIALOG://隐藏提示框
				hideProgressDialog();
				String hintMsg = (String)msg.obj;
				if(hintMsg!=null && !hintMsg.equals("")){
					Toast.makeText(mContext,hintMsg, Toast.LENGTH_LONG).show();
				}
				break;
			case BASE_MSG_TIMEOUT_ERROR://连接网络超时
				hideProgressDialog();
				String timeOutMsg = (String)msg.obj;
				Toast.makeText(mContext, timeOutMsg, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
		
	};
	protected CustomProgressDialog mProgressDialog;
	public void showProgressDialog(String msg){
		mProgressDialog = new CustomProgressDialog(mContext);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	public void hideProgressDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	private void showModifybgDialog(final Bbs bbs){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle("你还不是鱼塘成员,是否申请加入");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(!bbs.money.equals("0")){
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setIcon(R.drawable.ic_dialog_alert);
					builder.setTitle("你申请的群不是免费的,是否支付费用");
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							new Thread(){
								@Override
								public void run(){
									if(IMCommon.verifyNetwork(mContext)){
										try {
											IMCommon.getIMInfo().addBbsUser(bbs.id);
											return;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}.start();
							Toast.makeText(mContext,"申请成功", Toast.LENGTH_LONG).show();
						}
					});
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
					builder.create().show();
				}else{
					new Thread(){
						@Override
						public void run(){
							if(IMCommon.verifyNetwork(mContext)){
								try {
									IMCommon.getIMInfo().addBbsUser(bbs.id);
									return;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}.start();
					Toast.makeText(mContext,"申请成功", Toast.LENGTH_LONG).show();
				}
			}
		});
		if(bbs.type==1&&bbs.isVisitors ==1){
			builder.setNeutralButton(R.string.look, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					intent.putExtra("isvisitors", true);
					mContext.startActivity(intent);
				}
			});
		}
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}
}
