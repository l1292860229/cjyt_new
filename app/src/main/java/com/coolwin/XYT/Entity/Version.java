package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

public class Version implements Serializable {

	private static final long serialVersionUID = 19874543545465L;
	/**
	 * "id":"6","version":"3","url":"http://localhost/dinodirect/Public/apk/dalong_V1.5.apk","discription":"dvvdd","type":"OED"
	 */
	public String version;
	public String downloadUrl;
	public String discription;
	public boolean hasNewVersion = false;
	public String name;
	public Version(){}
	
	public Version(JSONObject json){
		try {
			
			if(!json.isNull("currVersion")){
				version = json.getString("currVersion");
			}
			
			if(!json.isNull("url")){
				downloadUrl = json.getString("url");
				name = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
			}
			
			if(!json.isNull("description")){
				discription = json.getString("description");
			}

			if(!json.isNull("hasNewVersion")){
				hasNewVersion = json.getInt("hasNewVersion") == 1 ? true : false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
