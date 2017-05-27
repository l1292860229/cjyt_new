package com.coolwin.XYT.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.CommentUser;
import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.Picture;
import com.coolwin.XYT.Entity.PopItem;
import com.coolwin.XYT.Entity.ShowFriendsLoopUser;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.FriendsLoopDetailActivity;
import com.coolwin.XYT.LocationActivity;
import com.coolwin.XYT.MyAlbumActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.ShowMultiImageActivity;
import com.coolwin.XYT.VideoPlayMainActivity;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.global.Utils;
import com.coolwin.XYT.net.IMException;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.coolwin.XYT.R.id.company;
import static com.coolwin.XYT.R.id.url;

/**
 * 朋友圈适配器
 * @author dongli
 *
 */
public class FriendsLoopAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<FriendsLoopItem> mData;
	private Context mContext;
	private ImageLoader mImageLoader;
	private List<PopItem> mPopMenuString = new ArrayList<PopItem>();
	private Handler mHandler;
	private boolean mIsBusy = false;
	private int mWidth,mSpliteWdith;
	private Bbs bbs;
	protected CustomProgressDialog mProgressDialog;

	public FriendsLoopAdapter(Context context, List<FriendsLoopItem> data, Handler handler, DisplayMetrics metric, Bbs bbs){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mSpliteWdith = metric.widthPixels;
		mWidth = mSpliteWdith - FeatureFunction.dip2px(mContext, 40);

		mData = data;
		mImageLoader = new ImageLoader();
		mPopMenuString.add(new PopItem(1, "赞"));
		mPopMenuString.add(new PopItem(2, "评论"));
		mHandler = handler;
		this.bbs = bbs;
	}
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setData(List<FriendsLoopItem> list){
		this.mData = list;
	}

	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageLoader.getImageBuffer();
	}

	public void setFlagBusy(boolean isBusy){
		mIsBusy = isBusy;
		//notifyDataSetChanged();
	}


	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				ShowFriendsLoopUser item = (ShowFriendsLoopUser)msg.obj;
				if(item !=null && item.parentLayout!=null
						&& (item.list!=null && item.list.size()>0)){
				}
				break;
			case 1:
				ShowFriendsLoopUser showItem = (ShowFriendsLoopUser)msg.obj;
				if(showItem!=null && showItem.parentLayout!=null
						&& showItem.childLayout!=null	){
					showItem.parentLayout.addView(showItem.childLayout);
				}
				break;

			default:
				break;
			}
		}

	};


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final FriendsLoopItem item = mData.get(position);
		final ViewHolder holder;  
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.friends_loop_item, null);
			holder=new ViewHolder();  
			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.name);
			holder.mContentTextView = (TextView) convertView.findViewById(R.id.content);
			holder.mBiaoQianTextView = (TextView) convertView.findViewById(R.id.biaoqian);
			holder.mContentMoreTextView = (TextView) convertView.findViewById(R.id.content_more);
			holder.mPicLayout = (LinearLayout)convertView.findViewById(R.id.send_img_layout);
			holder.mTimeTextView = (TextView) convertView.findViewById(R.id.time);
			holder.mFunctionBtn = (Button) convertView.findViewById(R.id.function_btn);
			holder.mZanLayout = (LinearLayout)convertView.findViewById(R.id.zan_layout);
			holder.mCommentLayout = (LinearLayout)convertView.findViewById(R.id.comment_layout);
			holder.mHeaderIcon = (ImageView)convertView.findViewById(R.id.friends_icon);
			holder.mZanIcon = (ImageView)convertView.findViewById(R.id.zan_icon);
			holder.mZanIcon.setVisibility(View.GONE);
			holder.jobTextView = (TextView) convertView.findViewById(R.id.job);
			holder.companyTextView = (TextView) convertView.findViewById(company);
			holder.mJumpLayout = (LinearLayout)convertView.findViewById(R.id.jump_layout);
			holder.mZanIconBtn = (LinearLayout)convertView.findViewById(R.id.zan_btn);
			holder.mZanTextView = (TextView)convertView.findViewById(R.id.zan_text);
			holder.mCommentTextView = (TextView)convertView.findViewById(R.id.comment_text);
			holder.mZanBtnIcon = (ImageView)convertView.findViewById(R.id.zan_btn_icon);
			holder.mLocationAddress = (TextView)convertView.findViewById(R.id.location_addr);
			holder.mDelBtn = (Button)convertView.findViewById(R.id.del_btn);
			holder.mCommentIconBtn = (LinearLayout)convertView.findViewById(R.id.comment_btn_layout);

			holder.videolayout= (RelativeLayout)convertView.findViewById(R.id.video_layout);
			holder.videoImageView= (ImageView) convertView.findViewById(R.id.chat_talk_msg_info_msg_video);
			holder.playImageView= (ImageView)convertView.findViewById(R.id.play_video);
			holder.videoPlayTime= (TextView) convertView.findViewById(R.id.play_video_time);

			holder.mOtherLayout = (LinearLayout)convertView.findViewById(R.id.other_layout);
			holder.urlLayout= (RelativeLayout)convertView.findViewById(url);
			holder.imageUrlView= (ImageView) convertView.findViewById(R.id.image_url);
			holder.titleView= (TextView) convertView.findViewById(R.id.url_text);
			holder.shareweichat = (LinearLayout) convertView.findViewById(R.id.zhuan_btn);
			holder.mTag = position;
			convertView.setTag(holder);  
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}

		if(item.uid.equals(IMCommon.getUserId(mContext))
				||(bbs!=null && (bbs.deldynamic==1|| bbs.uid.equals(IMCommon.getUserId(mContext))))) {
			holder.mDelBtn.setVisibility(View.VISIBLE);
			holder.mDelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_DEL_FRIENDS_LOOP, position);
				}
			});
		}else{
			holder.mDelBtn.setVisibility(View.GONE);
		}
		if(item.video!=null && !item.video.equals("")){
			final Video video = Video.getInfo(item.video);
			if(video!=null){
				holder.videolayout.setVisibility(View.VISIBLE);
				holder.videoPlayTime.setText(video.time);
				if(video.image!=null && !video.image.equals("")){
					mImageLoader.getBitmap(mContext, holder.videoImageView, null, video.image,0,false,false);
				}else{
					holder.videoImageView.setImageBitmap(Utils.createVideoThumbnail(video.url));
				}
				holder.playImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,VideoPlayMainActivity.class);
						intent.putExtra("vidoepath",video.url);
						mContext.startActivity(intent);
					}
				});
			}else{
				holder.videolayout.setVisibility(View.GONE);
			}
		}else{
			holder.videolayout.setVisibility(View.GONE);
		}
		if(item.job!=null && !item.job.equals("")){
			holder.jobTextView.setVisibility(View.VISIBLE);
			holder.jobTextView.setText(item.job);
		}else{
			holder.jobTextView.setVisibility(View.GONE);
		}
		if(item.company!=null && !item.company.equals("")){
			holder.companyTextView.setVisibility(View.VISIBLE);
			holder.companyTextView.setText(item.company);
		}else{
			holder.companyTextView.setVisibility(View.GONE);
		}
