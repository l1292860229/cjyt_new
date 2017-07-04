package com.coolwin.XYT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.RoomTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.DB.UserMenuTable;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.Card;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.InviteBBS;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MapInfo;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageResult;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.MovingContent;
import com.coolwin.XYT.Entity.MovingLoaction;
import com.coolwin.XYT.Entity.MovingPic;
import com.coolwin.XYT.Entity.MovingVoice;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.ShareUrl;
import com.coolwin.XYT.Entity.UserMenu;
import com.coolwin.XYT.Entity.UserMenuList;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.action.AudioPlayListener;
import com.coolwin.XYT.action.AudioRecorderAction;
import com.coolwin.XYT.activity.LocationActivity;
import com.coolwin.XYT.adapter.EmojiAdapter;
import com.coolwin.XYT.adapter.EmojiUtil;
import com.coolwin.XYT.adapter.IMViewPagerAdapter;
import com.coolwin.XYT.control.ReaderImpl;
import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.dialog.MMAlert.OnAlertSelectId;
import com.coolwin.XYT.global.AjaxCallBack;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.GlobleType;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.global.Utils;
import com.coolwin.XYT.global.VoiceTask;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.net.Utility;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.receiver.NotifyChatMessage;
import com.coolwin.XYT.receiver.PushChatMessage;
import com.coolwin.XYT.service.SnsService;
import com.coolwin.XYT.service.type.XmppType;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.widget.MainSearchDialog;
import com.coolwin.XYT.widget.MainSearchDialog.OnFinishClick;
import com.coolwin.XYT.widget.ResizeLayout;
import com.coolwin.XYT.widget.ResizeLayout.OnResizeListener;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.coolwin.XYT.global.IMCommon.getIMInfo;

/**
 * 
 * 功能： 个人会话 <br />
 * 
 * 
 * 1 成功;
 * 0 失败;
 * 2 正在发送;
 * @author dl
 * @since
 */
public class ChatMainActivity  extends BaseActivity implements OnItemLongClickListener, OnClickListener, OnTouchListener, OnPageChangeListener {
	/**
	 * 定义全局变量
	 */
	public static final String EMOJIREX = "emoji_[\\d]{0,3}";
	private static final String TAG = "chat_main";
	private final static int MSG_RESIZE = 1234;
	private final static int MSG_REFUSH= 1235;
	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();
	private ViewPager mViewPager;
	private IMViewPagerAdapter mEmotionAdapter;
	private LinkedList<View> mViewList = new LinkedList<View>();
	private LinearLayout mLayoutCircle;
	public int mPageIndxe = 0;
	private RelativeLayout mEmotionLayout;

	private RelativeLayout mChatBottmLayout;

	private ListView mListView;
	private Button mMsgSendBtn, mCameraBtn, mGalleryBtn, mLocationBtn,mCardBtn,mFavoritebtn,mRedPacketbtn,mVideobtn;
	private Button mVoiceSendBtn;
	private ToggleButton mToggleBtn,mAddBtn, mEmotionBtn;
	private EditText mContentEdit;
	private View mChatExpraLayout;
	private MyAdapter mAdapter;
	private List<MessageInfo> messageInfos;
	private ReaderImpl mReaderImpl;
	private AudioRecorderAction audioRecorder;
	private AudioPlayListener playListener;
	private AlertDialog messageDialog;		// 消息功能ui
	private Handler handler = new Handler();
	private boolean opconnectState = false;


	private ProgressDialog waitDialog;

	private List<String> downVoiceList = new ArrayList<String>();
	private ResizeLayout mRootLayout;
	private ResizeLayout mListLayout;
	private static final int BIGGER = 1;  
	private static final int SMALLER = 2;
	private ImageLoader mImageLoader = new ImageLoader();
	public static final int SEND_VOICE_TO_LIST = 4445;
	public static final int ADD_VOICE_TO_LIST = 4446;
	private static final int REQUEST_GET_IMAGE_BY_CAMERA = 1002;
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	public static final int REQUEST_GET_REDPACKET = 125;
	private boolean mIsFirst = true;
	public static final String DESTORY_ACTION = "com.wqdsoft.im.intent.action.DESTORY_ACTION";
	public static final String REFRESH_ADAPTER = "com.wqdsoft.im.intent.action.REFRESH_ADAPTER";
	public static final String ACTION_READ_VOICE_STATE = "com.wqdsoft.im.sns.push.ACTION_READ_VOICE_STATE";
	public static final String ACTION_CHANGE_FRIEND = "com.wqdsoft.im.intent.action.ACTION_CHANGE_FRIEND";
	public static final String ACTION_RECORD_AUTH = "com.wqdsoft.im.intent.action.ACTION_RECORD_AUTH";
	public static final String ACTION_RECOMMEND_CARD = "com.wqdsoft.im.intent.action.ACTION_RECOMMEND_CARD";
	public static final String ACTION_DESTROY_ROOM = "com.wqdsoft.im.intent.action.ACTION_RECOMMEND_CARD";
	public static final String ACTION_SHOW_NICKNAME = "com.wqdsoft.im.intent.action.show.nickname";
	private final static int MAX_SECOND = 10;
	private final static int MIN_SECOND = 2;
	private HashMap<String, SoftReference<Bitmap>> mBitmapCache;
	private Login mLogin;
	private String mFilePath = "";
	private int mScalcWith,mScalcHeigth;


	private final static int SEND_SUCCESS = 13454;
	private final static int SEND_FAILED = 13455;
	private final static int CHANGE_STATE = 13456;
	private final static int HIDE_PROGRESS_DIALOG = 15453;
	private final static int SHOW_KICK_OUT_DIALOG = 15454;
	private boolean mIsRegisterReceiver = false;
	private Login fCustomerVo;
	private boolean mHasLocalData = true;
	private int mType = 100;
	private static final int RESQUEST_CODE = 100;

