package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.GroupTable;
import com.coolwin.XYT.Entity.Group;
import com.coolwin.XYT.Entity.GroupList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.R;
import com.coolwin.XYT.UserInfoActivity;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.map.BMapApiApp;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.sortlist.CharacterParser;
import com.coolwin.XYT.sortlist.PinYin;
import com.coolwin.XYT.sortlist.PinyinComparator;
import com.coolwin.XYT.sortlist.SideBar;
import com.coolwin.XYT.sortlist.SortAdapter;
import com.coolwin.XYT.widget.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 通讯录Fragment的界面
 * 
 * @author dl
 */
public class ContactsFragment extends Fragment {

	/**
	 * 定义全局变量
	 */

	private boolean mIsRegisterReceiver = false;
	//显示新的朋友有上角的泡泡
	public final static String ACTION_SHOW_NEW_FRIENDS = "im_action_show_new_friends_tip";
	//取消新的朋友有上角的泡泡
	public final static String ACTION_HIDE_NEW_FRIENDS = "im_action_hide_new_friends_tip";
	public final static String REFRESH_FRIEND_ACTION = "im_refresh_action";

	private PullToRefreshLayout mContainer;
	private TextView mRefreshViewLastUpdated;
	private LinearLayout mCategoryLinear;
	private boolean mIsRefreshing = false;

	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter mAdapter;


