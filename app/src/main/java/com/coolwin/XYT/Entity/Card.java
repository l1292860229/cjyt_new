package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 名片信息
 * @author dongli
 *
 */
public class Card implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String uid;
	public String headsmall;
	public String nickname;
	public String content;
	
	
	
	
	public Card(String userID, String head, String name, String sign) {
		super();
		this.uid = userID;
		this.headsmall = head;
		this.nickname = name;
		this.content = sign;
	}

	public Card() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Card getInfo(String json) {
		try {
			return JSONObject.parseObject(json, Card.class);//toJavaObject(JSONObject.parseObject(json),
					//Card.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getInfo(Card info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}
	
}
