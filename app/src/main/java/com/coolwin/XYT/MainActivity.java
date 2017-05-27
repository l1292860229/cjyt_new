package com.coolwin.XYT;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Version;
import com.coolwin.XYT.Entity.VersionInfo;
import com.coolwin.XYT.exception.ExceptionHandler;
import com.coolwin.XYT.fragment.ChatFragment;
import com.coolwin.XYT.fragment.FatherContactsFragment;
import com.coolwin.XYT.fragment.FoundFragment;
import com.coolwin.XYT.fragment.FriensLoopFragment;
import com.coolwin.XYT.fragment.MyBbsListFragment;
import com.coolwin.XYT.fragment.ProfileFragment;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.receiver.NotifySystemMessage;
import com.coolwin.XYT.service.SnsService;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.widget.MainSearchDialog;
import com.coolwin.XYT.widget.SelectAddPopupWindow;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.coolwin.XYT.webactivity.MyWebViewClient.offlineResources;


/**
 * 高仿微信的主界面
 * 
 *
 * 
 * @author guolin
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

	/**
	 * 定义全局变量
	 */
	
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 102;
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 103;

	private boolean mIsRegisterReceiver = false;
	
	
	protected ImageView mSearchBtn,mAddBtn;
	private TextView mTitleView;
	private RelativeLayout mTitleLayout;
	
	protected AlertDialog mUpgradeNotifyDialog;
	private Version mVersion;
	protected ClientUpgrade mClientUpgrade;


	/**
	 * 聊天界面的Fragment
	 */
	//private ChatFragment chatFragment;
	private MyBbsListFragment myBbsListFragment;
	/**
	 * 发现界面的Fragment
	 */
	private FoundFragment foundFragment;
	//private BbsListFragment bbsListFragment;
	private FriensLoopFragment friensLoopFragment;
	/**
	 * 通讯录界面的Fragment
	 */
	private FatherContactsFragment fatherContactsFragment;

	/**
	 * 我的Fragment
	 */
	private ProfileFragment profileFragment;

	/**
	 * PagerSlidingTabStrip的实例
	 */
//	private PagerSlidingTabStrip tabs;
	private RelativeLayout[] mTabs;
	private Button shopBtn;
	private Fragment[] fragments;
	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	// 自定义的弹出框类
	SelectAddPopupWindow menuWindow2; // 弹出框



	private Timer mTimer;
	private StartServiceTask mServiceTask;
	private Context mContext;
	public static String lbs="";
	private LocationClient locationClient;
	private boolean isShowTitle=true;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.currentThread().setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		mContext = this;
		if(IMCommon.getLoginResult(mContext) == null ||IMCommon.getLoginResult(mContext).nickname==null){
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivityForResult(intent, GlobalParam.LOGIN_REQUEST);
		}else {
			loginXMPP();
			//sessionPromptUpdate();
			Intent
					intent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
			intent.putExtra("found_type", 1);
			mContext.sendBroadcast(intent);
			int friendsLoopTip = IMCommon.getFriendsLoopTip(mContext);
			if(friendsLoopTip!=0){
				findViewById(R.id.unread_find_number).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.unread_find_number)).setText(friendsLoopTip+"");
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
			}
			//检测更新
			checkUpgrade();
		}
		registerNetWorkMonitor();
		setActionBarLayout();
		dm = getResources().getDisplayMetrics();
		mTabs = new RelativeLayout[5];
		mTabs[0] = (RelativeLayout) findViewById(R.id.btn_conversation);
		mTabs[1] = (RelativeLayout) findViewById(R.id.btn_address_list);
		mTabs[2] = (RelativeLayout) findViewById(R.id.btn_container_supper);
		mTabs[3] = (RelativeLayout) findViewById(R.id.btn_find);
		mTabs[4] = (RelativeLayout) findViewById(R.id.btn_profile);
		mTabs[0].setSelected(true);
		myBbsListFragment = new MyBbsListFragment();
		foundFragment = new FoundFragment();
		friensLoopFragment = new FriensLoopFragment();
		profileFragment = new ProfileFragment();
		fatherContactsFragment = new FatherContactsFragment();
		fragments = new Fragment[]{myBbsListFragment,  fatherContactsFragment,friensLoopFragment,foundFragment, profileFragment};
		if (!fragments[0].isAdded()) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
					.show(fragments[0]).commit();
		}
		SharedPreferences mPreferences= mContext.getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
		String fxid=mPreferences.getString(IMCommon.USERNAME, "");
		isShowTitle=mPreferences.getBoolean(IMCommon.FIRSTOPEN2,true);
		if(fxid!=null&&fxid.equals("test005")){
				findViewById(R.id.btn_container_shop).setVisibility(View.GONE);
		}
		shopBtn= (Button) findViewById(R.id.btn_shop);
		shopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Login login = IMCommon.getLoginResult(mContext);
				Intent shopIntent = new Intent();
				if(login.url==null || login.url.equals("")){
					String kid=null;
					if (login.ypId==null || login.ypId.equals("")) {
						kid=getResources().getString(R.string.ypid);
					}else{
						kid=login.ypId;
					}
					login.url = "shop.wei20.cn/gouwu/wishmb/home.shtml?id="+kid;
				}
				shopIntent.putExtra("url","http://"+login.url+"&token="+login.token+"&lbs="+lbs);
				//shopIntent.putExtra("url","http://139.224.57.105/apptest/slider.html");
				shopIntent.setClass(MainActivity.this, WebViewActivity.class);