//		if(item.showView == 1){
//			holder.mJumpLayout.setVisibility(View.VISIBLE);
//		}else{
//			holder.mJumpLayout.setVisibility(View.GONE);
//		}
		if(item.address!=null && !item.address.equals("")){
			holder.mLocationAddress.setVisibility(View.VISIBLE);
			holder.mLocationAddress.setText(item.address);
			holder.mLocationAddress.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent  = new Intent(mContext, LocationActivity.class);
					intent.putExtra("show", true);
					intent.putExtra("lat", item.lat);
					intent.putExtra("lng",item.lng);
					intent.putExtra("addr", item.address);
					intent.putExtra("fuid", item.uid);
					mContext.startActivity(intent);
				}
			});
		}else{
			holder.mLocationAddress.setVisibility(View.GONE);
		}
		holder.mUserNameTextView.setText(item.nickname);
		if(item.type!=null && !item.type.equals("")){
			holder.mBiaoQianTextView.setVisibility(View.VISIBLE);
			holder.mBiaoQianTextView.setText(item.type.replace("来自",""));
		}else{
			holder.mBiaoQianTextView.setVisibility(View.GONE);
			holder.mBiaoQianTextView.setText("");
		}
		if(item.url!=null &&!item.url.equals("")){
			holder.urlLayout.setVisibility(View.VISIBLE);
			if(item.imageurl!=null &&!item.imageurl.equals("")){
				mImageLoader.getBitmap(mContext, holder.imageUrlView, null,  item.imageurl,0,false,false);
			}
			holder.titleView.setText(item.title);
			holder.urlLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("url",item.url);
					intent.setClass(mContext, WebViewActivity.class);
					mContext.startActivity(intent);
				}
			});
		}else{
			holder.urlLayout.setVisibility(View.GONE);
		}
		if(item.content!=null && !item.content.equals("") && !item.content.equals("null")){
			holder.mContentTextView.setVisibility(View.VISIBLE);
			SpannableString ss = EmojiUtil.getExpressionString(mContext, item.content, "emoji_[\\d]{0,3}");
			//判断是否有链接
			String patternStr = "(http://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5])|((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher m = pattern.matcher(item.content);
			while(m.find()){
				final String urlStr = m.group();
				int start=item.content.indexOf(urlStr),end=start+urlStr.length();
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
						String http = "http";
						if(urlStr.contains(http)) {
							intent.putExtra("url", urlStr);
						}else{
							intent.putExtra("url", http+urlStr);
						}
						intent.setClass(mContext, WebViewActivity.class);
						mContext.startActivity(intent);
					}
				}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			holder.mContentTextView.setText(ss);
			holder.mContentTextView.setMovementMethod(LinkMovementMethod.getInstance());
			holder.mContentTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					final int lineCount = holder.mContentTextView.getLineCount();
					if(lineCount>6){
						holder.mContentMoreTextView.setVisibility(View.VISIBLE);
						holder.mContentMoreTextView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								boolean isClick= holder.mContentTextView.getMaxLines()>6?false:true;
								if (isClick) {
									holder.mContentTextView.setMaxLines(lineCount);
									holder.mContentMoreTextView.setText("收起");
								}else{
									holder.mContentTextView.setMaxLines(6);
									holder.mContentMoreTextView.setText("全文");
								}

							}
						});
					}else{
						holder.mContentMoreTextView.setVisibility(View.GONE);
					}
					holder.mContentTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
		}else{
			holder.mContentTextView.setVisibility(View.GONE);
			holder.mContentMoreTextView.setVisibility(View.GONE);
		}
		if (!mIsBusy) {
			if(item.headsmall!=null && !item.headsmall.equals("")){
				mImageLoader.getBitmap(mContext, holder.mHeaderIcon, null, item.headsmall,0,false,true);
				holder.mHeaderIcon.setTag(item.headsmall);
			}else{
				holder.mHeaderIcon.setImageResource(R.drawable.contact_default_header);
			}
		}else{
			if(item.headsmall!=null && !item.headsmall.equals("")){
				if(mImageLoader.getImageBuffer().containsKey(item.headsmall)){
					mImageLoader.getBitmap(mContext, holder.mHeaderIcon, null, item.headsmall, 0, false, true);
				}else{
					holder.mHeaderIcon.setImageResource(R.drawable.contact_default_header);
				}
			}else{
				holder.mHeaderIcon.setImageResource(R.drawable.contact_default_header);
			}

		}


		holder.mHeaderIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent profileAlbumIntent = new Intent();
				profileAlbumIntent.setClass(mContext,MyAlbumActivity.class);
				profileAlbumIntent.putExtra("toUserID",item.uid);
				mContext.startActivity(profileAlbumIntent);
			}
		});

		holder.mTimeTextView.setText(FeatureFunction.calculaterReleasedTime(mContext, new Date((item.createtime*1000)),item.createtime*1000,0));

		holder.mZanIconBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IMCommon.sendMsg(mHandler, GlobalParam.MSG_COMMINT_ZAN,position);
			}
		});

		holder.mCommentIconBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_BOTTOM_COMMENT_MENU,"0",position);
			}
		});

