package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class ChildCity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String text;
	public List<Login> userList;
	
	public ChildCity() {
		super();
	}
	public ChildCity(JSONObject json) {
		super();
		try {
			if(json == null || json.equals("")){
				return;
			}
			id = json.getInt("id");
			text = json.getString("City");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	

}
