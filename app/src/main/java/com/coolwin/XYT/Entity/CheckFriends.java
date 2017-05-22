package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckFriends implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<CheckFriendsItem> childList;
	public IMJiaState status;
	public CheckFriends(List<CheckFriendsItem> childList, IMJiaState status) {
		super();
		this.childList = childList;
		this.status = status;
	}
	public CheckFriends() {
		super();
	}
	
	public CheckFriends(String reqString) {
		super();
		try {
			JSONObject jsonObj = new JSONObject(reqString);
			if (!jsonObj.isNull("data")) {
				JSONArray array = jsonObj.getJSONArray("data");
				if (array!=null && array.length()>0) {
					childList = new ArrayList<CheckFriendsItem>();
					for (int i = 0; i < array.length(); i++) {
						childList.add(new CheckFriendsItem(array.getJSONObject(i)));
					}
				}
			}
			if(!jsonObj.isNull("state")){
				status = new IMJiaState(jsonObj.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
