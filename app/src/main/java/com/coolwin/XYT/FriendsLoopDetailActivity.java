package com.coolwin.XYT;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.CommentUser;
import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Picture;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.adapter.EmojiAdapter;
import com.coolwin.XYT.adapter.EmojiUtil;
import com.coolwin.XYT.adapter.IMViewPagerAdapter;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.global.Utils;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.widget.CustomProgressDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.coolwin.XYT.R.id.zhuan_btn;

/**
 * 分享详情
 * @author dongli
 *
 */
public class FriendsLoopDetailActivity extends BaseActivity implements OnTouchListener, OnPageChangeListener {

	/*
	 * 定义全局变量
	 */
	private ImageView mHeaderIcon,mPicBtn;
	private TextView mNickNameTextView,mContentTextView,mTimeTextView,
	mAddressTextView,mZanTextView;
	private Button mDelBtn,mFunctionBtn,mSendBtn;
	private LinearLayout mSendImgLayout,mJumLayout,mZanLayout,mCommentLayout,
	mOtherLayout,mZanBtn,mCommentBtn,mShareweichat;
	private EditText mCommentEdit;

	private ImageView mZanIcon,mSplite;


	private ImageLoader mImageLoader;

	private LayoutInflater mInflater;

	private List<List<String>> mTotalEmotionList = new ArrayList<List<String>>();
	private ViewPager mViewPager;
	private IMViewPagerAdapter mEmotionAdapter;
	private LinkedList<View> mViewList = new LinkedList<View>();
	private LinearLayout mLayoutCircle;
	public int mPageIndxe = 0;
	private RelativeLayout mEmotionLayout;
	private ImageView mZanBtnIcon;



	private ScrollView mScrollView;
	private String mInputComment;
	private FriendsLoopItem item;
	private int mWidth,mSpliteWdith,mShareId;
	RelativeLayout urlLayout;
	ImageView imageUrlView;
	TextView titleView;
	RelativeLayout videolayout;
	ImageView videoImageView,playImageView;
	TextView videoPlayTime;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		setContentView(R.layout.share_detail_view);
		item = (FriendsLoopItem)getIntent().getSerializableExtra("item");
		if(item!=null){
			mShareId = item.id;
		}else{
			mShareId = getIntent().getIntExtra("shareId", 0);
		}

