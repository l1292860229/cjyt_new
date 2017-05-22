package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.ChatMainActivity;
import com.coolwin.XYT.ChatMainBbsActivity;
import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.UserList;
import com.coolwin.XYT.FeedBackActivity;
import com.coolwin.XYT.MainActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.WebViewActivity;
import com.coolwin.XYT.adapter.ChatTabAdapter;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.global.SystemContactGlobal;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;
import com.coolwin.XYT.widget.MainSearchDialog;
import com.coolwin.XYT.widget.SelectAddPopupWindow;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.coolwin.XYT.global.IMCommon.getIMInfo;


/**
 * 聊天Fragment的界面
 * @author dl
 */
public class ChatFragment extends Fragment implements OnItemClickListener,View.OnClickListener {
	/**
	 * 定义全局变量
	 */
	
	private View mView;
	private boolean mIsRegisterReceiver = false;
	//private Button mEditBtn;
	private ListView mListView;
	private ChatTabAdapter mAdapter;
	public final static int SHOW_PROGRESSDIALOG = 11101;
	public final static int HIDE_PROGRESSDIALOG = 11102;
	public final static int SHOW_REFRESHING_INDECATOR = 11103;
	public final static int HIDE_REFRESHING_INDECATOR = 11104;
	public final static int SHOW_LOADINGMORE_INDECATOR = 11105;
	public final static int HIDE_LOADINGMORE_INDECATOR = 11106;
	public final static int NETWORK_ERROR		= 11107;
	public final static int SHOW_MSG_NOTIFY		= 11108;
	public final static int SHOW_HANGYENULL = 11117;
	public final static int HIDE_SCROLLREFRESH  = 11118;
	private List<Session> mSessionList;
	private List<Login> mUserList = new ArrayList<Login>();

	public static final String ACTION_REFRESH_SESSION = "im_action_refresh_session";
	public static final String ACTION_RESET_SESSION_COUNT = "im_action_reset_session_count";
	public final static String DELETE_SESSION_ACTION = "im_delete_session_action";
	public final static String CREATE_MUC_SUCCESS = "im_create_muc_success_action";
	public final static String JOIN_MUC_SUCCESS = "im_join_muc_success";
	public final static String CLEAR_REFRESH_ADAPTER = "im_clear_refresh_adapter";
	public final static String DELETE_ROOM_SUCCESS = "im_delete_room_success";
	public final static String DELETE_ROOM_FAILED = "im_delete_room_failed";
	public final static String REFRESH_ROOM_NAME_ACTION = "im_refresh_room_name_action";
	public final static String ACTION_CHECK_NEW_FRIENDS = "im_action_check_new_friends";
	public final static String ACTION_CHECK_NEW = "im_action_check_new";
	public final static int SHOW_ACTION_CHECK_NEW = 1;
	private SystemContactGlobal mContact ;
	private int mContactCount;


	private int mDeletePos = -1;