//		holder.mFunctionBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(item.ispraise == 1){
//					holder.mZanTextView.setText(mContext.getResources().getString(R.string.cancel));
//					holder.mZanBtnIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.friends_cancle_zan_btn));
//				}else if(item.ispraise == 0){
//					holder.mZanTextView.setText(mContext.getResources().getString(R.string.zan_for_me));
//					holder.mZanBtnIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.friends_zan_btn));
//				}
//				holder.mJumpLayout.setVisibility(View.VISIBLE);
//				TranslateAnimation animation = new TranslateAnimation(mSpliteWdith, 0, 0, 0);
//				animation.setDuration(500);
//				animation.setAnimationListener(mAnimationListener);
//				holder.mJumpLayout.startAnimation(animation);
//				IMCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_FRIENDS_LOOP_POP_STATUS, position);
//			}
//		});
		if(item.ispraise == 1){
			holder.mZanBtnIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.friends_loop_zan_icon));
		}else if(item.ispraise == 0){
			holder.mZanBtnIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.friends_cancle_zan_btn));
		}
		if (holder.mPicLayout!=null && holder.mPicLayout.getChildCount()>0) {
			holder.mPicLayout.removeAllViews();
		}
		holder.mPicLayout.setTag(item.id);
		if(item.listpic!=null && item.listpic.size()>0){
			holder.shareweichat.setVisibility(View.VISIBLE);
			holder.shareweichat.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mProgressDialog = new CustomProgressDialog(mContext);
					mProgressDialog.show();
					new SaveImage(item.listpic,item.content,item.shopurl+"&tid="+IMCommon.getLoginResult(mContext).kai6Id).execute();
				}
			});
		}else{
			holder.shareweichat.setVisibility(View.INVISIBLE);
		}
		if(item.listpic != null && item.listpic.size()>0){
			int listpicsize = item.listpic.size();
			int cos = 3;
			if(listpicsize==4){
				cos = 2;
			}
			holder.mPicLayout.setVisibility(View.VISIBLE);
			int rows = item.listpic.size() % cos == 0 ? item.listpic.size() / cos : item.listpic.size() / cos + 1;
			int padding = FeatureFunction.dip2px(mContext, 2);
			for (int i = 0; i < rows; i++) {
				LinearLayout layout = new LinearLayout(mContext);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				for (int j = 0; j < cos; j++) {
					final int pos = i * cos + j;

					if(pos < item.listpic.size()){
						View view = mInflater.inflate(R.layout.picture_item, null);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth / 3, mWidth / 3);
						if(listpicsize==1){
							params = new LinearLayout.LayoutParams( item.listpic.get(pos).width, item.listpic.get(pos).height);
						}
						view.setLayoutParams(params);
						view.setPadding(padding, padding, padding, padding);
						view.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG,item,2,pos );
								return true;
							}
						});

						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext, ShowMultiImageActivity.class);
								intent.putExtra("share", mData.get(position));
								intent.putExtra("hide", 1);
								intent.putExtra("pos", pos);
								mContext.startActivity(intent);
							}
						});
						ImageView imageView = (ImageView) view.findViewById(R.id.pic);
						if(!TextUtils.isEmpty(item.listpic.get(pos).smallUrl) && !mIsBusy){
							imageView.setTag(item.listpic.get(pos).smallUrl);

							if(mImageLoader.getImageBuffer().get(item.listpic.get(pos).smallUrl) == null){
								imageView.setImageBitmap(null);
								imageView.setImageResource(R.drawable.normal);
							}
							mImageLoader.getBitmap(imageView,null,item.listpic.get(pos).smallUrl,0, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.contact_default_header));
