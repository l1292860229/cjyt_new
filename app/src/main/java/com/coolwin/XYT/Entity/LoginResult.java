package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;

public class LoginResult implements Serializable {

	private static final long serialVersionUID = 113454353454L;
	
	public Login mLogin;
	public IMJiaState mState;

	
	public LoginResult(){}
	public LoginResult(String validCode, int code, Login mLogin){
		mState = new IMJiaState(validCode,code);
		this.mLogin = mLogin;
	}
	public LoginResult(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			
			if(!json.isNull("data")){
				mLogin = new Login(json.getJSONObject("data"));
			}
			
			if(!json.isNull("state")){
				mState = new IMJiaState(json.getJSONObject("state"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
