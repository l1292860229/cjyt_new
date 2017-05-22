package com.coolwin.XYT.Entity;


public final class MessageType {
	
	/**
	 * 文字
	 */
	public static final int TEXT = 1;

	/**
	 * 图片
	 */
	public static final int PICTURE = 2;
	
	/**
	 * 声音
	 */
	public static final int VOICE = 3;

	
	/**
	 * 位置
	 */
	public static final int MAP = 4;
	
	/**
	 * 红包
	 */
	public static final int REDPACKET = 5;
	/**
	 * 分享url
	 */
	public static final int SHAREURL = 6;
	/**
	 * 通讯录名片
	 */
	public static final int CARD = 7;

	/**
	 * 邀请群聊
	 */
	public static final int INVITE = 9;
	/**
	 * 视频
	 */
	public static final int VIDEO = 10;

	public static String timeUid(){
		return System.currentTimeMillis() + "";
	}
}
