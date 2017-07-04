package com.coolwin.XYT.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.RoomTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.DB.UserTable;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.Entity.SNSMessage;
import com.coolwin.XYT.Entity.UnReadSessionInfo;
import com.coolwin.XYT.MettingDetailActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.MainActivity;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.GlobleType;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.service.SnsService;

public class ChatMessageNotifiy extends AbstractNotifiy{
	private static final String LOGTAG = "msgNotifiy";
	
	private Context mContext;
	public ChatMessageNotifiy(SnsService context) {
		super(context);
		mContext = context;
	}

	@Override
	public void notifiy(SNSMessage message) {
		Log.d(LOGTAG, "notify()...");
		MessageInfo messageInfo = null;
		if(message instanceof MessageInfo){
			messageInfo = (MessageInfo) message;
		}else{
			return;
		}
		String fuid = messageInfo.fromid;

		String msg = null;
		switch (messageInfo.typefile) {
		case MessageType.PICTURE:
			msg = messageInfo.fromname + " <" + mContext.getString(R.string.get_picture) + " > ";
			break;
		case MessageType.TEXT:
			msg = messageInfo.fromname + " : " +  messageInfo.getContent();
			break;
		case MessageType.VOICE:
			msg = messageInfo.fromname + " <" + mContext.getString(R.string.get_voice) + " > ";
			break;
		case MessageType.MAP:
			msg = messageInfo.fromname + " <" + mContext.getString(R.string.get_location) + " > ";
			break;

		default:
			break;
		}
		String notificationTitle = "";
		String notificationContent = "";
		Notification notification  = new Notification.Builder(mContext)
				.setContentTitle(notificationTitle)
				.setContentText(notificationContent)
				.build();
//		Notification notification = new Notification(R.drawable.tab_bar_icon_comment_d,
//				messageInfo.fromname + mContext.getResources().getString(R.string.send_one_msg),
//				System.currentTimeMillis());
		long currentTime = System.currentTimeMillis();
		if (currentTime - IMCommon.getNotificationTime(mContext) > IMCommon.NOTIFICATION_INTERVAL) {
			if(IMCommon.getLoginResult(mContext).isAcceptNew){
				if(IMCommon.getLoginResult(mContext).isOpenVoice){
					notification.defaults |= Notification.DEFAULT_SOUND;
				}
				if(IMCommon.getLoginResult(mContext).isOpenShake){
					notification.defaults |= Notification.DEFAULT_VIBRATE;
				}
			}
			IMCommon.saveNotificationTime(mContext, currentTime);
			
		}
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// 音频将被重复直到通知取消或通知窗口打开。
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.when = currentTime;
		int acceptId =  messageInfo.getFromId().hashCode();
		Login user = new Login();
        acceptId = messageInfo.getFromId().hashCode();
        user.uid = messageInfo.getFromId();
        user.phone = messageInfo.getFromId();
        user.nickname = messageInfo.fromname;
        user.headsmall = messageInfo.fromurl;
		try {
			ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if(cn.getClassName().equals(cn.getPackageName() + ".ChatMainActivity")){
				if(FeatureFunction.isAppOnForeground(mContext)){
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
		 if(messageInfo.typechat != GlobleType.MEETING_CHAT){
			mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
		}else{
			Intent intent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
			intent.putExtra("found_type", 1);
			mContext.sendBroadcast(intent);
			mContext.sendBroadcast(new Intent(MettingDetailActivity.ACTION_SHOW_NEW_MEETING_TIP));
		}
		if(FeatureFunction.isAppOnForeground(mContext)){
			return;
		}
		SQLiteDatabase dbDatabase = DBHelper.getInstance(mContext).getWritableDatabase();
		if (messageInfo.typechat==300) {
			RoomTable roomTab = new RoomTable(dbDatabase);
			Room room = roomTab.query(messageInfo.getToId());
			if (room!=null && room.isgetmsg==0) {
				return;
			}
		}else if(messageInfo.typechat==100){
			UserTable userTable = new UserTable(dbDatabase);
			Login login = userTable.query(messageInfo.getFromId());
			if (login!=null && login.isGetMsg==0) {
				return;
			}
		}
		Intent intent = new Intent(mContext, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("data", user);
		intent.putExtra("chatnotify", true);
		intent.putExtra("type", messageInfo.typechat);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, messageInfo.getToId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		SessionTable table = new SessionTable(db);
		UnReadSessionInfo sessionInfo = table.queryUnReadSessionInfo();
		
		if (sessionInfo.sessionCount > 1) {
			notificationTitle = mContext.getString(R.string.app_name);
			
			notificationContent = sessionInfo.sessionCount + mContext.getString(R.string.contact_count) 
					+ mContext.getString(R.string.send_in) + sessionInfo.msgCount 
					+ mContext.getString(R.string.msg_count_tip);
        }
		else {
			notificationTitle = messageInfo.fromname;
			
			if (sessionInfo.msgCount > 1) {
				notificationContent = mContext.getString(R.string.send_in) + sessionInfo.msgCount 
						+ mContext.getString(R.string.msg_count_tip);
			}
			else {
				notificationContent = messageInfo.getContent();
			}
		}
//		notification.setLatestEventInfo(mContext, notificationTitle, notificationContent, contentIntent);
		getNotificationManager().notify(acceptId, notification);

		
	}

}
