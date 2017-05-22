package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CountryList implements Serializable {

	private static final long serialVersionUID = 1946436545154L;
	
	public List<Country> mCountryList;
	public IMJiaState mState;
	public PageInfo mPageInfo;

	public CountryList(){}
	
	public CountryList(String reString){
		try {
			if(reString == null || reString.equals("")){
				return;
			}
			//JSONObject json = new JSONObject(reString);
			/*if(!json.isNull("data")){*/
				JSONArray array = new JSONArray(reString);
				if(array != null){
					mCountryList = new ArrayList<Country>();
					for (int i = 0; i < array.length(); i++) {
						mCountryList.add(new Country(array.getJSONObject(i)));
						
					}
				}
		/*	}*/
		/*	
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}*/
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
