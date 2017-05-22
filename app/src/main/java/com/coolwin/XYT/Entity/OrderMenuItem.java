package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderMenuItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public String url;
	public List<OrderMenuItem> childMenuList;
	public OrderMenuItem() {
		super();
	}
	
	public OrderMenuItem(JSONObject json) {
		super();
		if(json == null || json.equals("")){
			return;
		}
		
		try {
			id = json.getInt("id");
			name = json.getString("name");
			url = json.getString("url");
			if(!json.isNull("children")){
				String childObj = json.getString("children");
				if(childObj!=null && !childObj.equals("")){
					JSONArray array = json.getJSONArray("children");
					childMenuList = new ArrayList<OrderMenuItem>();
					for (int i = 0; i <array.length(); i++) {
						childMenuList.add(new OrderMenuItem(array.getJSONObject(i)));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
}
