package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

public class AddGroup implements Serializable {

	private static final long serialVersionUID = -14641564545645L;
	public Group mGroup;
	public IMJiaState mState;
	
	public AddGroup(){}
	
	public AddGroup(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("data")){
				mGroup = new Group(json.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
