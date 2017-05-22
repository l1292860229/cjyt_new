package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

/**
 * 
 * @author dongli
 *
 */
public class CheckFriendsItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String phone;
	public int type;
	public int isfriend;
	public String userID;
	public CheckFriendsItem(String phone, int type) {
		super();
		this.phone = phone;
		this.type = type;
	}
	public CheckFriendsItem() {
		super();
	}
	
	public CheckFriendsItem(JSONObject json) {
		super();
		init(json);
	}
	
	private void init(JSONObject json){
		try {
			this.phone = json.getString("phone");
			this.type = json.getInt("type");
			this.isfriend = json.getInt("isfriend");
			this.userID = json.getString("uid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	

}
