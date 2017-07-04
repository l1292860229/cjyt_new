package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

public class OrderDetail implements Serializable {

	private static final long serialVersionUID = -1454386741515L;
	public Order mOrder;
	public IMJiaState mState;
	
	public OrderDetail(){}
	
	public OrderDetail(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				JSONArray array = json.getJSONArray("data");
				if(array != null && array.length() > 0){
//					mOrder = new Order(array.getJSONObject(0));
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
