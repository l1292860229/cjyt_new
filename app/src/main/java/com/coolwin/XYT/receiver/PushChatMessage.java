package com.coolwin.XYT.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.fragment.ChatFragment;
import com.coolwin.XYT.service.SnsService;
import com.coolwin.XYT.service.XmppManager;

/**
 * 
 * 功能：发送聊天信息. <br />
 * 日期：2013-4-27<br />
 * 地点：无穷大软件<br />
 * 版本：ver 1.0<br />
 * 
 * guoxin
 * @since
 */
public class PushChatMessage implements PushMessage {
	private static final String TAG = "PushChatMessage";

	//public static final int DEL_CHAT_TAB_GROUP = 0;
	//public static final int DEL_CONTACT_GROUP = 1;
	public static final int DEL_CHAT_GROUP_DETAIL = 2;
	/**
	 * 可以接收该广播，帮助请求者发送消息<br/>
	 * 附加消息:{@link PushChatMessage#EXTRAS_MESSAGE}
	 */
	public static final String ACTION_SEND_MESSAGE = "com.wqdsoft.im.sns.push.ACTION_SEND_MESSAGE";
	/**
	 * 返回发送消息的结果<br/>
	 * 附加消息:{@link PushChatMessage#EXTRAS_MESSAGE}
	 */
	public static final String ACTION_SEND_STATE = "com.wqdsoft.im.sns.push.ACTION_SEND_STATE";
	/**
	 * 附加信息变量.<br/>
	 * 属性值:{@link MessageInfo}
	 */
	public static final String EXTRAS_MESSAGE = "extras_messae";

	private BroadcastReceiver broadcastReceiver;
	private XmppManager xmppManager;
	public PushChatMessage(XmppManager xmppManager){
		this.xmppManager = xmppManager;

		init();
	}

	private void init(){
		Log.d(TAG, "init()");
		this.broadcastReceiver = new MyBroadcastReceiver();
		registerReceiver();
	}

	private void registerReceiver(){
		Log.d(TAG, "registerReceiver()");
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SEND_MESSAGE);
		filter.addAction(SnsService.ACTION_SERVICE_STOP);
		xmppManager.getSnsService().registerReceiver(broadcastReceiver, filter);
	}

	private void unregisterReceiver(){
		Log.d(TAG, "unregisterReceiver()");
		xmppManager.getSnsService().unregisterReceiver(broadcastReceiver);
	}
	@Override
	public void pushMessage(MessageInfo msg, String group) {
		boolean flag = false;
		try {
			flag = xmppManager.getSnsMessageLisener().sendMessage(msg, group);
		} catch (Exception e) {
			e.printStackTrace();
		}
		msg.setSendState(flag ? 1 : 0);
		Log.d(TAG, "pushMessage():" + flag);
		sendCast(msg);
	}

	private void sendCast(MessageInfo msg){

		try {
			SQLiteDatabase db = DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase();
			MessageTable messageTable = new MessageTable(db);
			messageTable.update(msg);
		} catch (Exception e) {
			Log.d(TAG, "通知:", e);
		}

		Log.d(TAG, "sendBroadcast()");
		Intent intent = new Intent(ACTION_SEND_STATE);
		intent.putExtra(EXTRAS_MESSAGE, msg);
		xmppManager.getSnsService().sendBroadcast(intent);
	}

	/**
	 * 
	 * 功能：广播接收器，接收应用需要推送的消息. <br />
	 * 日期：2013-4-27<br />
	 * 地点：无穷大软件<br />
	 * 版本：ver 1.0<br />
	 * 
	 * guoxin
	 * @since
	 */
	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "MyBroadcast:onReceive()");
			String action = intent.getAction();
			if(ACTION_SEND_MESSAGE.equals(action)){
				MessageInfo info = (MessageInfo) intent.getSerializableExtra(EXTRAS_MESSAGE);
				String group = intent.getStringExtra("groupname");
				int isResend = intent.getIntExtra("isResend", 0);
				if(isResend == 0){
					Intent refreshIntent = new Intent(ChatFragment.ACTION_REFRESH_SESSION);
					refreshIntent.putExtra("message", info);
					context.sendBroadcast(refreshIntent);
				}

				if(info != null){
					pushMessage(info, group);
				}
			}else if(SnsService.ACTION_SERVICE_STOP.equals(action)){
				unregisterReceiver();
			}
		}

	}

}