//							mImageLoader.getBitmap(mContext, imageView, null, item.listpic.get(pos).smallUrl, 0, false, false);
						}else {
							imageView.setImageResource(R.drawable.normal);
						}

						layout.addView(view);
					}

				}
				holder.mPicLayout.addView(layout);
			}
		}else{
			holder.mPicLayout.setVisibility(View.GONE);
		}

		holder.mContentTextView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_FRIENDS_FAVORITE_DIALOG,item,1,0 );
				return true;
			}
		});


		if((item.praiselist!=null && item.praiselist.size()>0)
				|| (item.replylist!=null && item.replylist.size()>0)){
			holder.mOtherLayout.setVisibility(View.VISIBLE);
		}else{
			holder.mOtherLayout.setVisibility(View.GONE);
		}

		if(holder.mZanLayout!=null && holder.mZanLayout.getChildCount()>0){
			holder.mZanLayout.removeAllViews();
		}

		//赞
		List<CommentUser> zanList = item.praiselist;
		if (zanList!=null && zanList.size()>0) {
			holder.mZanIcon.setVisibility(View.VISIBLE);
			holder.mZanTextView.setText(zanList.size()+"");
			TextView tv = new TextView(mContext);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(param);
//			tv.setBackground(mContext.getResources().getDrawable(R.drawable.friends_long_click_bg_color));
//			tv.setTextColor(mContext.getResources().getColor(R.color.application_friends_loop_user_name));
			String userName=" ";
			boolean isHide=item.praiselist.size()>5;
			int nameCount;
			String nameStr = "";
			if(isHide){
				nameCount=5;
				nameStr="等"+item.praiselist.size()+"个人";
			}else{
				nameCount=item.praiselist.size();
			}
			for (int i = 0; i < nameCount; i++) {
				userName+=item.praiselist.get(i).nickname+",";
			}
			SpannableString spannableString = new SpannableString(userName.substring(0,userName.length()-1)+nameStr);
			for (int i = 0; i < nameCount; i++) {
				final int pos = i;
				int start=i-1<0?0:userName.indexOf(",",userName.indexOf(item.praiselist.get(i-1).nickname));
				int end;
				if (item.praiselist.get(i).nickname==null) {
					end  = start+1;
				}else{
					end  = start+item.praiselist.get(i).nickname.length()+1;
				}
				end= end<start?start:end;
				spannableString.setSpan(new ClickableSpan() {
					@Override
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						ds.setColor(mContext.getResources().getColor(R.color.application_friends_loop_user_name_lun));
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
			holder.mZanLayout.addView(tv);
		}else{
			holder.mZanTextView.setText("赞");
			holder.mZanIcon.setVisibility(View.GONE);
		}

		//评论

		if(holder.mCommentLayout!=null && holder.mCommentLayout.getChildCount()>0){
			holder.mCommentLayout.removeAllViews();
		}
		holder.mCommentLayout.setTag(item.id);
		List<CommentUser> commentList = item.replylist ;
		if (commentList!=null && commentList.size()>0) {
			holder.mCommentTextView.setText(commentList.size()+"");
			boolean isHide=item.replylist.size()>20;
			int nameCount;
			if(isHide){
				nameCount=20;
			}else{
				nameCount=item.replylist.size();
			}
			for (int i = 0; i <nameCount; i++) {
				final int pos = i;
				LinearLayout layout = new LinearLayout(mContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layout.setLayoutParams(params);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(mContext);
				final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				tv.setLayoutParams(param);
				tv.setBackground(mContext.getResources().getDrawable(R.drawable.friends_long_click_bg_color));
				String replyStr;
				if(item.replylist.get(i).fuid==null||item.replylist.get(i).fuid.equals("0")){
					replyStr = item.replylist.get(i).nickname+":"+item.replylist.get(i).content;
				}else{
					replyStr = item.replylist.get(i).nickname+"回复"+item.replylist.get(i).fnickname+":"+item.replylist.get(i).content;
				}
				SpannableString spannableString = EmojiUtil.getExpressionString(mContext,replyStr, "emoji_[\\d]{0,3}");
				spannableString.setSpan(new ClickableSpan() {
					@Override
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						ds.setColor(mContext.getResources().getColor(R.color.application_friends_loop_user_name_lun));
						ds.setUnderlineText(false);      //设置下划线
					}
					@Override
					public void onClick(View widget) {
						Intent profileAlbumIntent = new Intent();
						profileAlbumIntent.setClass(mContext,MyAlbumActivity.class);
						profileAlbumIntent.putExtra("toUserID",item.replylist.get(pos).uid);
						mContext.startActivity(profileAlbumIntent);
					}
				},0,item.replylist.get(i).nickname.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				if(item.replylist.get(i).fuid!=null&&!item.replylist.get(i).fuid.equals("0")){
					int start = (item.replylist.get(i).nickname+"回复").length();
					int end  = start+item.replylist.get(i).fnickname.length();
					spannableString.setSpan(new ClickableSpan() {
						@Override
						public void updateDrawState(TextPaint ds) {
							super.updateDrawState(ds);
							ds.setColor(mContext.getResources().getColor(R.color.application_friends_loop_user_name_lun));
							ds.setUnderlineText(false);      //设置下划线
						}
						@Override
						public void onClick(View widget) {
							Intent profileAlbumIntent = new Intent();
							profileAlbumIntent.setClass(mContext,MyAlbumActivity.class);
							profileAlbumIntent.putExtra("toUserID",item.replylist.get(pos).fuid);
							mContext.startActivity(profileAlbumIntent);
						}
					},start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				tv.setText(spannableString);
				tv.setMovementMethod(LinkMovementMethod.getInstance());
				tv.setOnClickListener(new OnClickListener() {
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
														notifyDataSetChanged();
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
						IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_BOTTOM_COMMENT_MENU,item.replylist.get(pos),position);
					}
				});
				layout.addView(tv);
				layout.setOnClickListener(new OnClickListener() {
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
														notifyDataSetChanged();
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
						IMCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_BOTTOM_COMMENT_MENU,item.replylist.get(pos),position);
					}
				});
				holder.mCommentLayout.addView(layout);
			}
			if(isHide){
				LinearLayout layout = new LinearLayout(mContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layout.setLayoutParams(params);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(mContext);
				LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				tv.setLayoutParams(param);
				tv.setBackground(mContext.getResources().getDrawable(R.drawable.friends_long_click_bg_color));
				tv.setTextColor(mContext.getResources().getColor(R.color.about_website_color));
				tv.setText("更多");
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(mContext, FriendsLoopDetailActivity.class);
						intent.putExtra("item", mData.get(position));
						mContext.startActivity(intent);
					}
				});
				layout.addView(tv);
				holder.mCommentLayout.addView(layout);
			}
		}else{
			holder.mCommentTextView.setText("评论");
		}
		return convertView;
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
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(sdcard + "/Download/" + imagename+"_" + ext,options);
					options.inSampleSize = calculateInSampleSize(options, 1024,
							1024);
					options.inJustDecodeBounds = false;
					Bitmap bitmap= BitmapFactory.decodeFile(sdcard + "/Download/" + imagename+"_" + ext,options);
					if (bbs!=null&&IMCommon.getLoginResult(mContext).userDj.equals("0")) {
						bitmap = addLogo(bitmap,Create2DCode("http://pre.im/jmac?bid="+bbs.id));
					}else{
						bitmap = addLogo(bitmap,Create2DCode(shopurl));
					}
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
				Toast.makeText(mContext,"未选择图片无法分享", Toast.LENGTH_SHORT).show();
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
	public static int calculateInSampleSize(BitmapFactory.Options options,
											int reqWidth, int reqHeight) {
		// 原始图片的宽、高
		final int height = options.outHeight;
		final int width = options.outWidth;
		final int heightRatio = Math.round((float) height
				/ (float) reqHeight);
		final int widthRatio = Math.round((float) width / (float) reqWidth);
		int inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		return inSampleSize;
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
	private static int[] getlessImage(int w,int h){
		if(w>1024){
			return getlessImage(w/2,h/2);
		}
		if(h>1024){
			return getlessImage(w/2,h/2);
		}
		return new int[]{w,h};
	}
	final static class ViewHolder { 
		int mTag;
		TextView mUserNameTextView;
		TextView mBiaoQianTextView;
		TextView mContentTextView;
		TextView jobTextView;
		TextView companyTextView;
		TextView mContentMoreTextView;
		LinearLayout mPicLayout;
		LinearLayout mZanLayout;
		LinearLayout mCommentLayout;

		TextView mTimeTextView;
		TextView mLocationAddress;
		LinearLayout mJumpLayout;

		LinearLayout mZanIconBtn;
		LinearLayout mCommentIconBtn;
		TextView mZanTextView;
		TextView mCommentTextView;
		ImageView mZanBtnIcon;


		Button mFunctionBtn,mDelBtn;
		// PopWindows mPopWindows;
		ImageView mHeaderIcon;
		ImageView mZanIcon;
		
		private LinearLayout mOtherLayout;

		RelativeLayout urlLayout;
		ImageView imageUrlView;
		TextView titleView;
		//小视频
		RelativeLayout videolayout;
		ImageView videoImageView,playImageView;
		TextView videoPlayTime;

		//分享微信朋友圈
		LinearLayout shareweichat;
		@Override
		public int hashCode() {
			return this.mUserNameTextView.hashCode() + mContentTextView.hashCode() +mBiaoQianTextView.hashCode()
					+ mPicLayout.hashCode() + mTimeTextView.hashCode()
					+ mFunctionBtn.hashCode()//+mPopWindows.hashCode()
					+ mZanLayout.hashCode() + mLocationAddress.hashCode()
					+ mCommentLayout.hashCode() + mHeaderIcon.hashCode() 
					+ mZanIcon.hashCode() + mJumpLayout.hashCode()
					+ mZanIconBtn.hashCode() + mCommentIconBtn.hashCode()
					+ mZanTextView.hashCode() + mZanBtnIcon.hashCode()
					+ mOtherLayout.hashCode()+ urlLayout.hashCode()
					+ imageUrlView.hashCode()+ titleView.hashCode();
		}
	} 

	AnimationListener mAnimationListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			/*  if (!mIsShowPopView) {
                mMenuScrollView.setVisibility(View.VISIBLE);
            }else{
            	mMenuScrollView.setVisibility(View.GONE);
            }
			 */
			//moveListView(mIsShowTypeMenu);
		}
	};

}
