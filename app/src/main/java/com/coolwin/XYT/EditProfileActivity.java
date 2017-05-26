package com.coolwin.XYT;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.LoginResult;
import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.dialog.MMAlert.OnAlertSelectId;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.GlobleType;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.net.IMException;

import java.io.File;


public class EditProfileActivity extends BaseActivity implements OnClickListener {

	/*
	 * 定义全局变量
	 */
	private static final String TEMP_FILE_NAME = "header.jpg";
	private RelativeLayout mHeaderLayout,mAddrLayout,mSexLayout,
	mSignLayout,mNickNameLayout,mGongSiZhuYeLayout,mHangYeLayout,mSuoZaiGongSiLayout,
			mGongSiDiZhiLayout,mZhiWeiLayout,mDianHuaLayout,mKeGongLayout,mXuQiULayout;

	private TextView mSexTextView,mAddrTextView,mSiTextView,mCompanywebsiteView,mIndustryView,
			mCompanyView,mCompanyaddressView,mJobView,mProvideView,mDemandView,mTelephoneView,mNickNameTextView;
	private TextView mHintText;
	private ImageView mImageView;

	private String mInputNickName,mInputAddr,mInputSign,mInputCompanywebsite,mInputindustry,
	mInputCompany,mInputcompanyaddress,mInputjob,mInputprovide,mInputdemand,mInputTelephone;
	private String mInputSex = "2";
	
	/**
	 * // 省id
	 */
	private String mProvice;
	/**
	 * //市id
	 */
	private String mCity;

	private int mType;

	private Login mLogin;
	private Bitmap mBitmap;
	private String mImageFilePath;
	private String mHeadUrl;
	private ImageLoader mImageLoader;

	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				LoginResult loginResult = (LoginResult) msg.obj;
				if (loginResult == null) {
					Toast.makeText(mContext, "提交数据失败...", Toast.LENGTH_LONG)
					.show();
					return;
				}
				if (loginResult.mState.code != 0) {
					Toast.makeText(mContext, loginResult.mState.errorMsg,
							Toast.LENGTH_LONG).show();
					return;
				}
				Login login = IMCommon.getLoginResult(mContext);
				String oldheadUrl = login.headsmall;
				String newHeadUrl = loginResult.mLogin.headsmall;
				login.headsmall = loginResult.mLogin.headsmall;
				login.nickname = loginResult.mLogin.nickname;
				login.gender = loginResult.mLogin.gender;
				login.provinceid = loginResult.mLogin.provinceid;
				login.cityid = loginResult.mLogin.cityid;
				login.sign = loginResult.mLogin.sign;
				login.companywebsite = loginResult.mLogin.companywebsite;
				login.industry = loginResult.mLogin.industry;
				login.company = loginResult.mLogin.company;
				login.companyaddress = loginResult.mLogin.companyaddress;
				login.job = loginResult.mLogin.job;
				login.provide = loginResult.mLogin.provide;
				login.demand = loginResult.mLogin.demand;
				login.telephone  = loginResult.mLogin.telephone;
				IMCommon.saveLoginResult(mContext, login);
				setResult(RESULT_OK);
				Intent intent = new Intent(GlobalParam.ACTION_REFRESH_CHAT_HEAD_URL);
				intent.putExtra("oldurl", oldheadUrl);
				intent.putExtra("newurl", newHeadUrl);
				sendBroadcast(intent);
				EditProfileActivity.this.finish();
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				if(mInputAddr != null && !mInputAddr.equals("")){
					mAddrTextView.setText(mInputAddr);
				}
				
				break;


