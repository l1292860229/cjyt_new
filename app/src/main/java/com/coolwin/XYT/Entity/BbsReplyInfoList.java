package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class BbsReplyInfoList implements Serializable {

	private static final long serialVersionUID = 115645454645L;

	public ArrayList<BbsReplyInfo> mBbsReplyInfoList;
	public IMJiaState mState;
	public String max;
	public String min;
	public String speakStatus;

	public BbsReplyInfoList(){}

	public BbsReplyInfoList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				String jsonData = json.getString("data");
				if(jsonData!=null && !jsonData.equals("")
						&& jsonData.startsWith("[")){
					JSONArray array = json.getJSONArray("data");
					if(array != null && array.length() != 0){
						mBbsReplyInfoList = new ArrayList<BbsReplyInfo>();
						for (int i = 0; i < array.length(); i++) {
							mBbsReplyInfoList.add(new BbsReplyInfo(array.getJSONObject(i)));
						}
					}
				}
				
			}
			
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
			if(!json.isNull("max")){
				max = json.getString("max");
			}
			if(!json.isNull("min")){
				min = json.getString("min");
			}
			if(!json.isNull("speakStatus")){
				speakStatus = json.getString("speakStatus");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
