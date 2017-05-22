package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Favorite implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<FavoriteItem> childList;
	public PageInfo page;
	public IMJiaState status;

	public Favorite(String reqString) {
		super();
		try {
			if(reqString == null || reqString.equals("")){
				return;
			}
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("data")){
				String jsonData = json.getString("data");
				if(jsonData.startsWith("[")){
					childList = new ArrayList<FavoriteItem>();
					JSONArray jsonArray = json.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						childList.add(new FavoriteItem(jsonArray.getString(i)));
					}
				}
			}
			if(!json.isNull("state")){
				status = new IMJiaState(json.getJSONObject("state"));
			}
			if(!json.isNull("pageInfo")){
				page = new PageInfo(json.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
