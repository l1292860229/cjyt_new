package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class IMProjectItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String id;
	public String text;
	public int vieworder;
	public List<Login> userList;
	public IMProjectItem() {
		super();
	}
	public IMProjectItem(JSONObject json) {
		super();
		try {
			if(json == null ){
				return;
			}
			if(!json.isNull("id")){
				this.id = json.getString("id");
			}
			if(!json.isNull("text")){
				this.text = json.getString("text");
			}
			if(!json.isNull("vieworder")){
				this.vieworder = json.getInt("vieworder");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