	private Context mParentContext;
	private ProgressDialog mProgressDialog;
	private RelativeLayout gonggaoLayout;
	private ImageView headerImageView;
	private TextView messageTextView,usernameTextView,releasetimeTextView,contentTextView;
	private String newDate="";
	private ImageLoader mImageLoader = new ImageLoader();
	private String newsurl;
	private boolean isOnclick=false;
	private Bbs bbs;
	private Activity mContext;
	private boolean isshowBBS=false;
	public static final ChatFragment newInstance(Bbs bbs){
		ChatFragment c = new ChatFragment();
		Bundle bdl = new Bundle();
		bdl.putSerializable("bbs", bbs);
		c.setArguments(bdl);
		return c;
	}
	public static final ChatFragment newInstance(boolean isshowBBS){
		ChatFragment c = new ChatFragment();
		Bundle bdl = new Bundle();
		bdl.putBoolean("isshowBBS",isshowBBS);
		c.setArguments(bdl);
		return c;
	}
	/**
	 * 导入控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		mContext = this.getActivity();
		mView =inflater.inflate(R.layout.chat_tab, container, false);
		Bundle bundle = getArguments();
		if(bundle!=null){
			bbs = (Bbs) bundle.getSerializable("bbs");
			isshowBBS = bundle.getBoolean("isshowBBS",false);
		}
		return mView;
	}
	RelativeLayout mTitleLayout;
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
				uploadImage2(mContext,mTitleLayout);
				break;
			case R.id.sousuo:
				MainSearchDialog dialog2 = new MainSearchDialog(mContext,0);
				dialog2.show();
				break;
			case R.id.re_tuiguangzhongxin:
				Intent cIntent = new Intent(mParentContext, ChatMainBbsActivity.class);
				startActivity(cIntent);
				break;
			default:
				break;
		}
	}
	SelectAddPopupWindow menuWindow2; // 弹出框
	public void uploadImage2(final Activity context, View view) {
		if (menuWindow2 != null && menuWindow2.isShowing()) {
			menuWindow2.dismiss();
			menuWindow2 = null;
		}
		menuWindow2 = new SelectAddPopupWindow(mContext, itemsOnClick2);
		// 显示窗口

		// 计算坐标的偏移量
		int xoffInPixels = menuWindow2.getWidth() - view.getWidth()+10;
		menuWindow2.showAsDropDown(view, -xoffInPixels, 0);
	}
	// 为弹出窗口实现监听类
	private View.OnClickListener itemsOnClick2 = new View.OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.feedback:
					Intent feedbackIntent = new Intent(mContext, FeedBackActivity.class);
					startActivity(feedbackIntent);
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
	/**
	 * 初始化控件
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParentContext = (Context)ChatFragment.this.getActivity();
		initRegister();
		initComponent();
	}

	/**
	 * 注册通知
	 */
	private void initRegister(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.SWITCH_LANGUAGE_ACTION);
		filter.addAction(ACTION_REFRESH_SESSION);
		filter.addAction(ACTION_RESET_SESSION_COUNT);
		filter.addAction(DELETE_SESSION_ACTION);
		filter.addAction(CREATE_MUC_SUCCESS);
		filter.addAction(JOIN_MUC_SUCCESS);
		filter.addAction(CLEAR_REFRESH_ADAPTER);
		filter.addAction(DELETE_ROOM_SUCCESS);
		filter.addAction(DELETE_ROOM_FAILED);
		filter.addAction(GlobalParam.REFRESH_ALIAS_ACTION);
		filter.addAction(GlobalParam.BE_KICKED_ACTION);
		filter.addAction(GlobalParam.BE_EXIT_ACTION);
		filter.addAction(GlobalParam.ROOM_BE_DELETED_ACTION);
		filter.addAction(GlobalParam.INVITED_USER_INTO_ROOM_ACTION);
		filter.addAction(GlobalParam.ACTION_CANCLE_NEW_ORDER);
		filter.addAction(GlobalParam.ACTION_CANCLE_NEW_SERVICE);
		filter.addAction(GlobalParam.ACTION_CANCLE_NEW_OUTLANDER);
		filter.addAction(ACTION_CHECK_NEW_FRIENDS);
		filter.addAction(GlobalParam.ACTION_RESET_GROUP_NAME);
		filter.addAction(GlobalParam.ACTION_REFRESH_CHAT_HEAD_URL);
		filter.addAction(ACTION_CHECK_NEW);
		mParentContext.registerReceiver(mReceiver, filter);
		mIsRegisterReceiver = true;
	}


	/**
	 * 处理通知，根据不同类型做相应的更改
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null){
				String action = intent.getAction();
				Log.e("ChatFragment_onReceive", "onReceive");
				if(action.equals(GlobalParam.SWITCH_LANGUAGE_ACTION)//重新加载聊天记录
						|| action.equals(ACTION_REFRESH_SESSION)){
					initSession(false);
				}else if(action.equals(ACTION_CHECK_NEW_FRIENDS)){	//检测是否有新的朋友
					mContactCount = intent.getIntExtra("count", 0);
					mContact = new SystemContactGlobal(mParentContext,mHandler);
				}else if(action.equals(DELETE_ROOM_SUCCESS)){//删除群成功
					String froomId = intent.getStringExtra("froom_id");
					SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
					MessageTable messageTable = new MessageTable(db);
					messageTable.delete(froomId,300);
					SessionTable sessionTable = new SessionTable(db);
					sessionTable.delete(froomId,300);
					for (int i = 0; i <mSessionList.size(); i++) {
						if(mSessionList.get(i).getFromId().equals(froomId)){
							mSessionList.remove(i);
							break;
						}
					}
					mAdapter.notifyDataSetChanged();
					hideProgressDialog();
					Toast.makeText(mParentContext,mParentContext.getResources().getString(R.string.operate_success), Toast.LENGTH_SHORT).show();

				}else if(action.equals(GlobalParam.ACTION_RESET_GROUP_NAME)){//刷新群名称
					String groupId = intent.getStringExtra("group_id");
					String groupName = intent.getStringExtra("group_name");
					if((groupId!=null && !groupId.equals(""))
							&& (groupName!=null && !groupName.equals(""))){
						for (int i = 0; i < mSessionList.size(); i++) {
							if(mSessionList.get(i).getFromId().equals(groupId)){
								mSessionList.get(i).name = groupName;
								if(mAdapter!=null){
									mAdapter.notifyDataSetChanged();
								}
							}
						}
					}
				}else if(action.equals(GlobalParam.ACTION_REFRESH_CHAT_HEAD_URL)){//刷新聊天记录的头像
					String oldUrl = intent.getStringExtra("oldurl");
					String newUrl = intent.getStringExtra("newurl");
					for (int i = 0; i < mSessionList.size(); i++) {
						String headUrl = mSessionList.get(i).heading;
						if(headUrl!=null && !headUrl.equals("")){
							String[] urlArray = headUrl.split(",");
							if(urlArray.length>0){
								for (int j = 0; j < urlArray.length; j++) {
									if(urlArray[j].equals(oldUrl)){
										urlArray[j] = newUrl;
										String newHeadUrl="";
										for (int j2 = 0; j2 < urlArray.length; j2++) {
											if(i == urlArray.length- 1){
												newHeadUrl += urlArray[j2];
												continue;
											}
											newHeadUrl += urlArray[j2]+",";
										}
										mSessionList.get(i).heading = newHeadUrl;
										SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
										SessionTable sessionTable = new SessionTable(db);
										sessionTable.update(mSessionList.get(i), mSessionList.get(i).type);
										break;
									}
								}
							}
						}
					}
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}else if(action.equals(ACTION_CHECK_NEW)){//更新公告
					getGongGaoMessage();
				}
			}
		}

	};

	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_GET_CONTACT_DATA:
				Log.e("ChatsTAb_mHandler","get system contact dataing...");
				break;
			case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
				if(mContact.getContactCount()==mContactCount){
					return;
				}
				if(IMCommon.getLoginResult(mParentContext)!=null
						&& IMCommon.getLoginResult(mParentContext).isTuiJianContact){
					checkNewFriends();
				}

				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mParentContext, R.string.network_error, Toast.LENGTH_LONG).show();
				hideProgressDialog();
				break;
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				hideProgressDialog();
				String timeOutMsg = (String)msg.obj;
				Toast.makeText(mParentContext, timeOutMsg, Toast.LENGTH_LONG).show();
				break;
			case SHOW_ACTION_CHECK_NEW:
				String result = (String)msg.obj;
				JSONObject json = null;
				try {
					if(result == null ){
						return;
					}
					json = new JSONObject(result.replace("(","").replace(")",""));
					String msgstr = json.getString("msg");
					if("成功".equals(msgstr)){
						usernameTextView.setText("系统通知");
						releasetimeTextView.setText(json.getString("news_date"));
						contentTextView.setText(json.getString("news_title"));
						newsurl = json.getString("url");
						mImageLoader.getBitmap(mParentContext, headerImageView, null, json.getString("news_pic"), 0, false, true);
						if (newDate.equals(json.getString("news_date"))&&isOnclick) {
							messageTextView.setVisibility(View.GONE);
							newDate = json.getString("news_date");
						}else{
							messageTextView.setVisibility(View.VISIBLE);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case SHOW_HANGYENULL:
				Toast.makeText(mParentContext,"该圈子不存在", Toast.LENGTH_SHORT).show();
					break;
			default:
				break;
			}
		}

	};

	/**
	 * 删除消息
	 * @param pos
	 */
	private void delete(int pos){
		if(pos>=mSessionList.size()|| pos<0){
			return;
		}
		SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
		MessageTable messageTable = new MessageTable(db);
		messageTable.delete(mSessionList.get(pos).getFromId(), mSessionList.get(pos).type);
		SessionTable sessionTable = new SessionTable(db);
		sessionTable.delete(mSessionList.get(pos).getFromId(),mSessionList.get(pos).type);
		mSessionList.remove(pos);
		mAdapter.notifyDataSetChanged();
		mParentContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
		NotificationManager notificationManager = (NotificationManager) mParentContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
	}


	/**
	 * 销毁页面
	 */
	@Override
	public void onDestroy() {
		if(mIsRegisterReceiver){
			mParentContext.unregisterReceiver(mReceiver);
		}
		TCAgent.onPageEnd(mParentContext, "ChatFragment");
		super.onDestroy();
	}
	/**
	 * 实例化控件
	 */
	RelativeLayout sousuoLayout,tuiguangzhongxinLayout;
	View mSearchHeader;
	TextView friendsMessageCountTextView;
	private void initComponent(){
		mListView = (ListView) mView.findViewById(R.id.chats_list);
		mListView.setOnItemClickListener(this);
		mListView.setOnCreateContextMenuListener(this);
		//设置定位条件
		mSearchHeader = LayoutInflater.from(getActivity()).inflate(R.layout.chat_tab_head,null);
		sousuoLayout  = (RelativeLayout) mSearchHeader.findViewById(R.id.sousuo);
		sousuoLayout.setOnClickListener(this);
		contentTextView = (TextView) mSearchHeader.findViewById(R.id.content);
		gonggaoLayout = (RelativeLayout) mSearchHeader.findViewById(R.id.gonggao_content);
		headerImageView = (ImageView) mSearchHeader.findViewById(R.id.header);
		messageTextView = (TextView) mSearchHeader.findViewById(R.id.message_count);
		usernameTextView = (TextView) mSearchHeader.findViewById(R.id.username);
		releasetimeTextView = (TextView) mSearchHeader.findViewById(R.id.releasetime);
		friendsMessageCountTextView = (TextView) mSearchHeader.findViewById(R.id.friends_message_count);
		tuiguangzhongxinLayout = (RelativeLayout) mSearchHeader.findViewById(R.id.re_tuiguangzhongxin);
		tuiguangzhongxinLayout.setOnClickListener(this);
		if(bbs==null&&isshowBBS==false){
			mListView.addHeaderView(mSearchHeader);
			gonggaoLayout.setVisibility(View.VISIBLE);
			gonggaoLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent newIntent = new Intent();
					newIntent.putExtra("url","http://"+newsurl);
					newIntent.setClass(mParentContext, WebViewActivity.class);
					startActivity(newIntent);
					isOnclick = true;
					messageTextView.setVisibility(View.GONE);
				}
			});
			getGongGaoMessage();
		}
		initSession(true);
	}
	private void getGongGaoMessage(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = getIMInfo().getGongGaoMessage(MainActivity.lbs);
					IMCommon.sendMsg(mHandler,SHOW_ACTION_CHECK_NEW,result);
				} catch (IMException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	/**
	 * 获取消息数据
	 * @param isFirst
	 */
	private void initSession(boolean isFirst){
		SQLiteDatabase db = DBHelper.getInstance(mParentContext).getReadableDatabase();
		SessionTable table = new SessionTable(db);
		List<Session> tempList = new ArrayList<Session>();
		if(mSessionList!=null && mSessionList.size()>0){
			mSessionList.clear();
			if(mAdapter!=null){
				mAdapter.notifyDataSetChanged();
			}
		}
		if(bbs!=null){
			tempList = table.query(bbs.id,true);
		}else{
			tempList = table.query(null,false);
		}
		if(mSessionList == null){
			mSessionList = new ArrayList<Session>();
		}
		if(tempList!=null){
			if(isshowBBS){
				for (int i = tempList.size() - 1; i >= 0; i--) {
					if ( tempList.get(i).bid==null || tempList.get(i).bid.equals("")) {
						tempList.remove(i);
					}
				}
				mSessionList.addAll(tempList);
			}else if(bbs==null){
				int mUnreadCount=0;
				for (int i = tempList.size() - 1; i >= 0; i--) {
					if ( !(tempList.get(i).bid==null || tempList.get(i).bid.equals(""))) {
						mUnreadCount+=tempList.get(i).mUnreadCount;
						tempList.remove(i);
					}
				}
				if(mUnreadCount==0){
					friendsMessageCountTextView.setVisibility(View.GONE);
				}else{
					friendsMessageCountTextView.setVisibility(View.VISIBLE);
					friendsMessageCountTextView.setText(mUnreadCount+"");
				}
				mSessionList.addAll(tempList);
			}else{
				mSessionList.addAll(tempList);
			}
		}
		if(isFirst){
			mAdapter = new ChatTabAdapter(mParentContext, mSessionList);
			mListView.setAdapter(mAdapter);
		}else{
			updateListView();
		}
	}

	/**
	 * 对消息记录排序
	 * @param sessionlist
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sort(List<Session> sessionlist) {
		Collections.sort(sessionlist, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				return ( (Long)((Session) o2).lastMessageTime)
						.compareTo((Long) ((Session) o1).lastMessageTime);
			}
		});
	}

	/**
	 * 显示消息数据
	 */
	private void updateListView(){
		if(mAdapter != null){
			mAdapter.setData(mSessionList);
			mAdapter.notifyDataSetChanged();
			return;
		}

		mAdapter = new ChatTabAdapter(mParentContext, mSessionList);
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 检测是否有新的朋友
	 */
	private void checkNewFriends(){
		if (!IMCommon.getNetWorkState()) {
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					UserList userList = getIMInfo().getNewFriend(mContact.getPhoneString());
					if (userList!=null && userList.newFriendList!=null && userList.newFriendList.size()>0) {
						SharedPreferences sharePreferences = mParentContext.getSharedPreferences("LAST_TIME", 0);
						Editor editor = sharePreferences.edit();

						editor.putString("last_time", FeatureFunction.formartTime(System.currentTimeMillis()/1000, "yyyy-MM-dd HH:mm:ss"));
						editor.putInt("contact_count", mContact.getContactCount());
						editor.commit();
						mParentContext.sendBroadcast(new Intent(ContactsFragment.ACTION_SHOW_NEW_FRIENDS));
						mParentContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_CONTACT_NEW_TIP));
						IMCommon.saveContactTip(mParentContext, 1);
					}
				} catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mHandler, GlobalParam.MSG_TICE_OUT_EXCEPTION,
							mParentContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}



	/**
	 * listview 聊天对话子项点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
		Login user = new Login();
		final int pot;
		if (isshowBBS==true || bbs!=null) {
			pot =position;
		}else{
			pot =position-1;
		}
		user.uid = mSessionList.get(pot).getFromId();
		user.phone = mSessionList.get(pot).getFromId();
		user.nickname = mSessionList.get(pot).name;
		user.headsmall = mSessionList.get(pot).heading;
		user.mIsRoom = mSessionList.get(pot).type;
		if(mSessionList.get(pot).type==100 && mSessionList.get(pot).bid!=null
				&& !mSessionList.get(pot).bid.equals("0")
				&& !mSessionList.get(pot).bid.equals("")){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						BbsList bl= IMCommon.getIMInfo().getBbs(mSessionList.get(pot).bid);
						if(bl.mBbsList.size()!=0){
							Intent intent = new Intent(mParentContext, BbsChatMainActivity.class);
							intent.putExtra("data", bl.mBbsList.get(0));
							intent.putExtra("fromid", mSessionList.get(pot).getFromId());
							startActivity(intent);
						}
					} catch (IMException e) {
						e.printStackTrace();
					} catch(Exception e){
						IMCommon.sendMsg(mHandler,SHOW_HANGYENULL,null);
					}
				}
			}).start();
		}else{
			Intent intent = new Intent(mParentContext, ChatMainActivity.class);
			intent.putExtra("bbs", bbs);
			intent.putExtra("data", user);
			startActivity(intent);
		}
	}


	/**
	 * 显示提示框
	 * @param msg
	 */
	public void showProgressDialog(String msg){
		mProgressDialog = new ProgressDialog(mParentContext);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}

	/**
	 * 隐藏提示框
	 */
	public void hideProgressDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}



	/**
	 * 初始化listview 子项长按菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		if(mSessionList == null || mSessionList.size() == 0){
			return;
		}
		if (info.position > mSessionList.size()){
			return;
		}
		//，转发，收藏菜单
		if(mSessionList.get(info.position).isTop !=0 ){
			menu.add(Menu.NONE, 0, 0, mParentContext.getResources().getString(R.string.cancle_top_item));
		}else{
			menu.add(Menu.NONE, 0, 0, mParentContext.getResources().getString(R.string.set_top_item));
		}
		
		menu.add(Menu.NONE, 1, 1,mParentContext.getResources().getString(R.string.del_chat));
	}


	/**
	 * listview 子项长按菜单选择事件
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

		int longClickItemIndex = info.position - mListView.getHeaderViewsCount();
		if(longClickItemIndex < mSessionList.size()){
			int menuItemIndex = item.getItemId();  
			Session session = mSessionList.get(longClickItemIndex);
			switch (menuItemIndex){
			case 0:
				if(session.isTop == 0){// 不置顶 -置顶
					
					SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
					SessionTable sessionTable = new SessionTable(db);
					session.isTop =  sessionTable.getTopSize()+1;
					sessionTable.update(session, session.type);
					mSessionList.remove(longClickItemIndex);
					mSessionList.add(0, session);
					mAdapter.notifyDataSetChanged();
				}else{
					SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
					SessionTable sessionTable = new SessionTable(db);
					List<Session> exitsSesList = sessionTable.getTopSessionList();
					if(exitsSesList!=null && exitsSesList.size()>0){
						for (int i = 0; i < exitsSesList.size(); i++) {
							Session ses = exitsSesList.get(i);
							if(ses.isTop>1){
								ses.isTop = ses.isTop-1;
								sessionTable.update(ses, ses.type);
							}
						}
					}
					session.isTop = 0;
					sessionTable.update(session, session.type);
					mSessionList.remove(longClickItemIndex);
					mAdapter.notifyDataSetChanged();
					initSession(false);
				}
				break;
			case 1:
				delete(longClickItemIndex);
				break;
			default:
				break;
			}
		}

		return true;  
	} 
}