	private static boolean mInit;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	public List<Login> mSourceDateList=new ArrayList<Login>();

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private View mView;
	private Context mParentContext;
	private ProgressDialog mProgressDialog;
	private List<Group> mGroupList = new ArrayList<Group>();
	private GroupList mGroup;
	private Login mLogin;
	private int friends;
	private int page=1;
	//联系人总数
//	private TextView contactsCount;
	public static final ContactsFragment newInstance(int friends){
		ContactsFragment c = new ContactsFragment();
		Bundle bdl = new Bundle();
		bdl.putInt("friends", friends);
		c.setArguments(bdl);
		return c;
	}
	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		mLogin = IMCommon.getLoginResult(BMapApiApp.getInstance());
		mView =inflater.inflate(R.layout.activity_contact_main, container, false);
		Bundle bundle = getArguments();
		friends = bundle.getInt("friends");
		return mView;
	}

	/**
	 * 实例化控件
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerBoardCast();
		sideBar = (SideBar)mView.findViewById(R.id.sidrbar);
		dialog = (TextView)mView.findViewById(R.id.dialog);
//		contactsCount = (TextView)mView.findViewById(R.id.contactsCount);
		sideBar.setTextView(dialog);
		//设置右侧触摸监听
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						sideBar.setBackgroundColor(Color.parseColor("#00000000"));
						sideBar.setChoose(-1);
						sideBar.invalidate();
						dialog.setVisibility(View.INVISIBLE);
					}
				}, 2000);
			}
		});
		mCategoryLinear = (LinearLayout)mView.findViewById(R.id.category_linear);
		mRefreshViewLastUpdated = (TextView) mView.findViewById(R.id.pull_to_refresh_time);
		mContainer = (PullToRefreshLayout) mView.findViewById(R.id.refresh_view);
		sortListView = (ListView) mView.findViewById(R.id.content_view);
		sortListView.setDivider(null);
		sortListView.setCacheColorHint(0);
		sortListView.setHeaderDividersEnabled(false);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * listview 子项点击事件
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
			Login login = (Login) mAdapter.getItem(position);
			Intent userInfoIntent = new Intent();
			userInfoIntent.setClass(mParentContext, UserInfoActivity.class);
			userInfoIntent.putExtra("type",2);
			userInfoIntent.putExtra("o",friends);
			userInfoIntent.putExtra("uid", login.uid);
			startActivity(userInfoIntent);
			}
		});
		sortListView.setSelector(mParentContext.getResources().getDrawable(R.drawable.transparent_selector));
		sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			private SparseArray recordSp = new SparseArray(0);
			private int mCurrentfirstVisibleItem = 0;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState){
					case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
						break;
					case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动
						break;
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				mCurrentfirstVisibleItem = firstVisibleItem;
				View firstView = view.getChildAt(0);
				if (null != firstView) {
					ItemRecod itemRecord = (ItemRecod) recordSp.get(firstVisibleItem);
					if (null == itemRecord) {
						itemRecord = new ItemRecod();
					}
					itemRecord.height = firstView.getHeight();
					itemRecord.top = firstView.getTop();
					recordSp.append(firstVisibleItem, itemRecord);
					int h = getScrollY();//滚动距离
					if(h!=0){
						Intent intent = new Intent(FatherContactsFragment.ACTION_MOVE_LAYOUT);
						intent.putExtra("move",h);
						ContactsFragment.this.getActivity().sendBroadcast(intent);
					}
				}
			}
			private int getScrollY() {
				int height = 0;
				for (int i = 0; i < mCurrentfirstVisibleItem; i++) {
					ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
					if(itemRecod!=null){
						height += itemRecod.height;
					}
				}
				ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentfirstVisibleItem);
				if (null == itemRecod) {
					itemRecod = new ItemRecod();
				}
				return height - itemRecod.top;
			}
			class ItemRecod {
				int height = 0;
				int top = 0;
			}
		});
		mContainer.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(final PullToRefreshLayout pullToRefreshLayout){
				// 下拉刷新操作
				new Handler(){
					@Override
					public void handleMessage(Message msg){
						// 千万别忘了告诉控件刷新完毕了哦！
						pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
						getUserList(GlobalParam.LIST_LOAD_FIRST,friends,1);
						page=1;
					}
				}.sendEmptyMessageDelayed(0, 3000);
			}

			@Override
			public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout){
				// 加载操作
				new Handler(){
					@Override
					public void handleMessage(Message msg)
					{
						// 千万别忘了告诉控件加载完毕了哦！
						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
						page++;
						mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
					}
				}.sendEmptyMessageDelayed(0, 3000);
			}
		});
		if(mSourceDateList!=null && mSourceDateList.size()>0){
			mSourceDateList.clear();
		}
		getData();
	}
	private void registerBoardCast(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.SWITCH_LANGUAGE_ACTION);
		filter.addAction(REFRESH_FRIEND_ACTION);
		filter.addAction(GlobalParam.REFRESH_ALIAS_ACTION);

		filter.addAction(GlobalParam.ACTION_CANCLE_NEW_ORDER);
		filter.addAction(GlobalParam.ACTION_CANCLE_NEW_SERVICE);
		mParentContext.registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
	}
	/**
	 * 处理通知
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Log.e("ContactsFragment", context+"");
			if (intent != null) {
				if (intent.getAction().equals(
						GlobalParam.SWITCH_LANGUAGE_ACTION)) {//刷新通讯录列表
					if(mSourceDateList!=null && mSourceDateList.size()>0){
						mSourceDateList.clear();
						if(mAdapter!=null){
							mAdapter.notifyDataSetChanged();
						}
					}
					getUserList(GlobalParam.LIST_LOAD_FIRST,friends,1);
					page = 1;
				} else if (intent.getAction().equals(REFRESH_FRIEND_ACTION)) {//刷新通讯录列表
					if (mSourceDateList != null) {
						mSourceDateList.clear();
					}
					getData();
				}else if(intent.getAction().equals(GlobalParam.REFRESH_ALIAS_ACTION)){//刷新通讯录列表
					if(mSourceDateList.size() != 0){
						mSourceDateList.clear();
					}
					SQLiteDatabase db = DBHelper.getInstance(mParentContext).getReadableDatabase();
					GroupTable table = new GroupTable(db);
					mGroupList = table.query();
					if(mGroupList != null && mGroupList.size() != 0){
						List<Login> tempList = new ArrayList<Login>();
						for (int i = 0; i < mGroupList.size(); i++) {
							if(mGroupList.get(i).mUserList != null && mGroupList.get(i).mUserList.size()>0){
								tempList.addAll(mGroupList.get(i).mUserList);
							}
						}
						updateListView();
					}
				}
			}
		}
	};



	/** Fragment第一次附属于Activity时调用,在onCreate之前调用 */

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mInit = true;
	}

	/**
	 * fragemnt 创建事件
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentContext = (Context)ContactsFragment.this.getActivity();
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
	}

	/**
	 * 获取通讯录数据
	 */
	private void getData(){
		SQLiteDatabase db = DBHelper.getInstance(mParentContext).getReadableDatabase();
		GroupTable table = new GroupTable(db);
		if(mGroupList==null){
			mGroupList = new ArrayList<Group>();
		}
		if(mGroupList.size()!=0){
			mGroupList.clear();
		}
		Group g  = table.query(friends);
		if(g!=null){
			mGroupList.add(g);
		}
		if(mGroupList != null && mGroupList.size() != 0){
			List<Login> tempList = new ArrayList<Login>();
			for (int i = 0; i < mGroupList.size(); i++) {
				if(mGroupList.get(i).mStarList!=null
						&& mGroupList.get(i).mStarList.size()>0){
					tempList.addAll(mGroupList.get(i).mStarList);
				}
			}
			for (int i = 0; i < mGroupList.size(); i++) {
				if(mGroupList.get(i).mUserList != null
						&& mGroupList.get(i).mUserList.size()>0){
					tempList.addAll(mGroupList.get(i).mUserList);
				}
			}
			mSourceDateList.addAll(tempList);
			updateListView();
		}else {
			mGroupList = new ArrayList<Group>();
			Message message = new Message();
			message.obj = BMapApiApp.getInstance().getResources().getString(R.string.add_more_loading);
			message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
			mHandler.sendMessage(message);
			getUserList(GlobalParam.LIST_LOAD_FIRST,friends,1);
		}
	}


	/**
	 * 获取通讯录人员列表
	 * @param loadType
	 */
	private void getUserList(final int loadType,final int number,final int page) {
		new Thread() {

			@Override
			public void run() {
				if (IMCommon.verifyNetwork(mParentContext)) {
					new Thread() {
						public void run() {
							try {
								mGroup = IMCommon.getIMInfo().getUserList(number,page);
								if (mGroup != null) {
									if (mGroup.mState != null && mGroup.mState.code == 0) {
										if (loadType != GlobalParam.LIST_LOAD_MORE && page == 1) {
											if (mGroupList != null) {
												mGroupList.clear();
											}
										}
										List<Login> tempList = new ArrayList<Login>();
										if (mGroup.mGroupList != null) {
											if(mGroup.mGroupList.size()==1&& mGroup.mGroupList.get(0).id!=number){
												mGroup.mGroupList.get(0).id = number;
											}
											mGroupList.addAll(mGroup.mGroupList);
											SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
											GroupTable table = new GroupTable(db);
											table.insert(mGroup.mGroupList);

											for (int i = 0; i < mGroupList.size(); i++) {
												if(mGroupList.get(i).mStarList!=null
														&& mGroupList.get(i).mStarList.size()>0){
													tempList.addAll(mGroupList.get(i).mStarList);
												}
											}
											for (int j = 0; j < mGroupList.size(); j++) {
												if(mGroupList.get(j).mUserList != null){
													tempList.addAll(mGroupList.get(j).mUserList);
												}
											}
											IMCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList);
										}
									} else {
										Message msg = new Message();
										msg.what = GlobalParam.MSG_LOAD_ERROR;
										if (mGroup.mState != null && mGroup.mState.errorMsg != null && !mGroup.mState.errorMsg.equals("")) {
											msg.obj = mGroup.mState.errorMsg;
										} else {
											msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.load_error);
										}
										mHandler.sendMessage(msg);
									}
								} else {
									mHandler.sendEmptyMessage(GlobalParam.MSG_LOAD_ERROR);
								}

							} catch (IMException e) {
								e.printStackTrace();
								Message msg = new Message();
								msg.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
								msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
								mHandler.sendMessage(msg);
							}

							switch (loadType) {
							case GlobalParam.LIST_LOAD_FIRST:
								mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
								break;
							case GlobalParam.LIST_LOAD_MORE:
								mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);

							case GlobalParam.LIST_LOAD_REFERSH:
								mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
								break;

							default:
								break;
							}
						}
					}.start();
				} else {
					switch (loadType) {
					case GlobalParam.LIST_LOAD_FIRST:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
						break;
					case GlobalParam.LIST_LOAD_MORE:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);

					case GlobalParam.LIST_LOAD_REFERSH:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
						break;

					default:
						break;
					}
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}

		}.start();
	}

	private void updateListView(){
//		filledData();
		//根据a-z排序
//		Collections.sort(mSourceDateList, pinyinComparator);
		mAdapter = new SortAdapter(getActivity(), mSourceDateList);
		sortListView.setAdapter(mAdapter);
//		contactsCount.setText("共"+mSourceDateList.size()+"联系人");
	}

	/**
	 * 显示通讯录数据
	 */
	private void refreshUpdateListView(){
//		filledData();
		//根据a-z排序
//		Collections.sort(mSourceDateList, pinyinComparator);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{
			mAdapter = new SortAdapter(getActivity(), mSourceDateList);
			sortListView.setAdapter(mAdapter);
		}
	}


	/**
	 * 为ListView填充数据
	 * @return
	 */
	private void filledData(){
		try {
			for(int i=0; i<mSourceDateList.size(); i++){
				String name="";
				if(mSourceDateList.get(i).nameType == 1  ){//0-普通用户 1-操作栏 2-星标朋友
					name = mSourceDateList.get(i).nickname;
				}else{
					name = mSourceDateList.get(i).remark;
				}
				if(name == null || name.equals("")){
					name = mSourceDateList.get(i).nickname;
				}
				//汉字转换成拼音
				String pinyin = characterParser.getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				String sName = mSourceDateList.get(i).sortName;
				if(sName!=null && !sName.equals("")){
					if(sName.equals("星标朋友")){
					}
				}else{
					if(sortString.matches("↑")){
						mSourceDateList.get(i).sort = "↑";
						mSourceDateList.get(i).sortName = "";
						mSourceDateList.get(i).remark = name.substring(1,name.length());
					}else if(sortString.matches("[A-Z]") || sortString.matches("[a-z]")){
						String sort = PinYin.getPingYin(name.trim());
						if(sort==null || sort.length()<=0){
							sort = "#";
						}else{
							sort = sort.substring(0, 1).toUpperCase();
						}
						mSourceDateList.get(i).sort = sort;
						mSourceDateList.get(i).sortName = sort;
					}else{
						mSourceDateList.get(i).sortName = "#";
						mSourceDateList.get(i).sort = "#";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String) msg.obj;
				showProgressDialog(dialogMsg);
				break;
			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				updateListView();
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
//					mContainer.onRefreshComplete();
					break;
				}
				mIsRefreshing = true;
				getUserList(GlobalParam.LIST_LOAD_REFERSH,friends,page);
				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
//				mContainer.onRefreshComplete();
				refreshUpdateListView();
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:

				if(mSourceDateList != null && mSourceDateList.size()>0){
					mSourceDateList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<Login> tempList = (List<Login>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mSourceDateList.addAll(tempList);
				}
//				contactsCount.setText("共"+mSourceDateList.size()+"联系人");
				break;

			case GlobalParam.MSG_LOAD_ERROR:
				String error_Detail = (String) msg.obj;
				if (error_Detail != null && !error_Detail.equals("")) {
					Toast.makeText(mParentContext, error_Detail, Toast.LENGTH_LONG)
					.show();
				} else {
					Toast.makeText(mParentContext, R.string.load_error,
							Toast.LENGTH_LONG).show();
				}
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mParentContext, R.string.network_error,
						Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:

				String message = (String) msg.obj;
				if (message == null || message.equals("")) {
					message = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
				}
				Toast.makeText(mParentContext, message, Toast.LENGTH_LONG).show();
				break;

			default:
				break;

			}
		}
	};



	/**
	 * 显示提示对话框
	 * @param msg
	 */
	public void showProgressDialog(String msg){
		mProgressDialog = new ProgressDialog(mParentContext);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}

	/**
	 * 隐藏提示对话框
	 */
	public void hideProgressDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	/**
	 * 销毁页面
	 */
	@Override
	public void onDestroy() {
		if (mIsRegisterReceiver) {
			mParentContext.unregisterReceiver(mReceiver);
		}
		freeBitmapCaches();
		super.onDestroy();
	}

	/**
	 * 释放头像
	 */
	private void freeBitmapCaches(){          
		if(mSourceDateList == null || mSourceDateList.size()>0){
		}
		for(int i = 0; i < mSourceDateList.size(); i++){
			if(mSourceDateList.get(i).headsmall != null && !mSourceDateList.get(i).headsmall.equals("")){
				if(mAdapter != null && mAdapter.getImageBuffer() != null){
					ImageView imageView = (ImageView) sortListView.findViewWithTag(mSourceDateList.get(i).headsmall);
					if(imageView != null){
						imageView.setImageBitmap(null);
						imageView.setImageResource(R.drawable.contact_default_header);
					}
					Bitmap bitmap = mAdapter.getImageBuffer().get(mSourceDateList.get(i).headsmall);
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
						bitmap = null;
						mAdapter.getImageBuffer().remove(mSourceDateList.get(i).headsmall);
					}

				}

			}
		}
		mAdapter.clearBitmapToMemoryCache();

	}







}
