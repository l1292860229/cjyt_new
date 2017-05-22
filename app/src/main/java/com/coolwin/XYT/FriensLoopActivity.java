package com.coolwin.XYT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.CommentUser;
import com.coolwin.XYT.Entity.FriendsLoop;
import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.MorePicture;
import com.coolwin.XYT.Entity.MovingContent;
import com.coolwin.XYT.Entity.MovingPic;
import com.coolwin.XYT.Entity.NotifiyVo;
import com.coolwin.XYT.adapter.EmojiAdapter;
import com.coolwin.XYT.adapter.FriendsLoopAdapter;
import com.coolwin.XYT.adapter.IMViewPagerAdapter;
import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.fragment.KindFragment;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.widget.MyPullToRefreshListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.coolwin.XYT.global.GlobalParam.LIST_LOAD_UPDATE;
import static com.coolwin.XYT.global.GlobalParam.MSG_GET_TYPE_DATA;

/**
 * 朋友圈
 * @author dongli
 *
 */
public class FriensLoopActivity extends BaseActivity implements OnTouchListener, MyPullToRefreshListView.OnChangeStateListener, OnPageChangeListener {

	/*
	 * 定义全局变量
	 */
	public final static String MSG_REFRESH_MOVIINF = "im_refresh_friends_loop_action";
	private ListView mListView;
	private MyPullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;//mLoginUserName;
	//mSetCoverHintTextView;
	private boolean mIsRefreshing = false;
	private TextView shareView;
	private ImageView shareIView;

	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();
	private ViewPager mViewPager;
	private IMViewPagerAdapter mEmotionAdapter;
	private LinkedList<View> mViewList = new LinkedList<View>();
	private LinearLayout mLayoutCircle;
	public int mPageIndxe = 0;
	private RelativeLayout mEmotionLayout;


	private RelativeLayout mBottomLayout;
//	private RoundImageView mProfileHeaderIcon;
	private ImageView mPicBtn;
//	private View mSearchHeader;
	private LinearLayout mFootView,mBottomMenuLayout;
	private EditText mCommentEdit;
	private Dialog mPhoneDialog;
	private RelativeLayout shareBtn;

	protected AlertDialog mUpgradeNotifyDialog;
	private Button mCommentBtn;


	private FriendsLoopAdapter mAdapter;

	private DisplayMetrics mMetric;
	private int ICON_SIZE_WIDTH;
	private static  int ICON_SIZE_HEIGHT ;

	private FriendsLoop mMyAlbum;
	private List<FriendsLoopItem> mDataList = new ArrayList<FriendsLoopItem>();

	private ImageLoader mImageLoader;
	private String mTempFileName="front_cover.jpg";
	private String mCropImgPath,mToUserId;
	private List<MorePicture> mListpic = new ArrayList<MorePicture>();
	private Bitmap mBitmap;
	private int mShareId,mPosition;
	private String mInputComment;
	//private LinearLayout mainBottom;
	private Button[] mTabs;
	private String type;
	private Login mlogin;
	private Bbs bbs;
	private String bid;
	private boolean isvisitors = false;
	private RadioButton kindBtn, distanceBtn, sortBtn;
	RadioGroup group;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mlogin = IMCommon.getLoginResult(mContext);
		bbs = (Bbs) getIntent().getSerializableExtra("bbs");
		isvisitors =  getIntent().getBooleanExtra("isvisitors",false);
		if(bbs!=null){
			bid = bbs.id;
		}
		mMetric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetric);
		mImageLoader = new ImageLoader();
		ICON_SIZE_WIDTH =mMetric.widthPixels;
		if(ICON_SIZE_WIDTH>680){
			ICON_SIZE_WIDTH = 680;
		}
		ICON_SIZE_HEIGHT = (ICON_SIZE_WIDTH/8)*7;//x:y 3:2
		setContentView(R.layout.friends_loop);
		registerReceiver();
		initCompent();
		IMCommon.saveReadFriendsLoopTip(mContext, true);
		Intent hideIntent = new Intent(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		hideIntent.putExtra("found_type",1);
		sendBroadcast(hideIntent);
		sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP));
		getLoopData(GlobalParam.LIST_LOAD_FIRST,type,bid);
	}

	/*
	 * 注册通知
	 */
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(MSG_REFRESH_MOVIINF);
		registerReceiver(Receiver, filter);
	}

	/*
	 * 释放通知
	 */
	private void unregisterReceiver(){
		unregisterReceiver(Receiver);
	}

	/*
	 * 处理通知
	 */
	private BroadcastReceiver Receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if(action.equals(MSG_REFRESH_MOVIINF)){
				mMyAlbum = null;
				getLoopData(LIST_LOAD_UPDATE,type,bid);
			}
		}
	};



	/*
	 * 实例化控件
	 */
	private void initCompent(){
		group = (RadioGroup) findViewById(R.id.group);
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				ischecked=true;
				switch (checkedId){
					case R.id.kind:
						findViewById(R.id.friends_fragment_container_layout).setVisibility(View.VISIBLE);
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.friends_fragment_container, KindFragment.newInstance(new ArrayList<String>(Arrays.asList(new String[]{"全部"})),mHandler,"全部")).commit();
						break;
					case R.id.distance:
						findViewById(R.id.friends_fragment_container_layout).setVisibility(View.VISIBLE);
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.friends_fragment_container, KindFragment.newInstance(new ArrayList<String>(Arrays.asList(new String[]{"全部"})),mHandler,"全部"))
								.commit();
						break;
					case R.id.sort:
						String strType="全部";
						if(!(type==null ||type.equals("")||type.equals("全部"))){
							strType=type;
						}
						findViewById(R.id.friends_fragment_container_layout).setVisibility(View.VISIBLE);
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.friends_fragment_container,
										KindFragment.newInstance(new ArrayList<String>(Arrays.asList(new String[]{"全部","企业","生活","微商","活动"})),mHandler,strType,1)).commit();
						break;
				}
			}
		});
		kindBtn = (RadioButton) findViewById(R.id.kind);
		kindBtn.setOnClickListener(this);
		distanceBtn = (RadioButton) findViewById(R.id.distance);
		distanceBtn.setOnClickListener(this);
		sortBtn = (RadioButton) findViewById(R.id.sort);
		sortBtn.setOnClickListener(this);
		//mainBottom = (LinearLayout)findViewById(R.id.main_bottom);
		if(isvisitors){
			setRightTextTitleContent(R.drawable.back_btn,0, R.string.bbs_loop);
		}else{
			if(bbs!=null){
				if(bbs.isclosefriedloop==0||bbs.power==1||bbs.uid.equals(mlogin.uid)){
					setRightTextTitleContent(R.drawable.back_btn, R.string.share,0);
				}else{
					setRightTextTitleContent(R.drawable.back_btn,0,0);
				}
				titileTextView.setText(bbs.title);
			}else{
				setRightTextTitleContent(R.drawable.back_btn, R.string.share, R.string.friends_loop);
			}
		}
		mLeftBtn.setOnClickListener(this);
		mRightTextBtn.setOnClickListener(this);
		mCommentBtn = (Button)findViewById(R.id.send);
		mCommentBtn.setOnClickListener(this);

		mBottomLayout = (RelativeLayout)findViewById(R.id.bottom_menu);
		mBottomLayout.setVisibility(View.GONE);
		mTabs = new Button[5];
		mTabs[0] = (Button) findViewById(R.id.live);
		mTabs[1] = (Button) findViewById(R.id.qiye);
		mTabs[2] = (Button) findViewById(R.id.btn_zhuye);
		mTabs[3] = (Button) findViewById(R.id.weishan);
		mTabs[4] = (Button) findViewById(R.id.huodong);
		mTabs[2].setSelected(true);
		mPicBtn = (ImageView)findViewById(R.id.pic);
		mPicBtn.setOnClickListener(this);
		mBottomMenuLayout = (LinearLayout)findViewById(R.id.comment_bottom_menu);
		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView)findViewById(R.id.container);
		mListView = mContainer.getList();
		//mListView.setOnTouchListener(this);
		mListView.setDivider(getResources().getDrawable(R.color.friend_loop_item_line_color));
		mListView.setDividerHeight(15);
		mListView.setCacheColorHint(0);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		mContainer.setOnChangeStateListener(this);
		mListView.setHeaderDividersEnabled(false);
