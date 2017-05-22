package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 名片信息
 * @author dongli
 *
 */
public class Video implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public String url;
	public String time;
	public String image;
	public String typefile;
	public Video(String url, String time, String image) {
		super();
		this.url = url;
		this.time = time;
		this.image = image;
	}
	public Video(String url, String time, String image, String typefile) {
		super();
		this.url = url;
		this.time = time;
		this.image = image;
		this.typefile = typefile;
	}
	public Video() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Video getInfo(String json) {
		try {
			return JSONObject.parseObject(json, Video.class);//toJavaObject(JSONObject.parseObject(json),
					//Card.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String getInfo(Video info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}
	
}
