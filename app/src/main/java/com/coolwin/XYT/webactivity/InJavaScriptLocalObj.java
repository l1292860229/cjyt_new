package com.coolwin.XYT.webactivity;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.JoinBBSActivity;
import com.coolwin.XYT.MainActivity;
import com.coolwin.XYT.MyBbsListActivity;
import com.coolwin.XYT.UserInfoActivity;
import com.coolwin.XYT.apientity.UserInfo;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/27.
 */

final class InJavaScriptLocalObj {
    private String mHtml;
    private Context context;
    public String getmHtml() {
        return mHtml;
    }
    public InJavaScriptLocalObj(Context context){
        this.context  = context;
    }
    @JavascriptInterface
    public void showSource(String html) {
        mHtml = html;
    }
    @JavascriptInterface
    public String postMessage(String html) {
        try {
            JSONObject o= new JSONObject(html);
            String method = o.getString("method");
            String param="";
            if (o.has("param1")) {
                param = o.getString("param1");
            }
            final String param1= param;
            if(method.equals("userDetail")){
                int param2 = o.getInt("param2");
                Intent userInfoIntent = new Intent();
                userInfoIntent.setClass(context, UserInfoActivity.class);
                userInfoIntent.putExtra("type",2);
                userInfoIntent.putExtra("o",param2);
                userInfoIntent.putExtra("uid", param1);
                context.startActivity(userInfoIntent);
            }else if(method.equals("home")){
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);
            }else if(method.equals("indurstryCircle")){
                Intent hanyeIntent = new Intent();
                if(param1==null&&param1.equals("")){
                    hanyeIntent.putExtra("type","1");
                    hanyeIntent.setClass(context, MyBbsListActivity.class);
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BbsList bl= IMCommon.getIMInfo().getBbs(param1);
                                if(bl.mBbsList.size()!=0){
                                    Bbs bbs =bl.mBbsList.get(0);
                                    if(bbs.isjoin==1){
                                        Intent intent = new Intent(context, BbsChatMainActivity.class);
                                        intent.putExtra("data", bbs);
                                        context.startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(context, JoinBBSActivity.class);
                                        intent.putExtra("data", bbs);
                                        context.startActivity(intent);
                                    }
                                }
                            } catch (IMException e) {
                                e.printStackTrace();
                            } catch(Exception e){
                            }
                        }
                    }).start();
                }
                context.startActivity(hanyeIntent);
            }else if(method.equals("userInfo")){
                return GsonUtil.objectToJson(new UserInfo(GetDataUtil.getLogin(context), GetDataUtil.getUsername(context)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
