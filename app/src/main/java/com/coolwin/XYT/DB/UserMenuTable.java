package com.coolwin.XYT.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.coolwin.XYT.Entity.Login;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserMenuTable {

	public static final String TABLE_NAME = "UserMenuTable";//数据表的名称

	public static final String COLUMN_PHONE = "phone"; //用户id
	public static final String COLUMN_USERMENU = "usermenu";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_TEXT_TYPE = "text";
	public static final String PRIMARY_KEY_TYPE = "primary key(";

	private SQLiteDatabase mDBStore;

	private static String mSQLCreateWeiboInfoTable = null;
	private static String mSQLDeleteWeiboInfoTable = null;
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public UserMenuTable(SQLiteDatabase sqlLiteDatabase) {
		mDBStore = sqlLiteDatabase;
	}

	public static String getCreateTableSQLString() {
		if (null == mSQLCreateWeiboInfoTable) {
			HashMap<String, String> columnNameAndType = new HashMap<String, String>();
			columnNameAndType.put(COLUMN_PHONE, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_USERMENU, COLUMN_TEXT_TYPE);
			columnNameAndType.put(COLUMN_TIME, COLUMN_TEXT_TYPE);
			String primary_key = PRIMARY_KEY_TYPE + COLUMN_PHONE + ")";

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

	public void insert(Login user) {
		if (user.uid == null || user.uid.equals("")) {
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();

		allPromotionInfoValues.put(COLUMN_PHONE, user.phone);
		allPromotionInfoValues.put(COLUMN_USERMENU, user.mUserMenuStr);
		allPromotionInfoValues.put(COLUMN_TIME, dateFormat.format(new Date()));
		try {
			mDBStore.insertOrThrow(TABLE_NAME, null, allPromotionInfoValues);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public void update(Login user) {
		if (user == null) {
			return;
		}
		ContentValues allPromotionInfoValues = new ContentValues();
		allPromotionInfoValues.put(COLUMN_USERMENU, user.mUserMenuStr);
		allPromotionInfoValues.put(COLUMN_TIME, dateFormat.format(new Date()));
		try {
			mDBStore.update(TABLE_NAME, allPromotionInfoValues, COLUMN_USERMENU + "='" + user.phone + "'", null);
		} catch (SQLiteConstraintException e) {
			e.printStackTrace();
		}
	}

	public void delete(Login user) {
		if (user.phone == null) {
			return;
		}
		mDBStore.delete(TABLE_NAME, COLUMN_PHONE + "='" + user.phone + "'", null);
	}

	public void delete() {
		mDBStore.delete(TABLE_NAME, null, null);
	}

	public Login query(String phone) {
		Login user = new Login();
		Cursor cursor = null;
		try {
			String sql = "SELECT * FROM " + TABLE_NAME +
					" WHERE (" + COLUMN_PHONE + "='" + phone + "')";
			cursor = mDBStore.rawQuery(sql, null);
			if (cursor != null) {
				if (!cursor.moveToFirst()) {
					return null;
				}
				int indexPhone = cursor.getColumnIndex(COLUMN_PHONE);
				int indexUsermenu = cursor.getColumnIndex(COLUMN_USERMENU);
				int indexTime = cursor.getColumnIndex(COLUMN_TIME);
				user.phone = cursor.getString(indexPhone);
				user.mUserMenuStr = cursor.getString(indexUsermenu);
				user.userMenuTime = cursor.getString(indexTime);
				user.setmUserMenu(user.mUserMenuStr);
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
}