package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BbsList implements Serializable {

	private static final long serialVersionUID = 115645454645L;

	public List<Bbs> mBbsList;
	public IMJiaState mState;

	public BbsList(){}

	public BbsList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				String jsonData = json.getString("data");
				if(jsonData!=null && !jsonData.equals("")
						&& jsonData.startsWith("[")){
					JSONArray array = json.getJSONArray("data");
					if(array != null && array.length() != 0){
						mBbsList = new ArrayList<Bbs>();
						for (int i = 0; i < array.length(); i++) {
							mBbsList.add(new Bbs(array.getJSONObject(i)));
						}
					}
				}
				
			}
			
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
