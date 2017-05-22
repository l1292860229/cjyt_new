package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 分享链接
 * @author dongli
 *
 */
public class ShareUrl implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String url;
	public String title;
	public String imageurl;




	public ShareUrl(String url, String title, String imageUrl) {
		super();
		this.url = url;
		this.title = title;
		this.imageurl = imageUrl;
	}

	public ShareUrl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static ShareUrl getInfo(String json) {
		try {
			return JSONObject.parseObject(json, ShareUrl.class);
					//Card.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getInfo(ShareUrl info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}
	
}
