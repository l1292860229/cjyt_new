package com.coolwin.XYT.service;

import android.util.Log;

import com.coolwin.XYT.global.FeatureFunction;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;

public class XMPPInvitationListener implements InvitationListener {
	
	public static final String LOGTAG = "XMPPInvitationListener";
	private XmppManager xmppManager;
	
	public XMPPInvitationListener(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
	}

	@Override
	public void invitationReceived(Connection conn, String room,
								   String inviter, String reason, String password, Message message) {
		Log.e("XMPPInvitationListener", "邀请用户回调");
		String roomName = room.split("@")[0];
		long time = System.currentTimeMillis();
		xmppManager.getSnsMessageLisener().joinRoom(roomName, FeatureFunction.getTimeDate(time));
	}

}