//				shopIntent.setClass(MainActivity.this, RedpacketMapActivity.class);
				startActivity(shopIntent);
			}
		});
		locationClient = new LocationClient(mContext);
		//设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);        //是否打开GPS
		option.setCoorType("bd09ll");       //设置返回值的坐标类型。
		option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
		option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		option.setScanSpan(60*1000);    //设置定时定位的时间间隔。单位毫秒
		locationClient.setLocOption(option);
		//注册位置监听器
		locationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				lbs= location.getLatitude()+","+location.getLongitude();
			}
			@Override
			public void onReceivePoi(BDLocation location) {}
		});
		locationClient.start();
		/*
		 *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
		 *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
		 *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
		 *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
		 *定时定位时，调用一次requestLocation，会定时监听到定位结果。
		 */
		locationClient.requestLocation();
		//加载js的文件到内存中
		new Thread(new Runnable() {
			@Override
			public void run() {//加载本地js
				if (offlineResources.size()==0) {
					fetchOfflineResources("wwww");
				}
			}
		}).start();
		if(isShowTitle){
			findViewById(R.id.showtitle).setVisibility(View.VISIBLE);
		}
		findViewById(R.id.showtitlejump).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.showtitle).setVisibility(View.GONE);
				mContext.getSharedPreferences(IMCommon.REMENBER_SHARED, 0).edit().putBoolean(IMCommon.FIRSTOPEN2,false).commit();
			}
		});
		findViewById(R.id.showtitlego).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findViewById(R.id.showtitle).setVisibility(View.GONE);
				Intent intent = new Intent(mContext,EditProfileActivity.class);
				startActivity(intent);
				mContext.getSharedPreferences(IMCommon.REMENBER_SHARED, 0).edit().putBoolean(IMCommon.FIRSTOPEN2,false).commit();
			}
		});
	}
	public void noClick(View view){}
	private void fetchOfflineResources(String dir) {
		AssetManager am = getAssets();
		try {
			String[] res = am.list(dir);
			if(res != null) {
				for(String r:res){
					if (!r.contains(".")){
						fetchOfflineResources(dir+"/"+r);
					}else{
						if (r.endsWith(".js") ||
								r.endsWith(".css") ||
								r.endsWith(".jpg") ||
								r.endsWith(".png")||
								r.endsWith(".html")) {
							offlineResources.put(r,dir+"/"+r);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		showView();
	}

	/**
	 * 自定义titlebar
	 */
//	TextView leftTitle;
	public void setActionBarLayout(){
		mTitleLayout = (RelativeLayout)findViewById(R.id.title_layout);
		mTitleView = (TextView)findViewById(R.id.title);
		mTitleView.setText(mContext.getResources().getString(R.string.ochat_app_name));
		//mSearchBtn = (ImageView)findViewById(R.id.search_btn);
		mAddBtn = (ImageView)findViewById(R.id.add_btn);
		//mSearchBtn.setVisibility(View.VISIBLE);
		mAddBtn.setVisibility(View.VISIBLE);
		//mSearchBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
//		leftTitle = (TextView)findViewById(R.id.left_title);
//		leftTitle.setText("忽略未读");
//		leftTitle.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				MessageTable messageTable = new MessageTable(DBHelper.getInstance(mContext).getWritableDatabase());
//				messageTable.updateRead();
//				sessionPromptUpdate();
//				sendBroadcast(new Intent(GlobalParam.SWITCH_LANGUAGE_ACTION));
//			}
//		});
	}

	
	

	/**
	 * 检测用户是否输入昵称
	 * @param login
	 * @return
	 */
	private boolean checkValue(Login login){
		boolean ischeck = true;
		if(login == null || login.equals("") ){
			ischeck = false;
		}else{
			if (login.nickname== null || login.nickname.equals("")) {
				ischeck = false;
			}
		}
		return ischeck;

	}


	/**
	 * 连接到xmpp
	 */
	private void loginXMPP(){
//		startGuidePage();
		mServiceTask = new StartServiceTask(mContext);
		mTimer = new Timer("starting");
		mTimer.scheduleAtFixedRate(mServiceTask, 0, 5000);
	}

	/**
	 * 检测用是否填写昵称，如果没有则跳转到完善资料页进行填写
	 */
	private void startGuidePage(){
		Login login = IMCommon.getLoginResult(mContext);
		if(checkValue(login)){
			checkUpgrade();//检测新版本
		}else{//跳转到完善资料页
			Intent completeIntent = new Intent();
			completeIntent.setClass(mContext, CompleteUserInfoActvity.class);
			completeIntent.putExtra("login", login);
			startActivityForResult(completeIntent, GlobalParam.SHOW_COMPLETE_REQUEST);
		}
		//return isShowGudie;
	}

	/**
	 * 开启聊天服务
	 * @author dongli
	 *
	 */
	private final class StartServiceTask extends TimerTask {
		private Context context;
		StartServiceTask(Context context){
			this.context = context;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), SnsService.class);
			this.context.startService(intent);
		}
	}

	/**
	 * 注册通知
	 */
	private void registerNetWorkMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_NETWORK_CHANGE);
		filter.addAction(GlobalParam.EXIT_ACTION);
		filter.addAction(GlobalParam.ACTION_REFRESH_NOTIFIY);
		filter.addAction(GlobalParam.ACTION_UPDATE_SESSION_COUNT);
		filter.addAction(GlobalParam.ACTION_CALLBACK);
		filter.addAction(GlobalParam.ACTION_REFRESH_FRIEND);
		filter.addAction(GlobalParam.ACTION_LOGIN_OUT);
		filter.addAction(NotifySystemMessage.ACTION_VIP_STATE);
		filter.addAction(GlobalParam.CANCLE_COMPLETE_USERINFO_ACTION);
		filter.addAction(GlobalParam.ACTION_SHOW_TOAST);
		filter.addAction(GlobalParam.ACTION_SHOW_REGISTER_REQUEST);
		filter.addAction(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_SHOW_CONTACT_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_HIDE_CONTACT_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_TAB_TITLE_1);
		filter.addAction(GlobalParam.ACTION_TAB_TITLE_2);
		//filter.addAction(GlobalParam.ACTION_UPDATE_MEETING_SESSION_COUNT);
		registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
	}

	/**
	 * 检测通知类型，进行不同的操作
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if (action.equals(GlobalParam.ACTION_NETWORK_CHANGE)) {//网络通知
				boolean isNetConnect = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null) {
					if (activeNetInfo.isConnected()) {
						isNetConnect = true;
					}
				}
				IMCommon.setNetWorkState(isNetConnect);
			} else if (action.equals(GlobalParam.SWITCH_TAB)){//却换到第一个标签
				if (!fragments[0].isAdded()) {
					getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
							.show(fragments[0]).commit();
				}
			}else if(action.equals(GlobalParam.EXIT_ACTION)){//退出登录
				moveTaskToBack(true);
				System.exit(0);
			}else if(GlobalParam.CANCLE_COMPLETE_USERINFO_ACTION.equals(action)){//跳转到登陆界面
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, GlobalParam.LOGIN_REQUEST);
			}else if(GlobalParam.ACTION_TAB_TITLE_1.equals(action)){//消息未读消息数
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments[3])
						.show(fragments[3]).commit();
			}else if(GlobalParam.ACTION_TAB_TITLE_2.equals(action)){//消息未读消息数
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments[0])
						.show(fragments[0]).commit();
			}else if(GlobalParam.ACTION_UPDATE_SESSION_COUNT.equals(action)){//消息未读消息数
				//sessionPromptUpdate();
			}
			else if(GlobalParam.ACTION_LOGIN_OUT.equals(action)){//却换用户登陆
				index=0;
				myBbsListFragment = new MyBbsListFragment();
				foundFragment = new FoundFragment();
				friensLoopFragment = new FriensLoopFragment();
				profileFragment = new ProfileFragment();
				fatherContactsFragment = new FatherContactsFragment();
				fragments = new Fragment[]{myBbsListFragment, fatherContactsFragment,friensLoopFragment,foundFragment, profileFragment};
				try {
					mTimer.cancel();
				} catch (Exception e) {
				}
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, GlobalParam.LOGIN_REQUEST);
			}else if(GlobalParam.ACTION_SHOW_TOAST.equals(action)){//显示账号在其他设备登陆的通知
				String hintMsg = intent.getStringExtra("toast_msg");
				if(hintMsg!=null && !hintMsg.equals("")){
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				}
			}else if(action.equals(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP)){//显示朋友圈和会议有新的消息
				int type = intent.getIntExtra("found_type", 0);
				if(type ==1){
					meetingPromptUpdate();
				}else if(type  == 2){//有新的会议通知
					findViewById(R.id.unread_find_number).setVisibility(View.VISIBLE);
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_MEETING));
				}else{//朋友圈有新的动态
					int tipCount = IMCommon.getFriendsLoopTip(mContext);
					tipCount = tipCount+1;
					IMCommon.saveFriendsLoopTip(mContext, tipCount);
					findViewById(R.id.unread_find_number).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.unread_find_number)).setText(tipCount+"");
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
				}
				
			}else if(action.equals(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP)){//隐藏发现按钮旁边的小红点
				int type = intent.getIntExtra("found_type",0);
				if(type !=0){
					IMCommon.saveFriendsLoopTip(mContext, 0);
				}
				if(IMCommon.getIsReadFoundTip(mContext)){
					findViewById(R.id.unread_find_number).setVisibility(View.GONE);
				}
			}else if(action.equals(GlobalParam.ACTION_SHOW_CONTACT_NEW_TIP)){//显示有新的联系人
				IMCommon.saveContactTip(mContext, 1);
				findViewById(R.id.unread_address_number).setVisibility(View.VISIBLE);
			}else if(action.equals(GlobalParam.ACTION_HIDE_CONTACT_NEW_TIP)){//隐藏有新的联系人小红点
				IMCommon.saveContactTip(mContext, 0);
				findViewById(R.id.unread_address_number).setVisibility(View.INVISIBLE);
			}
			
		}
	};


	//显示未读显示数
//	public void sessionPromptUpdate(){
//		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
//		SessionTable table = new SessionTable(db);
//		int count = table.querySessionCount(GlobleType.BBS_CHAT);
//		if(count>0){
//			findViewById(R.id.unread_msg_number).setVisibility(View.VISIBLE);
//			((TextView)findViewById(R.id.unread_msg_number)).setText(count+"");
//		}else{
//			findViewById(R.id.unread_msg_number).setVisibility(View.GONE);
//		}
//	}
	
	/**
	 * 查询未读会议数据
	 */
	public boolean meetingPromptUpdate(){
		boolean isExits = false;
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		SessionTable table = new SessionTable(db);
		int count = table.queryMeetingSessionCount();
		if(count!=0){
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_MEETING));
				}
			}, 1000);
			
			isExits = true;
		}
		return isExits;
	}
	private int index,currentTabIndex;
	public void onTabClicked(View view) {
		findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
		//leftTitle.setVisibility(View.GONE);
		mAddBtn.setVisibility(View.GONE);
		findViewById(R.id.title).setVisibility(View.VISIBLE);
		ToggleButton title2TB = (ToggleButton) findViewById(R.id.tglloop);
		title2TB.setVisibility(View.GONE);
		switch (view.getId()) {
			case R.id.btn_conversation:
				//leftTitle.setVisibility(View.VISIBLE);
				mAddBtn.setVisibility(View.VISIBLE);
				sendBroadcast(new Intent(ChatFragment.ACTION_CHECK_NEW));
				index = 0;
				break;
			case R.id.btn_address_list:
				index = 1;
				break;
			case R.id.btn_container_supper:
				findViewById(R.id.title).setVisibility(View.GONE);
				title2TB.setVisibility(View.VISIBLE);
				title2TB.setBackgroundResource(R.drawable.selector_donttai_toggle);
				//findViewById(R.id.title_layout).setVisibility(View.GONE);
				title2TB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						switch (buttonView.getId()) {
							case R.id.tglloop:
								if(isChecked){
									fragments[index] = new ChatFragment();
								}else{
									fragments[index] = new FriensLoopFragment();
								}
								getSupportFragmentManager().beginTransaction()
										.replace(R.id.fragment_container, fragments[index])
										.show(fragments[index]).commit();
								break;
							default:
								break;
						}
					}
				});
				index = 2;
				break;
			case R.id.btn_find:
				index = 3;
				break;
			case R.id.btn_profile:
				index = 4;
				break;
		}
		showView();
	}
	public void showView(){
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// set current tab selected
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}
	public void uploadImage2(final Activity context, View view) {
		if (menuWindow2 != null && menuWindow2.isShowing()) {
			menuWindow2.dismiss();
			menuWindow2 = null;
		}
		menuWindow2 = new SelectAddPopupWindow(MainActivity.this, itemsOnClick2);
		// 显示窗口

		// 计算坐标的偏移量
		int xoffInPixels = menuWindow2.getWidth() - view.getWidth()+10;
		menuWindow2.showAsDropDown(view, -xoffInPixels, 0);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick2 = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.my_chat_layout:
				Intent myintent = new Intent(mContext, MyBbsListActivity.class);
				myintent.putExtra("type","1");
				startActivity(myintent);
				break;
			case R.id.create_chat_layout:
				Intent addIntent = new Intent();
				addIntent.setClass(mContext, SendBbsActivity.class);
				addIntent.putExtra("type",1);
				startActivity(addIntent);
				break;
			case R.id.sao_layout:
				Intent scanIntent = new Intent(mContext, CaptureActivity.class);
				startActivity(scanIntent);
				break;
			default:
				break;
			}
			if (menuWindow2 != null && menuWindow2.isShowing()) {
				menuWindow2.dismiss();
				menuWindow2 = null;
			}
		}
	};
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			//exitDialog();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	

	/**
	 * 销毁页面
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mIsRegisterReceiver){
			mIsRegisterReceiver = false;
			unregisterReceiver(mReceiver);
		}
		if (locationClient!=null) {
			locationClient.stop();
			locationClient = null;
		}
		// Verify picture cache files whose created date more than Fifteen days.
		System.exit(0);
	}

	/**
	 * 页面返回结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case GlobalParam.LOGIN_REQUEST:
			if (resultCode == GlobalParam.RESULT_EXIT) {// dl repair
				//moveTaskToBack(true);
				MainActivity.this.finish();
				return;
			}else if(resultCode == RESULT_OK){
				Login login = IMCommon.getLoginResult(mContext);
				if(login.urltitle==null || login.urltitle.equals("")){
					shopBtn.setText("商城");
				}else{
					shopBtn.setText(login.urltitle);
				}
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						int mode = Context.MODE_WORLD_WRITEABLE;
						if(Build.VERSION.SDK_INT >= 11){
							mode = Context.MODE_MULTI_PROCESS;
						}
						SharedPreferences sharePreferences = mContext.getSharedPreferences("LAST_TIME", mode);
						String lastTime = sharePreferences.getString("last_time","");
						int contactCount = sharePreferences.getInt("contact_count",0);
						String currentTime = FeatureFunction.formartTime(System.currentTimeMillis()/1000, "yyyy-MM-dd HH:mm:ss");
						try {
							if((lastTime==null || lastTime.equals("")) || !(FeatureFunction.jisuan(lastTime, currentTime))){
								//发送检测新的朋友通知
								Intent checkIntent = new Intent(ChatFragment.ACTION_CHECK_NEW_FRIENDS);
								checkIntent.putExtra("count",contactCount);
								mContext.sendBroadcast(checkIntent);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 2000);
				
				/**
				 * 连接到xmpp、初始化页面
				 */
				loginXMPP();
				sendBroadcast(new Intent(GlobalParam.SWITCH_TAB));
				//sessionPromptUpdate();
				Intent sintent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
				sintent.putExtra("found_type", 1);
				mContext.sendBroadcast(sintent);
				if(IMCommon.getFriendsLoopTip(mContext)!=0){
					findViewById(R.id.unread_find_number).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.unread_find_number)).setText(IMCommon.getFriendsLoopTip(mContext)+"");
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
				}
			}
			break;
		case GlobalParam.SHOW_GUIDE_REQUEST:
			if(resultCode == RESULT_OK){
				checkUpgrade();
			}
			break;
		case GlobalParam.SHOW_COMPLETE_REQUEST:
			if(resultCode == GlobalParam.SHOW_COMPLETE_RESULT){
				loginXMPP();
				//sessionPromptUpdate();
				Intent sintent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
				sintent.putExtra("found_type", 1);
				mContext.sendBroadcast(sintent);
				if(IMCommon.getFriendsLoopTip(mContext)!=0){
					findViewById(R.id.unread_find_number).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.unread_find_number)).setText(IMCommon.getFriendsLoopTip(mContext)+"");
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
				}
			}
			break;
		case REQUEST_GET_URI: 
			if (resultCode == RESULT_OK) {
				doChoose(true, intent);
			}

			break;

		case REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
				doChoose(false, intent);
			}
			break;

		case REQUEST_GET_BITMAP:
			if(resultCode == RESULT_OK){
				String path = intent.getStringExtra("path");
				if(!TextUtils.isEmpty(path)){
					Intent sendMovingIntent = new Intent();
					sendMovingIntent.setClass(mContext, SendMovingActivity.class);
					sendMovingIntent.putExtra("moving_url",path);
					mContext.startActivity(sendMovingIntent);
				}
			}

			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

