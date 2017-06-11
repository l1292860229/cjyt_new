package com.coolwin.XYT.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.GroupTable;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.RoomTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.DB.UserTable;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.IntentService.DownloadService;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.SettingTabBinding;
import com.coolwin.XYT.fragment.ChatFragment;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.interfaceview.UISettingTab;
import com.coolwin.XYT.presenter.SettingTabPresenter;
import com.coolwin.XYT.service.SnsService;
import com.coolwin.XYT.util.FileUtil;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.XYT.webactivity.WebViewActivity;

import java.util.Map;


/**
 * 设置界面
 * @author dongli
 *
 */
public class SettingTabActivity extends BaseActivity<SettingTabPresenter> implements UISettingTab {

	SettingTabBinding binding;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		//清楚通知栏所有的通知
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		//删除所有联系人信息
		SQLiteDatabase db1 = DBHelper.getInstance(context).getWritableDatabase();
		GroupTable table = new GroupTable(db1);
		UserTable utable= new UserTable(db1);
		table.delete();
		utable.delete();
		sendBroadcast(new Intent(GlobalParam.CANCLE_COMPLETE_USERINFO_ACTION));
		this.finish();
	}
	@Override
	public void downNewVersion(final Map<String,String> map) {
		 AbDialogUtil.showAlertDialog(context, "软件更新提示", map.get("description"), new AbAlertDialogFragment.AbDialogOnClickListener() {
			@Override
			public void onPositiveClick() {
				Intent intent = new Intent(SettingTabActivity.this, DownloadService.class);
				intent.putExtra("apkUrl",map.get("url"));
				startService(intent);
				UIUtil.showMessage(context,"正在下载");
			}
			@Override
			public void onNegativeClick() {
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