	private Dialog mPhoneDialog;
	private int mIsOwner =0;
	private int mSendCard;
	private MessageInfo mCardMsg,mForMsg,mShareUrl;
	private int mIsShowSearchDialog,mFromPage; // fromage: 1=来自会话详情的查找聊天记录
	private String mSearchContent;
	private boolean isSendVideo=true;
	private Bbs bbs;
	private String bid;
	private ImageView qiehuan;
	private LinearLayout usermenuLayout;
	private boolean isqiehuan=false;
	private LocationClient locationClient;
	public static String lbs="";
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.chat_main);
		sendBroadcast(new Intent(DESTORY_ACTION));
		mSendCard = getIntent().getIntExtra("cardType", 0);
		mCardMsg = (MessageInfo)getIntent().getSerializableExtra("cardMsg");
		mShareUrl = (MessageInfo)getIntent().getSerializableExtra("shareUrlMsg");
		mForMsg = (MessageInfo)getIntent().getSerializableExtra("forMsg");
		mIsShowSearchDialog = getIntent().getIntExtra("is_show_dialog",0);
		mSearchContent = getIntent().getStringExtra("search_content");
		mFromPage = getIntent().getIntExtra("from_page", 0);
		bbs = (Bbs)getIntent().getSerializableExtra("bbs");
		if(bbs!=null){
			bid = bbs.id;
		}
		initComponent();
		locationClient = new LocationClient(mContext);
		//设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);        //是否打开GPS
		option.setCoorType("bd09ll");       //设置返回值的坐标类型。
		option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
		option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		option.setScanSpan(60*1000);    //设置定时定位的时间间隔。单位毫秒
		locationClient.setLocOption(option);
		//注册位置监听器
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				lbs= location.getLatitude()+","+location.getLongitude();
			}
		});
		locationClient.start();
		/*
		 *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
		 *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
		 *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
		 *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
		 *定时定位时，调用一次requestLocation，会定时监听到定位结果。
		 */
		locationClient.requestLocation();
	}


	/**
	 * 初始化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn, 0, "");
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		mBitmapCache = new HashMap<String, SoftReference<Bitmap>>();
		mLogin = IMCommon.getLoginResult(mContext);
		opconnectState = isOpconnect();
		//		broadcast = new MyBroadcast();
		registerReceiver();
		findViewById(R.id.ll_bar).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
		mChatBottmLayout = (RelativeLayout)findViewById(R.id.RelativeLayout1);
		mListView = (ListView) findViewById(R.id.chat_main_list_msg);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if(view.getFirstVisiblePosition() == 0){
						if(mHasLocalData){
							SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();

							MessageTable messageTable = new MessageTable(db);
							List<MessageInfo> tempList = messageTable.query(fCustomerVo.phone, messageInfos.get(0).auto_id, mType,bid);
							if(tempList == null || tempList.size() < 20){
								mHasLocalData = false;
							}

							if(tempList != null && tempList.size() != 0){
								messageInfos.addAll(0, tempList);
								mListView.setSelection(tempList.size());
								mAdapter.notifyDataSetChanged();

							}
						}

					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int beforeItem = firstVisibleItem - 4;
				if(beforeItem > 0){
					recycleBitmapCaches(0, beforeItem);
				}

				int endItem = firstVisibleItem + visibleItemCount + 4;
				if(endItem < totalItemCount){
					recycleBitmapCaches(endItem, totalItemCount);
				}
			}
		});

		mListView.setOnItemLongClickListener(this);
		mMsgSendBtn = (Button) findViewById(R.id.chat_box_btn_send);
		mMsgSendBtn.setText(mContext.getString(R.string.send));
		mMsgSendBtn.setOnClickListener(this);

		mContentEdit = (EditText) findViewById(R.id.chat_box_edit_keyword);
		mContentEdit.setOnClickListener(this);
		mContentEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text  = mContentEdit.getText().toString();
				if("".equals(text)){
					mAddBtn.setVisibility(View.VISIBLE);
					mMsgSendBtn.setVisibility(View.GONE);
				}else{
					mAddBtn.setVisibility(View.GONE);
					mMsgSendBtn.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {}
		});

		mToggleBtn = (ToggleButton) findViewById(R.id.chat_box_btn_info);
		mToggleBtn.setOnClickListener(this);

		mVoiceSendBtn = (Button) findViewById(R.id.chat_box_btn_voice);
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mVoiceSendBtn.setOnTouchListener(new OnVoice());


		mAddBtn = (ToggleButton) findViewById(R.id.chat_box_btn_add);
		mAddBtn.setOnClickListener(this);

		mChatExpraLayout = (View) findViewById(R.id.chat_box_layout_expra);

		mCameraBtn = (Button) findViewById(R.id.chat_box_expra_btn_camera);
		mCameraBtn.setOnClickListener(this);

		mGalleryBtn = (Button) findViewById(R.id.chat_box_expra_btn_picture);
		mGalleryBtn.setOnClickListener(this);

		mEmotionBtn = (ToggleButton) findViewById(R.id.chat_box_btn_emoji);
		mEmotionBtn.setOnClickListener(this);

		mLocationBtn = (Button) findViewById(R.id.chat_box_expra_btn_location);
		mLocationBtn.setOnClickListener(this);

		mCardBtn = (Button)findViewById(R.id.chat_box_expra_btn_card);
		mCardBtn.setOnClickListener(this);

		mFavoritebtn = (Button)findViewById(R.id.chat_box_expra_btn_favorite);
		mFavoritebtn.setOnClickListener(this);

		mVideobtn = (Button)findViewById(R.id.chat_box_expra_btn_video);
		mVideobtn.setOnClickListener(this);

		mRedPacketbtn = (Button)findViewById(R.id.chat_box_expra_btn_redpacket);
		mRedPacketbtn.setOnClickListener(this);


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




		mRootLayout = (ResizeLayout) findViewById(R.id.rootlayout);
		mRootLayout.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER; 
				if(mIsFirst){
					change = SMALLER;  
					mIsFirst = false;
				}
				if (h < oldh) {  
					change = SMALLER;  
				}                
				Message msg = new Message();
				msg.what = MSG_RESIZE;  
				msg.arg1 = change;  
				mHandler.sendMessage(msg); 
			}
		}); 
		mListLayout = (ResizeLayout) findViewById(R.id.listlayout);
		mListLayout.setOnResizeListener(new OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {

				int change = BIGGER; 
				if(mIsFirst){
					change = SMALLER;  
					mIsFirst = false;
				}
				if (h < oldh) {  
					change = SMALLER;  
				}                

				if(mListView.getLastVisiblePosition() == messageInfos.size() - 1){
					Message msg = new Message();
					msg.what = MSG_RESIZE;  
					msg.arg1 = change;  
					mHandler.sendMessage(msg); 
				}
			}
		});


		fCustomerVo = (Login)getIntent().getSerializableExtra("data");
		mType = fCustomerVo.mIsRoom;
		if(mType == GlobleType.SINGLE_CHAT || mType == GlobleType.BBS_CHAT){//单聊模式
			mRightBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.people_btn));
			mRightBtn.setVisibility(View.VISIBLE);
		}else if(mType == GlobleType.GROUP_CHAT){//群聊模式
			mRightBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chat_btn));
			mRightBtn.setVisibility(View.VISIBLE);
		}
		if(mFromPage == 1){
			mRightBtn.setVisibility(View.GONE);
			mChatBottmLayout.setVisibility(View.GONE);
		}else{
			mChatBottmLayout.setVisibility(View.VISIBLE);
		}

		titileTextView.setText(getFromName());

		clearNotification();

		audioRecorder = new AudioRecorderAction(getBaseContext());
		mReaderImpl = new ReaderImpl(ChatMainActivity.this, handler, audioRecorder){

			@Override
			public void stop(String path) {
				if(!isSendVideo){
					return;
				}
				if (TextUtils.isEmpty(path)) {
					showToast(mContext.getString(R.string.record_time_too_short));
					return;
				}

				if(audioRecorder.getRecordTime() > AudioRecorderAction.MAX_TIME){
					showToast(mContext.getString(R.string.record_time_too_long));
					return;
				}else if (audioRecorder.getRecordTime() < AudioRecorderAction.MIN_TIME) {
					showToast(mContext.getString(R.string.record_time_too_short));
					return;
				}
				File file = new File(path);
				if(file.exists()){
					sendFile(MessageType.VOICE, path,null);
				}else {
					Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
				}
			}

		};
		playListener = new AudioPlayListener(this){
			@Override
			public void down(MessageInfo msg) {
				super.down(msg);
				downVoice(msg, 1);
			}
		};
		initMessageInfos();
		mListView.setOnTouchListener(this);
		mListView.setOnItemLongClickListener(this);
		mVoiceSendBtn.setOnTouchListener(new OnVoice());
		if(!opconnectState){
			Toast.makeText(mContext, mContext.getString(R.string.connect_to_server), Toast.LENGTH_SHORT).show();
		}

		mContentEdit.setOnFocusChangeListener(sendTextFocusChangeListener);
		mContentEdit.setOnClickListener(sendTextClickListener);


		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		
		if(mSearchContent!=null && !mSearchContent.equals("")){//搜索内容需要定位
			checkRecordId();
		}else{
			mListView.setSelection(messageInfos.size() - 1);
		}
		if(messageInfos == null || messageInfos.size() < 20){
			mHasLocalData = false;
		}

		/**
		 * 发送名片
		 */
		if(mSendCard == 1 && mCardMsg!=null){
			createDialog(mContext,mContext.getResources().getString(R.string.confirm_send)
					+mCardMsg.cardOwerName+mContext.getResources().getString(R.string.from_card_to_chat));
		}
		/**
		 * 转发内容
		 */
		if(mForMsg!=null){
			sendBroad2Save(mForMsg,true);
		}
		/**
		 * 分享链接
		 */
		if(mShareUrl!=null){
			sendBroad2Save(mShareUrl,true);
		}

		/**
		 * 显示搜索对话框
		 */
		if(mIsShowSearchDialog == 1){
			MainSearchDialog searchDialog= new MainSearchDialog(mContext,1,new OnFinishClick() {

				@Override
				public void onFinishListener() {
					ChatMainActivity.this.finish();
				}
			},1,fCustomerVo.uid, mType);
			searchDialog.show();
		}
		qiehuan = (ImageView) findViewById(R.id.qiehuan);
		usermenuLayout = (LinearLayout) findViewById(R.id.usermenu);
		getUserMenu();
	}
	public void getUserMenu(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				UserMenuTable table = new UserMenuTable(db);
				Login tempLogin = table.query(fCustomerVo.phone);
				SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
				if(tempLogin==null){
					getUserMenus();
				}else if(dateFormat.format(new java.util.Date()).equals(tempLogin.userMenuTime)){
					fCustomerVo.mUserMenu = tempLogin.mUserMenu;
					showUserMenu();
				}else{
					getUserMenus();
				}
			}
		}).start();
	}
	public void getUserMenus(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				UserMenuTable table = new UserMenuTable(db);
				try {
					UserMenuList userMenuList = IMCommon.getIMInfo().getusermenu(fCustomerVo.phone);
					fCustomerVo.mUserMenu = userMenuList.mMenuList;
					fCustomerVo.mUserMenuStr = userMenuList.mMenuListStr;
					table.delete(fCustomerVo);
					table.insert(fCustomerVo);
					showUserMenu();
				} catch (IMException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	public void showUserMenu(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (fCustomerVo.mUserMenu!=null &&fCustomerVo.mUserMenu.size()!=0) {
					qiehuan.setVisibility(View.VISIBLE);
					usermenuLayout.setVisibility(LinearLayout.VISIBLE);
					mChatBottmLayout.setVisibility(View.GONE);
					LayoutParams imageViewlp = new LayoutParams(FeatureFunction.dip2px(mContext,1), LayoutParams.MATCH_PARENT);
					LinearLayout.LayoutParams imageViewButton = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, FeatureFunction.dip2px(mContext,48));
					imageViewButton.weight=1;
					for (final UserMenu userMenu : fCustomerVo.mUserMenu) {
						ImageView spIV = new ImageView(mContext);
						spIV.setLayoutParams(imageViewlp);
						spIV.setBackgroundColor(getResources().getColor(R.color.listview_foucs));
						Button b = new Button(mContext);
						b.setBackground(null);
						b.setLayoutParams(imageViewButton);
						b.setText(userMenu.menuname);
						b.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								String http = "http://";
								if(userMenu.menuurl.contains(http)) {
									intent.putExtra("url", userMenu.menuurl);
								}else{
									intent.putExtra("url", http+userMenu.menuurl);
								}
								intent.setClass(mContext, WebViewActivity.class);
								startActivity(intent);
							}
						});
						usermenuLayout.addView(spIV);
						usermenuLayout.addView(b);
					}
					qiehuan.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if(isqiehuan){
								isqiehuan=false;
								qiehuan.setImageDrawable(getResources().getDrawable(R.drawable.keyboard_u_btn));
								usermenuLayout.setVisibility(View.VISIBLE);
								mChatBottmLayout.setVisibility(View.GONE);
							}else{
								isqiehuan=true;
								qiehuan.setImageDrawable(getResources().getDrawable(R.drawable.keyboard_btn));
								usermenuLayout.setVisibility(View.GONE);
								mChatBottmLayout.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			}
		});
	}

	private String getFromName(){
		String nickname = fCustomerVo.remark;
		if(nickname == null || nickname.equals("")){
			nickname = fCustomerVo.nickname;
		}
		return nickname;
	}


	/**
	 * 创建发送名片对话框
	 * @param context
	 * @param cardTitle
	 */
	protected void createDialog(Context context, String cardTitle) {
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
		okBtn.setText(mContext.getResources().getString(R.string.ok));
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPhoneDialog!=null) {
					mPhoneDialog.dismiss();
					mPhoneDialog=null;
				}
				if(mCardMsg!=null){
					sendBroad2Save(mCardMsg,false);
				}

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



	/**
	 * 释放图片
	 * @param start
	 * @param end
	 */
	private void recycleBitmapCaches(int start, int end){                
		if(mAdapter != null){
			HashMap<String, Bitmap> buffer = mImageLoader.getImageBuffer();
			for(int i = start; i < end; i++){
				if(messageInfos.get(i).typefile == MessageType.PICTURE){
					String url = messageInfos.get(i).imgUrlS;
					ImageView imageView = (ImageView)mListView.findViewWithTag(url);
					if (imageView != null) {
						imageView.setImageBitmap(null);
						imageView.setImageResource(R.drawable.normal);
					}

					if(url.startsWith("http://") && 1 == messageInfos.get(i).getSendState()){
						Bitmap bitmap = buffer.get(url);
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
							buffer.remove(url);
						}
					}else {
						if(mBitmapCache!=null 
								&& mBitmapCache.get(url)!=null){
							Bitmap bitmap = mBitmapCache.get(url).get();
							if (bitmap != null && !bitmap.isRecycled()) {
								bitmap.recycle();
								bitmap = null;
								mBitmapCache.remove(url);
							}
						}

					}
				}
			}

		}
	}


	/**
	 * 创建销毁对话框
	 * @param title
	 */
	private void destoryDialog(String title){
		AlertDialog builder = new AlertDialog.Builder(this).create();
		builder.setTitle(title);
		builder.setButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ChatMainActivity.this.finish();
			}
		});
		builder.setCancelable(false);
		builder.setCanceledOnTouchOutside(false);
		builder.show();
	}

	/**
	 * 隐藏底部控件
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){

			if(mChatExpraLayout.getVisibility() == View.VISIBLE || mEmotionLayout.getVisibility() == View.VISIBLE){
				hideEmojiGridView();
			}

			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
				InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}  
		return super.onTouchEvent(event);  
	}

	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE:
				if(msg.arg1 != BIGGER){
					if((mSearchContent==null || mSearchContent.equals("")) && messageInfos != null && messageInfos.size() != 0){
						mListView.setSelection(messageInfos.size() - 1);
					}
				}
				break;
			case MSG_REFUSH:
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
				break;
			case SEND_SUCCESS:
				MessageInfo messageInfo = (MessageInfo) msg.obj;
				int isResend = msg.arg1;
				updateNewMessage(messageInfo);
				modifyMessageState(messageInfo);
				if(msg.arg2==4){
					//Toast.makeText(mContext, R.string.refuest_message, Toast.LENGTH_LONG).show();
				}
				break;
			case CHANGE_STATE:
				MessageInfo messageSend = (MessageInfo) msg.obj;
				updateMessage(messageSend);
				break;
			case SEND_FAILED:
				MessageInfo message = (MessageInfo) msg.obj;
				updateMessage(message);
				modifyMessageState(message);
				break;
			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				break;
			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String)msg.obj;
				showProgressDialog(dialogMsg);
				mProgressDialog.setCancelable(false);
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				hideProgressDialog();
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				hideProgressDialog();
				String prompt=(String)msg.obj;
				if (prompt==null || prompt.equals("")) {
					prompt=mContext.getString(R.string.timeout);
				}
				Toast.makeText(mContext,prompt, Toast.LENGTH_LONG).show();
				break;

			case HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				break;

			case SHOW_KICK_OUT_DIALOG:
				destoryDialog(mContext.getString(R.string.you_have_been_removed_from_group));
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

			default:
				break;
			}
		}

	};

	/**
	 * 拍一张照片
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String tmpUrl = FeatureFunction.getPhotoFileName(1);
		if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY,
					tmpUrl);//TEMP_FILE_NAME /*FeatureFunction.getPhotoFileName()*/);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	/**
	 * 从相册中选取图片
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		startActivityForResult(intent, REQUEST_GET_URI);
	}


	/**
	 * 处理图片
	 * @param isGallery
	 * @param data
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery||data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it
				String tempUrl = IMCommon.getCamerUrl(mContext);
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+tempUrl;
				Log.e("path", "path:"+path);
				if(tempUrl == null || tempUrl.equals("")){
					return;
				}
				Log.e("start-end", path.indexOf(".")+":"+path.length());
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
					//startPhotoZoom(Uri.fromFile(new File(path)));
					Intent intent = new Intent(mContext, RotateImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, REQUEST_GET_BITMAP);
				}
		}
	}

	private void originalImage(Intent data) {
		Uri uri = data.getData();
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri,
					new String[] { MediaStore.Images.Media.DATA }, null, null,
					null);
			if (null == cursor) {
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);
			}
		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);
			}
		}
	}


	/**
	 * 页面返回结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESQUEST_CODE:
			if(data != null && RESULT_OK == resultCode){
				Bundle bundle = data.getExtras();
				if(bundle != null){
					MapInfo mapInfo = (MapInfo)data.getSerializableExtra("mapInfo");
					if(mapInfo == null){
						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
						return;
					}
					sendMap(mapInfo);
				}
			}
			break;

		case REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}

			break;

		case REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
				doChoose(false, data);
			}
			break;

		case REQUEST_GET_BITMAP:
			if(resultCode == RESULT_OK){
				mFilePath = data.getStringExtra("path");
				int width = data.getIntExtra("width",0);
				int height = data.getIntExtra("height",0);
				if(width != 0 && height != 0 ){
					int[] scale = FeatureFunction.getScalcSize(width,height);
					if(scale[0] !=0 && scale[1] != 0){
						mScalcWith = scale[0];
						mScalcHeigth = scale[1];
						Log.e("width - height", mScalcWith+":"+mScalcHeigth);
					}

				}
				if(mFilePath != null && !mFilePath.equals("")){
					sendFile(MessageType.PICTURE, mFilePath,null);
				}
			}

			break;
			case REQUEST_GET_REDPACKET:
				if(data==null){
					return;
				}
				String url = data.getStringExtra("url");
				String title = data.getStringExtra("title");
				if(url==null){
					Toast.makeText(mContext,"红包发送失败", Toast.LENGTH_LONG).show();
					return;
				}
				if(title==null){
					title="恭喜发财,大吉大利";
				}
				MessageInfo msg = new MessageInfo();
				msg.fromid = IMCommon.getUserPhone(mContext);
				msg.tag = UUID.randomUUID().toString();
				msg.fromname = mLogin.nickname;
				msg.fromurl = mLogin.headsmall;
				msg.toid = fCustomerVo.phone;
				msg.toname = getFromName();
				msg.tourl = fCustomerVo.headsmall;
				msg.typefile = MessageType.REDPACKET;
				msg.content = "[红包]"+title;
				msg.redpacketTitle =title;
				msg.redpacketUrl = url;
				msg.typechat = mType;
				msg.time = System.currentTimeMillis();
				msg.readState = 1;
				msg.setSendState(1);
				addMessageInfo(msg);
				break;
			case RECORD_VIDEO:
                if(data==null){
                    return;
                }
                String path = data.getStringExtra("path");
                String time = data.getStringExtra("time");
                File file = new File(path);
                if(file.exists()){
                    sendFile(MessageType.VIDEO, path,time);
                }else {
                    Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
                }
				break;
			default:
			break;
		}
	}
	// 删除在/sdcard/dcim/Camera/默认生成的文件
	private void deleteDefaultFile(Uri uri) {
		String fileName = null;
		if (uri != null) {
			// content
			Log.e("Scheme", uri.getScheme().toString());
			if (uri.getScheme().toString().equals("content")) {
				Cursor cursor = this.getContentResolver().query(uri, null,
						null, null, null);
				if (cursor.moveToNext()) {
					int columnIndex = cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
					fileName = cursor.getString(columnIndex);
					//获取缩略图id
					int id = cursor.getInt(cursor
							.getColumnIndex(MediaStore.Video.VideoColumns._ID));
					//获取缩略图
					bitmap = MediaStore.Video.Thumbnails.getThumbnail(
							getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND,
							null);
					if (!fileName.startsWith("/mnt")) {
						fileName = "/mnt/" + fileName;
					}
					Log.e("fileName", fileName);
				}
			}
		}
		// 删除文件
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
			Log.d("delete", "删除成功");
		}
	}

	/**
	 * 获取本地聊天内容
	 */
	private void initMessageInfos() {
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable messageTable = new MessageTable(db);
		boolean status = messageTable.updateReadState(fCustomerVo.phone, mType,bid);
		if(status){
			mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
			if(mType == GlobleType.MEETING_CHAT){
				mContext.sendBroadcast(new Intent(MettingDetailActivity.ACTION_HIDE_NEW_MEETING_TIP));
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MEETING_LIST));
			}
		}
		messageInfos = messageTable.query(fCustomerVo.phone, -1, mType,bid);
		if(messageInfos == null){
			messageInfos = new ArrayList<MessageInfo>();
		}else {
			for (int i = 0; i < messageInfos.size(); i++) {
				if(messageInfos.get(i).readState == 0){
					messageInfos.get(i).readState = 1;
					updateMessage(messageInfos.get(i));
				}else if(messageInfos.get(i).sendState == 2){
					messageInfos.get(i).sendState = 0;
					updateMessage(messageInfos.get(i));
				}
			}
		}

	}


	/**
	 * 获取定位记录的下标
	 */
	private void checkRecordId(){
		int checkId = -1;
		if(mSearchContent !=null  && !mSearchContent.equals("")){
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
			MessageTable messageTable = new MessageTable(db);
			boolean isExits = true;
			for (int i = 0; i < messageInfos.size(); i++) {
				if((messageInfos.get(i).content!=null && !messageInfos.get(i).content.equals(""))
						&& !messageInfos.get(i).content.equals(mSearchContent)){
					isExits = false;
				}else if(messageInfos.get(i).content == null || messageInfos.get(i).content.equals("")){
					isExits = false;
				}else{
					checkId = i;
					isExits = true;
					break;
				}
				if(i == messageInfos.size() && !isExits){
					isExits = false;
				}
			}
			if(!isExits){
				List<MessageInfo> tempList = messageTable.query(fCustomerVo.uid, messageInfos.get(0).auto_id, mType,bid);
				if(tempList == null || tempList.size() < 20){
					mHasLocalData = false;
				}

				if(tempList != null && tempList.size() != 0){
					messageInfos.addAll(0, tempList);
					checkRecordId();
				}

			}
			if(checkId!=-1){
				mListView.setSelection(checkId);
			}
			mAdapter.notifyDataSetChanged();


		}
	}

	/**
	 * 发送文本
	 */
	private void sendText() {
		Log.d(TAG, "sendText()");
		String str = mContentEdit.getText().toString();
		if (str != null
				&& (str.trim().replaceAll("\r", "")
						.replaceAll("\t", "").replaceAll("\n", "")
						.replaceAll("\f", "")) != "") {
			if(str.length() > IMCommon.MESSAGE_CONTENT_LEN){
				//showToast(mContext.getString(R.string.message_limit_count));
				return;
			}
			mContentEdit.setText("");

			MessageInfo msg = new MessageInfo();
			msg.fromid = IMCommon.getUserPhone(mContext);
			msg.tag = UUID.randomUUID().toString();
			msg.fromname = mLogin.nickname;
			msg.fromurl = mLogin.headsmall;
			msg.toid = fCustomerVo.phone;
			msg.toname = getFromName();
			msg.tourl = fCustomerVo.headsmall;
			msg.typefile = MessageType.TEXT;
			msg.content = str;
			msg.typechat = mType;
			msg.time = System.currentTimeMillis();
			msg.readState = 1;
			msg.bid = bid;
			sendBroad2Save(msg,false);
		}
	}

	/**
	 * 发送地图
	 * @param mapInfo
	 */
	private void sendMap(MapInfo mapInfo){
		MessageInfo msg = new MessageInfo();
		msg.fromid = IMCommon.getUserPhone(mContext);
		msg.tag = UUID.randomUUID().toString();
		msg.fromname = mLogin.nickname;
		msg.fromurl = mLogin.headsmall;
		msg.toid = fCustomerVo.phone;
		msg.toname = getFromName();
		msg.tourl = fCustomerVo.headsmall;
		msg.typefile = MessageType.MAP;
		msg.mLat = Double.parseDouble(mapInfo.getLat());
		msg.mLng = Double.parseDouble(mapInfo.getLng());
		msg.mAddress = mapInfo.getAddr();
		msg.typechat = mType;
		msg.time = System.currentTimeMillis();
		msg.readState = 1;
		msg.bid = bid;
		sendBroad2Save(msg,false);
	}

	/**
	 * 发送声音和图片
	 * @param type
	 * @param filePath
	 */
	private void sendFile(int type, String filePath, String videotime){
		MessageInfo msg = new MessageInfo();
		msg.fromid = IMCommon.getUserPhone(mContext);
		msg.tag = UUID.randomUUID().toString();
		msg.fromname = mLogin.nickname;
		msg.fromurl = mLogin.headsmall;
		msg.toid = fCustomerVo.phone;
		msg.toname = getFromName();
		msg.tourl = fCustomerVo.headsmall;
		msg.imgWidth = mScalcWith;
		msg.imgHeight = mScalcHeigth;
		if(type == MessageType.VOICE){
			msg.voiceUrl = filePath;
			msg.setVoiceTime((int)mReaderImpl.getReaderTime());
		}else if(type == MessageType.PICTURE){
			msg.imgUrlS = filePath;
		}else if(type == MessageType.VIDEO){
			msg.content = Video.getInfo(new Video(filePath,videotime, ""));
        }
		msg.typefile = type;
		msg.typechat = mType;
		msg.time = System.currentTimeMillis();
		msg.readState = 1;
		addMessageInfo(msg);
		sendFilePath(msg, 0);

	}

	private void sendFilePath(MessageInfo messageInfo, int isResend){
		sendMessage(messageInfo, isResend,false);
	}

	/**
	 * 重发文件
	 * @param messageInfo
	 */
	private void resendFile(MessageInfo messageInfo){
		try {
			sendFilePath(messageInfo, 1);
		} catch (Exception e) {
			Log.d(TAG, "resendVoice:", e);
			showToast(mContext.getString(R.string.resend_failed));
		}
	}

	// 下载音频
	private synchronized void downVoice(final MessageInfo msg, final int type){
		if(!FeatureFunction.checkSDCard()){
			return;
		}

		if(downVoiceList.contains(msg.voiceUrl)){
			//showToast(mContext.getString(R.string.download_voice));
			return;
		}
		downVoiceList.add(msg.voiceUrl);
		File voicePath = ReaderImpl.getAudioPath(getBaseContext());
		String tag = FeatureFunction.generator(msg.voiceUrl);
		String tagName = new File(voicePath, tag).getAbsolutePath();
		HttpGet get = new HttpGet(msg.voiceUrl);
		DefaultHttpClient client = Utility.getHttpClient(mContext);


		VoiceTask<File> voiceTask = new VoiceTask<File>(client, new SyncBasicHttpContext(new BasicHttpContext()), new AjaxCallBack<File>() {
			@Override
			public void onSuccess(File t) {
				super.onSuccess(t);
				downVoiceSuccess(msg, type);
				downVoiceList.remove(msg.voiceUrl);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				showToast(mContext.getString(R.string.download_voice_error) + strMsg);
				downVoiceList.remove(msg.voiceUrl);
			}
		});

		Executor executor = Executors.newFixedThreadPool(5, new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);
			@Override
			public Thread newThread(Runnable r) {
				Thread tread = new Thread(r, "FinalHttp #" + mCount.getAndIncrement());
				tread.setPriority(Thread.NORM_PRIORITY - 1);
				return tread;
			}
		});
		voiceTask.executeOnExecutor(executor, get, tagName);
	}

	private void downVoiceSuccess(final MessageInfo msg, final int type){
		msg.setSendState(1);
		SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable messageTable = new MessageTable(dbDatabase);
		messageTable.update(msg);
		if(type == 1 && playListener.getMessageTag().equals(msg.tag)){
			playListener.play(msg);
		}
	}

	/**
	 * 删除临时图片
	 * @param path
	 */
	private void deleteImgFile(String path){
		File file = new File(path);
		if(file != null && file.exists()){
			file.delete();
		}
	}

	/*********  聊天选择器 ***********/

	private void togInfoSelect(){
		hideExpra();
		if (mToggleBtn.isChecked()) {
			// 语音开启显示
			mContentEdit.setVisibility(View.GONE);
			mVoiceSendBtn.setVisibility(View.VISIBLE);
			mEmotionBtn.setChecked(false);
			btnEmojiAction();
			hideSoftKeyboard(mContentEdit);
		} else {
			mContentEdit.setVisibility(View.VISIBLE);
			mVoiceSendBtn.setVisibility(View.GONE);
			hideSoftKeyboard(mContentEdit);
		}
	}

	/* 重发信息 */
	private void btnResendAction(MessageInfo messageInfo){
		if(messageDialog != null && messageDialog.isShowing()){
			messageDialog.cancel();
		}
		if(messageInfo != null){
			switch (messageInfo.typefile) {
			case MessageType.PICTURE:
			case MessageType.VOICE:
				resendFile(messageInfo);
				break;
			case MessageType.TEXT:
				sendMessage(messageInfo, 1,false);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 显示表情
	 */
	private void btnEmojiAction(){
		if(mEmotionBtn.isChecked()){
			showEmojiGridView();
		}else{
			hideEmojiGridView();
		}

	}

	/*
	 * 显示照相机
	 */
	private void btnCameraAction(){
		getImageFromCamera();
		hideExpra();
	}

	/*
	 * 选择图片
	 */
	private void btnPhotoAction(){
		getImageFromGallery();
		hideExpra();
	}

	/*
	 * 发送地图
	 */
	private void btnLocationAction(){
		hideExpra();
		Intent intent = new Intent(this, LocationActivity.class);
		startActivityForResult(intent, RESQUEST_CODE);
	}

	/*
	 * 处理+号事件
	 */
	private void btnAddAction(){
		mEmotionBtn.setChecked(false);
		if(mChatExpraLayout.getVisibility() == View.VISIBLE){
			hideExpra();
			showSoftKeyboard();
			mToggleBtn.setChecked(false);
			mContentEdit.setVisibility(View.VISIBLE);
			mVoiceSendBtn.setVisibility(View.GONE);
		}else{
			if(mEmotionLayout.getVisibility() == View.VISIBLE){
				hideEmojiGridView();
			}
			showExpra();
		}
	}

	/*
	 * 隐藏键盘显示聊天选择器
	 */
	private void showExpra(){
		hideSoftKeyboard();
		mChatExpraLayout.setVisibility(View.VISIBLE);
	}

	/*
	 * 隐藏聊天选择器
	 */
	private void hideExpra(){
		mChatExpraLayout.setVisibility(View.GONE);
		mAddBtn.setChecked(false);
	}

	/*
	 * 键盘返回事件
	 * (non-Javadoc)
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
				hideSoftKeyboard();
				if(mChatExpraLayout.getVisibility() == View.VISIBLE || mEmotionLayout.getVisibility() == View.VISIBLE){
					hideEmojiGridView();
					//hideExpra();
					return true;
				}
			}else{
				if(mIsShowSearchDialog == 1){
					ChatMainActivity.this.finish();
					return true;
				}
			}

		}
		return super.dispatchKeyEvent(event);
	}

	/*
	 * 消息发送成功
	 */
	private void sendBroad2Save(MessageInfo msg, boolean isForward){
		addMessageInfo(msg);
		msg.setSendState(1);
		sendMessage(msg, 0,isForward);
	}

	/*
	 * 发送消息
	 */
	private void sendMessage(final MessageInfo msg, final int isResend, final boolean isForward){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					msg.sendState = 2;
					Message stateMessage = new Message();
					stateMessage.obj= msg;
					stateMessage.what = CHANGE_STATE;
					mHandler.sendMessage(stateMessage);
					try {
						MessageResult result = getIMInfo().sendMessage(msg,isForward);
						if(result != null && result.mState != null &&
								(result.mState.code == 0 || result.mState.code == 4)){
							result.mMessageInfo.sendState = 1;
							if(msg.typefile == MessageType.VOICE){
								String voice = FeatureFunction.generator(result.mMessageInfo.voiceUrl);
								FeatureFunction.reNameFile(new File(msg.voiceUrl), voice);
							}
							result.mMessageInfo.readState = 1;
							Message message = new Message();
							message.what = SEND_SUCCESS;
							message.arg1 = isResend;
							if(result.mState.code == 4){
								message.arg2 = 4;
							}
							message.obj = result.mMessageInfo;
							mHandler.sendMessage(message);
							return;
						}else if(result != null && result.mState != null && result.mState.code == 3){
							SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
							SessionTable sessionTable = new SessionTable(db);
							MessageTable messageTable = new MessageTable(db);
							Session session = sessionTable.query(fCustomerVo.phone, 300);
							if(session != null){
								messageTable.delete(fCustomerVo.uid, 300);
								sessionTable.delete(fCustomerVo.phone, 300);

								mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
							}
							mHandler.sendEmptyMessage(SHOW_KICK_OUT_DIALOG);
							return;
						}
					} catch (IMException e) {
						e.printStackTrace();
					}

				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}

				msg.sendState = 0;
				Message message = new Message();
				message.what = SEND_FAILED;
				message.arg1 = isResend;
				message.obj = msg;
				mHandler.sendMessage(message);
			}
		}.start();
	}

	/**
	 * op是否连接
	 * @return
	 * 作者:fighter <br />
	 * 创建时间:2013-6-9<br />
	 * 修改时间:<br />
	 */
	private boolean isOpconnect(){
		return true;
	}

	/*
	 * 将消息显示在界面中
	 */
	private void addMessageInfo(MessageInfo info){

		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		if(messageInfos.size() == 0){
			messageInfos.add(info);
		}else {
			boolean isEixst = false;
			for (int i = 0; i < messageInfos.size(); i++) {
				if(messageInfos.get(i).tag.equals(info.tag)){
					isEixst = true;
					break;
				}
			}
			if(!isEixst){
				messageInfos.add(info);
			}
		}
		mAdapter.notifyDataSetInvalidated();
		if(messageInfos !=  null && messageInfos.size() != 0){
			mListView.setSelection(messageInfos.size() - 1);
		}
		//clearNotification();
		insertMessage(info);
		Session session = new Session();
		session.setFromId(fCustomerVo.phone);
		session.name = getFromName();
		session.heading = fCustomerVo.headsmall;
		session.type = mType;
		session.lastMessageTime =info.time;
		session.bid=bid;
		insertSession(session);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mVoiceSendBtn.setBackgroundResource(R.drawable.login_btn_n);
		if(!mReaderImpl.mIsStop){
			mReaderImpl.cancelDg();
		}else {
			mReaderImpl.mIsStop = false;
		}

	}

	/*
	 * 页面销毁
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy(){
		super.onDestroy();
		IMCommon.saveCamerUrl(mContext,"");

		//updateSessionList();
		if(mIsRegisterReceiver){
			unregisterReceiver();
		}

		if(mReaderImpl != null){
			mReaderImpl.unregisterRecordReceiver();
		}
		mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
		playListener.stop();

		/*++释放图片++*/
		if(messageInfos != null){
			for (int i = 0; i < messageInfos.size(); i++) {

				if(!TextUtils.isEmpty(messageInfos.get(i).fromurl)){
					ImageView headerView = (ImageView) mListView.findViewWithTag(messageInfos.get(i).fromurl);
					if(headerView != null){
						headerView.setImageBitmap(null);
						headerView.setImageResource(R.drawable.contact_default_header);
					}
				}

				if(messageInfos.get(i).typefile == MessageType.PICTURE){
					ImageView imageView = (ImageView) mListView.findViewWithTag(messageInfos.get(i).content);
					if(imageView != null){
						imageView.setImageBitmap(null);
						imageView.setVisibility(View.GONE);
					}

					if(!messageInfos.get(i).imgUrlS.startsWith("http://") && messageInfos.get(i).sendState != 1){
						mBitmapCache.remove(messageInfos.get(i).imgUrlS);
					}
				}
			}
		}

		Set<String> keys = mBitmapCache.keySet();
		if(keys != null && !keys.isEmpty()){
			for (String key : keys) {
				deleteImgFile(key);
			}
		}

		FeatureFunction.freeBitmap(mImageLoader.getImageBuffer());
		freeBitmap(mBitmapCache);
		System.gc();
		/*--释放图片--*/
	}

	/*
	 * 清空图片缓存
	 */
	private void freeBitmap(HashMap<String, SoftReference<Bitmap>> cache){
		if(cache == null || cache.isEmpty()){
			return;
		}
		for(SoftReference<Bitmap> bitmap:cache.values()){
			if(bitmap.get() != null && !bitmap.get().isRecycled()){
				bitmap.get().recycle();
				bitmap = null;

			}
		}
		cache.clear();
	}

	/*
	 * 注册通知
	 */
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(SnsService.ACTION_CONNECT_CHANGE);
		filter.addAction(PushChatMessage.ACTION_SEND_STATE);
		filter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		filter.addAction(NotifyChatMessage.ACTION_CHANGE_VOICE_CONTENT);
		filter.addAction(DESTORY_ACTION);
		filter.addAction(REFRESH_ADAPTER);
		filter.addAction(ACTION_READ_VOICE_STATE);
		filter.addAction(ACTION_CHANGE_FRIEND);
		filter.addAction(ACTION_RECORD_AUTH);
		filter.addAction(ACTION_DESTROY_ROOM);
		filter.addAction(GlobalParam.BE_KICKED_ACTION);
		filter.addAction(ACTION_RECOMMEND_CARD);
		filter.addAction(ACTION_SHOW_NICKNAME);
		filter.addAction(GlobalParam.ACTION_RESET_GROUP_NAME);
		registerReceiver(chatReceiver, filter);
		mIsRegisterReceiver = true;
	}

	/*
	 * 销毁通知
	 */
	private void unregisterReceiver(){
		unregisterReceiver(chatReceiver);
	}

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
							Editable dEditable = mContentEdit.getEditableText();
							int index = mContentEdit.getSelectionStart();
							dEditable.insert(index, spannableString);
						}
					}
				}else {
					int index = mContentEdit.getSelectionStart();

					String text = mContentEdit.getText().toString();
					if (index > 0) {
						String text2 = text.substring(index - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = index;
							mContentEdit.getText().delete(start, end);
							return;
						}
						mContentEdit.getText().delete(index - 1, index);
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

	/*
	 * 销毁通知栏的通知列表
	 */
	void clearNotification(){
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}
	/**
	 * 语音按钮触发
	 */
	class OnVoice implements OnTouchListener {
		// 说话键按下和弹起处理事件
		private float DownY;
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mVoiceSendBtn.setBackgroundResource(R.drawable.login_btn_d);
				if(!FeatureFunction.checkSDCard()){
					break;
				}
				mReaderImpl.showDg();
				DownY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float moveY = Math.abs(event.getY()-DownY);
				mVoiceSendBtn.setBackgroundResource(R.drawable.login_btn_n);
				if(!mReaderImpl.mIsStop){
					mReaderImpl.cancelDg();
				}else {
					mReaderImpl.mIsStop = false;
				}
				if(moveY>100){
					isSendVideo=false;
					return false;
				}
				isSendVideo =true;
				break;
				case MotionEvent.ACTION_MOVE:
					float move2Y = Math.abs(event.getY()-DownY);
					if(move2Y>100){
						mReaderImpl.cancelDg(true);
					}else{
						mReaderImpl.cancelDg(false);
					}
					return false;
			}
			return true;
		}

	}

	/*
	 * 消息适配器
	 */
	class MyAdapter extends BaseAdapter {
		private final LayoutInflater mInflater;
		HashMap<String, View> hashMap;
		private boolean mIsShowNickName;

		public MyAdapter(){
			mInflater = (LayoutInflater)mContext.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			hashMap= new HashMap<String, View>();
			if(mType == GlobleType.GROUP_CHAT){//单聊模式
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				RoomTable roomTable = new RoomTable(db);
				Room room = roomTable.query(fCustomerVo.uid);
				if(room!=null){
					mIsShowNickName = room.isShowNickname == 1?true:false;
				}
			}


		}

		@Override
		public int getCount() {
			return messageInfos.size();
		}

		@Override
		public MessageInfo getItem(int arg0) {
			return messageInfos.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setIsShowNickName(boolean isShow){
			this.mIsShowNickName =isShow;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			int type = 0;
			final MessageInfo messageInfo = messageInfos.get(position);
			if(messageInfo.getFromId().equals(mLogin.phone)){
				type = 0;
			}else{
				type = 1;
			}
			ViewHolder viewHolder = null;
			convertView = hashMap.get(messageInfo.tag);
			if(convertView == null){
				if(1 == type){
					convertView = mInflater.inflate(R.layout.chat_talk_left, null);
				}else{
					convertView = mInflater.inflate(R.layout.chat_talk_right, null);
				}
				viewHolder = ViewHolder.getInstance(convertView);
				convertView.setTag(viewHolder);
				hashMap.put(messageInfo.tag, convertView);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imgMsgPhoto.setImageBitmap(null);
			viewHolder.imgMsgPhoto.setImageResource(R.drawable.normal);
			if(messageInfo.typefile == MessageType.VOICE  && 1 == messageInfo.getSendState()){
				//声音
				setOnLongClick(viewHolder.mRootLayout, position,0,null,messageInfo);
			}else{
				if(1 == messageInfo.getSendState()){
					//文本
					setOnLongClick(viewHolder.txtMsg, position, 1, messageInfo.getContent(), messageInfo);
					//图片
					setOnLongClick(viewHolder.imgMsgPhoto, position,0,null,messageInfo);
					//地图
					setOnLongClick(viewHolder.mapLayout,position,0,null,messageInfo);
					setOnLongClick(viewHolder.cardLayout,position,0,null,messageInfo);
					setOnLongClick(viewHolder.urlLayout,position,0,null,messageInfo);
					//视频
					setOnLongClick(viewHolder.videolayout,position,0,null,messageInfo);
					setOnLongClick(viewHolder.playImageView,position,0,null,messageInfo);
				}

			}


			if(position >0){
				bindView(viewHolder, messageInfo,messageInfos.get(position-1).time);
			}else{
				bindView(viewHolder, messageInfo,0);
			}

			return convertView;
		}

		private void setOnLongClick(View v, final int position, final int type,
                                    final String content, final MessageInfo msg){
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					//showPromptDialog(position);
					showPromptDialog(position, type, content, msg,v);
					return true;
				}
			});
		}

		private void bindView(ViewHolder viewHolder, final MessageInfo messageInfo,
				final long lasttime){
			final int type = messageInfo.getFromId().equals(mLogin.phone) ? 0 : 1;
			if(0 == type){
				viewHolder.nickName .setVisibility(View.GONE);
				if(0 == messageInfo.getSendState()){
				}else{
					viewHolder.imgSendState.setVisibility(View.GONE);
				}

				viewHolder.imgSendState.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showResendDialog(messageInfo);
					}
				});
			}else{
				viewHolder.imgVoiceReadState.setVisibility(View.GONE);
				if(mIsShowNickName){
					viewHolder.nickName .setVisibility(View.VISIBLE);
					viewHolder.nickName.setText(messageInfo.fromname);
				}else{
					viewHolder.nickName .setVisibility(View.GONE);
				}

			}

			viewHolder.imgHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, UserInfoActivity.class);
					intent.putExtra("username", messageInfo.fromid);
					intent.putExtra("type", 2);
					mContext.startActivity(intent);
				}
			});

			if(!TextUtils.isEmpty(messageInfo.fromurl)){
				viewHolder.imgHead.setTag(messageInfo.fromurl);
				//viewHolder.imgHead.setOnClickListener(showInfo);
				mImageLoader.getBitmap(mContext, viewHolder.imgHead, null, messageInfo.fromurl, 0, false, false);
			}

			//viewHolder.txtTime.setVisibility(View.GONE);
			String time = FeatureFunction.calculaterReleasedTime(mContext,
					new Date(messageInfo.time),messageInfo.time,lasttime);
			if(time == null || time.equals("")){
				viewHolder.txtTime.setVisibility(View.GONE);
			}else{
				viewHolder.txtTime.setVisibility(View.VISIBLE);
				viewHolder.txtTime.setText(time);
			}

			if(messageInfo.typefile == MessageType.VOICE){
				int length = messageInfo.getVoiceTime();
				float max = mContext.getResources().getDimension(R.dimen.voice_max_length);
				float min = mContext.getResources().getDimension(R.dimen.voice_min_length);
				int width = (int)min;
				if(length>= MIN_SECOND && length <= MAX_SECOND){
					width +=  (length - MIN_SECOND) * (int)((max - min) / (MAX_SECOND - MIN_SECOND));
				}else if(length > MAX_SECOND){
					width = (int)max;
				}

				RelativeLayout.LayoutParams timeParams = (RelativeLayout.LayoutParams)viewHolder.txtVoiceNum.getLayoutParams();
				//timeParams.addRule(RelativeLayout.CENTER_VERTICAL);
				RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width, FeatureFunction.dip2px(mContext, 48));
				if(type == 0){
					param.addRule(RelativeLayout.LEFT_OF, viewHolder.imgHead.getId());
					param.setMargins(0, FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5));
				}else {
					param.addRule(RelativeLayout.RIGHT_OF, viewHolder.imgHead.getId());
					if(mIsShowNickName){
						param.setMargins(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 25), 0, FeatureFunction.dip2px(mContext, 5));
					}else{
						param.setMargins(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5), 0, FeatureFunction.dip2px(mContext, 5));
					}

				}
				param.addRule(RelativeLayout.BELOW, viewHolder.txtTime.getId());
				//viewHolder.txtVoiceNum.setLayoutParams(timeParams);
				viewHolder.mRootLayout.setLayoutParams(param);

			}else {
				int padding = FeatureFunction.dip2px(mContext, 5);
				if(type == 0){
					viewHolder.mParams.setMargins(0, padding, FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5));
				}else {
					viewHolder.mParams.setMargins(FeatureFunction.dip2px(mContext, 5), padding, 0, FeatureFunction.dip2px(mContext, 5));
				}
				viewHolder.mRootLayout.setLayoutParams(viewHolder.mParams);
			}

			switch (messageInfo.typefile) {
			case MessageType.TEXT:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if(viewHolder.wiatProgressBar != null){
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.VISIBLE);
				SpannableString ss = EmojiUtil.getExpressionString(getBaseContext(), messageInfo.getContent(), EMOJIREX);
				//判断是否有链接
				String patternStr = "(http://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5])|((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
				Pattern pattern = Pattern.compile(patternStr);
				Matcher m = pattern.matcher(messageInfo.getContent());
				while(m.find()){
					final String urlStr = m.group();
					int start=messageInfo.getContent().indexOf(urlStr),end=start+urlStr.length();
					ss.setSpan(new ClickableSpan() {
						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(Color.BLUE);       //设置文件颜色
							ds.setUnderlineText(true);      //设置下划线
						}
						@Override
						public void onClick(View widget) {
							Intent intent = new Intent();
							String http = "http://";
							if(urlStr.contains(http)) {
								intent.putExtra("url", urlStr);
							}else if(urlStr.contains("https://")) {
								intent.putExtra("url", urlStr);
							}else{
								intent.putExtra("url", http+urlStr);
							}
							intent.setClass(mContext, WebViewActivity.class);
							startActivity(intent);
						}
					}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				viewHolder.txtMsg.setText(ss);
				//必须设置否则点击事件无效
				viewHolder.txtMsg.setMovementMethod(LinkMovementMethod.getInstance());
				break;
			case MessageType.PICTURE:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);

				String urlpic = messageInfo.imgUrlS;
				if(urlpic == null || urlpic.equals("")){
					urlpic = messageInfo.imgUrlL;
				}
				final String path = urlpic;

				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setTag(path);
				if(path.startsWith("http://") && 1 == messageInfo.getSendState()){
					if(viewHolder.wiatProgressBar != null){
						viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
					}
					viewHolder.imgMsgVoice.setVisibility(View.GONE);
					viewHolder.txtMsg.setVisibility(View.GONE);

					viewHolder.imgMsgPhoto.setTag(path);
					if(!mImageLoader.getImageBuffer().containsKey(path)){
						viewHolder.imgMsgPhoto.setImageBitmap(null);
						viewHolder.imgMsgPhoto.setImageResource(R.drawable.normal);
						mImageLoader.getBitmap(mContext, viewHolder.imgMsgPhoto, viewHolder.wiatProgressBar, path, 0, false, false);
					}else {
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
						viewHolder.imgMsgPhoto.setImageBitmap(mImageLoader.getImageBuffer().get(path));
					}

					viewHolder.imgMsgPhoto.setVisibility(View.VISIBLE);
					viewHolder.imgMsgPhoto.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, ShowImageActivity.class);
							intent.putExtra("imageurl", messageInfo.imgUrlL);
							intent.putExtra("message", messageInfo);
							String ownerId ="";
							String groupId = "";

							if(messageInfo.getFromId().equals(mLogin.uid)){
								if(messageInfo.typechat == 300){
									groupId = messageInfo.fromid;
								}else if(messageInfo.typechat == 100){
									ownerId = messageInfo.fromid;
								}
							}else{
								if(messageInfo.typechat == 300){
									groupId = messageInfo.toid;
								}else if(messageInfo.typechat == 100){
									ownerId = messageInfo.toid;
								}

							}
							intent.putExtra("fuid", ownerId);
							intent.putExtra("groupid",groupId);
							mContext.startActivity(intent);
						}
					});

				}else{
					Bitmap bitmap = null;
					if(!mBitmapCache.containsKey(path)){
						bitmap = BitmapFactory.decodeFile(path);
						mBitmapCache.put(path, new SoftReference<Bitmap>(bitmap));
					}else {
						bitmap = mBitmapCache.get(path).get();
					}
					if(bitmap!=null && !bitmap.isRecycled()){
						viewHolder.imgMsgPhoto.setImageBitmap(bitmap);
					}/*else{
						bitmap = BitmapFactory.decodeFile(path);
						viewHolder.imgMsgPhoto.setImageBitmap(bitmap);
					}
					 */
					viewHolder.imgMsgPhoto.setVisibility(View.VISIBLE);
					if(viewHolder.wiatProgressBar != null){
						viewHolder.imgMsgVoice.setVisibility(View.GONE);
						//viewHolder.imgMsgPhoto.setVisibility(View.GONE);
						viewHolder.txtMsg.setVisibility(View.GONE);

						if(2 == messageInfo.getSendState()){
							viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
						}else {
							viewHolder.wiatProgressBar.setVisibility(View.GONE);
						}
					}
				}
				break;
			case MessageType.VOICE:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				if(2 == messageInfo.getSendState()){
					if(viewHolder.wiatProgressBar != null){
						viewHolder.imgMsgVoice.setVisibility(View.GONE);
						viewHolder.imgMsgPhoto.setVisibility(View.GONE);
						viewHolder.txtMsg.setVisibility(View.GONE);
						viewHolder.wiatProgressBar.setVisibility(View.VISIBLE);
					}
				}else{
					if(viewHolder.wiatProgressBar != null){
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
					}
					/*if(4 == messageInfo.getSendState()){
						downVoice(messageInfo);
					}*/
					viewHolder.imgMsgPhoto.setVisibility(View.GONE);
					viewHolder.imgMsgVoice.setVisibility(View.VISIBLE);
					viewHolder.txtMsg.setVisibility(View.GONE);
					viewHolder.mRootLayout.setTag(messageInfo);
					viewHolder.mRootLayout.setOnClickListener(playListener);
					viewHolder.txtVoiceNum.setVisibility(View.VISIBLE);
					viewHolder.txtVoiceNum.setText(messageInfo.getVoiceTime() + "''");
					//viewHolder.imgMsgVoice.setLayoutParams(new Rela)
					try {
						AnimationDrawable drawable = (AnimationDrawable) viewHolder.imgMsgVoice.getDrawable();
						if (playListener.getMessageTag().equals(messageInfo.tag)) {
							drawable.start();
						}else {
							drawable.stop();
							drawable.selectDrawable(0);
						}

					} catch (Exception e) {

					}
				}
				break;

			case MessageType.MAP:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.VISIBLE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if(viewHolder.wiatProgressBar != null){
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.GONE);
				try {
					if(!TextUtils.isEmpty(messageInfo.mAddress)){
						viewHolder.txtMsgMap.setText(messageInfo.mAddress);
					}

					String ImageURL = "http://api.map.baidu.com/staticimage?center="+messageInfo.mLng+","+messageInfo.mLat+
							"&width=200&height=140&zoom=16&markers="+messageInfo.mLng+","+messageInfo.mLat+"&markerStyles=s";
					mImageLoader.getBitmap(mContext, viewHolder.mapIcon, null,ImageURL,0,false,true);

					viewHolder.mapLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent  = new Intent(mContext, LocationActivity.class);
							intent.putExtra("show", true);
							intent.putExtra("lat", messageInfo.mLat);
							intent.putExtra("lng", messageInfo.mLng);
							intent.putExtra("addr", messageInfo.mAddress);
							String ownerId ="";
							String groupId = "";

							if(messageInfo.getFromId().equals(mLogin.uid)){
								if(messageInfo.typechat == 300){
									groupId = messageInfo.fromid;
								}else if(messageInfo.typechat == 100){
									ownerId = messageInfo.fromid;
								}
							}else{
								if(messageInfo.typechat == 300){
									groupId = messageInfo.toid;
								}else if(messageInfo.typechat == 100){
									ownerId = messageInfo.toid;
								}

							}
							intent.putExtra("fuid", ownerId);
							intent.putExtra("groupid", groupId);
							mContext.startActivity(intent);
						}
					});
				} catch (Exception e) {
				}

				viewHolder.txtMsgMap.setTag(messageInfo);
				break;
			case MessageType.CARD:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if(viewHolder.wiatProgressBar != null){
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.VISIBLE);
				final Card card = Card.getInfo(messageInfo.getContent());
				if(card!=null){
					final String userId = card.uid;
					if(card.headsmall!=null && !card.headsmall.equals("")){
						mImageLoader.getBitmap(mContext, viewHolder.cardHeader, null, card.headsmall,0,false,false);
					}
					viewHolder.cardLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(userId != null && !userId.equals("")){

								Intent intent = new Intent(mContext, UserInfoActivity.class);
								intent.putExtra("username", card.uid);
								intent.putExtra("type",2);
								startActivity(intent);
							}

						}
					});

					viewHolder.cardName.setText(card.nickname);
					viewHolder.cardEM.setText(card.content);
				}
				break;
			case MessageType.REDPACKET:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				if(viewHolder.wiatProgressBar != null){
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgVoice.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.txtMsg.setVisibility(View.GONE);
				viewHolder.txtMsgMap.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				viewHolder.redPacketLayout.setVisibility(View.VISIBLE);
				viewHolder.moneyGreeting.setText(messageInfo.redpacketTitle);
//				viewHolder.redRedPacketbg.setBackground(getResources().getDrawable(R.drawable.common_friend_message_bg));
				viewHolder.redPacketLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						String url = messageInfo.redpacketUrl;
						if(messageInfo.redpacketUrl.contains("?")){
							url = url+"&openid="+mLogin.phone+"&token="+mLogin.token;
						}else{
							url = url+"?id="+mLogin.ypId+"&openid="+mLogin.phone+"&token="+mLogin.token;
						}
						intent.putExtra("url",url);
						intent.setClass(mContext, WebViewActivity.class);
						startActivity(intent);
					}
				});
					break;
				case MessageType.SHAREURL:
                    viewHolder.videolayout.setVisibility(View.GONE);
					viewHolder.mapLayout.setVisibility(View.GONE);
					viewHolder.txtVoiceNum.setVisibility(View.GONE);
					if(viewHolder.wiatProgressBar != null){
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
					}
					viewHolder.imgMsgVoice.setVisibility(View.GONE);
					viewHolder.imgMsgPhoto.setVisibility(View.GONE);
					viewHolder.txtMsg.setVisibility(View.GONE);
					viewHolder.txtMsgMap.setVisibility(View.GONE);
					viewHolder.cardLayout.setVisibility(View.GONE);
					viewHolder.redPacketLayout.setVisibility(View.GONE);
					viewHolder.urlLayout.setVisibility(View.VISIBLE);
					final ShareUrl shareUrl = ShareUrl.getInfo(messageInfo.getContent());
					viewHolder.title.setText(shareUrl.title);
					if(shareUrl.imageurl!=null && !shareUrl.imageurl.equals("")){
						mImageLoader.getBitmap(mContext, viewHolder.imageView, null, shareUrl.imageurl,0,false,false);
					}
					viewHolder.urlLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("url",shareUrl.url);
							intent.setClass(mContext, WebViewActivity.class);
							mContext.startActivity(intent);
						}
					});
					break;
				case MessageType.INVITE:
                    viewHolder.videolayout.setVisibility(View.GONE);
					viewHolder.mapLayout.setVisibility(View.GONE);
					viewHolder.txtVoiceNum.setVisibility(View.GONE);
					if(viewHolder.wiatProgressBar != null){
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
					}
					viewHolder.imgMsgVoice.setVisibility(View.GONE);
					viewHolder.imgMsgPhoto.setVisibility(View.GONE);
					viewHolder.txtMsg.setVisibility(View.GONE);
					viewHolder.txtMsgMap.setVisibility(View.GONE);
					viewHolder.cardLayout.setVisibility(View.GONE);
					viewHolder.redPacketLayout.setVisibility(View.GONE);
					viewHolder.urlLayout.setVisibility(View.GONE);
					viewHolder.inviteLayout.setVisibility(View.VISIBLE);
					final InviteBBS inviteBBS = InviteBBS.getInfo(messageInfo.getContent());
					viewHolder.inviteTitleView.setText(inviteBBS.title);
					viewHolder.inviteContentView.setText(inviteBBS.content);
					if(inviteBBS.headsmall!=null && !inviteBBS.headsmall.equals("")){
						mImageLoader.getBitmap(mContext, viewHolder.inviteImageView, null, inviteBBS.headsmall,0,false,false);
					}
					viewHolder.inviteLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										BbsList bl= getIMInfo().getBbs(inviteBBS.id);
										if(bl.mBbsList!=null && bl.mBbsList.size()!=0){
											if(messageInfo.fromid.equals(IMCommon.getUserPhone(mContext))){
												Intent intent = new Intent(mContext, BbsChatMainActivity.class);
												intent.putExtra("data", bl.mBbsList.get(0));
												intent.putExtra("fromid", messageInfo.fromid);
												startActivity(intent);
											}else{
												Intent intent = new Intent(mContext, JoinBBSActivity.class);
												intent.putExtra("data", bl.mBbsList.get(0));
												startActivity(intent);
											}
										}
									} catch (IMException e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					});
					break;
                case MessageType.VIDEO:
                    viewHolder.mapLayout.setVisibility(View.GONE);
                    viewHolder.txtVoiceNum.setVisibility(View.GONE);
                    if(viewHolder.wiatProgressBar != null){
                        viewHolder.wiatProgressBar.setVisibility(View.GONE);
                    }
                    viewHolder.imgMsgVoice.setVisibility(View.GONE);
                    viewHolder.imgMsgPhoto.setVisibility(View.GONE);
                    viewHolder.txtMsg.setVisibility(View.GONE);
                    viewHolder.txtMsgMap.setVisibility(View.GONE);
                    viewHolder.cardLayout.setVisibility(View.GONE);
                    viewHolder.redPacketLayout.setVisibility(View.GONE);
                    viewHolder.urlLayout.setVisibility(View.GONE);
                    viewHolder.inviteLayout.setVisibility(View.GONE);
					final Video video = Video.getInfo(messageInfo.getContent());
					if(video!=null){
						viewHolder.videolayout.setVisibility(View.VISIBLE);
						viewHolder.videoPlayTime.setText(video.time);
						if(video.image!=null && !video.image.equals("")){
							mImageLoader.getBitmap(mContext, viewHolder.videoImageView, null, video.image,0,false,false);
						}else{
							viewHolder.videoImageView.setImageBitmap(Utils.createVideoThumbnail(video.url));
						}
						viewHolder.playImageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,VideoPlayMainActivity.class);
								intent.putExtra("vidoepath",video.url);
								startActivity(intent);
							}
						});
					}
                    break;
			default:
				break;
			}
		}

		private void showResendDialog(final MessageInfo messageInfo){


			MMAlert.showAlert(mContext, "", mContext.getResources().
					getStringArray(R.array.resend_item),
					null, new OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						messageInfo.sendState = 2;
						MyAdapter.this.notifyDataSetChanged();
						btnResendAction(messageInfo);
						break;
					default:
						break;
					}
				}
			});


		}

	}

	static class ViewHolder {
		int flag = 0;    // 1 好友 0 自己
		TextView txtTime, txtMsg, txtVoiceNum, mShideView;
		ImageView imgHead, imgMsgPhoto, imgMsgVoice, imgSendState, imgVoiceReadState;
		ProgressBar wiatProgressBar;
		RelativeLayout mRootLayout, mDisplayLayout;
		private RelativeLayout.LayoutParams mParams;
		private RelativeLayout.LayoutParams mVoiceTimeParams;


		//名片
		LinearLayout cardLayout;
		ImageView cardHeader;
		TextView cardName,cardEM;


		//地图
		RelativeLayout mapLayout;
		TextView txtMsgMap;
		ImageView mapIcon;

		//昵称
		TextView nickName;
		//红包
		RelativeLayout redPacketLayout;
		TextView moneyGreeting;
		RelativeLayout redRedPacketbg;
		//链接
		RelativeLayout urlLayout;
		TextView title;
		ImageView imageView;

		//邀请行业圈
		RelativeLayout inviteLayout;
		TextView inviteTitleView,inviteContentView;
		ImageView inviteImageView;
		//小视频
		RelativeLayout videolayout;
		ImageView videoImageView,playImageView;
        TextView videoPlayTime;
		public static ViewHolder getInstance(View view){
			ViewHolder holder = new ViewHolder();
			holder.mRootLayout = (RelativeLayout) view.findViewById(R.id.chat_talk_msg_info);
			holder.mParams = (RelativeLayout.LayoutParams)holder.mRootLayout.getLayoutParams();
			holder.txtTime = (TextView) view.findViewById(R.id.chat_talk_txt_time);
			holder.txtMsg = (TextView) view.findViewById(R.id.chat_talk_msg_info_text);

			holder.imgHead = (ImageView) view.findViewById(R.id.chat_talk_img_head);
			holder.imgMsgPhoto = (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_photo);
			holder.imgMsgVoice = (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_voice);

			holder.imgSendState = (ImageView) view.findViewById(R.id.chat_talk_msg_sendsate);
			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.chat_talk_msg_progressBar);
			holder.txtVoiceNum = (TextView) view.findViewById(R.id.chat_talk_voice_num);
			holder.mVoiceTimeParams = (RelativeLayout.LayoutParams)holder.txtVoiceNum.getLayoutParams();
			holder.imgVoiceReadState = (ImageView) view.findViewById(R.id.unread_voice_icon);

			//+++通讯录名片++//
			holder.cardLayout = (LinearLayout)view.findViewById(R.id.card_layout);
			holder.cardHeader = (ImageView)view.findViewById(R.id.card_header);
			holder.cardName = (TextView)view.findViewById(R.id.card_name);
			holder.cardEM = (TextView)view.findViewById(R.id.card_emal);
			//--通讯录名片--//


			//++地图++//
			holder.mapLayout = (RelativeLayout)view.findViewById(R.id.map_layout);
			holder.txtMsgMap = (TextView) view.findViewById(R.id.chat_talk_msg_map);
			holder.mapIcon = (ImageView)view.findViewById(R.id.map_icon);
			//--地图--//
			//++红包++//
			holder.redPacketLayout = (RelativeLayout)view.findViewById(R.id.bubble);
			holder.moneyGreeting = (TextView) view.findViewById(R.id.tv_money_greeting);
			holder.redRedPacketbg = (RelativeLayout)view.findViewById(R.id.chat_talk_msg_info);
			//--红包--//
			//++链接++//
			holder.urlLayout = (RelativeLayout)view.findViewById(R.id.url);
			holder.title = (TextView) view.findViewById(R.id.url_text);
			holder.imageView = (ImageView) view.findViewById(R.id.image_url);
			//--链接--//
			holder.nickName = (TextView)view.findViewById(R.id.from_message_nickname);
			//--邀请行业圈--//
			holder.inviteLayout = (RelativeLayout)view.findViewById(R.id.invite);
			holder.inviteTitleView = (TextView) view.findViewById(R.id.invite_title);
			holder.inviteContentView = (TextView) view.findViewById(R.id.invite_content);
			holder.inviteImageView = (ImageView) view.findViewById(R.id.invite_image);
			//小视频
			holder.videolayout= (RelativeLayout)view.findViewById(R.id.video_layout);
			holder.videoImageView= (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_video);
			holder.playImageView= (ImageView)view.findViewById(R.id.play_video);
            holder.videoPlayTime= (TextView) view.findViewById(R.id.play_video_time);
			return holder;
		}
	}

	/**************        表情功能       *************/

	// 显示表情列表
	private void showEmojiGridView(){
		mToggleBtn.setChecked(false);
		togInfoSelect();
		mEmotionLayout.setVisibility(View.VISIBLE);
	}

	// 隐藏表情列表
	private void hideEmojiGridView(){
		hideExpra();
		mEmotionLayout.setVisibility(View.GONE);
	}


	private View.OnFocusChangeListener sendTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				hideEmojiGridView();
			}

		}
	};

	private View.OnClickListener sendTextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 获取到文本框的点击事件隐藏表情
			mEmotionBtn.setChecked(false);
			hideEmojiGridView();
		}
	};

	private EditText.OnEditorActionListener mEditActionLister = new EditText.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_SEND && mContentEdit.getVisibility() == View.VISIBLE){
				hideSoftKeyboard();
				sendText();

				return true;
			}
			return false;
		}

	};

	/**  聊天广播 */
	private BroadcastReceiver chatReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(SnsService.ACTION_CONNECT_CHANGE.equals(action)){
				Log.d(TAG, "receiver:" + action);
				String type = intent.getExtras().getString(SnsService.EXTRAS_CHANGE);
				Log.d(TAG, "receiver:Exper" + type);
				if(XmppType.XMPP_STATE_AUTHENTICATION.equals(type)){
					// 认证成功
					opconnectState = true;
				}else if(XmppType.XMPP_STATE_AUTHERR.equals(type)){
					// 认证失败
					opconnectState = false;
					showToast(mContext.getString(R.string.login_user_auth_error));
				}else if(XmppType.XMPP_STATE_REAUTH.equals(type)){
					// 未认证
					opconnectState = false;
				}else if(XmppType.XMPP_STATE_START.equals(type)){
					// 开始登录
					opconnectState = false;
				}else if(XmppType.XMPP_STATE_STOP.equals(type)){
					// 没开启登录
					opconnectState = false;
				}
			}else if(PushChatMessage.ACTION_SEND_STATE.equals(action)){
				Log.d(TAG, "receiver:" + PushChatMessage.ACTION_SEND_STATE);
				MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
				updateMessage(messageInfo);
				changeSendState(messageInfo);

			}else if(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE.equals(action)){
				final MessageInfo msg = (MessageInfo) intent.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_CHAT_MESSAGE);
				if((msg.typechat == GlobleType.SINGLE_CHAT && msg.fromid.equals(fCustomerVo.phone))
						||(msg.typechat == GlobleType.BBS_CHAT && msg.fromid.equals(fCustomerVo.phone))
						|| ((msg.typechat == GlobleType.GROUP_CHAT || msg.typechat == GlobleType.MEETING_CHAT)  && msg.toid.equals(fCustomerVo.phone))){
					msg.readState = 1;
					updateMessage(msg);
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
					notifyMessage(msg);
				}
			}else if(action.equals(DESTORY_ACTION)){
				ChatMainActivity.this.finish();
			}else if(action.equals(REFRESH_ADAPTER)){
				String id = intent.getStringExtra("id");
				if(!TextUtils.isEmpty(id)){
					if(!fCustomerVo.uid.equals(id)){
						return;
					}
				}
				SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
				MessageTable messageTable = new MessageTable(db);
				messageInfos = messageTable.query(fCustomerVo.uid, -1, mType,bid);

				if(messageInfos == null){
					messageInfos = new ArrayList<MessageInfo>();
				}

				mAdapter.notifyDataSetChanged();
			}else if(action.equals(GlobalParam.BE_KICKED_ACTION)){
				String id = intent.getStringExtra("id");
				String uid = intent.getStringExtra("uid");
				int type = intent.getIntExtra("type", 0);
				String hintMsg = intent.getStringExtra("hintMsg");
				if(type!=0){
					if(id.equals(fCustomerVo.uid)){
						destoryDialog(hintMsg);
					}
				}else{
					if(!TextUtils.isEmpty(id)){
						if(!TextUtils.isEmpty(uid) && uid.equals(IMCommon.getUserId(mContext))){
							if(id.equals(fCustomerVo.uid)){
								destoryDialog(mContext.getString(R.string.you_have_been_removed_from_group));
							}
						}

					}
				}


			}else if(ACTION_READ_VOICE_STATE.equals(action)){
				final MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra(PushChatMessage.EXTRAS_MESSAGE);
				updateMessage(messageInfo);
				changeVoiceState(messageInfo);

			}else if(action.equals(ACTION_RECORD_AUTH)){
				Toast.makeText(mContext,mContext.getString(R.string.record_auth_control), Toast.LENGTH_LONG).show();
			}else if(action.equals(GlobalParam.ACTION_RESET_GROUP_NAME)){
				String groupId = intent.getStringExtra("group_id");
				String groupName = intent.getStringExtra("group_name");
				if(groupId!=null && !groupId.equals("")){
					if(groupName!=null && !groupName.equals("")){
						if(fCustomerVo.uid.equals(groupId)){
							titileTextView.setText(groupName);
						}
					}
				}

			}else if(action.equals(ACTION_RECOMMEND_CARD)){
				MessageInfo msg = (MessageInfo)intent.getSerializableExtra("cardMsg");
				if(msg!=null){
					Log.e("send_card","true++++++++");
					sendBroad2Save(msg,false);
				}
			}else if(action.equals(ACTION_SHOW_NICKNAME)){
				boolean isShowNickName = intent.getBooleanExtra("is_show_nickname",false);
				if(mAdapter!=null){
					mAdapter.setIsShowNickName(isShowNickName);
					mAdapter.notifyDataSetChanged();
					//mAdapter.
				}
			}
		}
	};

	private void updateNewMessage(MessageInfo messageInfo){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.updateMessage(messageInfo);
	}

	private void updateMessage(MessageInfo messageInfo){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.update(messageInfo);
	}

	private void insertSession(Session session){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		SessionTable table = new SessionTable(db);
		Session existSession = table.query(fCustomerVo.phone, session.type);
		if(existSession != null){
			if(existSession.isTop!=0){
				List<Session> exitsSesList = table.getTopSessionList();
				if(exitsSesList!=null && exitsSesList.size()>0){
					for (int i = 0; i < exitsSesList.size(); i++) {
						Session ses = exitsSesList.get(i);
						if(ses.isTop>1){
							ses.isTop = ses.isTop-1;
							table.update(ses, ses.type);
						}
					}
				}
				session.isTop = table.getTopSize();
			}
			table.update(session, session.type);
		}else {
			table.insert(session);
		}
	}

	private void insertMessage(MessageInfo messageInfo){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.insert(messageInfo);
	}
	private void delMessage(MessageInfo messageInfo){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		MessageTable table = new MessageTable(db);
		table.delete(messageInfo);
	}

	private void changeSendState(final MessageInfo messageInfo){
		if(messageInfo != null){
			handler.post(new Runnable() {

				@Override
				public void run() {
					modifyMessageState(messageInfo);
				}
			});
		}
	}

	private void notifyMessage(final MessageInfo msg){
		if(msg == null){
			return;
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					// 当该信息不来自好友就过滤掉!
					if(msg.getFromId().equals(IMCommon.getUserId(mContext))){
						return;
					}
					messageInfos.add(msg);
					mAdapter.notifyDataSetInvalidated();
					if(messageInfos.size() == 1 || (mListView.getLastVisiblePosition() == messageInfos.size() - 2)){
						mListView.setSelection(messageInfos.size() - 1);
					}
				} catch (Exception e) {

				}

			}
		});
	}

	private void changeVoiceState(final MessageInfo messageInfo){
		if(messageInfo != null){
			handler.post(new Runnable() {

				@Override
				public void run() {
					modifyMessageVoiceState(messageInfo);
				}
			});
		}
	}

	private void changeReadState(final MessageInfo messageInfo){
		if(messageInfo != null){
			handler.post(new Runnable() {

				@Override
				public void run() {
					modifyMessageReadState(messageInfo);
				}
			});
		}
	}

	private void modifyMessageReadState(MessageInfo messageInfo){
		for (int i = 0; i < messageInfos.size(); i++) {
			if(messageInfo.tag.equals(messageInfos.get(i).tag)){
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.setReadState(messageInfo.readState);
				mAdapter.notifyDataSetInvalidated();
				break;
			}
		}

	}

	private void modifyMessageState(MessageInfo messageInfo){

		for (int i = 0; i < messageInfos.size(); i++) {
			if(messageInfo.tag.equals(messageInfos.get(i).tag)){
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.setSendState(messageInfo.getSendState());
				tempInfo.id = messageInfo.id;
				tempInfo.imgUrlS = messageInfo.imgUrlS;
				tempInfo.imgUrlL = messageInfo.imgUrlL;
				tempInfo.imgWidth = messageInfo.imgWidth;
				tempInfo.imgHeight = messageInfo.imgHeight;
				tempInfo.voiceUrl = messageInfo.voiceUrl;
				tempInfo.readState = messageInfo.readState;
				tempInfo.time = messageInfo.time;
				mAdapter.notifyDataSetInvalidated();
				break;
			}
		}

	}

	private void modifyMessageVoiceState(MessageInfo messageInfo){
		for (int i = 0; i < messageInfos.size(); i++) {
			if(messageInfo.tag.equals(messageInfos.get(i).tag)){
				MessageInfo tempInfo = messageInfos.get(i);
				tempInfo.isReadVoice = messageInfo.isReadVoice;
				break;
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
		//showLongDialog(mContext, arg2);
		return true;
	}

	protected void createDialog(Context context, String cardTitle, final int type, final String okTitle) {
		mPhoneDialog = new Dialog(context, R.style.dialog);
		LayoutInflater factor = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.card_dialog, null);

		mPhoneDialog.setContentView(serviceView);
		mPhoneDialog.show();
		mPhoneDialog.setCancelable(false);	
		mPhoneDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT
				/*mContext.getResources().getDimensionPixelSize(R.dimen.bind_phone_height)*/
				, LayoutParams.WRAP_CONTENT);

		/*
		TextView signId=(TextView) serviceView
				.findViewById(R.id.sign_id);
		signId.setText(string[0]);*/
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

				IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "请求发送中,请稍后...");
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


	/**
	 * pop 长按事件
	 * @param pos
	 * @param type 1-text 消息
	 * @param content
	 */
	private void showPromptDialog(final int pos, final int type, final String content, final MessageInfo oldMsg, final View v){
		//normal_message_item
		String[] itemMenu ;
		ArrayList<String> itemMenuArrayList = new ArrayList<String>();
		if(type == 1){
			itemMenuArrayList.add("复制");
		}
		itemMenuArrayList.add("转发");
		itemMenuArrayList.add("收藏");
		itemMenuArrayList.add("删除");
		if(oldMsg.typefile == MessageType.VOICE ){
			itemMenuArrayList.add("听筒播放");
		}
		itemMenu = itemMenuArrayList.toArray(new String[itemMenuArrayList.size()]);
		if(oldMsg.typefile == MessageType.CARD || oldMsg.typefile == MessageType.SHAREURL){
			itemMenu = new String[]{mContext.getResources().getString(R.string.forward)};
		}
		if( oldMsg.typefile == MessageType.SHAREURL){
			itemMenu = new String[]{"转发","删除"};
		}
		if(oldMsg.typefile == MessageType.VIDEO ){
			itemMenu = new String[]{"收藏"};
		}
		final String[] itemMenuShow = itemMenu;
		MMAlert.showAlert(mContext, "", itemMenu,
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				if(whichButton>=itemMenuShow.length){
					return;
				}
				if (itemMenuShow[whichButton].equals("删除")) {
					for (int i = messageInfos.size() - 1; i >= 0; i--) {
						if (messageInfos.get(i).tag.equals(oldMsg.tag)) {
							messageInfos.remove(i);
							delMessage(oldMsg);
							if (oldMsg.typefile== MessageType.VOICE) {
								playListener.stop();
							}
							break;
						}
					}
					Message msg = new Message();
					msg.what = MSG_REFUSH;
					mHandler.sendMessage(msg);
				}
				if (itemMenuShow[whichButton].equals("复制")) {
					ClipboardManager cm =(ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText(content);
				}
				if (itemMenuShow[whichButton].equals("转发")) {
					Intent chooseUserIntent = new Intent();
					chooseUserIntent.setClass(mContext, ChooseUserActivity.class);
					chooseUserIntent.putExtra("forward_msg", oldMsg);
					startActivity(chooseUserIntent);
				}
				if (itemMenuShow[whichButton].equals("听筒播放")) {
					setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
					playListener.setTingTong(true);
					playListener.playVideo(oldMsg,v);
				}
				if (itemMenuShow[whichButton].equals("收藏")) {
					String ownerId ="";
					String groupId = "";
					if(oldMsg.typechat == 300 || oldMsg.typechat == 500){
						groupId = oldMsg.toid;
						ownerId = oldMsg.fromid;
					}else if(oldMsg.typechat == 100){
						ownerId = oldMsg.fromid;
					}
					switch (oldMsg.typefile) {
						case MessageType.TEXT:
							MovingContent movingContent = new MovingContent(URLEncoder.encode(content), MessageType.TEXT+"");
							favoriteMoving(MovingContent.getInfo(movingContent), ownerId,groupId);
							break;
						case MessageType.PICTURE:
							MovingPic movingPic = new MovingPic(oldMsg.imgUrlL,oldMsg.imgUrlS, MessageType.PICTURE+"");
							favoriteMoving(MovingPic.getInfo(movingPic), ownerId,groupId);
							break;
						case MessageType.VOICE:
							MovingVoice movingVoice = new MovingVoice(oldMsg.voicetime+"", oldMsg.voiceUrl , MessageType.VOICE+"");
							favoriteMoving(MovingVoice.getInfo(movingVoice), ownerId,groupId);
							break;
						case MessageType.MAP:
							MovingLoaction movingLocation = new MovingLoaction(oldMsg.mLat+"",oldMsg.mLng+"",oldMsg.mAddress, MessageType.MAP+"");
							favoriteMoving(MovingLoaction.getInfo(movingLocation), ownerId,groupId);
							break;
						case MessageType.VIDEO:
							Video video = Video.getInfo(oldMsg.content);
							video.typefile = MessageType.VIDEO+"";
							favoriteMoving(Video.getInfo(video), ownerId,groupId);
							break;
						default:
							break;
					}
				}
			}
		});
	}


	private void favoriteMoving(final String favoriteContent, final String ownerUid, final String groupid){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					IMJiaState status = getIMInfo().favoreiteMoving(ownerUid, groupid, favoriteContent);
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


	private static final int RECORD_VIDEO = 1;
	private static final int PLAY_VIDEO = 2;
	//缩略图
	private Bitmap bitmap = null;
	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.chat_box_btn_send:
			sendText();
			break;
		case R.id.chat_box_btn_info:
			togInfoSelect();
			if (!mToggleBtn.isChecked()) {
				showSoftKeyboard();
			}
			break;
		case R.id.chat_box_btn_add:
			btnAddAction();
			break;
		case R.id.chat_box_expra_btn_camera:
			btnCameraAction();
			break;
		case R.id.chat_box_expra_btn_picture:
			btnPhotoAction();
			break;
		case R.id.chat_box_expra_btn_location:
			btnLocationAction();
			break;

		case R.id.chat_box_btn_emoji:
			btnEmojiAction();
			if(!mEmotionBtn.isChecked()){
				showSoftKeyboard();
			}
			break;
		case R.id.chat_box_expra_btn_card:
			hideExpra();
			Intent atIntent = new Intent();
			atIntent.setClass(mContext, ChooseUserActivity.class);
			atIntent.putExtra("isJump",1);
			atIntent.putExtra("toLogin",fCustomerVo);
			startActivityForResult(atIntent, 1);
			break;
		case R.id.chat_box_expra_btn_favorite:
			hideExpra();
			Intent favoriteIntent = new Intent();
			favoriteIntent.setClass(mContext, MyFavoriteActivity.class);
			favoriteIntent.putExtra("isShow",false);
			favoriteIntent.putExtra("liaotian",true);
			favoriteIntent.putExtra("data",fCustomerVo);
			startActivity(favoriteIntent);
			break;
		case R.id.chat_box_expra_btn_video:
			Intent mIntent = new Intent();
			mIntent.setClass(mContext, VideoMainActivity.class);
			startActivityForResult(mIntent, RECORD_VIDEO);
			break;
		case  R.id.chat_box_expra_btn_redpacket:
			hideExpra();
			String toid="";
			String totype="2";
			if(mType == GlobleType.SINGLE_CHAT){//单聊模式
				toid = fCustomerVo.phone;
				totype = GlobleType.SINGLE_CHAT+"";
			}else if(mType == GlobleType.GROUP_CHAT){//群聊模式
				toid = fCustomerVo.phone;
				totype = GlobleType.GROUP_CHAT+"";
			}
			Intent tuiguangeweimaIntent = new Intent();
			tuiguangeweimaIntent.putExtra("url","http://shop.wei20.cn/repacket/wishmb/sendrepacket.shtml?id="+mLogin.ypId+"&formid="+mLogin.kai6Id+"&token="+mLogin.token+"&toid="+toid+"&totype="+totype+"&lbs="+lbs);
			tuiguangeweimaIntent.setClass(mContext, RedpacketWebViewActivity.class);
			startActivityForResult(tuiguangeweimaIntent,REQUEST_GET_REDPACKET);
			break;
		case R.id.left_btn:
			hideSoftKeyboard();
			this.finish();
			break;

		case R.id.right_btn:
			SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
			SessionTable sessionTable = new SessionTable(dbDatabase);
			Session existSession = sessionTable.query(fCustomerVo.phone,mType);
			Intent detailIntent = new Intent(mContext, ChatDetailActivity.class);
			if(existSession!=null){
				if(existSession.isTop>=1){
					detailIntent.putExtra("isTop",true);
				}else{
					detailIntent.putExtra("isTop",false);
				}
			}
			detailIntent.putExtra("groupid", fCustomerVo.phone);
			detailIntent.putExtra("typechat", mType);
			if(mType != 100){
				detailIntent.putExtra("isOwner", getIntent().getIntExtra("isOwner", 0));
			}else {
				detailIntent.putExtra("isSignChat",1);
				detailIntent.putExtra("to_login", fCustomerVo);
			}

			mContext.startActivity(detailIntent);
			//showMoreDialog(mContext);
			break;

		default:
			break;
		}
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

	public void showSoftKeyboard(){
		InputMethodManager imm = (InputMethodManager) mContentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	public ProgressDialog getWaitDialog() {
		return waitDialog;
	}

	public void showToast(String content){
		Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
	}



	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}



	@Override
	public void onPageSelected(int position) {
		mPageIndxe = position;
		showCircle(mViewList.size());
	}

}
