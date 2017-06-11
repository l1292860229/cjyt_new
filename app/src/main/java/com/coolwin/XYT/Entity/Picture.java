package com.coolwin.XYT.Entity;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class Picture implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String key;
	public String originUrl;
	public String smallUrl;
	public double width;
	public double height;
	
	public Picture(String small, String origin) {
		super();
		this.smallUrl = small;
		this.originUrl = origin;
	}

	public Picture() {
		super();
	}

	public static Picture getInfo(String json) {
		try {
			return JSON.parseObject(json, Picture.class);
			/*return JSONObject.toJavaObject(JSONObject.parseObject(json),
					Picture.class);*/
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getInfo(Picture info) {
		String json = null;
		try {
			 json = JSONObject.toJSONString(info).toString();//JSON.toJSONString(info);
		} catch (Exception e) {
			e.printStackTrace();
			json = null;
		}
		Log.e("picture",json);
		return json;
	}
	@Override
	public String toString() {
		return "Picture [smallUrl=" + smallUrl + ", originUrl=" + originUrl + "]";
	}

	
}