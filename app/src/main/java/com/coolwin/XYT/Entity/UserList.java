package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserList implements Serializable {

	private static final long serialVersionUID = 1156435454541L;
	
	public List<Login> mUserList;
	public List<NewFriendItem> newFriendList;
	public IMJiaState mState;
	public PageInfo mPageInfo;

	public UserList(){}
	
	/**
	 * type == 0 普通用户
	 * type == 1 新的用户
	 * @param reString
	 * @param type
	 */
	public UserList(String reString, int type){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				
				JSONArray array = json.getJSONArray("data");
				if(array != null){
					if(type == 0){
						mUserList = new ArrayList<Login>();
						for (int i = 0; i < array.length(); i++) {
							mUserList.add(new Login(array.getJSONObject(i)));
						}
					}else if(type == 1){
						newFriendList = new ArrayList<NewFriendItem>();
						for (int i = 0; i < array.length(); i++) {
							newFriendList.add(new NewFriendItem(array.getJSONObject(i)));
						}
					}
					
				}
			}
			
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
