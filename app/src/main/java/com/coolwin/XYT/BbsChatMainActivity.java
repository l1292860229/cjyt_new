package com.coolwin.XYT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.Entity.Card;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MapInfo;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.ShareUrl;
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
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.widget.MyPullToRefreshListView;
import com.coolwin.XYT.widget.ResizeLayout;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
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

import static android.R.attr.type;
import static com.coolwin.XYT.R.id.right_text_btn;
import static com.coolwin.XYT.global.GlobalParam.MSG_CHECK_FAVORITE_STATUS;

/**
 * 
 * 功能： Bbs会话 <br />
 * 
 * 
 * 1 成功;
 * 0 失败;
 * 2 正在发送;
 * @author dl
 * @since
 */
public class BbsChatMainActivity extends BaseActivity implements OnItemLongClickListener, OnClickListener, OnTouchListener, OnPageChangeListener,MyPullToRefreshListView.OnChangeStateListener {
	/**
	 * 定义全局变量
	 */
	public static final String EMOJIREX = "emoji_[\\d]{0,3}";
	private static final String TAG = "bbs_chat_main";
	private final static int MSG_RESIZE = 1234;

	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();
	private ViewPager mViewPager;
	private IMViewPagerAdapter mEmotionAdapter;
	private LinkedList<View> mViewList = new LinkedList<View>();
	private LinearLayout mLayoutCircle;
	public int mPageIndxe = 0;
	private RelativeLayout mEmotionLayout;

	private RelativeLayout mChatBottmLayout,redpacket_layout,relativeLayout;
	private LinearLayout menuLayout;
	private TextView jinYan;

	private ListView mListView;
	private Button mMsgSendBtn, mCameraBtn, mGalleryBtn, mLocationBtn,mCardBtn,mFavoritebtn,mRedPacketbtn,mVideobtn;
	private Button mVoiceSendBtn;
	private ToggleButton mToggleBtn,mAddBtn, mEmotionBtn;
	private EditText mContentEdit;
	private View mChatExpraLayout;
	private MyAdapter mAdapter;
	private List<BbsReplyInfo> messageInfos = new ArrayList<BbsReplyInfo>();
	private ReaderImpl mReaderImpl;
	private AudioRecorderAction audioRecorder;
	private AudioPlayListener playListener;
	private AlertDialog messageDialog;		// 消息功能ui
	private Handler handler = new Handler();

	private ProgressDialog waitDialog;

