package com.coolwin.XYT.Entity;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 名片信息
 * @author dongli
 *
 */
public class InviteBBS implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	public String title;
	public String content;
	public String headsmall;
	public InviteBBS(){}
	public InviteBBS(String id, String title, String content, String headsmall){
		this.id = id;
		this.title = title;
		this.content = content;
		this.headsmall = headsmall;
	}

	public static InviteBBS getInfo(String json) {
		Log.e("getInfo","json="+json);
		try {
			return JSONObject.parseObject(json, InviteBBS.class);//toJavaObject(JSONObject.parseObject(json),
					//Card.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getInfo(InviteBBS info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}
	
}
