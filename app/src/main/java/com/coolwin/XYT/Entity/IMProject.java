package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IMProject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IMJiaState state;
	
	public List<IMProjectItem> childList;

	public IMProject() {
		super();
	}
	
	public IMProject(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		try {
			JSONObject json = new JSONObject(reqString);
			if(!json.isNull("state")){
				this.state = new IMJiaState(json.getJSONObject("state"));
			}
			if(!json.isNull("data")){
				String dataString = json.getString("data");
				if(dataString!=null && !dataString.equals("")){
					JSONArray jsonArray = json.getJSONArray("data");
					if(jsonArray!=null && jsonArray.length()>0){
						childList = new ArrayList<IMProjectItem>();
						for (int i = 0; i < jsonArray.length(); i++) {
							childList.add(new IMProjectItem(jsonArray.getJSONObject(i)));
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