			default:
				break;
			}
		}

	};


	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_user_info);
		mContext = this;
		mLogin = IMCommon.getLoginResult(mContext); 
		mImageLoader = new ImageLoader();
		initCompent();


	}

	/*
	 * 实例化控件
	 */
	private void initCompent(){
		setTitleContent(R.drawable.back_btn, R.drawable.ok_btn, R.string.edit_profile);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mHeaderLayout = (RelativeLayout)findViewById(R.id.new_header_layout);
		mNickNameLayout = (RelativeLayout)findViewById(R.id.nickname_layout);

		mGongSiZhuYeLayout = (RelativeLayout)findViewById(R.id.gongsizhuye);
		mHangYeLayout      = (RelativeLayout)findViewById(R.id.hangye);
		mSuoZaiGongSiLayout= (RelativeLayout)findViewById(R.id.suozaigongsi);
		mGongSiDiZhiLayout = (RelativeLayout)findViewById(R.id.gongsidizhi);
		mZhiWeiLayout 	   = (RelativeLayout)findViewById(R.id.zhiwei);
		mDianHuaLayout     = (RelativeLayout)findViewById(R.id.dianhua);
		mKeGongLayout      = (RelativeLayout)findViewById(R.id.kegong);
		mXuQiULayout       = (RelativeLayout)findViewById(R.id.xuqiu);

		mAddrLayout = (RelativeLayout)findViewById(R.id.addr_layout);
		mSexLayout = (RelativeLayout)findViewById(R.id.sex_layout);
		mSignLayout = (RelativeLayout)findViewById(R.id.sign_layout);
		mHeaderLayout.setOnClickListener(this);
//		mNickNameLayout.setOnClickListener(this);
		mAddrLayout.setOnClickListener(this);
//		mSexLayout.setOnClickListener(this);
		mSignLayout.setOnClickListener(this);

		mGongSiZhuYeLayout.setOnClickListener(this);
		mHangYeLayout.setOnClickListener(this);
		mSuoZaiGongSiLayout.setOnClickListener(this);
		mGongSiDiZhiLayout.setOnClickListener(this);
		mZhiWeiLayout.setOnClickListener(this);
		mDianHuaLayout.setOnClickListener(this);
		mKeGongLayout.setOnClickListener(this);
		mXuQiULayout.setOnClickListener(this);

		mNickNameTextView = (TextView)findViewById(R.id.nickname_content);
		mSexTextView = (TextView)findViewById(R.id.sex_content);
		mAddrTextView = (TextView)findViewById(R.id.addr_content);
		mSiTextView = (TextView)findViewById(R.id.sign_content);

		mCompanywebsiteView = (TextView)findViewById(R.id.companywebsite);
		mIndustryView = (TextView)findViewById(R.id.industry);
		mCompanyView = (TextView)findViewById(R.id.company);
		mCompanyaddressView = (TextView)findViewById(R.id.companyaddress);
		mJobView = (TextView)findViewById(R.id.job);
		mProvideView = (TextView)findViewById(R.id.provide);
		mDemandView = (TextView)findViewById(R.id.demand);
		mTelephoneView = (TextView)findViewById(R.id.telephone);

		mImageView = (ImageView)findViewById(R.id.new_header_icon);
		setText();

	}

	/*
	 * 给控件设置值
	 */
	private void setText(){
		if(mLogin == null || mLogin.equals("")){
			return;
		}
		mHeadUrl = mLogin.headsmall;
		if(mLogin.headsmall!=null && !mLogin.headsmall.equals("")){
			mImageView.setTag(mLogin.headsmall);
			mImageLoader.getBitmap(mContext, mImageView, null,mLogin.headsmall,0,false,true);
		}
		mInputSex = mLogin.gender+"";
		if(mLogin.gender == 1){
			mSexTextView.setText(mContext.getResources().getString(R.string.femal));
		}else if(mLogin.gender == 0){
			mSexTextView.setText(mContext.getResources().getString(R.string.man));
		}else if(mLogin.gender == 2){
			mSexTextView.setText(mContext.getResources().getString(R.string.no_limit));
		}
		
		mInputNickName = mLogin.nickname;
		mNickNameTextView.setText(mInputNickName+" ");
		
		mInputSign = mLogin.sign;
		mSiTextView.setText(mInputSign+" ");
		
		mProvice = mLogin.provinceid;
		mCity = mLogin.cityid;
		mAddrTextView.setText(mProvice+"  "+mCity+" ");

		mInputCompanywebsite =mLogin.companywebsite;
		mCompanywebsiteView.setText(mInputCompanywebsite);
		mInputindustry =mLogin.industry;
		mIndustryView.setText(mInputindustry);
		mInputCompany =mLogin.company;
		mCompanyView.setText(mInputCompany);
		mInputcompanyaddress =mLogin.companyaddress;
		mCompanyaddressView.setText(mInputcompanyaddress);
		mInputjob =mLogin.job;
		mJobView.setText(mInputjob);
		mInputprovide =mLogin.provide;
		mProvideView.setText(mInputprovide);
		mInputdemand =mLogin.demand;
		mDemandView.setText(mInputdemand);
		mInputTelephone =mLogin.telephone;
		mTelephoneView.setText(mInputTelephone);
	}
	
	/*
	 * 完善用户资料
	 */
	private void completeUserInfo(){
		if(!IMCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
		}
		new Thread(){
			public void run() {

				try {
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
							mContext.getResources().getString(R.string.commit_dataing));
					LoginResult login = IMCommon.getIMInfo().modifyUserInfo(mImageFilePath,mInputNickName,
							mInputSex,mInputSign,mProvice, mCity,null,null,mInputCompanywebsite,mInputindustry,
							mInputCompany,mInputcompanyaddress,mInputjob,mInputprovide,mInputdemand,mInputTelephone);
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE, login);
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
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.right_btn:
			if(mInputNickName == null || mInputNickName.equals("")){
				Toast.makeText(mContext, mContext.getResources().getString(R.string.nickname_not_null), Toast.LENGTH_LONG).show();
				return;
			}
			completeUserInfo();
			break;
		case R.id.new_header_layout:
			selectImg();
			break;
		case R.id.nickname_layout:
			Intent nickNameIntent = new Intent();
			nickNameIntent.setClass(mContext, WriteUserInfoActivity.class);
			nickNameIntent.putExtra("content", mInputNickName);
			nickNameIntent.putExtra("type",GlobleType.COMPLETE_NICKNAME);
			mType = GlobleType.COMPLETE_NICKNAME;
			startActivityForResult(nickNameIntent, 1);
			break;
		case R.id.sign_layout:
			Intent signIntent = new Intent();
			signIntent.setClass(mContext, WriteUserInfoActivity.class);
			signIntent.putExtra("content", mInputSign);
			signIntent.putExtra("type",GlobleType.COMPLETE_SIGN);
			mType = GlobleType.COMPLETE_SIGN;
			startActivityForResult(signIntent, 1);
			break;
		case R.id.addr_layout:
			Intent intent = new Intent();
			intent.setClass(mContext, TreeViewActivity.class);
			intent.putExtra("type",GlobleType.TreeViewActivity_City_TYPE);
			mType = GlobleType.COMPLETE_ADDR;
			startActivityForResult(intent, 1);
			break;
		case R.id.sex_layout:
			MMAlert.showAlert(mContext, "", mContext.getResources().
					getStringArray(R.array.sex_array),
					null, new OnAlertSelectId() {

				@Override
				public void onClick(int whichButton) {
					switch (whichButton) {
					case 0:
						mInputSex = "0";
						mSexTextView.setText(mContext.getResources().getString(R.string.man));
						break;
					case 1:
						mInputSex="1";
						mSexTextView.setText(mContext.getResources().getString(R.string.femal));
						break;
					default:
						break;
					}
				}
			});
			
			break;
		case R.id.gongsizhuye:
			Intent gongsizhuyeIntent = new Intent();
			gongsizhuyeIntent.setClass(mContext, WriteUserInfoActivity.class);
			gongsizhuyeIntent.putExtra("content", mInputCompanywebsite);
			gongsizhuyeIntent.putExtra("type",GlobleType.COMPLETE_GONGSIZHUYE);
			mType = GlobleType.COMPLETE_GONGSIZHUYE;
			startActivityForResult(gongsizhuyeIntent, 1);
			break;
			case R.id.hangye:
				Intent hangyeIntent = new Intent();
				hangyeIntent.setClass(mContext, WriteUserInfoActivity.class);
				hangyeIntent.putExtra("content", mInputindustry);
				hangyeIntent.putExtra("type",GlobleType.COMPLETE_HANGYE);
				mType = GlobleType.COMPLETE_HANGYE;
				startActivityForResult(hangyeIntent, 1);
				break;
			case R.id.suozaigongsi:
				Intent suozaigongsiIntent = new Intent();
				suozaigongsiIntent.setClass(mContext, WriteUserInfoActivity.class);
				suozaigongsiIntent.putExtra("content", mInputCompany);
				suozaigongsiIntent.putExtra("type",GlobleType.COMPLETE_SUOZAIGONGSI);
				mType = GlobleType.COMPLETE_SUOZAIGONGSI;
				startActivityForResult(suozaigongsiIntent, 1);
				break;
			case R.id.gongsidizhi:
				Intent gongsidizhiIntent = new Intent();
				gongsidizhiIntent.setClass(mContext, WriteUserInfoActivity.class);
				gongsidizhiIntent.putExtra("content", mInputcompanyaddress);
				gongsidizhiIntent.putExtra("type",GlobleType.COMPLETE_GONGSIDIZI);
				mType = GlobleType.COMPLETE_GONGSIDIZI;
				startActivityForResult(gongsidizhiIntent, 1);
				break;
			case R.id.zhiwei:
				Intent zhiweiIntent = new Intent();
				zhiweiIntent.setClass(mContext, WriteUserInfoActivity.class);
				zhiweiIntent.putExtra("content", mInputjob);
				zhiweiIntent.putExtra("type",GlobleType.COMPLETE_ZHIWEI);
				mType = GlobleType.COMPLETE_ZHIWEI;
				startActivityForResult(zhiweiIntent, 1);
				break;
			case R.id.dianhua:
				Intent dianhuaIntent = new Intent();
				dianhuaIntent.setClass(mContext, WriteUserInfoActivity.class);
				dianhuaIntent.putExtra("content", mInputTelephone);
				dianhuaIntent.putExtra("type",GlobleType.COMPLETE_DIANHUA);
				mType = GlobleType.COMPLETE_DIANHUA;
				startActivityForResult(dianhuaIntent, 1);
				break;
			case R.id.kegong:
				Intent kegongIntent = new Intent();
				kegongIntent.setClass(mContext, WriteUserInfoActivity.class);
				kegongIntent.putExtra("content", mInputprovide);
				kegongIntent.putExtra("type",GlobleType.COMPLETE_KEGONG);
				mType = GlobleType.COMPLETE_KEGONG;
				startActivityForResult(kegongIntent, 1);
				break;
			case R.id.xuqiu:
				Intent xuqiuIntent = new Intent();
				xuqiuIntent.setClass(mContext, WriteUserInfoActivity.class);
				xuqiuIntent.putExtra("content", mInputdemand);
				xuqiuIntent.putExtra("type",GlobleType.COMPLETE_XUQIU);
				mType = GlobleType.COMPLETE_XUQIU;
				startActivityForResult(xuqiuIntent, 1);
				break;
		default:
			break;
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mImageLoader.getImageBuffer().containsKey(mLogin.headsmall)){
			mImageView.setImageDrawable(null);
			if(mImageLoader.getImageBuffer().get(mLogin.headsmall)!=null){
				mImageLoader.getImageBuffer().get(mLogin.headsmall).recycle();
			}
		}
	}

	/*
	 * 页面回调事件
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1 && resultCode == RESULT_OK){
			if(mType == GlobleType.COMPLETE_SEX){
				int sex = data.getIntExtra("sex",0);
				mInputSex = sex+"";
				if(sex == 0){
					mSexTextView.setText(mContext.getResources().getString(R.string.femal));
				}else if(sex == 1){
					mSexTextView.setText(mContext.getResources().getString(R.string.man));
				}else if(sex == 2){
					mSexTextView.setText(mContext.getResources().getString(R.string.no_limit));
				}
			}else if(mType == GlobleType.COMPLETE_NICKNAME){
				mInputNickName = data.getStringExtra("nickname");
				mNickNameTextView.setText(mInputNickName+" ");
			}else if(mType == GlobleType.COMPLETE_ADDR){
				mInputAddr = data.getStringExtra("addr");
				//省id
				//市id 
				mProvice = data.getStringExtra("provice");
				mCity = data.getStringExtra("city");
				mAddrTextView.setText(mInputAddr+" ");
			}else if(mType == GlobleType.COMPLETE_EMAIL){
				
			}else if(mType == GlobleType.COMPLETE_COMPANY){
				
			}else if(mType == GlobleType.COMPLETE_SIGN){
				mInputSign = data.getStringExtra("sign");
				mSiTextView.setText(mInputSign+" ");
			}else if(mType == GlobleType.COMPLETE_GONGSIZHUYE){
				mInputCompanywebsite = data.getStringExtra("companywebsite");
				mCompanywebsiteView.setText(mInputCompanywebsite);
			}else if(mType == GlobleType.COMPLETE_HANGYE){
				mInputindustry =data.getStringExtra("industry");
				mIndustryView.setText(mInputindustry);
			}else if(mType == GlobleType.COMPLETE_SUOZAIGONGSI){
				mInputCompany = data.getStringExtra("company");
				mCompanyView.setText(mInputCompany);
			}else if(mType == GlobleType.COMPLETE_GONGSIDIZI){
				mInputcompanyaddress =data.getStringExtra("companyaddress");
				mCompanyaddressView.setText(mInputcompanyaddress);
			}else if(mType == GlobleType.COMPLETE_ZHIWEI){
				mInputjob = data.getStringExtra("job");
				mJobView.setText(mInputjob);
			}else if(mType == GlobleType.COMPLETE_DIANHUA){
				mInputTelephone =data.getStringExtra("telephone");
				mTelephoneView.setText(mInputTelephone);
			}else if(mType == GlobleType.COMPLETE_KEGONG){
				mInputprovide =data.getStringExtra("provide");
				mProvideView.setText(mInputprovide);
			}else if(mType == GlobleType.COMPLETE_XUQIU){
				mInputdemand =data.getStringExtra("demand");
				mDemandView.setText(mInputdemand);
			}
		}

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
				if (extras != null) {

					mImageView.setImageBitmap(null);
					if(mBitmap != null && !mBitmap.isRecycled()){
						mBitmap.recycle();
						mBitmap = null;
					}
					mBitmap = extras.getParcelable("data");
					mImageView.setImageBitmap(mBitmap);
					File file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + TEMP_FILE_NAME);
					if(file != null && file.exists()){
						file.delete();
						file = null;
					}
					mImageFilePath = FeatureFunction.saveTempBitmap(mBitmap, "header.jpg");
				}

			}
			break;	
		default:
			break;
		}
	}

	/*
	 * 选择图片对话框
	 */
	private void selectImg(){
		MMAlert.showAlert(mContext, "", new String[]{"相册","拍一张"},
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				Log.e("whichButton", "whichButton: "+whichButton);
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
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, TEMP_FILE_NAME);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}

	}

	/*
	 * 从相册中选择
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//
//		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}

	/*
	 * 处理选择的图片
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery){
			originalImage(data);
		}else {
			if(data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it
				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
					startPhotoZoom(Uri.fromFile(new File(path)));
				}
			}
		}
	}

	private void originalImage(Intent data) {
		Uri uri = data.getData();
		//Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri,
					new String[] { MediaStore.Images.Media.DATA }, null, null,
					null);
			if (null == cursor) {
				//Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				startPhotoZoom(data.getData());
			}
		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				startPhotoZoom(uri);
			}
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
		intent.setDataAndType(uri, "image/*");
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 180);
		intent.putExtra("outputY", 180);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
	}


}
