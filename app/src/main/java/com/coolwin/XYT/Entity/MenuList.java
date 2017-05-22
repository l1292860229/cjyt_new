package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yf on 2017/1/11.
 */

public class MenuList {

    public List<Menu> mMenuList;
    public IMJiaState mState;

    public MenuList(){}
    public MenuList(String reString){
        try {
            JSONObject json = new JSONObject(reString);
            if(!json.isNull("data")){
                String jsonData = json.getString("data");
                if(jsonData!=null && !jsonData.equals("")
                        && jsonData.startsWith("[")){
                    JSONArray array = json.getJSONArray("data");
                    if(array != null && array.length() != 0){
                        mMenuList = new ArrayList<Menu>();
                        for (int i = 0; i < array.length(); i++) {
                            mMenuList.add(new Menu(array.getJSONObject(i)));
                        }
                    }
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