		IntentFilter filtern = new IntentFilter();
		filtern.addAction(GlobalParam.ACTION_REFRESH_MOVING_DETAIL);
		registerReceiver(mRefreshRceiver, filtern);
		initCompent();
	}


	/*
	 * 处理通知
	 */
	BroadcastReceiver mRefreshRceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent!=null){
				String action = intent.getAction();
				if(action.equals(GlobalParam.ACTION_REFRESH_MOVING_DETAIL)){
					getShareDetail();
				}
			}
		}
	};

	
	/*
	 * 实例化控件
	 */
	private void initCompent(){
		mInflater = (LayoutInflater)mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics mMetric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetric);
		mSpliteWdith = mMetric.widthPixels ;
		mWidth = mSpliteWdith- FeatureFunction.dip2px(mContext, 100);
		mImageLoader = new ImageLoader();
		setTitleContent(R.drawable.back_btn, 0, R.string.detail);
		mLeftBtn.setOnClickListener(this);

		mScrollView = (ScrollView)findViewById(R.id.scrollview);
		mScrollView.setOnTouchListener(this);
		mHeaderIcon = (ImageView)findViewById(R.id.friends_icon);
		mSplite= (ImageView)findViewById(R.id.c_z_splite);
		mZanBtnIcon = (ImageView)findViewById(R.id.zan_btn_icon);
		mNickNameTextView = (TextView)findViewById(R.id.name);
		mContentTextView= (TextView)findViewById(R.id.content);
		videolayout= (RelativeLayout)findViewById(R.id.video_layout);
		videoImageView= (ImageView) findViewById(R.id.chat_talk_msg_info_msg_video);
		playImageView= (ImageView)findViewById(R.id.play_video);
		videoPlayTime= (TextView) findViewById(R.id.play_video_time);
		mContentTextView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG,1,0 );
				return true;
			}
		});
		urlLayout= (RelativeLayout)findViewById(R.id.url);
		imageUrlView= (ImageView) findViewById(R.id.image_url);
		titleView= (TextView) findViewById(R.id.url_text);
		if(item!=null && item.url!=null&&!item.url.equals("")){
			urlLayout.setVisibility(View.VISIBLE);
			if(item.imageurl!=null &&!item.imageurl.equals("")){
				mImageLoader.getBitmap(mContext, imageUrlView, null,  item.imageurl,0,false,false);
			}
			titleView.setText(item.title);
			urlLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("url",item.url);
					intent.setClass(mContext, WebViewActivity.class);
					mContext.startActivity(intent);
				}
			});
		}
		if(item!=null &&item.video!=null && !item.video.equals("")){
			final Video video = Video.getInfo(item.video);
			if(video!=null){
				videolayout.setVisibility(View.VISIBLE);
				videoPlayTime.setText(video.time);
				if(video.image!=null && !video.image.equals("")){
					mImageLoader.getBitmap(mContext, videoImageView, null, video.image,0,false,false);
				}else{
					videoImageView.setImageBitmap(Utils.createVideoThumbnail(video.url));
				}
				playImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,VideoPlayMainActivity.class);
						intent.putExtra("vidoepath",video.url);
						mContext.startActivity(intent);
					}
				});
			}else{
				videolayout.setVisibility(View.GONE);
			}
		}else{
			videolayout.setVisibility(View.GONE);
		}
		mTimeTextView = (TextView)findViewById(R.id.time);
		mAddressTextView = (TextView)findViewById(R.id.location_addr);
		mAddressTextView.setOnClickListener(this);

		mZanTextView = (TextView)findViewById(R.id.zan_text);

		mDelBtn = (Button)findViewById(R.id.del_btn);
		mFunctionBtn = (Button)findViewById(R.id.function_btn);

		mDelBtn.setOnClickListener(this);
		mFunctionBtn.setOnClickListener(this);

		mSendImgLayout = (LinearLayout)findViewById(R.id.send_img_layout);
		mJumLayout = (LinearLayout)findViewById(R.id.jump_layout);
		mZanLayout = (LinearLayout)findViewById(R.id.zan_layout);
		mCommentLayout = (LinearLayout)findViewById(R.id.comment_layout);
		mOtherLayout = (LinearLayout)findViewById(R.id.other_layout);
		mZanBtn = (LinearLayout)findViewById(R.id.zan_btn);
		mCommentBtn = (LinearLayout)findViewById(R.id.comment_btn_layout);
		mShareweichat = (LinearLayout) findViewById(zhuan_btn);
		if(item.listpic!=null && item.listpic.size()>0){
			mShareweichat.setVisibility(View.VISIBLE);
		}else{
			mShareweichat.setVisibility(View.GONE);
		}
		mShareweichat.setOnClickListener(this);
		mZanBtn.setOnClickListener(this);
		mCommentBtn.setOnClickListener(this);

		mZanIcon = (ImageView)findViewById(R.id.zan_icon);

		mCommentEdit = (EditText)findViewById(R.id.edit);
		mCommentEdit.setOnFocusChangeListener(sendTextFocusChangeListener);
		mCommentEdit.setOnClickListener(sendTextClickListener);

		mSendBtn = (Button)findViewById(R.id.send);
		mPicBtn = (ImageView)findViewById(R.id.pic);

		mSendBtn.setOnClickListener(this);
		mPicBtn.setOnClickListener(this);


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

		setContent();
		getShareDetail();
	}




	/*
	 * 给控件设置问题在
	 */
	private void setContent(){
		if(item == null){
			return;
		}
		if(item.uid.equals(IMCommon.getUserId(mContext))){
			mDelBtn.setVisibility(View.VISIBLE);
		}
		if(item.headsmall!=null && !item.headsmall.equals("")){
			mImageLoader.getBitmap(mContext, mHeaderIcon, null,item.headsmall,0, false,true);
		}

		mNickNameTextView.setText(item.nickname);
		mContentTextView.setText(item.content);
		if(item.content!=null && !item.content.equals("")){
			mContentTextView.setVisibility(View.VISIBLE);
		}
		mTimeTextView.setText(FeatureFunction.calculaterReleasedTime(mContext, new Date((item.createtime*1000)),
				item.createtime*1000,0));
		mAddressTextView.setText(item.address);
		if(item.address!=null && !item.address.equals("")){
			mAddressTextView.setVisibility(View.VISIBLE);
		}
		showImage();
		showOtherItem();
	}

	/*
	 * 显示图片
	 */
	private void showImage(){
		if(mSendImgLayout == null || item.listpic == null
				|| item.listpic.size()<0){
			return;
		}
		if(mSendImgLayout !=null && mSendImgLayout.getChildCount()>0){
			mSendImgLayout.removeAllViews();
		}

		if(item.listpic != null){
			int rows = item.listpic.size() % 3 == 0 ? item.listpic.size() / 3 : item.listpic.size() / 3 + 1;
			int padding = FeatureFunction.dip2px(mContext, 2);
			for (int i = 0; i < rows; i++) {
				LinearLayout layout = new LinearLayout(mContext);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				for (int j = 0; j < 3; j++) {
					final int pos = i * 3 + j;

					if(pos < item.listpic.size()){
						View view = mInflater.inflate(R.layout.picture_item, null);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth / 3, mWidth / 3);
						view.setLayoutParams(params);

						view.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG,2,pos );
								return true;
							}
						});

						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext, ShowMultiImageActivity.class);
								intent.putExtra("share", item);
								intent.putExtra("pos", pos);
								intent.putExtra("hide", 1);
								mContext.startActivity(intent);
							}
						});
						view.setPadding(padding, padding, padding, padding);
						ImageView imageView = (ImageView) view.findViewById(R.id.pic);
						if(!TextUtils.isEmpty(item.listpic.get(pos).smallUrl)){
							imageView.setTag(item.listpic.get(pos).smallUrl);

							if(mImageLoader.getImageBuffer().get(item.listpic.get(pos).smallUrl) == null){
								imageView.setImageBitmap(null);
								imageView.setImageResource(R.drawable.normal);
							}

							mImageLoader.getBitmap(mContext, imageView, null, item.listpic.get(pos).smallUrl, 0, false, false);
						}else {
							imageView.setImageResource(R.drawable.normal);
						}

						layout.addView(view);
					}

				}
				mSendImgLayout.addView(layout);
			}
		}
	}

	/*
	 * 显示赞和评论内容
	 */
	private void showOtherItem(){
		if((item.praiselist == null || item.praiselist.size()<=0)
				&& (item.replylist == null || item.replylist.size()<=0)){
			mOtherLayout.setVisibility(View.GONE);
		}else{
			mOtherLayout.setVisibility(View.VISIBLE);
		}

		if((item.praiselist!=null && item.praiselist.size()>0)
				&& (item.replylist!=null && item.replylist.size()>0)){
			mSplite.setVisibility(View.VISIBLE);
		}else{
			mSplite.setVisibility(View.GONE);
		}
		if(mZanLayout!=null && mZanLayout.getChildCount()>0){
			mZanLayout.removeAllViews();
		}

		int itemWidth = FeatureFunction.dip2px(mContext, 40);
		//赞
		if (item.praiselist!=null) {
			List<CommentUser> zanList = item.praiselist;
			if (zanList!=null && zanList.size()>0) {
				mZanIcon.setVisibility(View.VISIBLE);
				TextView tv = new TextView(mContext);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				tv.setLayoutParams(param);
				String userName="";
				for (int i = 0; i < zanList.size(); i++) {
					userName+=zanList.get(i).nickname+",";
				}
				userName = userName.substring(0,userName.length()-1);
				SpannableString spannableString = new SpannableString(userName);
				for (int i = 0; i < zanList.size(); i++) {
					final int pos = i;
					int start=i-1<0?0:userName.indexOf(",",userName.indexOf(zanList.get(i-1).nickname));
					int end;
					if (zanList.get(i).nickname==null) {
						end  = start+1;
					}else{
						end  = start+zanList.get(i).nickname.length();
					}
					end= end<start?start:end;
					spannableString.setSpan(new ClickableSpan() {
						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(mContext.getResources().getColor(R.color.application_friends_loop_user_name));
							ds.setUnderlineText(false);      //设置下划线
						}
						@Override
						public void onClick(View widget) {
							Intent profileAlbumIntent = new Intent();
							profileAlbumIntent.setClass(mContext,MyAlbumActivity.class);
							profileAlbumIntent.putExtra("toUserID",item.praiselist.get(pos).uid);
							mContext.startActivity(profileAlbumIntent);
						}
					},start,end , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				tv.setText(spannableString);
				tv.setMovementMethod(LinkMovementMethod.getInstance());
				mZanLayout.addView(tv);
			}else{
				mZanIcon.setVisibility(View.GONE);
			}
		}else{
			mZanIcon.setVisibility(View.GONE);
		}

		//评论

		if(mCommentLayout!=null && mCommentLayout.getChildCount()>0){
			mCommentLayout.removeAllViews();
		}

		if(item.replylist!=null){

			List<CommentUser> commentList = item.replylist;
			if (commentList!=null && commentList.size()>0) {
				mCommentLayout.setVisibility(View.VISIBLE);
				for (int i = 0; i < commentList.size(); i++) {
					View view = mInflater.inflate(R.layout.comment_moving_item, null);
					ImageView userIcon = (ImageView)view.findViewById(R.id.user_icon);
					TextView ueserName = (TextView)view.findViewById(R.id.user_name);
					TextView time = (TextView)view.findViewById(R.id.time);
					if(commentList.get(i).headsmall!=null && !commentList.get(i).headsmall.equals("")){
						mImageLoader.getBitmap(mContext, userIcon, null, commentList.get(i).headsmall, 0, false, true);
					}
					//					if(commentList.get(i).){
					//
					//					}

					ueserName.setText(commentList.get(i).nickname);
					time.setText(FeatureFunction.calculaterReleasedTime(mContext, new Date((commentList.get(i).createtime*1000)),
							commentList.get(i).createtime*1000, 0));
					if(commentList.get(i).content!=null && !commentList.get(i).content.equals("")){
						TextView content = (TextView)view.findViewById(R.id.content);
						content.setText(EmojiUtil.getExpressionString(mContext, commentList.get(i).content, "emoji_[\\d]{0,3}"));
					}
					final int pos = i;
					view.setOnClickListener(new OnClickListener() {
						Dialog mPhoneDialog = new Dialog(mContext, R.style.dialog);
						@Override
						public void onClick(View v) {
							if (item.replylist.get(pos).uid.equals(IMCommon.getUserId(mContext))) {
								LayoutInflater factor = (LayoutInflater) mContext
										.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View serviceView = factor.inflate(R.layout.card_dialog, null);
								mPhoneDialog.setContentView(serviceView);
								mPhoneDialog.show();
								mPhoneDialog.setCancelable(false);
								mPhoneDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT
										, RelativeLayout.LayoutParams.WRAP_CONTENT);
								final TextView phoneEdit=(TextView)serviceView.findViewById(R.id.card_title);
								phoneEdit.setText("删除该评论");
								Button okBtn=(Button)serviceView.findViewById(R.id.yes);
								okBtn.setText("确定");
								okBtn.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (mPhoneDialog!=null) {
											mPhoneDialog.dismiss();
										}
										new Thread(){
											public void run() {
												try {
													IMCommon.getIMInfo().deleteReply(item.replylist.get(pos).id,item.id);
													item.replylist.remove(pos);
													mHandler.post(new Runnable() {
														@Override
														public void run() {
															sendBroadcast(new Intent(GlobalParam.ACTION_REFRESH_MOVING_DETAIL));
														}
													});
												} catch (Resources.NotFoundException e) {
													e.printStackTrace();
												} catch (IMException e) {
													e.printStackTrace();
												}catch (Exception e) {
													e.printStackTrace();
												}
											}
										}.start();
									}
								});
								Button Cancel = (Button)serviceView.findViewById(R.id.no);
								Cancel.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (mPhoneDialog!=null) {
											mPhoneDialog.dismiss();
										}
									}
								});
								return;
							}
						}
					});
					mCommentLayout.addView(view);
				}
			}else{
				mCommentLayout.setVisibility(View.GONE);
			}
		}else{
			mCommentLayout.setVisibility(View.GONE);
		}
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


	private View.OnFocusChangeListener sendTextFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				// 文本框得到焦点，隐藏附加信息和表情列表
				hideEmojiGridView();
			}

		}
	};

	private View.OnClickListener sendTextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// 获取到文本框的点击事件隐藏表情
			hideEmojiGridView();
		}
	};


	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			FriendsLoopDetailActivity.this.finish();
			break;
		case R.id.del_btn:
			delShare();
			break;
		case R.id.function_btn:
			mJumLayout.setVisibility(View.VISIBLE);
			if(item.ispraise == 1){
				mZanTextView.setText(mContext.getResources().getString(R.string.cancel));
				mZanBtnIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.friends_zan_btn));
			}else{
				mZanTextView.setText(mContext.getResources().getString(R.string.zan_for_me));
				mZanBtnIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.friends_cancle_zan_btn));
			}
			TranslateAnimation animation = new TranslateAnimation(mSpliteWdith, 0, 0, 0);
			animation.setDuration(500);
			animation.setAnimationListener(mAnimationListener);
			mJumLayout.startAnimation(animation);
			break;
		case R.id.pic:
			showEmojiGridView();
			break;
		case R.id.comment_btn_layout:
			mJumLayout.setVisibility(View.GONE);
			mCommentEdit.setFocusable(true);
			mCommentEdit.setFocusableInTouchMode(true);
			mCommentEdit.requestFocus();
			InputMethodManager inputManager =(InputMethodManager)mCommentEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(mCommentEdit, 0);
			break;
		case R.id.zan_btn:
			mJumLayout.setVisibility(View.GONE);
			zan();
			break;
		case R.id.zhuan_btn:
			mProgressDialog = new CustomProgressDialog(mContext);
			mProgressDialog.show();
			if(item.uid.equals(IMCommon.getUserId(mContext))){
				new SaveImage(item.listpic,item.content,item.shopurl).execute();
			}else{
				new SaveImage(item.listpic,item.content,item.shopurl+"&tid="+ IMCommon.getLoginResult(mContext).tid).execute();
			}
				break;
		case R.id.send:
			mInputComment = mCommentEdit.getText().toString();
			if (mInputComment == null || mInputComment.equals("")) {
				Toast.makeText(mContext,mContext.getResources().getString(R.string.please_input_comment_contnet), Toast.LENGTH_LONG).show();
				return;
			}
			comment(mInputComment);
			hideEmojiGridView();
			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
				InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			break;
		case R.id.location_addr:
			if(item.lat!=0&& item.lng!=0){
				Intent intent  = new Intent(mContext, LocationActivity.class);
				intent.putExtra("show", true);
				intent.putExtra("lat", item.lat);
				intent.putExtra("lng",item.lng);
				intent.putExtra("addr", item.address);
				mContext.startActivity(intent);
			}
			break;

		default:
			break;
		}
	}


	/*
	 * 获取分享详情
	 */
	private void getShareDetail(){
		if(!IMCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					FriendsLoopItem shareDetail = IMCommon.getIMInfo().shareDetail(mShareId);
					if(shareDetail !=null){
						IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_LOAD_DATA,shareDetail);
					}
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}



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

					IMCommon.sendMsg(mBaseHandler,BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					IMJiaState status = IMCommon.getIMInfo().shareReply(item.id,item.uid,content);
					if(status !=null && status.code == 0){
						mHandler.sendEmptyMessage(GlobalParam.MSG_CLEAR_EDITEXIT_STATUS);
						getShareDetail();
					}else{
						IMCommon.sendMsg(mHandler, GlobalParam.MSG_COMMENT_STATUS,status);
					}

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
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					IMJiaState status = IMCommon.getIMInfo().sharePraise(item.id);
					if(status!=null && status.code == 0){
						getShareDetail();
					}else{
						IMCommon.sendMsg(mHandler, GlobalParam.MSG_ZAN_STATUS,status);
					}
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
	private void delShare(){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.send_request));
					IMJiaState status = IMCommon.getIMInfo().deleteShare(item.id,"0");
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
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG:

				break;
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				FriendsLoopItem shareDetail= (FriendsLoopItem)msg.obj;
				if(shareDetail!=null){
					item = shareDetail;
				}
				setContent();
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
				sendBroadcast(new Intent(GlobalParam.ACTION_DESTROY_ACTIVITY_DEL_SHARE));
				sendBroadcast(new Intent(FriensLoopActivity.MSG_REFRESH_MOVIINF));
				FriendsLoopDetailActivity.this.finish();
				break;
			case GlobalParam.MSG_CLEAR_EDITEXIT_STATUS:
				mInputComment  ="";
				mCommentEdit.setText("");
				break;
			default:
				break;
			}
		}

	};


	/*
	 * 隐藏键盘
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){



			/*if(mBqLayout.getVisibility() == View.VISIBLE){
				mBqLayout.setVisibility(View.GONE);
			}*/
			if(mEmotionLayout.getVisibility() == View.VISIBLE ){
				hideEmojiGridView();
			}
			mJumLayout.setVisibility(View.GONE);
			if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
				InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
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
	private class SaveImage extends AsyncTask<String, Void, String> {
		private List<Picture> imgurl;
		private String content;
		private String shopurl;
		public SaveImage(List<Picture> imgurl, String content, String shopurl){
			this.imgurl = imgurl;
			this.content = content;
			this.shopurl = shopurl;
		}
		private List<File> files= new ArrayList<File>();
		@Override
		protected String doInBackground(String... params) {
			int imagename=0;
			for(Picture iurl:imgurl){
				imagename++;
				try {
					String sdcard = Environment.getExternalStorageDirectory().toString();
					File file = new File(sdcard + "/Download");
					if (!file.exists()) {
						file.mkdirs();
					}
					int idx = iurl.originUrl.lastIndexOf(".");
					String ext = iurl.originUrl.substring(idx);
					file = new File(sdcard + "/Download/" + imagename+"_" + ext);
					InputStream inputStream = null;
					URL url = new URL(iurl.originUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(20000);
					if (conn.getResponseCode() == 200) {
						inputStream = conn.getInputStream();
					}
					byte[] buffer = new byte[4096];
					int len = 0;
					FileOutputStream outStream = new FileOutputStream(file);
					while ((len = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					outStream.close();
					Bitmap bitmap= BitmapFactory.decodeFile(sdcard + "/Download/" + imagename+"_" + ext);
					if(bitmap.getWidth()>1024 || bitmap.getHeight()>1024){
						files.clear();
						return "";
					}
					bitmap = addLogo(bitmap,Create2DCode(shopurl));
					Log.e("doInBackground","bitmap="+bitmap);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
					files.add(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			if(mProgressDialog!=null){
				mProgressDialog.dismiss();
			}
			if(files.size()==0){
				Toast.makeText(mContext,"未选择图片或图片过大无法分享", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(Intent.ACTION_SEND);
			ArrayList<Uri> imageUris = new ArrayList<Uri>();
			intent.setType("image/*");
			for (File f : files) {
				imageUris.add(Uri.fromFile(f));
			}
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
			intent.putExtra(Intent.EXTRA_SUBJECT, content);
			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			intent.putExtra(Intent.EXTRA_TEXT, content);
			intent.putExtra("Kdescription", content);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(Intent.createChooser(intent, "分享"));
		}
	}
	public Bitmap Create2DCode(String str) throws WriterException {
		//生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 400, 400);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		//二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if(matrix.get(x, y)){
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}

			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	/**
	 * 在二维码中间添加Logo图案
	 */
	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}
		if (logo == null) {
			return src;
		}
		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();
		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}
//		int[] size =getlessImage(srcWidth,srcHeight);
//		srcWidth = size[0];
//		srcHeight = size[1];
		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}
		src = src.copy(Bitmap.Config.ARGB_8888, true);
		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(src);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth, srcHeight);
			canvas.drawBitmap(logo, (srcWidth - logoWidth), (srcHeight - logoHeight), null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.printStackTrace();
		}
		return bitmap;
	}


	AnimationListener mAnimationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			mJumLayout.setVisibility(View.VISIBLE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
		
		}
	};


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
	protected void onDestroy() {
		unregisterReceiver(mRefreshRceiver);
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



}
