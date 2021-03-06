package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommodityList implements Serializable {

	private static final long serialVersionUID = 1946436545154L;
	
	public List<Commodity> mCommodityList;
	public IMJiaState mState;
	public PageInfo mPageInfo;

	public CommodityList(){}
	
	public CommodityList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				JSONArray array = json.getJSONArray("data");
				if(array != null){
					mCommodityList = new ArrayList<Commodity>();
					List<Commodity> tempList = Commodity.constructCommodityList(array);
					if(tempList != null){
						mCommodityList.addAll(tempList);
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
