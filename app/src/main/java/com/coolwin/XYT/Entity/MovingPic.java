package com.coolwin.XYT.Entity;

import com.alibaba.fastjson.JSONObject;
import com.coolwin.XYT.org.json.JSONException;

import java.io.Serializable;

public class MovingPic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String urllarge;
	public String urlsmall;
	public String typefile;
	
	
	
	public MovingPic(String urlsmall, String typefile) {
		super();
		this.urlsmall = urlsmall;
		this.typefile = typefile;
	}
	
	
	

	public MovingPic(String urllarge, String urlsmall, String typefile) {
		super();
		this.urllarge = urllarge;
		this.urlsmall = urlsmall;
		this.typefile = typefile;
	}




	public MovingPic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MovingPic(String json) {
		super();
		if(json == null || json.equals("")){
			return;
		}
		
		try {
			com.coolwin.XYT.org.json.JSONObject parentJson = new com.coolwin.XYT.org.json.JSONObject(json);
			if(!parentJson.isNull("typefile")){
				typefile = parentJson.getString("typefile");
			}
			if(!parentJson.isNull("urllarge")){
				urllarge = parentJson.getString("urllarge");
			}
			
			if(!parentJson.isNull("urlsmall")){
				urlsmall = parentJson.getString("urlsmall");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static MovingPic getInfo(String json) {
		try {
			return JSONObject.parseObject(json, MovingPic.class);//toJavaObject(JSONObject.parseObject(json),
					//Card.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getInfo(MovingPic info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}

}