	private List<String> downVoiceList = new ArrayList<String>();
	private ResizeLayout mRootLayout;
	private ResizeLayout mListLayout;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private ImageLoader mImageLoader = new ImageLoader();
	public static final int SEND_VOICE_TO_LIST = 4445;
	public static final int DEL_BBS_REPLY_SUCCESS = 4446;
	private static final int REQUEST_GET_IMAGE_BY_CAMERA = 102;
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 124;
	public static final int REQUEST_GET_BBS = 125;
	private final static int MAX_SECOND = 10;
	private final static int MIN_SECOND = 2;
	private HashMap<String, SoftReference<Bitmap>> mBitmapCache;
	private String mFilePath = "";
	private int mScalcWith,mScalcHeigth;
	private final static int SEND_SUCCESS = 13454;
	private final static int SEND_FAILED = 13455;
	private final static int CHANGE_STATE = 13456;
	private final static int HIDE_PROGRESS_DIALOG = 15453;
	private final static int SHOW_KICK_OUT_DIALOG = 15454;
	private boolean mIsRegisterReceiver = false;
	private boolean mHasLocalData = true;
	private int mType = 100;
	private static final int RESQUEST_CODE = 100;
	private static final int REFUCH_DATA = 103;
	private static final int YOU_NOT_SPEAK = 104;
	private static final int NOT_SPEAK = 105;
	private static final int IS_NULL = 106;
	private static final int OPEN_SPEAK = 107;
	private static final int CLOSE_BBS = 108;
	private static final int LAST_DATA = 109;
	private Dialog mPhoneDialog;
	private int mIsOwner =0;
	private Bbs bbs;
	private Login mLogin;
	private String max="0",min="0";
	private boolean mIsFirst = true;
	private TextView mleftTextView;
	private boolean isShowLouZu;
	private boolean flag=true;
	private List<String> jinYanList = new ArrayList<String>();
	private boolean isSendVideo=true;
	private MyPullToRefreshListView mContainer;
	private TextView mRefreshViewLastUpdated;
	private ImageView qiehuanIV;
	private boolean isqiehuan=false;
	private Button friendsBtn,friendsLoopBtn,chatBtn;
	public String fromid;
	private boolean isvisitors=false;
	private BbsReplyInfo mShareUrl;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.bbs_chat_main);
		mLogin = IMCommon.getLoginResult(mContext);
		initComponent();
		getDataThread();
	}
	/**
	 * 初始化控件
	 */
	private void initComponent(){
		findViewById(R.id.ll_bar).setLayoutParams(new RelativeLayout.LayoutParams(0,0));
		bbs = (Bbs)getIntent().getSerializableExtra("data");
		isvisitors = getIntent().getBooleanExtra("isvisitors",false);
		mShareUrl = (BbsReplyInfo)getIntent().getSerializableExtra("shareUrlMsg");
		if(mShareUrl!=null){
			sendBroad2Save(mShareUrl,true);
		}
		setTitleContent(R.drawable.back_btn, 0, "");
		mleftTextView = (TextView) findViewById(right_text_btn);
		if(bbs.type==1){
			mleftTextView.setText("圈主");
		}else{
			mleftTextView.setText("楼主");
		}
		mleftTextView.setOnClickListener(this);
		mleftTextView.setBackground(getResources().getDrawable(R.drawable.louzu_false));
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		mBitmapCache = new HashMap<String, SoftReference<Bitmap>>();
		mChatBottmLayout = (RelativeLayout)findViewById(R.id.noraml_keyborad_layout);
		mContainer = (MyPullToRefreshListView)findViewById(R.id.container);
		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer.setOnChangeStateListener(this);
		mListView = mContainer.getList();
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
		mRootLayout = (ResizeLayout) findViewById(R.id.rootlayout);
		mRootLayout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

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
		mListLayout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

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
		redpacket_layout = (RelativeLayout) findViewById(R.id.redpacket_layout);
		redpacket_layout.setVisibility(View.GONE);
		mVideobtn = (Button)findViewById(R.id.chat_box_expra_btn_video);
		mVideobtn.setOnClickListener(this);
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
		mListLayout = (ResizeLayout) findViewById(R.id.listlayout);
		jinYan =  (TextView) findViewById(R.id.jinyan);
		if(bbs.speakStatus!=0&&!mLogin.uid.equals(bbs.uid)){
			mChatBottmLayout.setVisibility(View.GONE);
			jinYan.setVisibility(View.VISIBLE);
		}
		if(bbs.status==0){
			mChatBottmLayout.setVisibility(View.GONE);
			jinYan.setVisibility(View.VISIBLE);
			jinYan.setText("楼主已闭贴");
		}
		mRightBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.people_btn));
		mRightBtn.setVisibility(View.VISIBLE);
		titileTextView.setText(getFromName());
		audioRecorder = new AudioRecorderAction(getBaseContext());
		mReaderImpl = new ReaderImpl(BbsChatMainActivity.this, handler, audioRecorder){
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
			public void down(BbsReplyInfo msg) {
				super.down(msg);
				downVoice(msg, 1);
			}
		};
		mListView.setOnTouchListener(this);
		mListView.setOnItemLongClickListener(this);
		mVoiceSendBtn.setOnTouchListener(new OnVoice());
		mContentEdit.setOnFocusChangeListener(sendTextFocusChangeListener);
		mContentEdit.setOnClickListener(sendTextClickListener);
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		mAdapter = new MyAdapter();
		mListView.setAdapter(mAdapter);
		if(messageInfos == null || messageInfos.size() < 20){
			mHasLocalData = false;
		}
		bbsProhibitionList(bbs.id);
		relativeLayout = (RelativeLayout)findViewById(R.id.RelativeLayout1);
		menuLayout = (LinearLayout) findViewById(R.id.menu);
		chatBtn = (Button) findViewById(R.id.chat);
		chatBtn.setOnClickListener(this);
		friendsBtn = (Button) findViewById(R.id.friends);
		friendsBtn.setOnClickListener(this);
		friendsLoopBtn = (Button) findViewById(R.id.friends_loop);
		friendsLoopBtn.setOnClickListener(this);
		if(bbs.type==1){
			qiehuanIV = (ImageView)findViewById(R.id.qiehuan);
			qiehuanIV.setVisibility(View.VISIBLE);
			menuLayout.setVisibility(View.VISIBLE);
			relativeLayout.setVisibility(View.GONE);
			if(!isvisitors){
				qiehuanIV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(isqiehuan){
							isqiehuan=false;
							qiehuanIV.setImageDrawable(getResources().getDrawable(R.drawable.keyboard_u_btn));
							menuLayout.setVisibility(View.VISIBLE);
							relativeLayout.setVisibility(View.GONE);
						}else{
							isqiehuan=true;
							qiehuanIV.setImageDrawable(getResources().getDrawable(R.drawable.keyboard_btn));
							menuLayout.setVisibility(View.GONE);
							relativeLayout.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		}
		fromid =  getIntent().getStringExtra("fromid");
		if(fromid!=null){
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
			MessageTable table = new MessageTable(db);
			table.updateReadState(fromid,100,null);
		}
		Button bbsOneMessageCount = (Button) findViewById(R.id.bbs_one_message_count);
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		SessionTable table = new SessionTable(db);
		int count = table.querySessionBBSCount(GlobleType.BBS_CHAT,bbs.id);
		if (count==0) {
			bbsOneMessageCount.setVisibility(View.GONE);
		}else{
			bbsOneMessageCount.setVisibility(View.VISIBLE);
			bbsOneMessageCount.setText(count+"");
		}
	}
	private String getFromName(){
		return bbs.title;
	}
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

					if(url.startsWith("http://") ){
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
	public void getDataThread(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (flag){
					getData(isShowLouZu?"1":"0");
					try {
						Thread.sleep(10*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	private void getData(final String showLouZu){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().getBbsReplyList(bbs.id,max,showLouZu);
						if(result != null && result.mState != null){
							if(result.mState.code == 0&& result.mBbsReplyInfoList!=null ){
								if(messageInfos.size()!=0){
									for (int i = messageInfos.size() - 1; i >= 0; i--) {
										for (BbsReplyInfo bbsReplyInfo : result.mBbsReplyInfoList) {
											if (messageInfos.get(i).id.equals(bbsReplyInfo.id)) {
												messageInfos.remove(i);
												if(messageInfos.size()==0){
													break;
												}
											}
										}
									}
								}
								Collections.reverse(result.mBbsReplyInfoList);
								messageInfos.addAll(result.mBbsReplyInfoList);
								max = result.max;
								min = result.min;
								bbs.status=1;
								Message message = new Message();
								message.what = REFUCH_DATA;
								mHandler.sendMessage(message);
							}
							Message message = new Message();
							if(result.mState.code == 4){
								bbs.status = 0;
								if(jinYan.getVisibility()== View.VISIBLE){
									return;
								}
								message.what = CLOSE_BBS;
								mHandler.sendMessage(message);
								return;
							}
							if(result.speakStatus!=null){
								if(result.speakStatus.equals("1")
										&&!bbs.uid.equals(mLogin.uid)){
									if(jinYan.getVisibility()== View.VISIBLE){
										return;
									}
									message.what = YOU_NOT_SPEAK;
									message.obj = result.mState;
									mHandler.sendMessage(message);
								}else if(result.speakStatus.equals("0")){
									if(jinYan.getVisibility()== View.GONE){
										return;
									}
									message.what = OPEN_SPEAK;
									mHandler.sendMessage(message);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}).start();
	}
	private void getlastData(final String showLouZu){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().getBbsReplyLastList(bbs.id,min,showLouZu);
						if(result != null && result.mState != null){
							if(result.mState.code == 0&& result.mBbsReplyInfoList!=null ){
								if(messageInfos.size()!=0){
									for (int i = messageInfos.size() - 1; i >= 0; i--) {
										for (BbsReplyInfo bbsReplyInfo : result.mBbsReplyInfoList) {
											if (messageInfos.get(i).id.equals(bbsReplyInfo.id)) {
												messageInfos.remove(i);
											}
										}
									}
								}
								Collections.reverse(result.mBbsReplyInfoList);
								result.mBbsReplyInfoList.addAll(messageInfos);
								messageInfos = result.mBbsReplyInfoList;
								min = result.min;
								bbs.status=1;
								Message message = new Message();
								message.what = LAST_DATA;
								mHandler.sendMessage(message);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}).start();
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
				BbsChatMainActivity.this.finish();
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
				String tempUrl = IMCommon.getCamerUrl(mContext);
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+tempUrl;
				Log.e("path", "path:"+path);
				if(tempUrl == null || tempUrl.equals("")){
					return;
				}
				Log.e("start-end", path.indexOf(".")+":"+path.length());
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
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
	private static final int RECORD_VIDEO = 1;
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
		case REQUEST_GET_BBS:
			if(data!=null){
				Bbs tbbs = (Bbs) data.getSerializableExtra("bbs");
				bbs = tbbs;
				titileTextView.setText(getFromName());
				sendBroadcast(new Intent(MyBbsListActivity.MSG_REFRESH_MOVIINF));
			}
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
			BbsReplyInfo msg = new BbsReplyInfo();
			msg.id = UUID.randomUUID().toString();
			msg.uid = IMCommon.getUserId(mContext);
			msg.bid = bbs.id;
			msg.nickname = mLogin.nickname;
			msg.headsmall = mLogin.headsmall;
			msg.typefile = MessageType.TEXT;
			msg.content = str;
			msg.time = System.currentTimeMillis();
			sendBroad2Save(msg,false);
		}
	}
	/**
	 * 发送地图
	 * @param mapInfo
	 */
	private void sendMap(MapInfo mapInfo){
		BbsReplyInfo msg = new BbsReplyInfo();
		msg.id = UUID.randomUUID().toString();
		msg.uid = IMCommon.getUserId(mContext);
		msg.bid = bbs.id;
		msg.nickname = mLogin.nickname;
		msg.headsmall = mLogin.headsmall;
		msg.time = System.currentTimeMillis();
		msg.typefile = MessageType.MAP;
		msg.mLat = Double.parseDouble(mapInfo.getLat());
		msg.mLng = Double.parseDouble(mapInfo.getLng());
		msg.mAddress = mapInfo.getAddr();
		msg.time = System.currentTimeMillis();
		sendBroad2Save(msg,false);
	}

	/**
	 * 发送声音和图片
	 * @param type
	 * @param filePath
	 */
	private void sendFile(int type, String filePath, String videotime){
		BbsReplyInfo msg = new BbsReplyInfo();
		msg.id = UUID.randomUUID().toString();
		msg.uid = IMCommon.getUserId(mContext);
		msg.bid = bbs.id;
		msg.nickname = mLogin.nickname;
		msg.headsmall = mLogin.headsmall;
		msg.time = System.currentTimeMillis();
		msg.imgWidth = mScalcWith;
		msg.imgHeight = mScalcHeigth;
		if(type == MessageType.VOICE){
			msg.voiceUrl = filePath;
			msg.voicetime = (int)mReaderImpl.getReaderTime();
		}else if(type == MessageType.PICTURE){
			msg.imgUrlS = filePath;
		} if(type == MessageType.VIDEO){
			msg.content = Video.getInfo(new Video(filePath,videotime, ""));
		}
		msg.typefile = type;
		msg.time = System.currentTimeMillis();
		sendFilePath(msg, 0);
	}
	private void sendFilePath(BbsReplyInfo messageInfo, int isResend){
		sendMessage(messageInfo, isResend,false);
		MessageInfo msgmi = new MessageInfo();
		msgmi.fromid = "bbs_"+bbs.id;
		msgmi.tag = UUID.randomUUID().toString();
		msgmi.fromname = mLogin.nickname;
		msgmi.fromurl = mLogin.headsmall;
		msgmi.toid = bbs.id;
		msgmi.toname = getFromName();
		msgmi.tourl = bbs.headsmall;
		msgmi.imgWidth = messageInfo.imgWidth;
		msgmi.imgHeight = messageInfo.imgHeight;
		if(type == MessageType.VOICE){
			msgmi.voiceUrl = messageInfo.voiceUrl;
			msgmi.voicetime = messageInfo.voicetime;
		}else if(type == MessageType.PICTURE){
			msgmi.imgUrlS = messageInfo.imgUrlS;
		}
		msgmi.typefile = messageInfo.typefile;
		msgmi.bid = bbs.id;
		msgmi.readState = 1;
		insertMessage(msgmi);
		insertSession();
	}
	/**
	 * 重发文件
	 * @param messageInfo
	 */
	private void resendFile(BbsReplyInfo messageInfo){
		try {
			sendFilePath(messageInfo, 1);
		} catch (Exception e) {
			Log.d(TAG, "resendVoice:", e);
			showToast(mContext.getString(R.string.resend_failed));
		}
	}
	private synchronized void downVoice(final BbsReplyInfo msg, final int type){
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

	private void downVoiceSuccess(final BbsReplyInfo msg, final int type){
		if(type == 1 && playListener.getMessageTag().equals(msg.id)){
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
			hideSoftKeyboard(mToggleBtn);
		} else {
			mContentEdit.setVisibility(View.VISIBLE);
			mVoiceSendBtn.setVisibility(View.GONE);
			hideSoftKeyboard(mToggleBtn);
		}
	}

	/* 重发信息 */
	private void btnResendAction(BbsReplyInfo messageInfo){
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
					return true;
				}
			}
		}
		return super.dispatchKeyEvent(event);
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
						if( messageInfos != null && messageInfos.size() != 0){
							mListView.setSelection(messageInfos.size() - 1);
						}
					}
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
				case MSG_CHECK_FAVORITE_STATUS:
					IMJiaState favoriteResult = (IMJiaState)msg.obj;
					if(favoriteResult == null){
						Toast.makeText(mContext, R.string.commit_dataing, Toast.LENGTH_LONG).show();
						return;
					}
					if(favoriteResult.code!=0){
						Toast.makeText(mContext, favoriteResult.errorMsg, Toast.LENGTH_LONG).show();
						return;
					}
					break;
				case DEL_BBS_REPLY_SUCCESS:
					String ids = (String)msg.obj;
					String[] idList = ids.split(",");
					for(String s:idList){
						for (int i = messageInfos.size() - 1; i >= 0; i--) {
							if (messageInfos.get(i).id.equals(s)) {
								messageInfos.remove(i);
							}
						}
					}
					mListView.setSelection(messageInfos.size());
					mAdapter.notifyDataSetChanged();
					break;
				case REFUCH_DATA:
					BbsReplyInfo br= (BbsReplyInfo)msg.obj;
					if(br!=null){
						addMessageInfo((BbsReplyInfo)msg.obj);
						return;
					}
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(messageInfos.size() - 1);
					break;
				case YOU_NOT_SPEAK:
					IMJiaState youNotSpeakResult = (IMJiaState)msg.obj;
					Toast.makeText(mContext,youNotSpeakResult.errorMsg , Toast.LENGTH_LONG).show();
					mChatBottmLayout.setVisibility(View.GONE);
					jinYan.setVisibility(View.VISIBLE);
					break;
				case NOT_SPEAK:
					IMJiaState notSpeakResult = (IMJiaState)msg.obj;
					Toast.makeText(mContext,notSpeakResult.errorMsg , Toast.LENGTH_LONG).show();
					if(!mLogin.uid.equals(bbs.uid)){
						mChatBottmLayout.setVisibility(View.GONE);
						jinYan.setVisibility(View.VISIBLE);
						jinYan.setText("楼主禁言中");
					}
					sendBroadcast(new Intent(MyBbsListActivity.MSG_REFRESH_MOVIINF));
					break;
				case IS_NULL:
					IMJiaState isNullResult = (IMJiaState)msg.obj;
					BbsChatMainActivity.this.finish();
					Toast.makeText(mContext,isNullResult.errorMsg , Toast.LENGTH_LONG).show();
					sendBroadcast(new Intent(MyBbsListActivity.MSG_REFRESH_MOVIINF));
					break;
				case OPEN_SPEAK:
					mChatBottmLayout.setVisibility(View.VISIBLE);
					jinYan.setVisibility(View.GONE);
					break;
				case CLOSE_BBS:
					bbs.status=0;
					mChatBottmLayout.setVisibility(View.GONE);
					jinYan.setVisibility(View.VISIBLE);
					jinYan.setText("楼主已闭贴");
				case GlobalParam.SHOW_SCROLLREFRESH:
					getlastData(isShowLouZu?"1":"0");
					mContainer.onRefreshComplete();
					break;
				case LAST_DATA:
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(0);
					break;
				default:
					break;
			}
		}

	};
	/*
	 * 消息发送成功
	 */
	private void sendBroad2Save(BbsReplyInfo msg,boolean isForward){
		sendMessage(msg, 0,isForward);
		MessageInfo msgmi = new MessageInfo();
		msgmi.fromid = "bbs_"+bbs.id;
		msgmi.tag = UUID.randomUUID().toString();
		msgmi.fromname = mLogin.uid;
		msgmi.fromurl = mLogin.headsmall;
		msgmi.toid = bbs.id;
		msgmi.toname = getFromName();
		msgmi.tourl = bbs.headsmall;
		msgmi.typefile = msg.typefile;
		msgmi.typechat = mType;
		msgmi.content = msg.content;
		msgmi.time = System.currentTimeMillis();
		msgmi.readState = 1;
		msgmi.mLat = msg.mLat;
		msgmi.mLng = msg.mLng;
		msgmi.mAddress = msg.mAddress;
		msgmi.bid = bbs.id;
		insertMessage(msgmi);
		insertSession();
	}
	/*
	 * 发送消息
	 */
	private void sendMessage(final BbsReplyInfo msg, final int isResend,final boolean isForward){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().addBbsReply(msg);
						if(result != null && result.mState != null){
							Message message = new Message();
							if(result.mState.code == 0){
								if(msg.typefile == MessageType.VOICE&&result.mBbsReplyInfoList!=null &&result.mBbsReplyInfoList.size()>0){
									String voice = FeatureFunction.generator(result.mBbsReplyInfoList.get(0).voiceUrl);
									FeatureFunction.reNameFile(new File(msg.voiceUrl), voice);
								}
								if( result.mBbsReplyInfoList!=null &&  result.mBbsReplyInfoList.size()>0){
									msg.id = result.mBbsReplyInfoList.get(0).id;
									if(msg.typefile== MessageType.VOICE){
										msg.voiceUrl = result.mBbsReplyInfoList.get(0).voiceUrl;
									}
								}
								message.what = REFUCH_DATA;
								message.obj= msg;
								mHandler.sendMessage(message);
								return;
							}else if(result.mState.code == 1){
								message.what = YOU_NOT_SPEAK;
							}else if(result.mState.code == 2){
								message.what =  NOT_SPEAK;
							}else if(result.mState.code == 3){
								message.what =  IS_NULL;
							}else if(result.mState.code == 4){
								message.what =  CLOSE_BBS;
							}
							message.obj= result.mState;
							mHandler.sendMessage(message);
							return;
						}else if(result != null && result.mState != null){
							mHandler.sendEmptyMessage(SHOW_KICK_OUT_DIALOG);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
				Message message = new Message();
				message.what = SEND_FAILED;
				message.arg1 = isResend;
				message.obj = msg;
				mHandler.sendMessage(message);
			}
		}.start();
	}
	private void insertSession(){
		Session session = new Session();
		session.setFromId("bbs_"+bbs.id);
		session.name = getFromName();
		session.heading = bbs.headsmall;
		session.type = mType;
		session.lastMessageTime = System.currentTimeMillis();
		session.bid=bbs.id;
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		SessionTable table = new SessionTable(db);
		Session existSession = table.query(session.getFromId(), session.type);
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
	private void delBbsReply(final String ids){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().delBbsReply(ids);
						if(result != null && result.mState != null){
							if(result.mState.code == 0) {
								Message message = new Message();
								message.what = DEL_BBS_REPLY_SUCCESS;
								message.obj = ids;
								mHandler.sendMessage(message);
								return;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}
	private void addBbsProhibition(final String uid, final String bid){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().addBbsProhibition(uid,bid);
						if(result != null && result.mState != null){
							jinYanList.add(uid);
							Message message = new Message();
							message.what = GlobalParam.MSG_CHECK_FAVORITE_STATUS;
							result.mState.code=1;
							result.mState.errorMsg="禁言成功";
							message.obj = result.mState;
							mHandler.sendMessage(message);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}
	private void bbsProhibitionList(final String bid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().bbsProhibitionList(bid);
						if(result != null && result.mState != null){
							if(result.mState.code == 0&& result.mBbsReplyInfoList!=null ){
								if(jinYanList!=null && jinYanList.size()>0){
									jinYanList.clear();
								}
								List<BbsReplyInfo> tempList = result.mBbsReplyInfoList;
								for (BbsReplyInfo bbsReplyInfo : tempList) {
									jinYanList.add(bbsReplyInfo.uid);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	private void delBbsProhibition(final String uid, final String bid){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().delBbsProhibition(uid,bid);
						Message message = new Message();
						message.what = GlobalParam.MSG_CHECK_FAVORITE_STATUS;
						result.mState.code=1;
						result.mState.errorMsg="解除禁言成功";
						message.obj = result.mState;
						mHandler.sendMessage(message);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}
	/*
	 * 将消息显示在界面中
	 */
	private void addMessageInfo(BbsReplyInfo info){
		mVoiceSendBtn.setText(mContext.getString(R.string.pressed_to_record));
		mContentEdit.setHint(mContext.getString(R.string.input_message_hint));
		messageInfos.add(info);
		mAdapter.notifyDataSetInvalidated();
		if(messageInfos !=  null && messageInfos.size() != 0){
			mListView.setSelection(messageInfos.size() - 1);
		}
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
		flag = false;
		IMCommon.saveCamerUrl(mContext,"");
		if(mReaderImpl != null){
			mReaderImpl.unregisterRecordReceiver();
		}
		mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
		playListener.stop();

		/*++释放图片++*/
		if(messageInfos != null){
			for (int i = 0; i < messageInfos.size(); i++) {

				if(!TextUtils.isEmpty(messageInfos.get(i).headsmall)){
					ImageView headerView = (ImageView) mListView.findViewWithTag(messageInfos.get(i).headsmall);
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
					if(!messageInfos.get(i).imgUrlS.startsWith("http://")){
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

	@Override
	public void onChangeState(MyPullToRefreshListView container, int state) {
		mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());
		switch (state) {
			case MyPullToRefreshListView.STATE_LOADING:
				mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
				break;
		}
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

		public MyAdapter(){
			mInflater = (LayoutInflater)mContext.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			hashMap= new HashMap<String, View>();
		}

		@Override
		public int getCount() {
			return messageInfos.size();
		}

		@Override
		public BbsReplyInfo getItem(int arg0) {
			return messageInfos.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			int type = 0;
			final BbsReplyInfo messageInfo = messageInfos.get(position);
			if(messageInfo.uid.equals(mLogin.uid)){
				type = 0;
			}else{
				type = 1;
			}
			ViewHolder viewHolder = null;
			convertView = hashMap.get(messageInfo.id);
			if(convertView == null){
				if(1 == type){
					convertView = mInflater.inflate(R.layout.chat_talk_left, null);
				}else{
					convertView = mInflater.inflate(R.layout.chat_talk_right, null);
				}
				viewHolder = ViewHolder.getInstance(convertView);
				convertView.setTag(viewHolder);
				hashMap.put(messageInfo.id, convertView);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.imgMsgPhoto.setImageBitmap(null);
			viewHolder.imgMsgPhoto.setImageResource(R.drawable.normal);
			if(messageInfo.typefile == MessageType.VOICE ){
				//声音
				setOnLongClick(viewHolder.mRootLayout, position,0,null,messageInfo);
			}else{
				//文本
				setOnLongClick(viewHolder.txtMsg, position, 1, messageInfo.content, messageInfo);
				//图片
				setOnLongClick(viewHolder.imgMsgPhoto, position,0,null,messageInfo);
				//地图
				setOnLongClick(viewHolder.mapLayout,position,0,null,messageInfo);
				setOnLongClick(viewHolder.cardLayout,position,0,null,messageInfo);
				setOnLongClick(viewHolder.urlLayout,position,0,null,messageInfo);
			}
			if(position >0){
				bindView(viewHolder, messageInfo,messageInfos.get(position-1).time);
			}else{
				bindView(viewHolder, messageInfo,0);
			}

			return convertView;
		}

		private void setOnLongClick(View v, final int position, final int type,
                                    final String content, final BbsReplyInfo msg){
			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					showPromptDialog(position, type, content, msg);
					return true;
				}
			});
		}

		private void bindView(ViewHolder viewHolder, final BbsReplyInfo messageInfo,
				final long lasttime){
			final int type = messageInfo.uid.equals(mLogin.uid) ? 0 : 1;
			if(0 == type){
				viewHolder.nickName .setVisibility(View.GONE);
				viewHolder.imgSendState.setVisibility(View.GONE);
				viewHolder.imgSendState.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showResendDialog(messageInfo);
					}
				});
			}else{
				viewHolder.imgVoiceReadState.setVisibility(View.GONE);
				viewHolder.nickName .setVisibility(View.VISIBLE);
				viewHolder.nickName.setText(messageInfo.nickname);
			}
			if(messageInfo.uid.equals("0")){
				viewHolder.duihua.setVisibility(View.GONE);
				viewHolder.xitong.setVisibility(View.VISIBLE);
				viewHolder.xitongTextView.setText(messageInfo.content);
			}
			viewHolder.imgHead.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, UserInfoActivity.class);
					intent.putExtra("uid", messageInfo.uid);
					intent.putExtra("bbs", bbs);
					intent.putExtra("type", 2);
					mContext.startActivity(intent);
				}
			});
			if(!TextUtils.isEmpty(messageInfo.headsmall)){
				viewHolder.imgHead.setTag(messageInfo.headsmall);
				mImageLoader.getBitmap(mContext, viewHolder.imgHead, null, messageInfo.headsmall, 0, false, false);
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
				int length = messageInfo.voicetime;
				float max = mContext.getResources().getDimension(R.dimen.voice_max_length);
				float min = mContext.getResources().getDimension(R.dimen.voice_min_length);
				int width = (int)min;
				if(length>= MIN_SECOND && length <= MAX_SECOND){
					width +=  (length - MIN_SECOND) * (int)((max - min) / (MAX_SECOND - MIN_SECOND));
				}else if(length > MAX_SECOND){
					width = (int)max;
				}
				//timeParams.addRule(RelativeLayout.CENTER_VERTICAL);
				LayoutParams param = new LayoutParams(width, FeatureFunction.dip2px(mContext, 48));
				if(type == 0){
					param.addRule(RelativeLayout.LEFT_OF, viewHolder.imgHead.getId());
					param.setMargins(0, FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 5));
				}else {
					param.addRule(RelativeLayout.RIGHT_OF, viewHolder.imgHead.getId());
					param.setMargins(FeatureFunction.dip2px(mContext, 5), FeatureFunction.dip2px(mContext, 25), 0, FeatureFunction.dip2px(mContext, 5));

				}
				param.addRule(RelativeLayout.BELOW, viewHolder.txtTime.getId());
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
				SpannableString ss = EmojiUtil.getExpressionString(getBaseContext(), messageInfo.content, EMOJIREX);
				//判断是否有链接
				String patternStr = "(http://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5])|((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
				Pattern pattern = Pattern.compile(patternStr);
				Matcher m = pattern.matcher(messageInfo.content);
				while(m.find()){
					final String urlStr = m.group();
					int start=messageInfo.content.indexOf(urlStr),end=start+urlStr.length();
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
				Log.e("BbsChatMain","urlpic="+urlpic);
				if(urlpic == null || urlpic.equals("")){
					urlpic = messageInfo.imgUrlL;
				}
				final String path = urlpic;

				viewHolder.txtVoiceNum.setVisibility(View.GONE);
				viewHolder.imgMsgPhoto.setTag(path);
				if(path.startsWith("http://")){
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
							intent.putExtra("bbsReply", messageInfo);
							intent.putExtra("fuid", messageInfo.uid);
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
					}
					viewHolder.imgMsgPhoto.setVisibility(View.VISIBLE);
					if(viewHolder.wiatProgressBar != null){
						viewHolder.imgMsgVoice.setVisibility(View.GONE);
						//viewHolder.imgMsgPhoto.setVisibility(View.GONE);
						viewHolder.txtMsg.setVisibility(View.GONE);
						viewHolder.wiatProgressBar.setVisibility(View.GONE);
					}
				}
				break;
			case MessageType.VOICE:
                viewHolder.videolayout.setVisibility(View.GONE);
				viewHolder.mapLayout.setVisibility(View.GONE);
				viewHolder.cardLayout.setVisibility(View.GONE);
				if(viewHolder.wiatProgressBar != null){
					viewHolder.wiatProgressBar.setVisibility(View.GONE);
				}
				viewHolder.imgMsgPhoto.setVisibility(View.GONE);
				viewHolder.imgMsgVoice.setVisibility(View.VISIBLE);
				viewHolder.txtMsg.setVisibility(View.GONE);
				viewHolder.mRootLayout.setTag(messageInfo);
				viewHolder.mRootLayout.setOnClickListener(playListener);
				viewHolder.txtVoiceNum.setVisibility(View.VISIBLE);
				viewHolder.txtVoiceNum.setText(messageInfo.voicetime + "''");
				//viewHolder.imgMsgVoice.setLayoutParams(new Rela)
				try {
					AnimationDrawable drawable = (AnimationDrawable) viewHolder.imgMsgVoice.getDrawable();
					if (playListener.getMessageTag().equals(messageInfo.id)) {
						drawable.start();
					}else {
						drawable.stop();
						drawable.selectDrawable(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
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
				final Card card = Card.getInfo(messageInfo.content);
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
				case MessageType.SHAREURL:
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
                    viewHolder.videolayout.setVisibility(View.GONE);
					viewHolder.urlLayout.setVisibility(View.VISIBLE);
					final ShareUrl shareUrl = ShareUrl.getInfo(messageInfo.content);
					if(shareUrl!=null){
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
					}
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
					viewHolder.urlLayout.setVisibility(View.GONE);
					final Video video = Video.getInfo(messageInfo.content);
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
		private void showResendDialog(final BbsReplyInfo messageInfo){
			MMAlert.showAlert(mContext, "", mContext.getResources().
					getStringArray(R.array.resend_item),
					null, new OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
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
		private LayoutParams mParams;
		private LayoutParams mVoiceTimeParams;
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

		RelativeLayout duihua,xitong;
		TextView xitongTextView;

		//链接
		RelativeLayout urlLayout;
		TextView title;
		ImageView imageView;
		//小视频
		RelativeLayout videolayout;
		ImageView videoImageView,playImageView;
		TextView videoPlayTime;
		public static ViewHolder getInstance(View view){
			ViewHolder holder = new ViewHolder();
			holder.mRootLayout = (RelativeLayout) view.findViewById(R.id.chat_talk_msg_info);
			holder.mParams = (LayoutParams)holder.mRootLayout.getLayoutParams();
			holder.txtTime = (TextView) view.findViewById(R.id.chat_talk_txt_time);
			holder.txtMsg = (TextView) view.findViewById(R.id.chat_talk_msg_info_text);

			holder.imgHead = (ImageView) view.findViewById(R.id.chat_talk_img_head);
			holder.imgMsgPhoto = (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_photo);
			holder.imgMsgVoice = (ImageView) view.findViewById(R.id.chat_talk_msg_info_msg_voice);

			holder.imgSendState = (ImageView) view.findViewById(R.id.chat_talk_msg_sendsate);
			holder.wiatProgressBar = (ProgressBar) view.findViewById(R.id.chat_talk_msg_progressBar);
			holder.txtVoiceNum = (TextView) view.findViewById(R.id.chat_talk_voice_num);
			holder.mVoiceTimeParams = (LayoutParams)holder.txtVoiceNum.getLayoutParams();
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

			holder.nickName = (TextView)view.findViewById(R.id.from_message_nickname);

			holder.duihua = (RelativeLayout)view.findViewById(R.id.duihua);
			holder.xitong = (RelativeLayout)view.findViewById(R.id.xitong);
			holder.xitongTextView = (TextView)view.findViewById(R.id.xitongtextview);

			holder.urlLayout = (RelativeLayout)view.findViewById(R.id.url);
			holder.title = (TextView) view.findViewById(R.id.url_text);
			holder.imageView = (ImageView) view.findViewById(R.id.image_url);
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


	private OnFocusChangeListener sendTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				hideEmojiGridView();
			}

		}
	};

	private OnClickListener sendTextClickListener = new OnClickListener() {

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

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
		return true;
	}
	/**
	 * pop 长按事件
	 * @param pos
	 * @param type 1-text 消息
	 * @param content
	 */
	private void showPromptDialog(final int pos, final int type, final String content, final BbsReplyInfo oldMsg){
		//normal_message_item
		String[] itemMenu =  new String[0];
		ArrayList<String> itemMenuArrayList = new ArrayList<String>();
		if(type == 1){
			itemMenuArrayList.add("复制");
		}
		if(bbs.type==1){
			if(bbs.uid.equals(mLogin.uid)||bbs.delchat==1){
				itemMenuArrayList.add("删除");
			}
			if(bbs.uid.equals(mLogin.uid)&& !oldMsg.uid.equals(mLogin.uid)){
				boolean isJinYan=true;
				for (String s : jinYanList) {
					if(s.equals(oldMsg.uid)){
						itemMenuArrayList.add("解除禁言");
						isJinYan=false;
					}
				}
				if(isJinYan){
					itemMenuArrayList.add("禁言");
				}
			}
		}else{
			if(bbs.uid.equals(mLogin.uid) &&mLogin.quId!=null&&mLogin.kai6Id.equals(mLogin.quId)){
				itemMenuArrayList.add("删除");
			}
			if(bbs.uid.equals(mLogin.uid)&& !oldMsg.uid.equals(mLogin.uid) &&mLogin.kai6Id.equals(mLogin.quId)){
				boolean isJinYan=true;
				for (String s : jinYanList) {
					if(s.equals(oldMsg.uid)){
						itemMenuArrayList.add("解除禁言");
						isJinYan=false;
					}
				}
				if(isJinYan){
					itemMenuArrayList.add("禁言");
				}
			}
		}
		itemMenu = itemMenuArrayList.toArray(new String[itemMenuArrayList.size()]);
		if(oldMsg.typefile == MessageType.CARD){
			itemMenu = new String[]{mContext.getResources().getString(R.string.forward)};
		}
		final String[] itemMenuShow = itemMenuArrayList.toArray(new String[itemMenuArrayList.size()]);
		MMAlert.showAlert(mContext, "", itemMenu,
				null, new OnAlertSelectId() {
			@Override
			public void onClick(int whichButton) {
				if(whichButton>=itemMenuShow.length){
					return;
				}
				if (itemMenuShow[whichButton].equals("删除")) {
					delBbsReply(oldMsg.id);
					playListener.stop();
				}
				if (itemMenuShow[whichButton].equals("禁言")) {
					addBbsProhibition(oldMsg.uid,bbs.id);
				}
				if (itemMenuShow[whichButton].equals("解除禁言")) {
					delBbsProhibition(oldMsg.uid,bbs.id);
					jinYanList.remove(oldMsg.uid);
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
					IMJiaState status = IMCommon.getIMInfo().favoreiteMoving(ownerUid, groupid, favoriteContent);
					IMCommon.sendMsg(mHandler, MSG_CHECK_FAVORITE_STATUS,status);
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
			break;
		case R.id.chat_box_expra_btn_favorite:
			hideExpra();
			Intent favoriteIntent = new Intent();
			favoriteIntent.setClass(mContext, MyFavoriteActivity.class);
			favoriteIntent.putExtra("isShow",false);
			//startActivityForResult(favoriteIntent, 1);
			startActivity(favoriteIntent);
			break;
		case R.id.chat_box_expra_btn_video:
			Intent mIntent = new Intent();
			mIntent.setClass(mContext, VideoMainActivity.class);
			startActivityForResult(mIntent, RECORD_VIDEO);
			break;
		case R.id.left_btn:
			hideSoftKeyboard();
			this.finish();
			break;
		case R.id.right_text_btn:
			if(isShowLouZu){
				max="0";
				messageInfos.clear();
				getData("0");
				isShowLouZu = false;
				mleftTextView.setBackground(getResources().getDrawable(R.drawable.louzu_false));
			}else{
				max="0";
				messageInfos.clear();
				getData("1");
				isShowLouZu = true;
				mleftTextView.setBackground(getResources().getDrawable(R.drawable.louzu_true));
			}

			break;
		case R.id.right_btn:
			// TODO: 2016/12/15 open bbsinfo
			Intent showIntent = new Intent();
			showIntent.setClass(mContext, BbsDetailActivity.class);
			showIntent.putExtra("data",bbs);
			startActivityForResult(showIntent,REQUEST_GET_BBS);
			break;
		case R.id.friends:
			Intent bbsUserIntent = new Intent();
			bbsUserIntent.setClass(mContext, bbsUserActivity.class);
			bbsUserIntent.putExtra("bbs",bbs);
			bbsUserIntent.putExtra("isallow","2");
			bbsUserIntent.putExtra("isvisitors", isvisitors);
			startActivity(bbsUserIntent);
			break;
		case R.id.friends_loop:
			Intent friendsloopIntent = new Intent();
			friendsloopIntent.setClass(mContext, FriensLoopActivity.class);
			friendsloopIntent.putExtra("bbs",bbs);
			friendsloopIntent.putExtra("isvisitors", isvisitors);
			startActivity(friendsloopIntent);
			break;
		case R.id.chat:
			Intent chatIntent = new Intent();
			chatIntent.setClass(mContext, BbsChatActivity.class);
			chatIntent.putExtra("bbs",bbs);
			startActivity(chatIntent);
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