//	@Override
//	protected void onNewIntent(Intent intent) {
//		if(intent == null){
//			return ;
//		}
//
//		boolean isChatNotify = intent.getBooleanExtra("chatnotify", false);
//		boolean isNotify = intent.getBooleanExtra("notify", false);
//		if(isChatNotify){
//			Login user = (Login)intent.getSerializableExtra("data");
//			user.mIsRoom = intent.getIntExtra("type", 100);
//			Intent chatIntent = new Intent(mContext, ChatMainActivity.class);
//			chatIntent.putExtra("data", user);
//			startActivity(chatIntent);
//		}else if(isNotify){
//			Intent chatIntent = new Intent(mContext, NewFriendsActivity.class);
//			startActivity(chatIntent);
//		}else {
//			sendBroadcast(new Intent(GlobalParam.SWITCH_TAB));
//		}
//
//		super.onNewIntent(intent);
//	}

	/**
	 * 选择图片
	 * @param isGallery
	 * @param data
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery){
			originalImage(data);
		}else {
			if(data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it

				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+"moving.jpg";
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
					Intent intent = new Intent(mContext, RotateImageActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", 0);
					startActivityForResult(intent, REQUEST_GET_BITMAP);
				}
			}
		}
	}

	private void originalImage(Intent data) {
		// case FLAG_CHOOSE:
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
	 * 处理消息
	 */
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.SHOW_UPGRADE_DIALOG:
				showUpgradeDialog();
				break;
			case GlobalParam.NO_NEW_VERSION:
				Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.no_version), Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_LONG).show();
				return;

			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message=(String)msg.obj;
				if (message==null || message.equals("")) {
					message=mContext.getResources().getString(R.string.timeout);
				}
				Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 检测更新
	 */
	private void checkUpgrade(){
		new Thread(){
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {

						VersionInfo versionInfo = IMCommon.getIMInfo().checkUpgrade(FeatureFunction.getAppVersionName(mContext));
						if(versionInfo != null && versionInfo.mVersion!=null && versionInfo.mState != null && versionInfo.mState.code == 0){
							mClientUpgrade = new ClientUpgrade();
							mVersion = versionInfo.mVersion;
							if(mClientUpgrade.compareVersion(FeatureFunction.getAppVersionName(mContext), mVersion.version)){
								mHandler.sendEmptyMessage(GlobalParam.SHOW_UPGRADE_DIALOG);
							}else{
								//mHandler.sendEmptyMessage(NO_NEW_VERSION);
							}
						}
					} catch (IMException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}


	/**
	 * 初始化版本更新对话框
	 */
	private void showUpgradeDialog() {
		LayoutInflater factor = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.client_dialog, null);
		TextView titleTextView = (TextView) serviceView.findViewById(R.id.title);
		titleTextView.setText(mContext.getResources().getString(R.string.check_new_version));
		TextView contentView = (TextView) serviceView.findViewById(R.id.updatelog);
		contentView.setText(mVersion.discription);
		Button okBtn = (Button)serviceView.findViewById(R.id.okbtn);
		okBtn.setText(mContext.getResources().getString(R.string.upgrade));
		okBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

				showDownloadApkDilog();//下载新的版本

				if (mUpgradeNotifyDialog != null){
					mUpgradeNotifyDialog.dismiss();
					mUpgradeNotifyDialog = null;
				}
			}
		});

		Button cancelBtn = (Button)serviceView.findViewById(R.id.cancelbtn);
		cancelBtn.setText(mContext.getResources().getString(R.string.cancel));
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {//隐藏版本更新对话框
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
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_btn:
			MainSearchDialog dialog = new MainSearchDialog(mContext,0);
			dialog.show();
			break;
		case R.id.add_btn:
			uploadImage2(MainActivity.this,mTitleLayout);
			break;
		default:
			break;
		}
	}



}