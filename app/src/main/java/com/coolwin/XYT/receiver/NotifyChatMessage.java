package com.coolwin.XYT.receiver;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.RoomTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.DB.UserTable;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.SessionList;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.org.json.JSONObject;
import com.coolwin.XYT.service.XmppManager;

import java.util.List;

/**
 * 
 * 功能： 接收到发送的消息.通过广播发送出去 <br />
 * 
 * guoxin
 * @since
 */
public class NotifyChatMessage implements NotifyMessage {
	private static final String TAG = "NotifyChatMessage";

	/**
	 * 聊天服务发来聊天信息, 广播包<br/>
	 * 附加参数: {@link NotifyChatMessage#EXTRAS_NOTIFY_CHAT_MESSAGE}
	 */
	public static final String ACTION_NOTIFY_CHAT_MESSAGE = "com.wqdsoft.im.sns.notify.ACTION_NOTIFY_CHAT_MESSAGE";
	/**
	 * 某消息列表有更新，注意查收
	 * 附加参数: {@link NotifyChatMessage#EXTRAS_NOTIFY_SESSION_MESSAGE}
	 */
	public static final String ACTION_NOTIFY_SESSION_MESSAGE = "com.wqdsoft.im.sns.notify.ACTION_NOTIFY_SESSION_MESSAGE";

	/**
	 * 更新语音转文字成功之后语音消息对应的文本信息通知
	 */
	public static final String ACTION_CHANGE_VOICE_CONTENT = "com.teamchat.chat.intent.action.ACTION_CHANGE_VOICE_CONTENT";

	
	/**
	 * 附加信息<br/> {@link MessageInfo}
	 */
	public static final String EXTRAS_NOTIFY_CHAT_MESSAGE = "extras_message";
	/**
	 * 附加信息<br/> {@link SessionList}
	 */
	public static final String EXTRAS_NOTIFY_SESSION_MESSAGE = "extras_session";

	private ChatMessageNotifiy chatMessageNotifiy;
	public XmppManager xmppManager;
	public Login userInfoVo;
	

	public NotifyChatMessage(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
		this.userInfoVo = xmppManager.getSnsService().getUserInfoVo();
		chatMessageNotifiy = new ChatMessageNotifiy(xmppManager.getSnsService());
	}

	@Override
	public void notifyMessage(String msg) {
		Log.e("NotifyChatMessage", msg);
		try {
			
			if(msg == null || msg.equals("" )
					|| msg.equals("This room is not anonymous.")){
				return;
			}
			
			MessageInfo info = new MessageInfo(new JSONObject(msg));
			if(info.typechat != 100  && info.getFromId().equals(IMCommon.getUserId(xmppManager.getSnsService()))){
				return;
			}
			info.sendState = 1;
			if (info != null) {
				saveMessageInfo(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMessageInfo(MessageInfo info) {
		
		if(info.typefile == MessageType.VOICE){
			info.setSendState(4);
		}
		SQLiteDatabase dbDatabase = DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase();
		MessageTable table = new MessageTable(dbDatabase);
		try {
			table.insertThrowException(info);
			Session session = new Session();
			if(info.typechat == 100 || info.typechat == 400){
				session.setFromId(info.fromid);
				session.name = info.fromname;
				session.heading = info.fromurl;
				session.lastMessageTime = info.time;
			} else {
				session.setFromId(info.toid);
				session.name = info.toname;
				session.heading = info.tourl;
				session.lastMessageTime = info.time;
			}

			session.type = info.typechat;
			session.bid = info.bid;
			SessionTable sessionTable = new SessionTable(dbDatabase);
			Session existSession = sessionTable.query(session.getFromId(), info.typechat);
			if(existSession != null){
				if(existSession.isTop!=0){
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
					session.isTop = sessionTable.getTopSize();
				}
				sessionTable.update(session, info.typechat);
			}else {
				sessionTable.insert(session);
			}
			sendBroad(info);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}
	private  long nowTime=0;
	private void sendBroad(MessageInfo info) {
		Log.d(TAG, "sendBroad()");
		Intent intent = new Intent(ACTION_NOTIFY_CHAT_MESSAGE);
		intent.putExtra(EXTRAS_NOTIFY_CHAT_MESSAGE, info);
		chatMessageNotifiy.notifiy(info);
		if (xmppManager != null && xmppManager.getSnsService() != null) {
			xmppManager.getSnsService().sendBroadcast(intent);
		}
		if (info.typechat==300) {
			RoomTable roomTab = new RoomTable(DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase());
			Room room = roomTab.query(info.getToId());
			if (room.isgetmsg==0) {
				return;
			}
		}else if (info.typechat==100) {
			UserTable userTable = new UserTable(DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase());
			Login login = userTable.query(info.getFromId());
			if (login!=null && login.isGetMsg==0) {
				return;
			}
		}
		long temp = System.currentTimeMillis();
		if (temp-nowTime>1000) {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(xmppManager.getSnsService(), notification);
			r.play();
			nowTime = temp;
		}
	}
}
