package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Country implements Serializable {

	private static final long serialVersionUID = -146445435454L;
	
	public String countryID;//id
	public String country;//text
	public List<ChildCity> childList;
	
	public Country(){
		
	}

	public Country(JSONObject json){
		try {
			countryID = json.getString("id");
			country = json.getString("State");
		
			if(!json.isNull("Cities")){
				JSONArray jsonArray = json.getJSONArray("Cities");
				childList = new ArrayList<ChildCity>();
				for (int i = 0; i < jsonArray.length(); i++) {
					childList.add(new ChildCity(jsonArray.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
