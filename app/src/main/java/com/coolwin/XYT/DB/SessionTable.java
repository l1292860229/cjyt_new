package com.coolwin.XYT.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.UnReadSessionInfo;
import com.coolwin.XYT.global.GlobleType;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.map.BMapApiApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SessionTable {

	public static final String TABLE_NAME = "SessionTable";//数据表的名称
	public static final String COLUMN_SESSION_ID = "sessionID";			//会话ID（如果是群就是群ID，如果是用户就是用户ID）
	public static final String COLUMN_LOGIN_ID = "loginId";
	public static final String COLUMN_NAME = "name";					//显示名称（如果是群就是群名称，如果是用户就是用户名称）
	public static final String COLUMN_HEAD = "head";					//显示头像（如果是群就是群头像，如果是用户就是用户头像）
	public static final String COLUMN_MESSAGE_TYPE = "type";			//100--单聊		300--群聊   500-会议
	public static final String COLUMN_MESSAGE_ISTOP = "isTop";			//0--不置顶		1-置顶
	public static final String COLUMN_SEND_TIME = "sendTime";
	public static final String COLUMN_BID = "bid";

	public static final String COLUMN_INTEGER_TYPE = "integer";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;

	public SessionTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}
	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {

			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_SESSION_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_NAME, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_HEAD, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_LOGIN_ID, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_MESSAGE_TYPE, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_MESSAGE_ISTOP, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_SEND_TIME, COLUMN_INTEGER_TYPE);
			columnNameAndType.put(COLUMN_BID, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_SESSION_ID + "," + COLUMN_MESSAGE_TYPE + "," + COLUMN_LOGIN_ID + ")";

			mSQLCreateWeiboInfoTable = SqlHelper.formCreateTableSqlString(TABLE_NAME, columnNameAndType, primary_key);
		}
		return mSQLCreateWeiboInfoTable;

	}

	public static String getDeleteTableSQLString() {
		if (null == mSQLDeleteWeiboInfoTable) {
			mSQLDeleteWeiboInfoTable = SqlHelper.formDeleteTableSqlString(TABLE_NAME);
		}  
		return mSQLDeleteWeiboInfoTable;
	}

	public void insert(List<Session> sessions) {
		List<Session> sessionList = new ArrayList<Session>();
		sessionList.addAll(sessions);
		for (Session session : sessionList) {
			ContentValues allPromotionInfoValues = new ContentValues();

			allPromotionInfoValues.put(COLUMN_SESSION_ID, session.getFromId());
			allPromotionInfoValues.put(COLUMN_NAME, session.name);
			allPromotionInfoValues.put(COLUMN_HEAD, session.heading);
			allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE, session.type);
			allPromotionInfoValues.put(COLUMN_MESSAGE_ISTOP, session.isTop);
			allPromotionInfoValues.put(COLUMN_SEND_TIME, session.lastMessageTime);
			allPromotionInfoValues.put(COLUMN_LOGIN_ID, IMCommon.getUserId(BMapApiApp.getInstance()));
			allPromotionInfoValues.put(COLUMN_BID, session.bid);
			try {
				mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			} catch (SQLiteConstraintException e) {
				e.printStackTrace();
			}
		}						
	}

	public boolean insert(Session session) {
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_SESSION_ID, session.getFromId());
		allPromotionInfoValues.put(COLUMN_NAME, session.name);
		allPromotionInfoValues.put(COLUMN_HEAD, session.heading);
		allPromotionInfoValues.put(COLUMN_MESSAGE_TYPE, session.type);
		allPromotionInfoValues.put(COLUMN_MESSAGE_ISTOP, session.isTop);
		allPromotionInfoValues.put(COLUMN_SEND_TIME, session.lastMessageTime);
		allPromotionInfoValues.put(COLUMN_LOGIN_ID, IMCommon.getUserId(BMapApiApp.getInstance()));
		allPromotionInfoValues.put(COLUMN_BID, session.bid);

		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			
		return false;
	}

	public boolean update(Session session, int type) {
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_NAME, session.name);
		allPromotionInfoValues.put(COLUMN_HEAD, session.heading);
		allPromotionInfoValues.put(COLUMN_MESSAGE_ISTOP, session.isTop);
		allPromotionInfoValues.put(COLUMN_SEND_TIME, session.lastMessageTime);
		allPromotionInfoValues.put(COLUMN_BID, session.bid);
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_SESSION_ID + " = '" + session.getFromId() + "' AND " + COLUMN_MESSAGE_TYPE + "=" + type + " AND " + COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}			

		return false;
	}

	public boolean delete(String fromId, int type) {
		try {
			mDBStore.delete(TABLE_NAME, COLUMN_SESSION_ID + " = '" + fromId + "' AND " + COLUMN_MESSAGE_TYPE + "=" + type + " AND " + COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public Session query(String fromId, int type){
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_SESSION_ID + "='" + 
					fromId + "' AND " + COLUMN_MESSAGE_TYPE + "=" + type 
					+ " AND " + COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "'", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexNameId = cursor.getColumnIndex(COLUMN_NAME);
				int indexHeadId = cursor.getColumnIndex(COLUMN_HEAD);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexIsTop = cursor.getColumnIndex(COLUMN_MESSAGE_ISTOP);
				int indexSendTimeId = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexbid = cursor.getColumnIndex(COLUMN_BID);

				Session session = new Session();
				session.setFromId(cursor.getString(indexFromId));
				session.name = cursor.getString(indexNameId);
				session.heading = cursor.getString(indexHeadId);
				session.lastMessageTime = cursor.getLong(indexSendTimeId);
				session.type = cursor.getInt(indexTypeId);
				session.isTop = cursor.getInt(indexIsTop);
				session.bid = cursor.getString(indexbid);
				return session;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * 获取一共有多少条消息
	 */
	public int getTopSize(){
		int count = 0;
		Cursor cursor = null;
		try {
			cursor =  mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "' AND "
					+COLUMN_MESSAGE_ISTOP +" !=0 ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}
				count = cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}


	/**
	 * 查询置顶消息
	 * @return
	 */
	public List<Session> getTopSessionList(){
		List<Session> allInfo = new ArrayList<Session>();
		Cursor cursor = null;
		try {
			cursor =  mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
					COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "' AND "
					+COLUMN_MESSAGE_ISTOP +" !=0 ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexNameId = cursor.getColumnIndex(COLUMN_NAME);
				int indexHeadId = cursor.getColumnIndex(COLUMN_HEAD);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexIsTop = cursor.getColumnIndex(COLUMN_MESSAGE_ISTOP);
				int indexSendTimeId = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexBid = cursor.getColumnIndex(COLUMN_BID);

				do {
					Session session = new Session();
					session.setFromId(cursor.getString(indexFromId));
					session.name = cursor.getString(indexNameId);
					session.heading = cursor.getString(indexHeadId);
					session.type = cursor.getInt(indexTypeId);
					session.isTop = cursor.getInt(indexIsTop);
					session.bid = cursor.getString(indexBid);

					MessageTable messageTable = new MessageTable(mDBStore);
					List<MessageInfo> messageList = messageTable.query(session.getFromId(), -1, session.type,session.bid);
					session.lastMessageTime = cursor.getLong(indexSendTimeId);
					if(messageList != null){
						session.mMessageInfo = messageList.get(messageList.size() - 1);
						//session.lastMessageTime = messageList.get(messageList.size() - 1).time;
					}
					session.mUnreadCount = messageTable.queryUnreadCountByID(session.getFromId(), session.type);

					allInfo.add(session);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return allInfo;
	}



	public List<Session> query(String bid, boolean isShow) {
		List<Session> allInfo = new ArrayList<Session>();
		Cursor cursor = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
				+ IMCommon.getUserId(BMapApiApp.getInstance()) + "' ";
				if(isShow){
					sql +=" AND "+ COLUMN_MESSAGE_TYPE + "="+GlobleType.BBS_CHAT
							+"  AND "+ COLUMN_BID + "="+bid;
				}else{
					sql +=" AND "+ COLUMN_MESSAGE_TYPE + "!="+GlobleType.BBS_CHAT;
				}
				sql+=" ORDER BY "+COLUMN_MESSAGE_ISTOP +" DESC ,"+
				COLUMN_SEND_TIME + " DESC ";
		try {
			cursor = mDBStore.rawQuery(sql, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return null;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexNameId = cursor.getColumnIndex(COLUMN_NAME);
				int indexHeadId = cursor.getColumnIndex(COLUMN_HEAD);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
				int indexIsTop = cursor.getColumnIndex(COLUMN_MESSAGE_ISTOP);
				int indexSendTimeId = cursor.getColumnIndex(COLUMN_SEND_TIME);
				int indexBid = cursor.getColumnIndex(COLUMN_BID);

				do {
					Session session = new Session();
					session.setFromId(cursor.getString(indexFromId));
					session.name = cursor.getString(indexNameId);
					session.heading = cursor.getString(indexHeadId);
					session.type = cursor.getInt(indexTypeId);
					session.isTop = cursor.getInt(indexIsTop);
					session.bid = cursor.getString(indexBid);
					MessageTable messageTable = new MessageTable(mDBStore);
					List<MessageInfo> messageList = messageTable.query(session.getFromId(), -1, session.type,session.bid);
					session.lastMessageTime = cursor.getLong(indexSendTimeId);
					if(messageList != null){
						session.mMessageInfo = messageList.get(messageList.size() - 1);
						//session.lastMessageTime = messageList.get(messageList.size() - 1).time;
					}
					session.mUnreadCount = messageTable.queryUnreadCountByID(session.getFromId(), session.type);

					allInfo.add(session);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return allInfo;
	}


	public int querySessionBBSCount(int searchType,String bid) {
		int count = 0;
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME +
					" WHERE " + COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "' "
					+" AND "+ COLUMN_MESSAGE_TYPE + "=="+searchType+" AND " + COLUMN_BID + "=="+bid, null);
			if (cursor != null) {
				if (!cursor.moveToFirst()) {
					return 0;
				}
				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);
				do {
					String fromid = cursor.getString(indexFromId);
					int type = cursor.getInt(indexTypeId);
					MessageTable messageTable = new MessageTable(mDBStore);
					int mUnreadCount = messageTable.queryUnreadCountByID(fromid, type);
					count += mUnreadCount;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return count;
	}
	public int querySessionCount(int searchType) {
		int count = 0;
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME +
					" WHERE " + COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "' "
					+" AND "+ COLUMN_MESSAGE_TYPE + "!="+searchType, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);

				do {
					String fromid = cursor.getString(indexFromId);
					int type = cursor.getInt(indexTypeId);

					MessageTable messageTable = new MessageTable(mDBStore);
					int mUnreadCount = messageTable.queryUnreadCountByID(fromid, type);
					count += mUnreadCount;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return count;
	}

	

	public int queryMeetingSessionCount() {
		int count = 0;
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME +
					" WHERE " + COLUMN_LOGIN_ID + "='" + IMCommon.getUserId(BMapApiApp.getInstance()) + "' "
					+" AND "+ COLUMN_MESSAGE_TYPE + "="+GlobleType.MEETING_CHAT, null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					return 0;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);

				do {
					String fromid = cursor.getString(indexFromId);
					int type = cursor.getInt(indexTypeId);

					MessageTable messageTable = new MessageTable(mDBStore);
					int mUnreadCount = messageTable.queryUnreadCountByID(fromid, type);
					count += mUnreadCount;
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return count;
	}
	
	public UnReadSessionInfo queryUnReadSessionInfo() {
		UnReadSessionInfo sessionInfo = new UnReadSessionInfo();
		Cursor cursor = null;
		try {
			cursor = mDBStore.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_LOGIN_ID + "='"
					+ IMCommon.getUserId(BMapApiApp.getInstance()) + "' ", null);
			if (cursor != null) {

				if (!cursor.moveToFirst()) {
					sessionInfo.msgCount = 0;
					return sessionInfo;
				}

				int indexFromId = cursor.getColumnIndex(COLUMN_SESSION_ID);
				int indexTypeId = cursor.getColumnIndex(COLUMN_MESSAGE_TYPE);

				do {
					String fromid = cursor.getString(indexFromId);
					int type = cursor.getInt(indexTypeId);

					MessageTable messageTable = new MessageTable(mDBStore);
					int mUnreadCount = messageTable.queryUnreadCountByID(fromid, type);
					sessionInfo.msgCount += mUnreadCount;
					if (mUnreadCount > 0) {
						sessionInfo.sessionCount ++;
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return sessionInfo;
	}
}
