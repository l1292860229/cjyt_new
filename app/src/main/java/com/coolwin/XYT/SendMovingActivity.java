package com.coolwin.XYT;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.Bimp;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.ImageItem;
import com.coolwin.XYT.Entity.ImagePublicWay;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MapInfo;
import com.coolwin.XYT.Entity.MorePicture;
import com.coolwin.XYT.Entity.UploadImg;
import com.coolwin.XYT.adapter.UploadPicAdapter;
import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.dialog.MMAlert.OnAlertSelectId;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.global.Utils;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.widget.MyGridView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 发布一条动态信息
 * @author dongli
 *
 */
public class SendMovingActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	public static final int MSG_SHOW_IMAGE = 0x00023;
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 102;
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 103;
	private static final int RESQUEST_CODE = 100;
	private static final int RESQUEST_AREA_CODE = 104;
	public static final int REQUEST_GET_AllBITMAPS = 105;




	private EditText mContentEdit;


	private int IMAGE_MAX = 9;
	private MyGridView mGridView;
	private UploadPicAdapter mAdapter;
	private List<UploadImg> mImageList = new ArrayList<UploadImg>();
	private DisplayMetrics mMetrics;
	private int mWidth = 0;
	private ImageView mLocationIcon,mAreaIcon;
	private TextView mLocationAddress;
	private LinearLayout mLocationLayout,mAreaLayout;

	private String mInputTitle;

	private String mInputContetn;

	private String mLat,mLng,mAddress,mAreaUid;

	private String mJumpImageUrl;
	private List<Login> mSelectUserList ;

	private int mCheckId = 0;
	private RadioGroup sRadioGroup;
	private String type="微商";
	private RadioButton liveRB,qiyeRB,weishanRB,huodongRb;
	private LinearLayout showLayout;
	private CheckBox cb;
	private Bbs bbs;
	private String bid;
	private RelativeLayout urlLayout;
	private String url,urlImage,urlTitle;
	private ImageView url_image;
	private TextView url_title;
	private VideoView videoView;
	private String videoPath;
	private String videoTime="";
	private ToggleButton hecengeweimaBtn;
	private boolean isheceng=false;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				IMJiaState status = (IMJiaState)msg.obj;
				if (status == null) {
					Toast.makeText(mContext, R.string.commit_data_error, Toast.LENGTH_LONG).show();
					return;
				}
				String hintMsg = status.errorMsg;
				
				if(status.code!=0){
					if(hintMsg==null || hintMsg.equals("")){
						hintMsg = mContext.getResources().getString(R.string.send_moving_error);
					}
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
					return;
				}
				
				if(hintMsg == null || hintMsg.equals("")){
					hintMsg = mContext.getResources().getString(R.string.send_moving_success);
				}
				Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				SendMovingActivity.this.finish();
				sendBroadcast(new Intent(FriensLoopActivity.MSG_REFRESH_MOVIINF));
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_moving);
		mContext = this;
		mJumpImageUrl = getIntent().getStringExtra("moving_url");
		mMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		mWidth = mMetrics.widthPixels;
		bbs = (Bbs) getIntent().getSerializableExtra("bbs");
		if(bbs!=null){
			type = bbs.title;
			bid = bbs.id;
		}
		url= getIntent().getStringExtra("url");
		urlImage= getIntent().getStringExtra("imagePath");
		urlTitle= getIntent().getStringExtra("title");
		initCompent();
	}

	private void initCompent(){
		setRightTextTitleContent(R.drawable.back_btn, R.string.send, R.string.share);
		mLeftBtn.setOnClickListener(this);
		mRightTextBtn.setOnClickListener(this);
		videoView = (VideoView) findViewById(R.id.video);
		mContentEdit = (EditText)findViewById(R.id.content);
		hecengeweimaBtn= (ToggleButton)findViewById(R.id.heeweibtn);
		mContentEdit.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				mContentEdit.invalidate();
				mContentEdit.requestLayout();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
				int length = s.length();
				updateWordCount(length);         
			}

		});
		hecengeweimaBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					isheceng = true;
				}else{
					isheceng = false;
				}
			}
		});
		showLayout = (LinearLayout) findViewById(R.id.isshowlayout);
		if(bbs!=null){
			showLayout.setVisibility(View.VISIBLE);
		}
		cb = (CheckBox) findViewById(R.id.isshow);
		Login mLogin = IMCommon.getLoginResult(mContext);
		if (mLogin.userDj.equals("0")) {
			cb.setChecked(false);
			showLayout.setVisibility(View.GONE);
		}
		sRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		liveRB = (RadioButton) findViewById(R.id.live);
		qiyeRB = (RadioButton) findViewById(R.id.qiye);
		weishanRB = (RadioButton) findViewById(R.id.weishan);
		huodongRb = (RadioButton) findViewById(R.id.huodong);
		sRadioGroup.setOnCheckedChangeListener(new  RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				if(checkedId == liveRB.getId()){
					type = liveRB.getText().toString();
				}else if(checkedId == qiyeRB.getId()){
					type = qiyeRB.getText().toString();
				}else if(checkedId == weishanRB.getId()){
					type = weishanRB.getText().toString();
				}else if(checkedId == huodongRb.getId()){
					type = huodongRb.getText().toString();
				}
			}
		});
		mLocationIcon = (ImageView)findViewById(R.id.location_icon);
		mAreaIcon = (ImageView)findViewById(R.id.area_icon);
		mAreaLayout = (LinearLayout)findViewById(R.id.area_layout);
		mLocationLayout = (LinearLayout)findViewById(R.id.loacation_layout);
		mLocationLayout.setOnClickListener(this);
		mAreaLayout.setOnClickListener(this);
		
		mLocationAddress = (TextView)findViewById(R.id.loaction_addr);



		mGridView = (MyGridView) findViewById(R.id.gridview);
		mGridView.setOnItemClickListener(this);

		if(mJumpImageUrl!=null && !mJumpImageUrl.equals("")){
			mImageList.add(new UploadImg(mJumpImageUrl, 0));
		}
		mImageList.add(new UploadImg("", 1));

		mAdapter = new UploadPicAdapter(mContext, mImageList, mWidth);
		mGridView.setAdapter(mAdapter);
		urlLayout = (RelativeLayout) findViewById(R.id.url);
		if (url != null&& !url.equals("")) {
			urlLayout.setVisibility(View.VISIBLE);
			mGridView.setVisibility(View.GONE);
			url_image = (ImageView) findViewById(R.id.image_url);
			url_title= (TextView) findViewById(R.id.url_text);
			url_title.setText(urlTitle);
			ImageLoader mImageLoader = new ImageLoader();
			if(urlImage!=null &&!urlImage.equals("")){
				mImageLoader.getBitmap(mContext, url_image, null, urlImage, 0, false, false);
			}
			urlLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("url",url);
					intent.setClass(mContext, WebViewActivity.class);
					mContext.startActivity(intent);
				}
			});
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mImageList != null){
			for (int i = 0; i < mImageList.size(); i++) {
				if(!TextUtils.isEmpty(mImageList.get(i).mPicPath)){
					ImageView view = (ImageView) mGridView.findViewWithTag(mImageList.get(i).mPicPath);
					if(view != null){
						view.setImageBitmap(null);
					}
				}
			}
		}

		if(mAdapter != null){
			FeatureFunction.freeBitmap(mAdapter.getImageBuffer());
		}
	}


	private boolean  checkText(){
		boolean isCheck = true;
		String hinMsg = "";
		mInputContetn = mContentEdit.getText().toString();

		if ((mInputContetn == null || mInputContetn.equals(""))
				&& mImageList.size() == 1) {
			isCheck = false;
			hinMsg = mContext.getResources().getString(R.string.please_wirte_content);
		}

	
		if (!isCheck && hinMsg!=null && !hinMsg.equals("")) {
			Toast.makeText(mContext, hinMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck ;
	}
	/**
	 * 用字符串生成二维码
	 * @param str
	 * @return
	 * @throws WriterException
	 */
	private Bitmap eweima;
	public void Create2DCode(String str) throws WriterException {
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
		eweima =  bitmap;
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
		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}
		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth, srcHeight);
			canvas.drawBitmap(logo, (srcWidth - logoWidth), (srcHeight - logoHeight), null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;
	}
	private void sendMoving(){
		if (!IMCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		if (!checkText()) {
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMCommon.sendMsg(mBaseHandler,BASE_SHOW_PROGRESS_DIALOG,"正在提交数据,请稍后...");
					List<MorePicture> picList = new ArrayList<MorePicture>();
					String shopurl="http://"+ IMCommon.getLoginResult(mContext).url+"&token="+ IMCommon.getLoginResult(mContext).token+"&lbs="+ MainActivity.lbs;
					if (mImageList!=null && mImageList.size()>0 ) {

						for (int i = 0; i <mImageList.size(); i++) {
							if(mImageList.get(i).mType != 1){
								String key = "picture";
								if (i > 0) {
									int index = i+1;
									key = key+index;
								}
								if(mImageList.get(i).iseweima || isheceng){
									Bitmap bitmap= BitmapFactory.decodeFile(mImageList.get(i).mPicPath);
									bitmap = addLogo(bitmap,eweima);
									File file=new File(mImageList.get(i).mPicPath);//将要保存图片的路径
									try {
										String filePath = file.getParent()+"/"+ System.currentTimeMillis()+".jpg";
										BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
										bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
										bos.flush();
										bos.close();
										picList.add(new MorePicture(key,filePath));
									} catch (IOException e) {
										e.printStackTrace();
									}
								}else{
									picList.add(new MorePicture(key,mImageList.get(i).mPicPath));
								}
							}

						}
					}
					if(videoPath!=null && !videoPath.equals("")){
						picList.add(new MorePicture("file_upload", videoPath));
						picList.add(new MorePicture("file_upload2", Utils.createVideoThumbnailImagePath(videoPath)));
					}
					IMJiaState status = IMCommon.getIMInfo()
							.addShare(picList, mInputContetn, mLng,mLat, mAddress,
									mAreaUid,type,bid,cb.isChecked()?"1":"0",url,urlTitle,urlImage,videoTime,shopurl);

					IMCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,status);
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


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SendMovingActivity.this.finish();
			break;
		case R.id.loacation_layout:
			Intent intent = new Intent(this, LocationActivity.class);
			startActivityForResult(intent, RESQUEST_CODE);
			break;
		case R.id.area_layout:
			Intent areaIntent = new Intent(this, AreaActivity.class);
			if(mSelectUserList!=null && mSelectUserList.size()>0){
				areaIntent.putExtra("userlist",(Serializable)mSelectUserList);
			}
			areaIntent.putExtra("checkId", mCheckId);
			startActivityForResult(areaIntent, RESQUEST_AREA_CODE);
			break;
		case R.id.right_text_btn://发送一条动态信息
			try {
				if(bbs!=null && IMCommon.getLoginResult(mContext).userDj.equals("0")){
					Create2DCode("http://pre.im/jmac?bid="+bbs.id);
				}else{
					Create2DCode("http://"+ IMCommon.getLoginResult(mContext).url+"&token="+ IMCommon.getLoginResult(mContext).token+"&lbs="+ MainActivity.lbs);
				}
				if(bbs!=null){
					type=bbs.title;
				}
				sendMoving();
				Bimp.tempSelectBitmap.clear();
				ImagePublicWay.num = IMAGE_MAX;
			} catch (WriterException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
	private static final int RECORD_VIDEO = 2;
	private void selectImg(){
		MMAlert.showAlert(mContext, mContext.getResources().getString(R.string.select_image),
				mContext.getResources().getStringArray(R.array.camer_item),
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				Log.e("whichButton", "whichButton: "+whichButton);
				switch (whichButton) {
				case 0:
					Intent mIntent = new Intent();
					mIntent.setClass(mContext, VideoMainActivity.class);
					startActivityForResult(mIntent, RECORD_VIDEO);
					break;
				case 1:
					Intent intent = new Intent(SendMovingActivity.this,
							ImageActivity.class);
					startActivityForResult(intent, REQUEST_GET_AllBITMAPS);
					break;
				case 2:
					getImageFromCamera();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 1:
			if(data!=null && resultCode == 2){
				List<UploadImg> imgList = (List<UploadImg>) data.getSerializableExtra("img_list");
				if(imgList!=null && imgList.size()>0){
					if(mImageList!=null && mImageList.size()>0){
						mImageList.clear();
					}
					mImageList.addAll(imgList);
					if(mImageList.size()==0|| (mImageList.size()<9&&mImageList.get(mImageList.size()-1).mType!=1)){
						mImageList.add(new UploadImg("", 1));
					}
					ImagePublicWay.num = IMAGE_MAX+1-mImageList.size();
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
		case RESQUEST_CODE:
			if(data != null && RESULT_OK == resultCode){
				Bundle bundle = data.getExtras();
				if(bundle != null){

					MapInfo mapInfo = (MapInfo)data.getSerializableExtra("mapInfo");
					if(mapInfo == null){
						mLocationIcon.setBackgroundResource(R.drawable.share_location_icon);
						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();
						mLocationAddress.setText(mContext.getResources().getString(R.string.location_current));
						return;
					}
					mLocationAddress.setText(mapInfo.getAddr());
					mLat = mapInfo.getLat();
					mLng = mapInfo.getLng();
					mLocationIcon.setBackgroundResource(R.drawable.share_location_icon_check);
					mAddress = mapInfo.getAddr();

				}
			}
			break;
		case RESQUEST_AREA_CODE:
			if(data != null && RESULT_OK == resultCode){
				Bundle bundle = data.getExtras();
				mAreaUid="";
				if(bundle != null){
					mAreaUid = data.getStringExtra("area_uid");
					mCheckId = data.getIntExtra("checkId",0);
					if(mCheckId == 1 && (mAreaUid!=null && !mAreaUid.equals("")
							&& !mAreaUid.startsWith(",") && !mAreaUid.endsWith(","))){
						mAreaIcon.setBackgroundResource(R.drawable.share_area_check);
					}else{
						mAreaIcon.setBackgroundResource(R.drawable.share_area);
					}
					mSelectUserList = (List<Login>)data.getSerializableExtra("userlist");
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
				String path = data.getStringExtra("path");
				if(!TextUtils.isEmpty(path)){
					mImageList.add(mImageList.size() - 1, new UploadImg(path, 0));
					if(mImageList.size() - 1 == IMAGE_MAX){
						mImageList.remove(mImageList.size() - 1);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
			case REQUEST_GET_AllBITMAPS:
				for (ImageItem imageItem : Bimp.tempSelectBitmap) {
					if(!TextUtils.isEmpty(imageItem.getThumbnailPath())){
						mImageList.add(mImageList.size() - 1, new UploadImg(imageItem.getThumbnailPath(), 0,imageItem.addEWeiMa));
						if(mImageList.size() - 1 == IMAGE_MAX){
							mImageList.remove(mImageList.size() - 1);
							break;
						}
					}else if(!TextUtils.isEmpty(imageItem.getImagePath())){
						mImageList.add(mImageList.size() - 1, new UploadImg(imageItem.getImagePath(), 0,imageItem.addEWeiMa));
						if(mImageList.size() - 1 == IMAGE_MAX){
							mImageList.remove(mImageList.size() - 1);
							break;
						}
					}
				}
				Bimp.tempSelectBitmap.clear();
				ImagePublicWay.num = IMAGE_MAX+1-mImageList.size();
				mAdapter.notifyDataSetChanged();
				break;
			case RECORD_VIDEO:
				if(data==null){
					return;
				}
				videoPath = data.getStringExtra("path");
				videoTime = data.getStringExtra("time");
				File file = new File(videoPath);
				if(file.exists()){
					videoView.setVisibility(View.VISIBLE);
					mGridView.setVisibility(View.GONE);
					MediaController mediaCtlr = new MediaController(this);
					mediaCtlr.setVisibility(View.INVISIBLE); //设置隐藏
					videoView.setMediaController(mediaCtlr);
					videoView.setVideoURI(Uri.parse(videoPath));
					videoView.start();
					videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							mp.setVolume(0f, 0f);
							mp.start();
							mp.setLooping(true);
						}
					});
				}else {
					Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String tempUrl = FeatureFunction.getPhotoFileName(1);
		if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, tempUrl);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}
	}

	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}


	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery|| data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it
				String tempUrl = IMCommon.getCamerUrl(mContext);
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+tempUrl;
				Log.e("path", "path:"+path);
				if(tempUrl == null || tempUrl.equals("")){
					Toast.makeText(mContext, "图片不存在!", Toast.LENGTH_LONG).show();
					return;
				}
				Log.e("start-end", path.indexOf(".")+":"+path.length());
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
					//startPhotoZoom(Uri.fromFile(new File(path)));
					Intent intent = new Intent(mContext, RotateImageActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", 0);
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
			Log.d("Authority()=true=>may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			File file = new File(extension);
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

	private void updateWordCount(int length){
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2 < mImageList.size()){
			if(mImageList.get(arg2).mType == 0){
				if(mAdapter.getIsDelete()){
					HashMap<String, Bitmap> hashMap = mAdapter.getImageBuffer();
					String path = mImageList.get(arg2).mPicPath;
					ImageView view = (ImageView) mGridView.findViewWithTag(path);
					mImageList.remove(arg2);
					if(view != null){
						view.setImageBitmap(null);
					}
					if (hashMap.get(path) != null) {
						Bitmap bitmap = hashMap.get(path);
						if(bitmap != null && !bitmap.isRecycled()){
							bitmap.recycle();
							bitmap = null;
						}

						hashMap.remove(path);
					}
					if(mImageList.get(mImageList.size() - 1).mType != 1){
						mImageList.add(new UploadImg("", 1));
					}
					mAdapter.notifyDataSetChanged();
				}else{
					Intent showImageIntent = new Intent();
					showImageIntent.setClass(mContext, ShowImageActivity.class);
					showImageIntent.putExtra("type",1);
					showImageIntent.putExtra("pos",arg2);
					showImageIntent.putExtra("img_list",(Serializable)mImageList);
					startActivityForResult(showImageIntent, 1);
				}
			}else if(mImageList.get(arg2).mType == 1){
				if(mAdapter.getIsDelete()){
					mAdapter.setIsDelete(false);
					mAdapter.notifyDataSetChanged();
				}else {
					if(mImageList.size() - 1 >= IMAGE_MAX){
						Toast.makeText(mContext, mContext.getString(R.string.upload_image_max), Toast.LENGTH_SHORT).show();
						return;
					}
					selectImg();
				}
			}
		}else {
			if(mAdapter.getIsDelete()){
				mAdapter.setIsDelete(false);
				mAdapter.notifyDataSetChanged();
			}
		}
	}
}
