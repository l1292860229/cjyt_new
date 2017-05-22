package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
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
import android.view.ViewGroup;
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

import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Picture;
import com.coolwin.XYT.Entity.PopItem;
import com.coolwin.XYT.Entity.ShowFriendsLoopUser;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.FriendsLoopDetailActivity;
import com.coolwin.XYT.LocationActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.ShowMultiImageActivity;
import com.coolwin.XYT.VideoPlayMainActivity;
import com.coolwin.XYT.WebViewActivity;
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

/**
 * 朋友圈适配器
 * @author dongli
 *
 */
public class MyAlbum2Adapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private List<FriendsLoopItem> mData;
	private Context mContext;
	private ImageLoader mImageLoader;
	private List<PopItem> mPopMenuString = new ArrayList<PopItem>();
	private Handler mHandler;
	private boolean mIsBusy = false;
	private int mWidth,mSpliteWdith;
	protected CustomProgressDialog mProgressDialog;

	public MyAlbum2Adapter(Context context, List<FriendsLoopItem> data, Handler handler, DisplayMetrics metric){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mSpliteWdith = metric.widthPixels;
		mWidth = mSpliteWdith - FeatureFunction.dip2px(mContext, 100);

		mData = data;
		mImageLoader = new ImageLoader();
		mPopMenuString.add(new PopItem(1, "赞"));
		mPopMenuString.add(new PopItem(2, "评论"));
		mHandler = handler;
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
			convertView=mInflater.inflate(R.layout.myalbum_item, null);
			holder=new ViewHolder();
			holder.mContentTextView = (TextView) convertView.findViewById(R.id.content);
			holder.mPicLayout = (LinearLayout)convertView.findViewById(R.id.send_img_layout);
			holder.mTimeTextView = (TextView) convertView.findViewById(R.id.time);
			holder.mJumpLayout = (LinearLayout)convertView.findViewById(R.id.jump_layout);
			holder.mZanIconBtn = (LinearLayout)convertView.findViewById(R.id.zan_btn);
			holder.mZanTextView = (TextView)convertView.findViewById(R.id.zan_text);
			holder.commentView = (TextView)convertView.findViewById(R.id.comment);
			holder.mZanBtnIcon = (ImageView)convertView.findViewById(R.id.zan_btn_icon);
			holder.mLocationAddress = (TextView)convertView.findViewById(R.id.location_addr);
			holder.mDelBtn = (Button)convertView.findViewById(R.id.del_btn);
			holder.mCommentIconBtn = (LinearLayout)convertView.findViewById(R.id.comment_btn_layout);
			holder.urlLayout= (RelativeLayout)convertView.findViewById(R.id.url);
			holder.imageUrlView= (ImageView) convertView.findViewById(R.id.image_url);
			holder.titleView= (TextView) convertView.findViewById(R.id.url_text);
			holder.mBiaoQianTextView = (TextView)convertView.findViewById(R.id.biaoqian);

			holder.videolayout= (RelativeLayout)convertView.findViewById(R.id.video_layout);
			holder.videoImageView= (ImageView) convertView.findViewById(R.id.chat_talk_msg_info_msg_video);
			holder.playImageView= (ImageView)convertView.findViewById(R.id.play_video);
			holder.videoPlayTime= (TextView) convertView.findViewById(R.id.play_video_time);
			holder.shareweichat = (LinearLayout) convertView.findViewById(R.id.zhuan_btn);

			holder.mTag = position;
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}

//		if(item.uid.equals(IMCommon.getUserId(mContext))) {
//			holder.mDelBtn.setVisibility(View.VISIBLE);
//			holder.mDelBtn.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					IMCommon.sendMsg(mHandler, GlobalParam.MSG_DEL_FRIENDS_LOOP, position);
//				}
//			});
//		}else{
			holder.mDelBtn.setVisibility(View.GONE);
