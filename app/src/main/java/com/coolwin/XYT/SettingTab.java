package com.coolwin.XYT;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.GroupTable;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.RoomTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.DB.UserTable;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Version;
import com.coolwin.XYT.Entity.VersionInfo;
import com.coolwin.XYT.fragment.ChatFragment;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.GlobleType;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.map.BMapApiApp;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.service.SnsService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * 设置界面
 * @author dongli
 *
 */
public class SettingTab extends BaseActivity implements OnClickListener {

	/**
	 * 定义全局变量
	 */
	
	private RelativeLayout mNotifyLayout, mScanLayout, mBlockLayout,
			 mFeedBackLayout, mHelpLayout, mUpgradeLayout, mAboutLayout,
			 mNewMsgNotifyLayout,mModifyPwdLayout,mLogoutLayout,mClearLayout,mClearcacheLayout;
	
	private boolean mIsRegisterReceiver = false;
	protected AlertDialog mUpgradeNotifyDialog;
	protected AlertDialog mDownDialog;
	private Version mVersion;
	public final static int SHOW_UPGRADE_DIALOG = 10001;
	public final static int NO_NEW_VERSION = 11315;
	public final static String REFRESH_HEADER = "action_refresh_header";
	private Login mUser;
	private ImageLoader mImageLoader = new ImageLoader();
	protected ClientUpgrade mClientUpgrade;
	private TextView mClearcachesize;
	
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.setting_tab);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.SWITCH_LANGUAGE_ACTION);
		filter.addAction(REFRESH_HEADER);
		registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
		
		initComponent();
	}
	
	/**
	 * 根据通知处理界面逻辑
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent){
			if(intent != null){
				if(intent.getAction().equals(GlobalParam.SWITCH_LANGUAGE_ACTION)){
					
				}else if(intent.getAction().equals(REFRESH_HEADER)){
					mUser = IMCommon.getLoginResult(mContext);
				}
			}
		}
	};
	
	/**
	 * 页面销毁
	 */
	@Override
	protected void onDestroy() {
		if(mIsRegisterReceiver){
			unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}
	
	/**
	 * 初始化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn,0, R.string.setting);
		mLeftBtn.setOnClickListener(this);

		mLogoutLayout = (RelativeLayout) findViewById(R.id.logoutlayout);
		mLogoutLayout.setOnClickListener(this);
		
		mNotifyLayout = (RelativeLayout) findViewById(R.id.notifylayout);
		mNotifyLayout.setOnClickListener(this);
		
		mScanLayout = (RelativeLayout) findViewById(R.id.private_set_layout);
		mScanLayout.setOnClickListener(this);
		
		mBlockLayout = (RelativeLayout) findViewById(R.id.blocklayout);
		mBlockLayout.setOnClickListener(this);
		
		
		mFeedBackLayout = (RelativeLayout) findViewById(R.id.feedbacklayout);
		mFeedBackLayout.setOnClickListener(this);
		
		mHelpLayout = (RelativeLayout) findViewById(R.id.helplayout);
		mHelpLayout.setOnClickListener(this);
		
		mUpgradeLayout = (RelativeLayout) findViewById(R.id.upgradelayout);
		mUpgradeLayout.setOnClickListener(this);
		
		mAboutLayout = (RelativeLayout) findViewById(R.id.aboutlayout);
		mAboutLayout.setOnClickListener(this);
		
		mNewMsgNotifyLayout = (RelativeLayout)findViewById(R.id.new_message_notify_layout);
		mNewMsgNotifyLayout.setOnClickListener(this);
		
		mModifyPwdLayout = (RelativeLayout)findViewById(R.id.modify_pwd_layout);
		mModifyPwdLayout.setOnClickListener(this);

		mClearLayout = (RelativeLayout)findViewById(R.id.clearlayout);
		mClearLayout.setOnClickListener(this);

		mClearcacheLayout = (RelativeLayout)findViewById(R.id.clearcachelayout);
		mClearcacheLayout.setOnClickListener(this);

		mClearcachesize = (TextView) findViewById(R.id.clearcachesize);
		try {
			mClearcachesize.setText(getFormatSize(getFolderSize(mContext.getCacheDir())));
		} catch (Exception e) {
			e.printStackTrace();
			mClearcachesize.setText("0KB");
		}

		mUser = IMCommon.getLoginResult(mContext);
		
	}
	
	

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn://返回按钮
			SettingTab.this.finish();
			break;
		case R.id.modify_pwd_layout://修改密码
//			Intent PwdIntent = new Intent(mContext, ModifyPwdActivity.class);
//			startActivity(PwdIntent);
			Intent modpwdIntent = new Intent();
			modpwdIntent.putExtra("url","http://shop.wei20.cn/gouwu/wish3d/userpass.shtml?id="+mUser.ypId+"&tid="+mUser.kai6Id+"&m=ptbbs&token="+mUser.token);
			modpwdIntent.setClass(mContext, WebViewActivity.class);
			startActivity(modpwdIntent);
			break;
		case R.id.new_message_notify_layout://新消息通知
			Intent newMsgNotifyIntent = new Intent(mContext, PrivateSetActivity.class);
			newMsgNotifyIntent.putExtra("type", GlobleType.PrivateSetActivity_New_Msg_Notify_TYPE);
			startActivity(newMsgNotifyIntent);
			break;
		case R.id.clearlayout:
			SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
			db.execSQL(SessionTable.getDeleteTableSQLString());
			db.execSQL(MessageTable.getDeleteTableSQLString());
			db.execSQL(GroupTable.getDeleteTableSQLString());
			db.execSQL(RoomTable.getDeleteTableSQLString());
			db.execSQL(SessionTable.getCreateTableSQLString());
			db.execSQL(MessageTable.getCreateTableSQLString());
			db.execSQL(GroupTable.getCreateTableSQLString());
			db.execSQL(RoomTable.getCreateTableSQLString());
			mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
			Toast.makeText(mContext,"清除成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.private_set_layout://隐私设置
			Intent scanIntent = new Intent(mContext, PrivateSetActivity.class);
			startActivity(scanIntent);
			break;
			
		case R.id.blocklayout://黑名单
			Intent intent = new Intent(mContext, BlockListActivity.class);
			startActivity(intent);
			break;
	
		case R.id.feedbacklayout://意见反馈
			Intent feedbackIntent = new Intent(mContext, FeedBackActivity.class);
			startActivity(feedbackIntent);
			break;
			
		case R.id.helplayout://帮助
			Intent helpIntent = new Intent(mContext, HelpWebViewActivity.class);
			helpIntent.putExtra("type", 1);
			startActivity(helpIntent);
			break;
			
		case R.id.upgradelayout://版本检测
			checkUpgrade();
			break;
			
		case R.id.aboutlayout://关于我们
			Intent aboutIntent = new Intent(mContext, AboutActivity.class);
			startActivity(aboutIntent);
			break;
			
		case R.id.logoutlayout://退出登陆
			sendBroadcast(new Intent(GlobalParam.SWITCH_TAB));
			SharedPreferences preferences = this.getSharedPreferences(IMCommon.LOGIN_SHARED, 0);
			Editor editor = preferences.edit();
			editor.remove(IMCommon.LOGIN_RESULT);
			editor.commit();
			IMCommon.setUid("");
			IMCommon.setServer("");
			
			SharedPreferences server = this.getSharedPreferences(IMCommon.SERVER_SHARED, 0);
			Editor serverEditor = server.edit();
			serverEditor.remove(IMCommon.SERVER_PREFIX);
			serverEditor.commit();
			
			Intent serviceIntent = new Intent(mContext, SnsService.class);
			mContext.stopService(serviceIntent);
			
			sendBroadcast(new Intent(GlobalParam.ACTION_LOGIN_OUT));
			SettingTab.this.finish();
			
			//清楚通知栏所有的通知
			NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancelAll();
			//删除所有联系人信息
			SQLiteDatabase db1 = DBHelper.getInstance(mContext).getWritableDatabase();
			GroupTable table = new GroupTable(db1);
			UserTable utable= new UserTable(db1);
			table.delete();
			utable.delete();
			break;
			case R.id.clearcachelayout:
				deleteFilesByDirectory(mContext.getCacheDir());
				mClearcachesize.setText("0KB");
				break;
		default:
			break;
		}
	}
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}
	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte";
		}
		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}
		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}
		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}
	/**
	 * 处理消息
	 */
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPGRADE_DIALOG:
				showUpgradeDialog();
				break;
			case NO_NEW_VERSION:
				Toast.makeText(getApplicationContext(), BMapApiApp.getInstance().getResources().getString(R.string.no_version), Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
				return;
				
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message=(String)msg.obj;
				if (message==null || message.equals("")) {
					message= BMapApiApp.getInstance().getResources().getString(R.string.timeout);
				}
				Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
		
	};
	
	/**
	 * 检测是否为最新版本
	 */
	private void checkUpgrade(){
		new Thread(){
            @Override
            public void run() {
        		if(IMCommon.verifyNetwork(mContext)){
        			try {
        				
    					VersionInfo versionInfo = IMCommon.getIMInfo().checkUpgrade(FeatureFunction.getAppVersionName(mContext));
    					if(versionInfo != null && versionInfo.mVersion!=null && versionInfo.mState != null && versionInfo.mState.code == 0){
    						mVersion = versionInfo.mVersion;
    						mClientUpgrade = new ClientUpgrade();
							mVersion = versionInfo.mVersion;
							if(mClientUpgrade.compareVersion(FeatureFunction.getAppVersionName(mContext), mVersion.version)){
								mHandler.sendEmptyMessage(GlobalParam.SHOW_UPGRADE_DIALOG);
							}else{
								mHandler.sendEmptyMessage(NO_NEW_VERSION);
    		        			return;
							}
    					}
    				} catch (IMException e) {
    					e.printStackTrace();
    					mHandler.sendEmptyMessage(GlobalParam.MSG_TICE_OUT_EXCEPTION);
    				}
        		}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
            }
		}.start();
	}
	
	
	/**
	 * 显示升级对话框
	 */
	private void showUpgradeDialog() {
		LayoutInflater factor = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.client_dialog, null);
		TextView titleTextView = (TextView) serviceView.findViewById(R.id.title);
		titleTextView.setText(BMapApiApp.getInstance().getResources().getString(R.string.check_new_version));
		TextView contentView = (TextView) serviceView.findViewById(R.id.updatelog);
		contentView.setText(mVersion.discription);
		Button okBtn = (Button)serviceView.findViewById(R.id.okbtn);
		okBtn.setText(BMapApiApp.getInstance().getResources().getString(R.string.upgrade));
		okBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				showDownloadApkDilog();
				
				if (mUpgradeNotifyDialog != null){
					mUpgradeNotifyDialog.dismiss();
					mUpgradeNotifyDialog = null;
				}
			}
		});
		
		Button cancelBtn = (Button)serviceView.findViewById(R.id.cancelbtn);
		cancelBtn.setText(BMapApiApp.getInstance().getResources().getString(R.string.cancel));
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (mUpgradeNotifyDialog != null){
					mUpgradeNotifyDialog.dismiss();
					mUpgradeNotifyDialog = null;
				}
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		mUpgradeNotifyDialog = builder.create();
		mUpgradeNotifyDialog.show();
		mUpgradeNotifyDialog.setContentView(serviceView);
		FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setMargins(FeatureFunction.dip2px(mContext, 10), 0, FeatureFunction.dip2px(mContext, 10), 0);
		serviceView.setLayoutParams(layout);
	}
	ProgressDialog pBar;
	private void showDownloadApkDilog() {
		pBar = new ProgressDialog(mContext);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍候...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		downFile(mVersion.downloadUrl);
	}
	private void downFile(final String url) {
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								mVersion.name);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	void down() {
		new Handler(mContext.getMainLooper()).post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});
	}
	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), mVersion.name)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
}
