package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupList implements Serializable {

	private static final long serialVersionUID = -21746454545461L;
	public IMJiaState mState;
	public List<Group> mGroupList;
	public PageInfo mPageInfo;

	public GroupList(){}
	public GroupList(String validCode, int code, List<Group> mGroupList){
		mState = new IMJiaState(validCode,code);
		this.mGroupList = mGroupList;
	}
	public GroupList(String validCode, int code, String message){
		mState = new IMJiaState(validCode,code,message);
	}
	public GroupList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
			
			if(mState != null && mState.code == 0){
				if(!json.isNull("data")){
					JSONArray array = json.getJSONArray("data");
					if(array != null && array.length() != 0){
						mGroupList = new ArrayList<Group>();
						for (int i = 0; i < array.length(); i++) {
							Group grop = new Group(array.getJSONObject(i));
							grop.id = i+1;
							mGroupList.add(grop);
						}
					}
				}
			}
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
