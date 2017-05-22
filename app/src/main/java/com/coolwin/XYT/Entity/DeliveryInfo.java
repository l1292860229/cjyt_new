package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

public class DeliveryInfo implements Serializable {

	private static final long serialVersionUID = -14564545455L;
	
	public Delivery mDelivery;
	public IMJiaState mState;
	
	public DeliveryInfo(){
		
	}
	
	public DeliveryInfo(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				mDelivery = new Delivery(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
