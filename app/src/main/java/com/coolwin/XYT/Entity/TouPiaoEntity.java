package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TouPiaoEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<Options> childList;
	public IMJiaState state;
	public TouPiaoEntity() {
		super();
	}
	
	public TouPiaoEntity(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("data")){
				String data = json.getString("data");
				if(data!=null && !data.equals("")){
					childList = new ArrayList<Options>();
					JSONArray array = json.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						childList.add(new Options(array.getJSONObject(i)));
					}
				}
			}
			if(!json.isNull("state")){
				state = new IMJiaState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	

}
