package com.coolwin.XYT.receiver;

import com.coolwin.XYT.service.XmppManager;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class MultiMessageListener implements PacketListener {
	
	public static final String LOGTAG = "MultiMessageListener";
	private XmppManager xmppManager;
	
	public MultiMessageListener(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		Message message = (Message) packet;
		xmppManager.getSnsMessageLisener().notityMessage(xmppManager.getSnsMessageLisener().getNotifyChatMessage(), message.getBody());
	}

}