//		}
		convertView.findViewById(R.id.alllayout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, FriendsLoopDetailActivity.class);
				intent.putExtra("item", mData.get(position));
				mContext.startActivity(intent);
			}
		});
		holder.mCommentIconBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, FriendsLoopDetailActivity.class);
				intent.putExtra("item", mData.get(position));
				mContext.startActivity(intent);
			}
		});
		holder.mBiaoQianTextView.setText(mData.get(position).type);
		holder.mZanIconBtn.setOnClickListener(new OnClickListener() {
			boolean click = true;
			@Override
			public void onClick(View v) {
				if(click){
					holder.mZanTextView.setText(""+(item.praises+1));
					click=false;
				}else{
					holder.mZanTextView.setText(""+(item.praises));
					click=true;
				}
				zan(item.id);
			}
		});
		if(item.listpic!=null && item.listpic.size()>0){
			holder.shareweichat.setVisibility(View.VISIBLE);
			holder.shareweichat.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mProgressDialog = new CustomProgressDialog(mContext);
					mProgressDialog.show();
					if(item.uid.equals(IMCommon.getUserId(mContext))){
						new SaveImage(item.listpic,item.content,item.shopurl).execute();
					}else{
						new SaveImage(item.listpic,item.content,item.shopurl+"&tid="+ IMCommon.getLoginResult(mContext).tid).execute();
					}
				}
			});
		}else{
			holder.shareweichat.setVisibility(View.INVISIBLE);
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
			holder.mContentTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, FriendsLoopDetailActivity.class);
					intent.putExtra("item", mData.get(position));
					mContext.startActivity(intent);
				}
			});
		}else{
			holder.mContentTextView.setVisibility(View.GONE);
		}

		holder.mTimeTextView.setText(FeatureFunction.calculaterReleasedTime(mContext, new Date((item.createtime*1000)),item.createtime*1000,0));

		if (holder.mPicLayout!=null && holder.mPicLayout.getChildCount()>0) {
			holder.mPicLayout.removeAllViews();
		}
		holder.mPicLayout.setTag(item.id);
		if(item.listpic != null && item.listpic.size()>0){
			holder.mPicLayout.setVisibility(View.VISIBLE);
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
						LayoutParams params = new LayoutParams(mWidth / 3, mWidth / 3);
						view.setLayoutParams(params);
						view.setPadding(padding, padding, padding, padding);
						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext, ShowMultiImageActivity.class);
								intent.putExtra("share", mData.get(position));
								intent.putExtra("pos", 0);
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

		//赞
		if(item.praises>0){
			holder.mZanTextView.setText(""+item.praises);
		}
		//评论
		if(item.replys>0){
			holder.commentView.setText(""+item.replys);
		}
		return convertView;
	}
	private void zan(final int id){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						IMJiaState state = IMCommon.getIMInfo().sharePraise(id);
						if(state != null && state.code == 0){
						}else {
							Message msg=new Message();
							msg.what = GlobalParam.MSG_LOAD_ERROR;
							if(state != null && state.errorMsg != null && !state.errorMsg.equals("")){
								msg.obj = state.errorMsg;
							}else {
								msg.obj = mContext.getString(R.string.operate_failed);
							}
							mHandler.sendMessage(msg);
						}
					} catch (IMException e) {
						e.printStackTrace();
					}
				}else {
				}
			}
		}.start();
	}


	final static class ViewHolder { 
		int mTag;
		TextView mContentTextView;
		LinearLayout mPicLayout;
		TextView mTimeTextView;
		LinearLayout mJumpLayout;
		LinearLayout mZanIconBtn;
		TextView mZanTextView;
		TextView mBiaoQianTextView;
		ImageView mZanBtnIcon;
		TextView mLocationAddress;
		Button mDelBtn;
		LinearLayout mCommentIconBtn;
		RelativeLayout urlLayout;
		ImageView imageUrlView;
		TextView titleView;
		TextView commentView;

		//小视频
		RelativeLayout videolayout;
		ImageView videoImageView,playImageView;
		TextView videoPlayTime;
		//分享微信朋友圈
		LinearLayout shareweichat;
		@Override
		public int hashCode() {
			return  mContentTextView.hashCode()
					+ mPicLayout.hashCode() + mTimeTextView.hashCode()
					+ mLocationAddress.hashCode()
					+ mJumpLayout.hashCode()
					+ mZanIconBtn.hashCode() + mCommentIconBtn.hashCode()
					+ mZanTextView.hashCode() + mZanBtnIcon.hashCode()
					+ urlLayout.hashCode()
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
}
