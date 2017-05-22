package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yf on 2017/1/11.
 */

public class UserMenuList {

    public ArrayList<UserMenu> mMenuList;
    public IMJiaState mState;
    public String mMenuListStr;
    public UserMenuList(){}
    public UserMenuList(String reString){
        try {
            JSONObject json = new JSONObject(reString);
            if(!json.isNull("data")){
                String jsonData = json.getString("data");
                if(jsonData!=null && !jsonData.equals("")
                        && jsonData.startsWith("[")){
                    JSONArray array = json.getJSONArray("data");
                    mMenuListStr = json.getString("data");
                    if(array != null && array.length() != 0){
                        mMenuList = new ArrayList<UserMenu>();
                        for (int i = 0; i < array.length(); i++) {
                            mMenuList.add(UserMenu.getInfo(array.getString(i)));
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