//		mSearchHeader = LayoutInflater.from(this).inflate(R.layout.friends_loop_header,null);
//		mProfileHeaderIcon = (RoundImageView)mSearchHeader.findViewById(R.id.header_icon);
//		mImageLoader.getBitmap(mContext, mProfileHeaderIcon,null, IMCommon.getLoginResult(mContext).headsmall,0,false,false);
//		mHeaderBg = (ImageView)mSearchHeader.findViewById(R.id.img_bg);
//		RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ICON_SIZE_HEIGHT);
//		mHeaderBg.setLayoutParams(params);
//		mSetCoverHintTextView = (TextView)mSearchHeader.findViewById(R.id.set_cover_hint);
//		int count = IMCommon.getFriendsLoopTip(mContext);
//		if(count>0){
//			List<NotifiyVo> mListData = IMCommon.getMovingResult(mContext);
//			if(mListData.size()>0){
//				shareBtn = (RelativeLayout)mSearchHeader.findViewById(R.id.share_btn);
//				shareBtn.setVisibility(View.VISIBLE);
//				shareView= (TextView)mSearchHeader.findViewById(R.id.url_text);
//				shareView.setText(count+"条新消息");
//				shareIView= (ImageView)mSearchHeader.findViewById(R.id.image_url);
//				mImageLoader.getBitmap(mContext, shareIView,null, mListData.get(0).getUser().headsmall,0,false,false);
//				shareBtn.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						shareBtn.setVisibility(View.GONE);
//						Intent intent = new Intent();
//						intent.setClass(mContext, MyMovingMessageListActivity.class);
//						startActivity(intent);
//					}
//				});
//			}
//		}
//		String coverUrl;
//		if(bbs!=null){
//			coverUrl = bbs.headsmall;
//			//mainBottom.setVisibility(View.GONE);
//		}else{
//			coverUrl = IMCommon.getLoginResult(mContext).cover;
//		}
//		if(coverUrl!=null && !coverUrl.equals("")){
//			mImageLoader.getBitmap(mContext, mHeaderBg, null, coverUrl, 0, false, false);
//			mSetCoverHintTextView.setVisibility(View.GONE);
//		}else{
//			mSetCoverHintTextView.setVisibility(View.VISIBLE);
//		}
//		mLoginUserName = (TextView)mSearchHeader.findViewById(R.id.login_user_name);//
//		mLoginUserName.setText(IMCommon.getLoginResult(mContext).nickname);
//		((TextView)mSearchHeader.findViewById(R.id.sign)).setText(IMCommon.getLoginResult(mContext).sign);
//		mProfileHeaderIcon.setOnClickListener(this);
//		mHeaderBg.setOnClickListener(this);
//
//		if(mListView.getHeaderViewsCount() ==0  || mSearchHeader!=null){
//			mListView.addHeaderView(mSearchHeader);
//		}
		
		mAdapter = new FriendsLoopAdapter(mContext, mDataList, mHandler, mMetric,bbs);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				switch (scrollState) {  
//				case OnScrollListener.SCROLL_STATE_FLING:
//					mAdapter.setFlagBusy(true);
//					break;
				case OnScrollListener.SCROLL_STATE_IDLE: //加载更多
					mAdapter.setFlagBusy(false); 
					if (mBottomLayout.getVisibility() == View.VISIBLE) {
						mBottomLayout.setVisibility(View.GONE);
						mCommentEdit.setText("");
						if(bbs==null){
							//mainBottom.setVisibility(View.VISIBLE);
						}
					}
					
					if ( view.getFirstVisiblePosition() == 0) {
						mHandler.sendEmptyMessage(GlobalParam.LIST_LOAD_REFERSH);
					}
					if (view.getLastVisiblePosition() == (view.getCount() - 1) ) {
						if(mMyAlbum == null){
							return;
						}
						if ( mMyAlbum.pageInfo.hasMore == 1) {
							if (mFootView == null) {
								mFootView = (LinearLayout) LayoutInflater.from(mContext)
										.inflate(R.layout.hometab_listview_footer, null);
							}

							ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
							pb.setVisibility(View.VISIBLE);
							TextView more = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
							more.setText(mContext.getString(R.string.add_more_loading));
							if (mListView.getFooterViewsCount() == 0) {
								mListView.addFooterView(mFootView);	
							}
							getLoopData(GlobalParam.LIST_LOAD_MORE,type,bid);

						}else{//没有更多数据时
							mHandler.sendEmptyMessage(GlobalParam.MSG_CHECK_STATE);
						}

					}
					
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}

					break;  
