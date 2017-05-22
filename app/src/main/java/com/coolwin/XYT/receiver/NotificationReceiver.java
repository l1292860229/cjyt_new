package com.coolwin.XYT.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * 功能： 消息通知。 <br />
 * 日期：2013-4-23<br />
 * 
 * guoxin
 * @since
 */
public final class NotificationReceiver extends BroadcastReceiver {
	 // 普通消息
    public static final String ACTION_SHOW_NOTIFICATION = "com.wqdsoft.im.SHOW_NOTIFICATION";
    // 系统消息
    public static final String ACTION_NOTIFICATION_SYSTEM = "com.wqdsoft.im.NOTIFICATION_CLICKED";

    public static final String ACTION_NOTIFICATION_CLEARED = "com.wqdsoft.im.NOTIFICATION_CLEARED";
	@Override
	public void onReceive(Context context, Intent intent) {}

}
