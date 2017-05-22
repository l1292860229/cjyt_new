package com.coolwin.XYT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.Entity.InviteBBS;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.adapter.ProhibitionPersonAdapter;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.sortlist.UserListAdapter;
import com.coolwin.XYT.widget.BbsReplyInfoSearchDialog;
import com.coolwin.XYT.widget.MyGridView;
import com.coolwin.XYT.widget.PullToRefreshLayout;
import com.coolwin.XYT.widget.PullableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/*
 * 我的群详情
 */
public class bbsUserActivity extends BaseActivity implements OnItemClickListener {
	public static final int REQUEST_GET_USERLIST = 104;
	public static final int REQUEST_GET_DELUSER = 105;
	final  int SHOW =0;
	private MyGridView mGridView;
	private ProhibitionPersonAdapter mAdapter;
	private UserListAdapter userListAdapter;
	private ArrayList<BbsReplyInfo> mList = new ArrayList<BbsReplyInfo>();
	private Bbs bbs;
	private final  int PROHIBITION_DATA_SUCCESS=1;
	private String isallow="1";
	private PullToRefreshLayout mContainer;
	private PullableListView sortListView;
	private boolean isvisitors = false;
	private int page=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbs_prohibition_page);
		mContext = this;
		bbs = (Bbs) getIntent().getSerializableExtra("bbs");
		isvisitors =  getIntent().getBooleanExtra("isvisitors",false);
		isallow = getIntent().getStringExtra("isallow");
		initCompent();
	}
	private void initCompent(){
		if("2".equals(isallow)){
			setTitleContent(R.drawable.back_btn,0, R.string.bbs_persion);
			findViewById(R.id.category_linear).setVisibility(View.VISIBLE);
			mContainer = (PullToRefreshLayout) findViewById(R.id.refresh_view);
			sortListView = (PullableListView) findViewById(R.id.content_view);
			mSearchBtn = (ImageView)findViewById(R.id.search_btn);
			mSearchBtn.setVisibility(View.VISIBLE);
			mSearchBtn.setOnClickListener(this);
			sortListView.setOnItemClickListener(new OnItemClickListener() {

				/**
				 * listview 子项点击事件
				 */
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
					Login mLogin = IMCommon.getLoginResult(mContext);
					if (mLogin.uid.equals(bbs.uid) && !mList.get(position).uid.equals(mLogin.uid)) {
						Intent intent = new Intent(mContext, BbsQuanXianActivity.class);
						intent.putExtra("bbsReplyInfo", mList.get(position));
						intent.putExtra("data", bbs);
						startActivityForResult(intent,REQUEST_GET_DELUSER);
					}else{
						Intent intent = new Intent(mContext, UserInfoActivity.class);
						intent.putExtra("uid", mList.get(position).uid);
						intent.putExtra("type", 2);
						if(mList.get(position).id==null||mList.get(position).id.equals("")){
							intent.putExtra("o", 0);
						}
						intent.putExtra("bbs", bbs);
						intent.putExtra("isvisitors", isvisitors);
						mContext.startActivity(intent);
					}
				}
			});
			mAddBtn = (ImageView)findViewById(R.id.add_btn);
			mAddBtn.setVisibility(View.VISIBLE);
			mAddBtn.setOnClickListener(this);
			mContainer.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
					page = 1;
					getDataloading(bbs.id,true,pullToRefreshLayout);
				}
				@Override
				public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
					page++;
					getDataloading(bbs.id,false,pullToRefreshLayout);
				}
			});
		}else if("1".equals(isallow)){
			setTitleContent(R.drawable.back_btn,0, R.string.bbs_join_persion);
			mGridView = (MyGridView) findViewById(R.id.gridview);
			 findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
			mGridView.setOnItemClickListener(this);
		}
		mLeftBtn.setOnClickListener(this);
		getData(bbs.id,isallow);
	}
	private void showItem(){
		if("2".equals(isallow)){
			if (userListAdapter==null) {
				userListAdapter = new UserListAdapter(mContext, mList);
				sortListView.setAdapter(userListAdapter);
			}else{
				userListAdapter.setData(mList);
				userListAdapter.notifyDataSetChanged();
			}
		}else if("1".equals(isallow)){
			mAdapter = new ProhibitionPersonAdapter(mContext, mList);
			mGridView.setAdapter(mAdapter);
		}

	}
	private void getData(final String bid, final String isallow){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {
						IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,"数据加载中,请稍后...");
						BbsReplyInfoList result = IMCommon.getIMInfo().bbsUserList(bid,isallow,page,null);
						if(result != null && result.mState != null){
							if(result.mState.code == 0&& result.mBbsReplyInfoList!=null ){
								Collections.reverse(result.mBbsReplyInfoList);
								mList = result.mBbsReplyInfoList;
							}
							Message msg = new Message();
							msg.what =PROHIBITION_DATA_SUCCESS;
							mHandler.sendMessage(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	private void getDataloading(final String bid, final boolean isRefush, final PullToRefreshLayout pullToRefreshLayout){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {
						final BbsReplyInfoList result = IMCommon.getIMInfo().bbsUserList(bid,isallow,page,null);
						if(result != null && result.mState != null){
							if(result.mState.code == 0&& result.mBbsReplyInfoList!=null ){
								Collections.reverse(result.mBbsReplyInfoList);
								if(isRefush){
									mList = result.mBbsReplyInfoList;
								}else{
									mList.addAll(result.mBbsReplyInfoList);
								}
							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if(isRefush){
										pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
									}else{
										pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
										if(result.mBbsReplyInfoList==null || result.mBbsReplyInfoList.size()==0){
											Toast.makeText(mContext,"没有更多数据了", Toast.LENGTH_SHORT).show();
										}
									}
								}
							});
							Message msg = new Message();
							msg.what =PROHIBITION_DATA_SUCCESS;
							mHandler.sendMessage(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
						if(isRefush){
							pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
						}else{
							pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
						}
					}
				}
			}
		}).start();
	}
	private void updateBbsUserPower(final String bid, final String uid, final String Power){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						IMCommon.getIMInfo().updatebbsUserPower(bid,uid,Power);
						Message message = new Message();
						message.what = SHOW;
						mHandler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}
	private void updateBbsUser(final String bid, final String uid, final String isallow){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						IMCommon.getIMInfo().updatebbsUser(bid,uid,isallow);
						Message message = new Message();
						message.what = GlobalParam.MSG_CHECK_FAVORITE_STATUS;
						message.obj = bid+uid;
						mHandler.sendMessage(message);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case PROHIBITION_DATA_SUCCESS:
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					showItem();
					break;
				case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
					String id = (String)msg.obj;
					for (int i = mList.size() - 1; i >= 0; i--) {
						if (id.equals(mList.get(i).bid+mList.get(i).uid)) {
							mList.remove(i);
						}
					}
					if (mAdapter != null) {
						Toast.makeText(mContext, "设置成功", Toast.LENGTH_LONG).show();
						mAdapter.notifyDataSetChanged();
					}
					break;
				case SHOW:
					Toast.makeText(mContext, "设置成功", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}

	};
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			bbsUserActivity.this.finish();
			break;
		case R.id.add_btn:
			Intent intent = new Intent();
			intent.setClass(mContext, ChooseUserActivity.class);
			intent.putExtra("adduser",true);
			intent.putExtra("noselectuser",mList);
			startActivityForResult(intent,REQUEST_GET_USERLIST);
			break;
		case R.id.search_btn://搜索群
			BbsReplyInfoSearchDialog dialog = new BbsReplyInfoSearchDialog(mContext,bbs,isvisitors);
			dialog.show();
			break;
		default:
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2 < mList.size()){
			final int index = arg2;
			new AlertDialog.Builder(bbsUserActivity.this).setTitle("提示")//设置对话框标题
					.setMessage("是否同意申请?")//设置显示的内容
					.setPositiveButton("同意",new DialogInterface.OnClickListener() {//添加确定按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
							//delBbsProhibition(mList.get(id).uid,bbs.id);
							updateBbsUser(mList.get(index).bid,mList.get(index).uid,"2");
						}
					}).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					updateBbsUser(mList.get(index).bid,mList.get(index).uid,"3");
				}
			}).show();//在按键响应事件中显示此对话框
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_GET_USERLIST:
				if (resultCode == RESULT_OK) {
					if(data!=null){
						final ArrayList<Login> users = (ArrayList<Login>) data.getSerializableExtra("userlist");
						StringBuffer sb = new StringBuffer();
						for (Login user : users) {
							sb.append(user.phone).append(",");
							mList.add(new BbsReplyInfo(bbs.id,user.uid,user.nickname,user.headsmall));
						}
						final Login mlogin = IMCommon.getLoginResult(mContext);
						if(bbs.uid.equals(mlogin.uid)){
							final String openids = sb.deleteCharAt(sb.length()-1).toString();
							sortListView.setAdapter(new UserListAdapter(mContext, mList));
							new Thread(){
								@Override
								public void run(){
									try {
										IMCommon.getIMInfo().addBbsUserList(bbs.id,openids);
										return;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}.start();
						}else{
							new Thread(new Runnable() {
								@Override
								public void run() {
									SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
									MessageTable table = new MessageTable(db);
									for (Login user : users) {
										MessageInfo msg = new MessageInfo();
										msg.fromid = IMCommon.getUserPhone(mContext);
										msg.tag = UUID.randomUUID().toString();
										msg.fromname = mlogin.nickname;
										msg.fromurl = mlogin.headsmall;
										msg.toid = user.phone;
										msg.toname = user.nickname;
										msg.tourl = user.headsmall;
										msg.typefile = MessageType.INVITE;
										msg.content = InviteBBS.getInfo(new InviteBBS(bbs.id,"邀请你加入鱼塘",
												"\""+ mlogin.nickname+"\""+"邀请你加入鱼塘【"+bbs.title+"】进入可查看详情",bbs.headsmall));
										msg.typechat = 100;
										msg.time = System.currentTimeMillis();
										msg.readState = 1;
										msg.setSendState(1);
										table.insert(msg);
										Session session = new Session();
										session.setFromId(user.phone);
										session.name = user.nickname;
										session.heading = user.headsmall;
										session.type = 100;
										session.lastMessageTime =msg.time;
										insertSession(session,user.phone);
										try {
											IMCommon.getIMInfo().sendMessage(msg,false);
										} catch (IMException e) {
											e.printStackTrace();
										}
									}
								}
							}).start();
							Toast.makeText(mContext,"已发出邀请", Toast.LENGTH_SHORT).show();
						}
					}
				}
				break;
			case REQUEST_GET_DELUSER:
				if (resultCode == RESULT_OK) {
					if(data!=null){
						String uid =  data.getStringExtra("uid");
						for (int i = mList.size() - 1; i >= 0; i--) {
							if(mList.get(i).uid.equals(uid)){
								mList.remove(i);
								sortListView.setAdapter(new UserListAdapter(mContext, mList));
								break;
							}
						}
					}
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void insertSession(Session session, String phone){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
		SessionTable table = new SessionTable(db);
		Session existSession = table.query(phone, session.type);
		if(existSession != null){
			if(existSession.isTop!=0){
				List<Session> exitsSesList = table.getTopSessionList();
				if(exitsSesList!=null && exitsSesList.size()>0){
					for (int i = 0; i < exitsSesList.size(); i++) {
						Session ses = exitsSesList.get(i);
						if(ses.isTop>1){
							ses.isTop = ses.isTop-1;
							table.update(ses, ses.type);
						}
					}
				}
				session.isTop = table.getTopSize();
			}
			table.update(session, session.type);
		}else {
			table.insert(session);
		}
	}
}