//				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//					mAdapter.setFlagBusy(true);
//					break;
				default:  
					break;  
				}


			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//				int beforeItem = firstVisibleItem - 2;
//				if(beforeItem > 1){
//					scrollRecycleBitmapCaches(1, beforeItem);
//				}
//
//				int endItem = firstVisibleItem + visibleItemCount + 2;
//				if(endItem < totalItemCount-2){
//					scrollRecycleBitmapCaches(endItem, totalItemCount-2);
//				}
			}
		});

		
		mCommentEdit = (EditText)findViewById(R.id.edit);
		mCommentEdit.setOnFocusChangeListener(sendTextFocusChangeListener);
		mCommentEdit.setOnClickListener(sendTextClickListener);


		mViewPager = (ViewPager) findViewById(R.id.imagepager);
		mViewPager.setOnPageChangeListener(this);
		mLayoutCircle = (LinearLayout) findViewById(R.id.circlelayout);
		mEmotionLayout = (RelativeLayout) findViewById(R.id.emotionlayout);
		mEmotionLayout.setVisibility(View.GONE);

		mTotalEmotionList = getEmojiList();
		for (int i = 0; i < mTotalEmotionList.size(); i++) {
			addView(i);
		}

		mEmotionAdapter = new IMViewPagerAdapter(mViewList);
		mViewPager.setAdapter(mEmotionAdapter);
		mViewPager.setCurrentItem(0);
		showCircle(mViewList.size());

	}
	private int index,currentTabIndex=2;
	public void onTabClicked(View view) {
		switch (view.getId()) {
			case R.id.live:
				index = 0;
				type="生活";
				break;
			case R.id.qiye:
				index = 1;
				type="企业";
				break;
			case R.id.btn_zhuye:
				index = 2;
				type="";
				break;
			case R.id.weishan:
				index = 3;
				type="微商";
				break;
			case R.id.huodong:
				index = 4;
				type="活动";
				break;
		}
		showData();
	}
	public void showData(){
		mTabs[currentTabIndex].setSelected(false);
		mTabs[index].setSelected(true);
		currentTabIndex = index;
		mMyAlbum = null;
		getLoopData(GlobalParam.LIST_LOAD_FIRST,type,bid);
	}
	// 显示表情列表
	private void showEmojiGridView(){
		
		hideSoftKeyboard();
		mEmotionLayout.setVisibility(View.VISIBLE);
	}



	// 隐藏表情列表
	private void hideEmojiGridView(){
		mEmotionLayout.setVisibility(View.GONE);
	}


	private OnFocusChangeListener sendTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				// 文本框得到焦点，隐藏附加信息和表情列表
				hideEmojiGridView();
			}

		}
	};

	private OnClickListener sendTextClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// 获取到文本框的点击事件隐藏表情
			hideEmojiGridView();
		}
	};


	/*
	 * 获取朋友圈内容
	 */
	private void getLoopData(final int loadType, final String type, final String bid){
		IMCommon.verifyNetwork(mContext);
		if (!IMCommon.getNetWorkState()) {
			switch (loadType) {
			case GlobalParam.LIST_LOAD_FIRST:
				mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
				break;
			case GlobalParam.LIST_LOAD_MORE:
				mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);
				break;
			case GlobalParam.LIST_LOAD_REFERSH:
				mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
				break;
			default:
				break;
			}
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					if (loadType == GlobalParam.LIST_LOAD_FIRST) {
						IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
								mContext.getResources().getString(R.string.get_dataing));
					}
					boolean isExitsData = true;
					if (mMyAlbum!=null && mMyAlbum.pageInfo.currentPage == mMyAlbum.pageInfo.totalPage) {
						isExitsData = false;
					}
					int page = 0;
					if (loadType == GlobalParam.LIST_LOAD_FIRST
							||loadType == LIST_LOAD_UPDATE) {
						page = 1;
					}else if(loadType == GlobalParam.LIST_LOAD_MORE){
						page = mMyAlbum.pageInfo.currentPage+1;
					}
					if (isExitsData) {
						mMyAlbum = IMCommon.getIMInfo().shareList(page,type,bid);
						List<FriendsLoopItem> tempList = new ArrayList<FriendsLoopItem>();
						if (mMyAlbum != null && mMyAlbum.childList!=null && mMyAlbum.childList.size() > 0) {
							isExitsData = true;
							tempList.addAll(mMyAlbum.childList);
						} else{
							isExitsData = false;
						}
						IMCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList,loadType);
					}
					if (loadType == GlobalParam.LIST_LOAD_FIRST
							||loadType == LIST_LOAD_UPDATE) {
						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					}

					switch (loadType) {
					case LIST_LOAD_UPDATE:
					case GlobalParam.LIST_LOAD_FIRST:
						mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LISTVIEW_DATA);
						break;

					case GlobalParam.LIST_LOAD_MORE:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);
						break;
					case GlobalParam.LIST_LOAD_REFERSH:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
						break;

					default:
						break;
					}
					if (!isExitsData) {
						mHandler.sendEmptyMessage(GlobalParam.MSG_CHECK_STATE);
					}

				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler,BASE_MSG_NETWORK_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					switch (loadType) {
					case LIST_LOAD_UPDATE:
					case GlobalParam.LIST_LOAD_FIRST:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
						break;

					case GlobalParam.LIST_LOAD_MORE:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);
						break;
					case GlobalParam.LIST_LOAD_REFERSH:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
						break;

					default:
						break;
					}
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*
	 * 显示listview 数据
	 */
	private void updateListView(){
		if (mDataList == null || mDataList.size() == 0) {
			return;
		}

		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{

			// add listview first item
//			if(mListView.getHeaderViewsCount() ==0  || mSearchHeader!=null){
//				mListView.addHeaderView(mSearchHeader);
//			}


			// add listview last item
			boolean isLoadMore = (mMyAlbum!=null && mMyAlbum.pageInfo!=null && mMyAlbum.pageInfo.hasMore == 1)?true:false;
			if (isLoadMore) {
				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				if (mListView.getFooterViewsCount() == 0) {
					mListView.addFooterView(mFootView);
				}
			}
			mAdapter = new FriendsLoopAdapter(mContext, mDataList,mHandler,mMetric,bbs);
			mListView.setAdapter(mAdapter);
		}



	}


	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	boolean ischecked=true;
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.kind:
			if (kindBtn.isChecked()) {
				if(ischecked){
					ischecked=false;
				}else{
					group.clearCheck();
					findViewById(R.id.friends_fragment_container_layout).setVisibility(View.GONE);
				}
			}
			break;
		case R.id.distance:
			if (distanceBtn.isChecked()) {
				if(ischecked){
					ischecked=false;
				}else{
					group.clearCheck();
					findViewById(R.id.friends_fragment_container_layout).setVisibility(View.GONE);
				}
			}
			break;
		case R.id.sort:
			if (sortBtn.isChecked()) {
				if(ischecked){
					ischecked=false;
				}else{
					group.clearCheck();
					findViewById(R.id.friends_fragment_container_layout).setVisibility(View.GONE);
				}
			}
			break;
		case R.id.left_btn:
			FriensLoopActivity.this.finish();
			break;
		case R.id.right_text_btn:
			if(bbs==null){
				if (!mlogin.quId.equals(mlogin.kai6Id) && mlogin.userDj.equals("0")) {
					Toast.makeText(mContext,"你不是特权会员或渠道", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			Intent intent = new Intent();
			intent.setClass(mContext, SendMovingActivity.class);
			intent.putExtra("bbs",bbs);
			startActivity(intent);
			break;
		case R.id.header_icon:
			Intent myAlbumIntent = new Intent();
			myAlbumIntent.setClass(mContext, MyAlbumActivity.class);
			myAlbumIntent.putExtra("toUserID", IMCommon.getUserId(mContext));
			startActivity(myAlbumIntent);
			break;
		case R.id.pic:
			showEmojiGridView();
			mCommentEdit.setVisibility(View.VISIBLE);
			break;
		case R.id.send:
			mInputComment = mCommentEdit.getText().toString();
			if (mInputComment == null || mInputComment.equals("")) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.please_input_comment_contnet), Toast.LENGTH_LONG).show();
				return;
			}
			hideEmojiGridView();
			if(mDataList!=null && mDataList.size()>=mPosition){
				if(mDataList.get(mPosition).replylist == null){
					mDataList.get(mPosition).replylist = new ArrayList<CommentUser>();
				}
				String fiunickname = mCommentEdit.getHint().toString();
				if (fiunickname.contains("回复")) {
					fiunickname = fiunickname.substring(fiunickname.indexOf("回复")+"回复".length());
				}
				mDataList.get(mPosition).replylist.add(new CommentUser(mShareId, IMCommon.getUserId(mContext),
						IMCommon.getLoginResult(mContext).nickname,
						mToUserId,fiunickname,mInputComment, System.currentTimeMillis()/1000));
				if(mAdapter!=null){
					mAdapter.setData(mDataList);
					mAdapter.notifyDataSetChanged();
				}
			}
			if (mBottomLayout.getVisibility() == View.VISIBLE) {
				mBottomLayout.setVisibility(View.GONE);
				if(bbs==null){
					//mainBottom.setVisibility(View.VISIBLE);
				}
			}
			comment(mInputComment);
			break;
		default:
			break;
		}
	}
	public void onC(View view){
		group.clearCheck();
		findViewById(R.id.friends_fragment_container_layout).setVisibility(View.GONE);
	}
	/**
	 * 选择图片对话框
	 */
	private void selectImg(){
		MMAlert.showAlert(mContext,"",
				mContext.getResources().getStringArray(R.array.camer_item),
				null, new MMAlert.OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0:
					getImageFromGallery();
					break;
				case 1:
					getImageFromCamera();
					break;
				default:
					break;
				}
			}
		});
	}


	/*
	 * 拍一张
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, mTempFileName);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}

	}

	/*
	 * 从相册中选取
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}

	/*
	 * 处理选择的图片
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		Log.e("doChoose", "isGallery=" + isGallery);
		if(isGallery||data != null){
				originalImage(data);
		}else{
			String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+mTempFileName;
			startPhotoZoom(Uri.fromFile(new File(path)));
		}
	}

	private void originalImage(Intent data) {
		Uri uri = data.getData();
		if (uri != null) {
			Log.e("originalImage", "uri.getAuthority()=" + uri.getAuthority());
			if (!TextUtils.isEmpty(uri.getAuthority())) {
				Cursor cursor = getContentResolver().query(uri,
						new String[] { MediaStore.Images.Media.DATA }, null, null,
						null);
				if (null == cursor) {
					Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
					return;
				}
				cursor.moveToFirst();
				String imageFilePath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				Log.e("may", "path=" + imageFilePath);
				File file = new File(imageFilePath);
				Log.e("may", "file.exists()=" + file.exists());
				startPhotoZoom(uri);
			} else {
				Log.e("mayelse", "path=" + uri.getPath());
				Intent intent = new Intent(mContext,FriensLoopActivity.class);
				intent.putExtra("path", uri.getPath());
				startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
			}
		}
		else {
			Intent intent = new Intent(mContext, RotateImageActivity.class);
			intent.putExtra("path", Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+mTempFileName);
			startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
		}
	}

	/*
	 * 裁剪图片
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
		 * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
		 * 制做的了...吼吼
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String url=getPath(mContext,uri);
			intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
		}else{
			intent.setDataAndType(uri, "image/*");
		}
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 2);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", /*ICON_SIZE_WIDTH*/720);
		intent.putExtra("outputY", /*ICON_SIZE_HEIGHT*/480);
		mCropImgPath = Environment.getExternalStorageDirectory() +  FeatureFunction.PUB_TEMP_DIRECTORY + "album.jpg";
		File file = new File(mCropImgPath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Uri imageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		Log.e("startPhotoZoom", "width:"+ICON_SIZE_WIDTH+" height:"+ICON_SIZE_HEIGHT);
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
	}

	//以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}
	/*
	 * 处理选择的图片
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case GlobalParam.REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}
			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
				doChoose(false, data);
			}
			break;
		case GlobalParam.REQUEST_GET_BITMAP:
			if(resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				if(mCropImgPath != null && !mCropImgPath.equals("")){
					String path = mCropImgPath;
					//mHeaderBg.setImageBitmap(null);
//					if(mBitmap != null && !mBitmap.isRecycled()){
//						mBitmap.recycle();
//						mBitmap = null;
//					}
//					mBitmap =BitmapFactory.decodeFile(path);
//					if(mBitmap != null)
//					{
//						mHeaderBg.setImageBitmap(mBitmap);
//					}
					File file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + mTempFileName+".jpg");
					if(file != null && file.exists()){
						file.delete();
						file = null;
					}
					//mCropImgPath = FeatureFunction.saveTempBitmap(mBitmap, "album.jpg");
					showModifybgDialog();
				}

			}

		

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 显示更换背景提示框
	 */
	private void showModifybgDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(FriensLoopActivity.this);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(mContext.getResources().getString(R.string.are_you_change_bg));
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//这里添加点击确定后的逻辑
				//showDialog("你选择了确定");
				uploadBg();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//这里添加点击确定后的逻辑
				//showDialog("你选择了取消");
