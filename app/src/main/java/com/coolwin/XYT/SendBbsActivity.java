package com.coolwin.XYT;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.dialog.MMAlert;
import com.coolwin.XYT.dialog.MMAlert.OnAlertSelectId;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;

import java.io.File;

/**
 * 发布贴子
 * @author dongli
 *
 */
public class SendBbsActivity extends BaseActivity implements OnClickListener {
	public static final int MSG_SHOW_IMAGE = 0x00023;
	private static final String TEMP_FILE_NAME = "bbsheader.jpg";
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 102;
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 103;
	private EditText mTitleEdit,mContentEdit,mMoneyEdit;
	private ImageView mHeadSmall;
	private DisplayMetrics mMetrics;
	private String title,content,money,type,mImageFilePath;
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
						hintMsg = "发表失败";
					}
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
					return;
				}
				if(hintMsg == null || hintMsg.equals("")){
					hintMsg = "发表成功,等待 审核";
				}
				Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				SendBbsActivity.this.finish();
				sendBroadcast(new Intent(MyBbsListActivity.MSG_REFRESH_MOVIINF));
				break;
			default:
				break;
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_bbs);
		mContext = this;
		mMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		type=getIntent().getStringExtra("type");
		initCompent();
	}

	private void initCompent(){
		setRightTextTitleContent(R.drawable.back_btn, R.string.send, R.string.bbs);
		mLeftBtn.setOnClickListener(this);
		mRightTextBtn.setOnClickListener(this);
		mTitleEdit = (EditText)findViewById(R.id.bbstitle);
		mContentEdit = (EditText)findViewById(R.id.bbscontent);
		mMoneyEdit= (EditText)findViewById(R.id.bbsmoney);
		mHeadSmall = (ImageView)findViewById(R.id.headsmall);
		mHeadSmall.setOnClickListener(this);
		if("1".equals(type)){
			mMoneyEdit.setVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private boolean  checkText(){
		boolean isCheck = true;
		String hinMsg = "";
		title = mTitleEdit.getText().toString();
		content = mContentEdit.getText().toString();
		money = mMoneyEdit.getText().toString();
		if (title == null || title.equals("")) {
			isCheck = false;
			hinMsg = "请填写标题";
		}
		if (title == null || title.equals("") || content == null || content.equals("")) {
			isCheck = false;
			hinMsg = "请填写内容";
		}
		if (!isCheck && hinMsg!=null && !hinMsg.equals("")) {
			Toast.makeText(mContext, hinMsg, Toast.LENGTH_LONG).show();
		}
		return isCheck ;
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
					IMJiaState status = IMCommon.getIMInfo().addBbs(title, content,mImageFilePath,type,money);
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
			SendBbsActivity.this.finish();
			break;
		case R.id.right_text_btn://发送一条动态信息
			sendMoving();
			break;
		case R.id.headsmall://发送一条动态信息
			selectImg();
			break;
//		case R.id.adduser:
//			Intent intent = new Intent();
//			intent.setClass(mContext, ChooseUserActivity.class);
//			intent.putExtra("adduser",true);
//			startActivityForResult(intent,REQUEST_GET_USERLIST);
//			break;
		default:
			break;
		}
	}
	
	private void selectImg(){
		MMAlert.showAlert(mContext, mContext.getResources().getString(R.string.select_image),
				new String[]{"相册","拍照"},
				null, new OnAlertSelectId() {

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
//			case REQUEST_GET_USERLIST:
//				if (resultCode == RESULT_OK) {
//					if(data!=null){
//						ArrayList<Login> users = (ArrayList<Login>) data.getSerializableExtra("userlist");
//						userlistView.setAdapter(new SortAdapter(mContext,users));
//					}
//				}
//				break;
			case REQUEST_GET_URI:
				if (resultCode == RESULT_OK) {
					doChoose(true, data);
				}
				break;
			case REQUEST_GET_IMAGE_BY_CAMERA:
				if (resultCode == RESULT_OK) {
					doChoose(false, data);
				}
				break;
			case REQUEST_GET_BITMAP:
				if(resultCode == RESULT_OK){
					String path = data.getStringExtra("path");
					if (!TextUtils.isEmpty(path)) {
						Bitmap mBitmap = BitmapFactory.decodeFile(path);
						mHeadSmall.setImageBitmap(mBitmap);
						File file = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY + TEMP_FILE_NAME);
						if(file != null && file.exists()){
							file.delete();
							file = null;
						}
						mImageFilePath = FeatureFunction.saveTempBitmap(mBitmap, TEMP_FILE_NAME);
					}

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
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		startActivityForResult(intent, REQUEST_GET_URI);
	}

	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery|| data != null){
				originalImage(data);
			}else{
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
}
