package com.coolwin.XYT.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.ClientUpgrade;
import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.GroupTable;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.RoomTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.DB.UserTable;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Version;
import com.coolwin.XYT.Entity.VersionInfo;
import com.coolwin.XYT.FeedBackActivity;
import com.coolwin.XYT.IntentService.DownloadService;
import com.coolwin.XYT.R;
import com.coolwin.XYT.WebViewActivity;
import com.coolwin.XYT.constant.UrlConstants;
import com.coolwin.XYT.databinding.SettingTabBinding;
import com.coolwin.XYT.fragment.ChatFragment;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.interfaceview.UISettingTab;
import com.coolwin.XYT.map.BMapApiApp;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.presenter.SettingTabPresenter;
import com.coolwin.XYT.service.SnsService;
import com.coolwin.XYT.util.FileUtil;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.UIUtil;

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
import java.util.Map;

import static com.coolwin.XYT.global.GlobalParam.NO_NEW_VERSION;
import static com.coolwin.XYT.global.GlobalParam.SHOW_UPGRADE_DIALOG;

/**
 * 设置界面
 * @author dongli
 *
 */
public class SettingTabActivity extends BaseActivity<SettingTabPresenter> implements UISettingTab {

	SettingTabBinding binding;
	Context context;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		binding =  DataBindingUtil.setContentView(this,R.layout.setting_tab);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		initComponent();
	}
	/**
	 * 初始化控件
	 */
	private void initComponent(){
		binding.titleLayout.title.setText("设置");
		binding.titleLayout.leftIcon.setImageResource(R.drawable.back_icon);
		try {
			binding.setCachesize(FileUtil.getFormatSize(FileUtil.getFolderSize(context.getCacheDir())));
		} catch (Exception e) {
			e.printStackTrace();
			binding.setCachesize("0KB");
		}

	}

	/**
	 * 打开修改密码窗口
	 * @param view
	 */
	public void openUpdatePassword(View view){
		Login login = GetDataUtil.getLogin(context);
		Intent intent = new Intent();
		intent.putExtra("url", UrlConstants.USERPASS+"?id="+login.ypId+"&tid="+login.kai6Id+"&m=ptbbs&token="+login.token);
		intent.setClass(context, WebViewActivity.class);
		startActivity(intent);
	}

	/**
	 * 检测版本是否是最新
	 * @param view
	 */
	public void openCheckVersion(View view){
		mPresenter.getNewVersion(UIUtil.getAppVersionName(context));
//		checkUpgrade();
	}

	/**
	 * 清除所有聊天记录
	 * @param view
	 */
	public void clearConversationData(View view){
		SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
		db.execSQL(SessionTable.getDeleteTableSQLString());
		db.execSQL(MessageTable.getDeleteTableSQLString());
		db.execSQL(GroupTable.getDeleteTableSQLString());
		db.execSQL(RoomTable.getDeleteTableSQLString());
		db.execSQL(SessionTable.getCreateTableSQLString());
		db.execSQL(MessageTable.getCreateTableSQLString());
		db.execSQL(GroupTable.getCreateTableSQLString());
		db.execSQL(RoomTable.getCreateTableSQLString());
		context.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
		Toast.makeText(context,"清除成功", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 清除缓存
	 * @param view
	 */
	public void clearCache(View view){
		FileUtil.deleteFilesByDirectory(context.getCacheDir());
		binding.setCachesize("0KB");
	}

	/**
	 * 打开意见反馈
	 * @param view
	 */
	public void openFeedbackLayout(View view){
		startActivity(new Intent(context, FeedBackActivity.class));
	}

	/**
	 * 打开关于我们
	 * @param view
	 */
	public void openAboutLayout(View view){
		startActivity(new Intent(context, AboutActivity.class));
	}

	/**
	 * 退出登录
	 * @param view
	 */
	public void openNoLogin(View view){
		sendBroadcast(new Intent(GlobalParam.SWITCH_TAB));
		//移除本地信息记录
		GetDataUtil.removLogin(context);
		//停止openfire
		Intent serviceIntent = new Intent(context, SnsService.class);
		context.stopService(serviceIntent);
		//退出主程序
		sendBroadcast(new Intent(GlobalParam.ACTION_LOGIN_OUT));
		this.finish();
		//清楚通知栏所有的通知
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		//删除所有联系人信息
		SQLiteDatabase db1 = DBHelper.getInstance(context).getWritableDatabase();
		GroupTable table = new GroupTable(db1);
		UserTable utable= new UserTable(db1);
		table.delete();
		utable.delete();
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
				Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
				return;
				
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message=(String)msg.obj;
				if (message==null || message.equals("")) {
					message= BMapApiApp.getInstance().getResources().getString(R.string.timeout);
				}
				Toast.makeText(context,message, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
		
	};
//
//	/**
//	 * 检测是否为最新版本
//	 */
	protected AlertDialog mUpgradeNotifyDialog;
	private Version mVersion;
	protected ClientUpgrade mClientUpgrade;
	private void checkUpgrade(){
		new Thread(){
            @Override
            public void run() {
        		if(IMCommon.verifyNetwork(context)){
        			try {

    					VersionInfo versionInfo = IMCommon.getIMInfo().checkUpgrade(FeatureFunction.getAppVersionName(context));
    					if(versionInfo != null && versionInfo.mVersion!=null && versionInfo.mState != null && versionInfo.mState.code == 0){
    						mVersion = versionInfo.mVersion;
    						mClientUpgrade = new ClientUpgrade();
							if(mClientUpgrade.compareVersion(FeatureFunction.getAppVersionName(context), mVersion.version)){
								mHandler.sendEmptyMessage(SHOW_UPGRADE_DIALOG);
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
//
//
//	/**
//	 * 显示升级对话框
//	 */
	private void showUpgradeDialog() {
		LayoutInflater factor = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.client_dialog, null);
		TextView titleTextView = (TextView) serviceView.findViewById(R.id.title);
		titleTextView.setText(BMapApiApp.getInstance().getResources().getString(R.string.check_new_version));
		TextView contentView = (TextView) serviceView.findViewById(R.id.updatelog);
		contentView.setText(mVersion.discription);
		Button okBtn = (Button)serviceView.findViewById(R.id.okbtn);
		okBtn.setText(BMapApiApp.getInstance().getResources().getString(R.string.upgrade));
		okBtn.setOnClickListener(new View.OnClickListener(){
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
		cancelBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (mUpgradeNotifyDialog != null){
					mUpgradeNotifyDialog.dismiss();
					mUpgradeNotifyDialog = null;
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		mUpgradeNotifyDialog = builder.create();
		mUpgradeNotifyDialog.show();
		mUpgradeNotifyDialog.setContentView(serviceView);
		FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		layout.setMargins(FeatureFunction.dip2px(context, 10), 0, FeatureFunction.dip2px(context, 10), 0);
		serviceView.setLayoutParams(layout);
	}
	ProgressDialog pBar;
	private void showDownloadApkDilog() {
		pBar = new ProgressDialog(context);
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
		new Handler(context.getMainLooper()).post(new Runnable() {
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

	@Override
	public void downNewVersion(Map<String,String> map) {
		 AbDialogUtil.showAlertDialog(context, "软件更新提示", map.get("description"), new AbAlertDialogFragment.AbDialogOnClickListener() {
			@Override
			public void onPositiveClick() {
				Intent intent = new Intent(SettingTabActivity.this, DownloadService.class);
				startService(intent);
			}
			@Override
			public void onNegativeClick() {
			}
		});
	}
}