//				if (mMyAlbum!=null && mMyAlbum.frontCover!=null && !mMyAlbum.frontCover.equals("")) {
//					mImageLoader.getBitmap(mContext, mHeaderBg,null,mMyAlbum.frontCover, 0, false,false);
//				}else{
//					mHeaderBg.setImageResource(R.drawable.head_img);
//				}

			}
		});
		builder.create().show();
	}

	/*
	 * 更换背景
	 */
	private void uploadBg(){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			public void run() {
				try {

					if (mCropImgPath !=null && !mCropImgPath.equals("")) {
						/*if (out.length() > 5 * Mb) {
							QiyueCommon.sendMsg(mHandler, MainActivity.MSG_EXCEPTION,
									mContext.getResources().getString(
											R.string.error_image_oversize));
							return;
						}*/
					}
					mListpic.add(new MorePicture("picture",mCropImgPath));
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, mContext.getResources().getString(R.string.send_request));
					IMJiaState status = IMCommon.getIMInfo().uploadUserBg(
							IMCommon.getUserId(mContext), mListpic);
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_UPLOAD_STATUS,status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler, GlobalParam.MSG_TICE_OUT_EXCEPTION,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}



	
	/*
	 * 隐藏键盘
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){

			for (int i = 0; i < mDataList.size(); i++) {
				if(mDataList.get(i).showView == 1){
					mDataList.get(i).showView = 0;
				}
			}
			if(mAdapter!=null ){
				mAdapter.notifyDataSetChanged();
			}

			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
				InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}  
			
			if (mBottomLayout.getVisibility() == View.VISIBLE) {
				mBottomLayout.setVisibility(View.GONE);
				mCommentEdit.setText("");
				if(bbs==null){
					//mainBottom.setVisibility(View.VISIBLE);
				}
			}
			
			
		}  
		return super.onTouchEvent(event);  
	}


	/*
	 * 下拉刷新
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.widget.MyPullToRefreshListView.OnChangeStateListener#onChangeState(com.wqdsoft.im.widget.MyPullToRefreshListView, int)
	 */
	@Override
	public void onChangeState(MyPullToRefreshListView container, int state) {
		mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());
		switch (state) {
		case MyPullToRefreshListView.STATE_LOADING:
			mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
			break;
		}
	}


	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_GET_TYPE_DATA:
				String stype = (String)msg.obj;
				if("全部".equals(stype)){
					type="";
				}else{
					type = stype;
				}
				group.clearCheck();
				findViewById(R.id.friends_fragment_container_layout).setVisibility(View.GONE);
				getLoopData(LIST_LOAD_UPDATE,type,bid);
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:

				if ((msg.arg1 == GlobalParam.LIST_LOAD_FIRST
						|| msg.arg1 == GlobalParam.LIST_LOAD_REFERSH
						|| msg.arg1 == LIST_LOAD_UPDATE)
						&& mDataList!=null && mDataList.size()>0) {
					mDataList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<FriendsLoopItem> tempList = (List<FriendsLoopItem>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mDataList.addAll(tempList);
				}
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
					mContainer.onRefreshComplete();
					break;
				}
				mIsRefreshing = true;

				mMyAlbum = null;
				getLoopData(GlobalParam.LIST_LOAD_REFERSH,type,bid);
				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				updateListView();
				break;
			case GlobalParam.MSG_CHECK_STATE:
				if (mFootView != null && mListView.getFooterViewsCount()>0) {
					mListView.removeFooterView(mFootView); 
				}
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				updateListView();
				break;
			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:

				if (mFootView == null) {
					mFootView = (LinearLayout) LayoutInflater.from(mContext)
							.inflate(R.layout.hometab_listview_footer, null);
				}
				ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);
				pb.setVisibility(View.GONE);
				TextView more = (TextView)mFootView.findViewById(R.id.hometab_footer_text);
				more.setText("");
				if (mAdapter != null){
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalParam.MSG_UPLOAD_STATUS:
				IMJiaState returnStatus = (IMJiaState)msg.obj;
				if (returnStatus == null) {
					Toast.makeText(mContext, R.string.commit_data_error, Toast.LENGTH_LONG).show();
					return;
				}
				if (returnStatus.code !=0) {
					Toast.makeText(mContext, returnStatus.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				Login login = IMCommon.getLoginResult(mContext);
				login.cover = returnStatus.frontCover;
				IMCommon.saveLoginResult(mContext, login);
				break;
			case GlobalParam.MSG_DEL_FRIENDS_LOOP:
				int delPos = msg.arg1;
				if(mDataList!=null && mDataList.size()>0){
					mShareId = mDataList.get(delPos).id;
					mToUserId = mDataList.get(delPos).uid;
					mPosition = delPos;
					createDialog(mContext, mContext.getResources().getString(R.string.del_friends_loop_hint),
							mContext.getResources().getString(R.string.ok),mShareId);
				}

				break;
			case  GlobalParam.MSG_CHECK_FRIENDS_LOOP_POP_STATUS:

				for (int i = 0; i < mDataList.size(); i++) {
					if(mDataList.get(i).showView == 1){
						mDataList.get(i).showView = 0;
					}
				}
				int pos = msg.arg1;
				mDataList.get(pos).showView = 1;
				if(mAdapter!=null ){
					mAdapter.notifyDataSetChanged();
				}
				break;
			case GlobalParam.MSG_SHOW_BOTTOM_COMMENT_MENU:
				for (int i = 0; i < mDataList.size(); i++) {
					if(mDataList.get(i).showView == 1){
						mDataList.get(i).showView = 0;
					}
				}
				if(mAdapter!=null ){
					mAdapter.notifyDataSetChanged();
				}

				int commentIndex = msg.arg1;
				if (msg.obj instanceof String) {
					mToUserId = (String) msg.obj;
					mCommentEdit.setHint("说点什么呢");
				}else if(msg.obj instanceof CommentUser){
					CommentUser cu = (CommentUser) msg.obj;
					mToUserId = cu.uid;
					mCommentEdit.setHint("回复:"+cu.nickname);
				}
				FriendsLoopItem commentMvoing = mDataList.get(commentIndex);
				mBottomLayout.setVisibility(View.VISIBLE);
				if(bbs==null){
					//mainBottom.setVisibility(View.GONE);
				}
				TranslateAnimation animation = new TranslateAnimation(mMetric.widthPixels, 0, 0, 0);
				animation.setDuration(500);
				animation.setAnimationListener(mAnimationListener);
				mBottomLayout.startAnimation(animation);

				mShareId = commentMvoing.id;
				mPosition = commentIndex;
				break;
			case GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG:
				FriendsLoopItem item = (FriendsLoopItem) msg.obj;
				int type = msg.arg1;  //1收藏文本 2收藏图片
				int picIndex = msg.arg2;
				mShareId = item.id;
				mToUserId = item.uid;
				showFavoriteDialog(type,item,picIndex);
				break;
			case GlobalParam.MSG_COMMINT_ZAN:
				int zanIndex = msg.arg1;
				if (zanIndex < 0) {
					return;
				}
				mPosition = zanIndex;
				FriendsLoopItem zanMvoing = mDataList.get(zanIndex);
				mShareId = zanMvoing.id;
				if(mDataList!=null && mDataList.size()>=mPosition){
					if(mDataList.get(mPosition).praiselist == null){
						mDataList.get(mPosition).praiselist = new ArrayList<CommentUser>();
					}
					if(mDataList.get(mPosition).ispraise==0){
						mDataList.get(mPosition).praiselist.add(new CommentUser(mShareId,mlogin.uid,
								mlogin.nickname,
								mToUserId,null,mInputComment, System.currentTimeMillis()/1000));
						mDataList.get(mPosition).showView = 0;
					}else{
						for (int i = mDataList.get(mPosition).praiselist.size() - 1; i >= 0; i--) {
							if (mDataList.get(mPosition).praiselist.get(i).nickname.equals(mlogin.nickname)) {
								mDataList.get(mPosition).praiselist.remove(i);
								break;
							}
						}
						mDataList.get(mPosition).showView = 0;
					}
					if(mAdapter!=null){
						mAdapter.setData(mDataList);
						mAdapter.notifyDataSetChanged();
					}
				}
				zan();
				break;
			case GlobalParam.MSG_COMMENT_STATUS:
				IMJiaState commentResult = (IMJiaState)msg.obj;
				if(commentResult == null){
					Toast.makeText(mContext, "提交数据失败!", Toast.LENGTH_LONG).show();
					return;
				}
				if( commentResult.code!=0){
					Toast.makeText(mContext, commentResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				mInputComment = "";
				mCommentEdit.setText("");
				mShareId = 0;
				mToUserId = "";
				mPosition = 0;
				break;
			case GlobalParam.MSG_ZAN_STATUS:
				IMJiaState zanResult = (IMJiaState)msg.obj;
				if(zanResult == null){
					Toast.makeText(mContext, R.string.commit_dataing, Toast.LENGTH_LONG).show();
					return;
				}
				if(zanResult.code!=0){
					Toast.makeText(mContext, zanResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				if(zanResult.friendsLoopitem!=null){
					if(mDataList!=null && mDataList.size()>=mPosition){
						zanResult.friendsLoopitem.job = mDataList.get(mPosition).job;
						zanResult.friendsLoopitem.company = mDataList.get(mPosition).company;
						mDataList.remove(mPosition);
						if(zanResult.friendsLoopitem.listpic!=null){
							Collections.reverse(zanResult.friendsLoopitem.listpic);
						}
						mDataList.add(mPosition, zanResult.friendsLoopitem);
					}
				}

				for (int i = 0; i < mDataList.size(); i++) {
					if(mDataList.get(i).showView == 1){
						mDataList.get(i).showView = 0;
					}
				}
				if(mAdapter!=null ){
					mAdapter.notifyDataSetChanged();
				}

				break;
			case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
				IMJiaState favoriteResult = (IMJiaState)msg.obj;
				if(favoriteResult == null){
					Toast.makeText(mContext, R.string.commit_dataing, Toast.LENGTH_LONG).show();
					return;
				}
				if(favoriteResult.code!=0){
					Toast.makeText(mContext, favoriteResult.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(mContext, favoriteResult.errorMsg, Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_CHECK_DEL_SHARE_STATUS:
				IMJiaState delStatus = (IMJiaState)msg.obj;
				if(delStatus == null){
					Toast.makeText(mContext, R.string.commit_data_error, Toast.LENGTH_LONG).show();
					return;
				}
				if(delStatus.code !=0){
					Toast.makeText(mContext, delStatus.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				mDataList.remove(mPosition);
				if(mAdapter!=null){
					mAdapter.notifyDataSetChanged();
				}
				List<NotifiyVo> messageList = new ArrayList<NotifiyVo>();
				List<NotifiyVo> tempMsgList = IMCommon.getMovingResult(mContext);
				if(tempMsgList!=null && tempMsgList.size()>0){
					for (NotifiyVo notifiyVo : tempMsgList) {
						if(notifiyVo.shareId != mShareId){
							messageList.add(notifiyVo);
						}
					}
				
					IMCommon.saveMoving(mContext, messageList);
				}
				break;
			default:
				break;
			}
		}

	};

	/*
	 * 评论
	 */
	private void comment(final String content){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {

					IMJiaState status = IMCommon.getIMInfo().shareReply(mShareId,mToUserId,content);
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_COMMENT_STATUS,status);
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*
	 * 赞
	 */
	private void zan(){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMJiaState status = IMCommon.getIMInfo().sharePraise(mShareId);
					if(status !=null && status.code == 0){
						FriendsLoopItem shareDetail = IMCommon.getIMInfo().shareDetail(mShareId);
						if(shareDetail !=null){
							status.friendsLoopitem = shareDetail;
						}
					}
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_ZAN_STATUS,status);
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler,BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}



	/*
	 * 收藏图片
	 */
	private void favoriteMoving(final String favoriteContent){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					IMJiaState status = IMCommon.getIMInfo().favoreiteMoving(mToUserId, null, favoriteContent);
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_FAVORITE_STATUS,status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler,BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/*
	 * 删除分享
	 */
	private void delShare(final int shareId){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					String deldynamic="0";
					if(bbs!=null){
						deldynamic =bbs.deldynamic+"";
					}
					IMJiaState status = IMCommon.getIMInfo().deleteShare(shareId,deldynamic);
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_DEL_SHARE_STATUS, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (NotFoundException e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();

	}


	/*
	 * 滑动删除图片
	 */
	private void scrollRecycleBitmapCaches(	int start, int end){                
		for(int i = start; i < end; i++){
			if(mDataList.get(i).listpic != null && mDataList.get(i).listpic.size() != 0){

				if(mAdapter != null && mAdapter.getImageBuffer() != null){
					LinearLayout picLayout = (LinearLayout) mListView.findViewWithTag(mDataList.get(i).id);
					for (int j = 0; j < mDataList.get(i).listpic.size(); j++) {

						if(picLayout != null){
							ImageView imageView = (ImageView) picLayout.findViewWithTag(mDataList.get(i).listpic.get(j).smallUrl);
							if(imageView != null){
								imageView.setImageBitmap(null);
								imageView.setImageResource(R.drawable.normal);
							}
						}

						Bitmap bitmap = mAdapter.getImageBuffer().get(mDataList.get(i).listpic.get(j).smallUrl);
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
							mAdapter.getImageBuffer().remove(mDataList.get(i).listpic.get(j).smallUrl);
						}

					}
				}

			}
		}

	}


	/**
	 * 
	 * @param type 1-收藏或取消收藏文本 2-收藏或取消收藏图片
	 */
	private void showFavoriteDialog(final int type, final FriendsLoopItem item, final int picIndex) {
		if(item == null ){
			return;
		}
		String[] items ;
		items =  new String[] { getString(R.string.favorite)};
		if(type==1){
			items =  new String[] { "收藏","复制"};
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(
				FriensLoopActivity.this)
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (type) {
				case 1:
					switch(which){
						case 0:
							MovingContent movingContent = new MovingContent(item.content, MessageType.TEXT+"");
							favoriteMoving(MovingContent.getInfo(movingContent));
							break;
						case 1:
							ClipboardManager cm =(ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
							cm.setText(item.content);
							break;
					}
					break;
				case 2:
					if(picIndex!=-1 && (item.listpic!=null && item.listpic.size()>0)){
						MovingPic pic = new MovingPic(item.listpic.get(picIndex).originUrl,item.listpic.get(picIndex).smallUrl, MessageType.PICTURE+"");
						favoriteMoving(MovingPic.getInfo(pic));
					}
					break;

				default:
					break;
				}
				if (mUpgradeNotifyDialog != null) {
					mUpgradeNotifyDialog.dismiss();
				}
			}
		});
		mUpgradeNotifyDialog = builder.show();
	}

	AnimationListener mAnimationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			mBottomLayout.setVisibility(View.VISIBLE);
			if(bbs==null){
				//mainBottom.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mBottomLayout.clearAnimation();
			mCommentEdit.setFocusable(true); 
			mCommentEdit.setFocusableInTouchMode(true);
			mCommentEdit.requestFocus();
			InputMethodManager inputManager =(InputMethodManager)mCommentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(mCommentEdit, 0);

		}
	};

	/**
	 * 添加表情滑动控件
	 * @param i					添加的位置
	 */
	private void addView(final int i){
		View view = LayoutInflater.from(mContext).inflate(R.layout.emotion_gridview, null);
		GridView gridView = (GridView) view.findViewById(R.id.emoji_grid);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				if(position < mTotalEmotionList.get(i).size() - 1){
					ImageView imageView = (ImageView)view.findViewById(R.id.emotion);
					if(imageView != null){
						Drawable drawable = imageView.getDrawable();
						if(drawable instanceof BitmapDrawable){
							Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
							String name = mTotalEmotionList.get(i).get(position);

							Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
							int width = getResources().getDimensionPixelSize(R.dimen.pl_emoji);
							int height = width;
							mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0); 
							ImageSpan span = new ImageSpan(mDrawable);

							SpannableString spannableString = new SpannableString("[" + name + "]");
							//类似于集合中的(start, end)，不包括起始值也不包括结束值。
							// 同理，Spannable.SPAN_INCLUSIVE_EXCLUSIVE类似于 [start，end)
							spannableString.setSpan(span, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
							Editable dEditable = mCommentEdit.getEditableText();
							int index = mCommentEdit.getSelectionStart();
							dEditable.insert(index, spannableString);
						}
					}
				}else {
					int index = mCommentEdit.getSelectionStart();

					String text = mCommentEdit.getText().toString();
					if (index > 0) {  
						String text2 = text.substring(index - 1);
						if ("]".equals(text2)) {  
							int start = text.lastIndexOf("[");  
							int end = index;  
							mCommentEdit.getText().delete(start, end);  
							return;  
						}  
						mCommentEdit.getText().delete(index - 1, index);  
					}  
				}
			}

		});
		gridView.setAdapter(new EmojiAdapter(mContext, mTotalEmotionList.get(i), IMCommon.mScreenWidth));
		mViewList.add(view);
	}


	/**
	 * 显示表情处于第几页标志
	 * @param size
	 */
	private void showCircle(int size){
		mLayoutCircle.removeAllViews();

		for( int i = 0; i < size; i++){
			ImageView img = new ImageView(mContext);
			img.setLayoutParams(new LinearLayout.LayoutParams(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5)));
			LinearLayout layout = new LinearLayout(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int margin = FeatureFunction.dip2px(mContext, 5);
			params.setMargins(margin, 0, margin, 0);
			layout.setLayoutParams(params);
			layout.addView(img);
			//img.setLayoutParams()
			if ( mPageIndxe == i){
				img.setImageResource(R.drawable.circle_d);
			} else{
				img.setImageResource(R.drawable.circle_n);
			}
			mLayoutCircle.addView(layout);
		}
	}


	/**
	 * 获取表情列表
	 * @return
	 * guoxin <br />
	 * 创建时间:2013-6-21<br />
	 * 修改时间:<br />
	 */
	private List<List<String>> getEmojiList() {
		List<String> emojiList = new ArrayList<String>();
		String baseName = "emoji_";
		for (int i = 85; i <= 88; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 340; i <= 363; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 94; i <= 101; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 115; i <= 117; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 364; i <= 373; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 12; i <= 17; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 0; i <= 11; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 18; i <= 84; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 89; i <= 93; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 101; i <= 114; i++) {
			emojiList.add(baseName + i);
		}

		for (int i = 114; i <= 339; i++) {
			emojiList.add(baseName + i);
		}

		List<List<String>> totalList = new ArrayList<List<String>>();
		int page = emojiList.size() % 20 ==0 ? emojiList.size() / 20 : emojiList.size() / 20 + 1;
		for (int i = 0; i < page; i++) {
			int startIndex = i * 20;
			List<String> singleList = new ArrayList<String>();
			if(singleList != null){
				singleList.clear();
			}
			int endIndex = 0;
			if(i < page - 1){
				endIndex = startIndex + 20;
			}else if(i == page - 1){
				endIndex = emojiList.size() - 1;
			}

			singleList.addAll(emojiList.subList(startIndex, endIndex));
			singleList.add("delete_emotion_btn");
			totalList.add(singleList);

		}

		return totalList;
	}





	public void hideSoftKeyboard(){
		hideSoftKeyboard(getCurrentFocus());
	}
	public void hideSoftKeyboard(View view){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(view != null){
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		

		InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(mCommentEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	
		if(mBottomLayout.getVisibility() == View.VISIBLE){
			mBottomLayout.setVisibility(View.GONE);
			mCommentEdit.setText("");
			if(bbs==null){
				//mainBottom.setVisibility(View.VISIBLE);
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		IMCommon.saveReadFriendsLoopTip(mContext, true);
		Intent hideIntent = new Intent(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		hideIntent.putExtra("found_type",1);
		sendBroadcast(hideIntent);
		sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP));
		unregisterReceiver(Receiver);
		super.onDestroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		mPageIndxe = position;
		showCircle(mViewList.size());
	}
	/**
	 * 
	 * @param context
	 * @param cardTitle
	 * @param okTitle
	 */
	protected void createDialog(Context context, String cardTitle, final String okTitle, final int shareId) {
		mPhoneDialog = new Dialog(context, R.style.dialog);
		LayoutInflater factor = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.card_dialog, null);

		mPhoneDialog.setContentView(serviceView);
		mPhoneDialog.show();
		mPhoneDialog.setCancelable(false);	
		mPhoneDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT
				, LayoutParams.WRAP_CONTENT);

	
		final TextView phoneEdit=(TextView)serviceView.findViewById(R.id.card_title);
		phoneEdit.setText(cardTitle);


		Button okBtn=(Button)serviceView.findViewById(R.id.yes);
		okBtn.setText(okTitle);


		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPhoneDialog!=null) {
					mPhoneDialog.dismiss();
					mPhoneDialog=null;
				}
				IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
						mContext.getResources().getString(R.string.send_request));
				delShare(shareId);
			}
		});

		Button Cancel = (Button)serviceView.findViewById(R.id.no);
		Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPhoneDialog!=null) {
					mPhoneDialog.dismiss();
					mPhoneDialog = null;
				}
			}
		});
	}
	public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}



}
