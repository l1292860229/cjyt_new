package com.coolwin.XYT.net;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.coolwin.XYT.Entity.AddGroup;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.Entity.BranchSearchRests;
import com.coolwin.XYT.Entity.ChatImg;
import com.coolwin.XYT.Entity.CheckFriends;
import com.coolwin.XYT.Entity.CountryList;
import com.coolwin.XYT.Entity.Favorite;
import com.coolwin.XYT.Entity.FriendsLoop;
import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.GetRedpacketMoney;
import com.coolwin.XYT.Entity.Group;
import com.coolwin.XYT.Entity.GroupList;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.LoginResult;
import com.coolwin.XYT.Entity.Meeting;
import com.coolwin.XYT.Entity.MeetingItem;
import com.coolwin.XYT.Entity.MenuList;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageResult;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.MorePicture;
import com.coolwin.XYT.Entity.RedpacketDetails;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.Entity.RoomList;
import com.coolwin.XYT.Entity.RoomUsrList;
import com.coolwin.XYT.Entity.UserList;
import com.coolwin.XYT.Entity.UserMenuList;
import com.coolwin.XYT.Entity.VersionInfo;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.Utils;
import com.coolwin.XYT.map.BMapApiApp;
import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;
import com.tencent.mm.sdk.modelpay.PayReq;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.coolwin.XYT.global.IMCommon.getUserId;
import static com.coolwin.XYT.global.IMCommon.getUserPhone;

public class IMInfo  implements Serializable {
	private static final long serialVersionUID = 1651654562644564L;

	
	
//	public static final String SERVER_PREFIX = "http://im.api.wqdsoft.com/index.php";
//	public static final String CODE_URL ="http://im.api.wqdsoft.com/";
//	public static final String HEAD_URL = "http://im.api.wqdsoft.com/index.php";

	public static final String SERVER_PREFIX = "http://139.224.57.105/im5/index.php";
	public static final String CODE_URL ="http://139.224.57.105/im5/";
	public static final String HEAD_URL ="http://139.224.57.105/im5/index.php";
	
	public static final int PAGESIZE = 20;

	public static final String APPKEY ="0e93f53b5b02e29ca3eb6f37da3b05b9";

	public String request(String url, IMParameters params, String httpMethod, int loginType) throws IMException{
		String rlt = null;
		Log.d("URL",url);
		safeDate(params);
		rlt = Utility.openUrl(url, httpMethod, params,loginType);
		Log.e("request","rlt="+rlt);
		if(rlt != null && rlt.length() != 0){
			int c = rlt.indexOf("{");
			if(c != 0&&c!=-1){
				rlt = rlt.subSequence(c, rlt.length()).toString();
			}
		}

		return rlt;

	}
	private static MessageDigest md;
	private static StringBuilder sb;
	public static void safeDate(IMParameters requestParams) {
		String key ="1Zxm^*s7ZowzjR3@PRA^/www.winchat.com.cn-2012-2016wei20.com";
		long time = System.currentTimeMillis();
		try {
			initMD5();
			md.reset();
			md.update(("android|"+key+"|"+time).getBytes());
			byte encrypt[] = md.digest();
			for (byte t : encrypt) {
				String s = Integer.toHexString(t & 0xFF);
				if (s.length() == 1) {
					s = "0" + s;
				}
				sb.append(s);
			}
			requestParams.add("esign",sb.toString());
			requestParams.add("timestamp",time+"");
			requestParams.add("devicetype","android");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	private static void initMD5() throws NoSuchAlgorithmException {
		if(md==null){
			md = MessageDigest.getInstance("MD5");
		}
		if(sb==null){
			sb = new StringBuilder();
		}else{
			sb.setLength(0);
		}
	}
	public String requestProtocol(String url, IMParameters params, String httpMethod) throws IMException{
		String rlt = null;
		rlt = Utility.openUrl(url, httpMethod, params,0);
		return rlt;

	}

	/**
	 * 用户注册协议
	 *  /user/apiother/regist
	 * @throws IMException 
	 */
	public String getProtocol() throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX+ "/user/apiother/regist";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if(reString != null && !reString.equals("") ){
			Log.e("reString", reString);
			return reString;
		}
		return null;

	}


	/**
	 * 获取验证码 /user/apiother/getCode
	 * /user/apiother/getCode?act=getcode&tel=13808172548
	 *  isGetCode true=getcode false=-clean
	 * WeiboException
	 */
	public IMJiaState getVerCode(String tel, int type) throws IMException{
		if (tel == null || tel.equals("")) {
			return null;
		}
		IMParameters bundle = new IMParameters();
		bundle.add("phone",tel);
		if(type!=0){
			bundle.add("type", String.valueOf(type));
		}
		String url = SERVER_PREFIX + "/user/apiother/getCode";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		try {
			if(reString != null && !reString.equals("null") && !reString.equals("")){
				return new IMJiaState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 3.	验证验证码(/user/apiother/checkCode)
	 * @param phone	true	string	手机号
	 */

	public IMJiaState checkVerCode(String phone) throws IMException{
		if (phone == null || phone.equals("")) {
			return null;
		}

		IMParameters bundle = new IMParameters();
		bundle.add("phone",phone);
		String url = SERVER_PREFIX + "/user/apiother/getCode";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		try {
			if(reString != null && !reString.equals("null") && !reString.equals("")){
				return new IMJiaState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	//二、登陆注册
	/**
	 * 
	 * 1、注册
		①　非学员注册(/user/api/regist)
		1、HTTP请求方式 GET/POST
		2、是否需要登录 false
		3、支持格式 JSON
		参数	必选	类型	说明
		phone	true	string	用户的手机号
		password	true	string	密码
		name	true	string	用户姓名
		validCode	true	string	邀请码验证码
	 */
	public LoginResult register(String phone, String password) throws IMException{
		LoginResult register = null;
		IMParameters bundle = new IMParameters();
		if ((phone == null || phone.equals(""))
				|| (password == null || password.equals(""))) {
			return null;
		}
		bundle.add("phone",phone);
		bundle.add("password",password);
	
		String url = SERVER_PREFIX + "/user/api/regist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return  new LoginResult(reString);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		return register;

	}
	//在我们自己的服务器上注册
	public String register(String password, String usernick, String tjr, String telephone) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("username",telephone);
		bundle.add("name",usernick);
		bundle.add("tjr",tjr);
		bundle.add("password",password);
		bundle.add("id",BMapApiApp.getInstance().getResources().getString(R.string.ypid));
		bundle.add("tid","");
		String url = SERVER_PREFIX + "/user/api/registforother";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return reString;
		}
		return null;

	}

	/**
	 * 
	 * 用户登录 /user/api/login
	 * @param phone		true	string	用户的手机号
	 * @param password	true	string	密码
	 *  appkey
	 * @return
	 * @throws IMException
	 * http://192.168.1.12/im/index.php/user/api/login?phone=13689084790&password=123456
	 */
	public LoginResult getLogin(String phone, String password) throws IMException {
		IMParameters bundle = new IMParameters();
		bundle.add("phone", phone);
		bundle.add("password", password);
		String url = SERVER_PREFIX  + "/user/api/loginforother";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new LoginResult(reString.trim());
		}
		return null;

	}
	/**
	 * ①　忘记密码，获取新密码(/api/index/forgetpwd)
	 * @param phone	true	int	
	 * @throws IMException 
	 */
	public IMJiaState findPwd(String phone) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("phone", phone);
		String url = SERVER_PREFIX  + "/api/index/forgetpwd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	} 

	/**
	 * 
	 * 更改用户资料 post方式请求
	 * /user/api/edit
	 picture     true     file 上传图片
	@param nickname    true     String 昵称
	@param gender	   false	string	 0-男 1-女 2-未知 未填写
	@param sign	       false	string	签名
	province  false	int	省
	@param city	   	   false	int	市
	 * @throws IMException 
	 * 
	 */
	public LoginResult modifyUserInfo(
            String file, String nickname, String gender,
            String sign, String provinceid, String city, String headsmall, String uid,
            String companywebsite, String industry, String company, String companyaddress,
            String job, String provide, String demand, String telephone) throws IMException{
		IMParameters bundle = new IMParameters();
		//必填选项
		bundle.add("appkey", APPKEY);
		if(headsmall !=null && !headsmall.equals("")){
			bundle.add("headsmall", headsmall);
		}
		if(file!=null && !file.equals("") && file.length()>0){
			List<MorePicture> listpic = new ArrayList<MorePicture>();
			listpic.add(new MorePicture("picture",file));
			bundle.addPicture("pic", listpic);
		}
		if(uid!=null && !uid.equals("")){
			bundle.add("uid", uid);
		}else{
			bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		}
		if(nickname !=null && !nickname.equals("")){
			bundle.add("nickname",nickname);
		}
		if(gender !=null && !gender.equals("")){
			bundle.add("gender",gender);
		}
		bundle.add("sign",sign);
		if(provinceid!=null && !provinceid.equals("")){
			bundle.add("province", provinceid);
		}
		if(city!=null && !city.equals("")){
			bundle.add("city", city);
		}
		if(companywebsite!=null && !companywebsite.equals("")){
			bundle.add("companywebsite", companywebsite);
		}
		if(industry!=null && !industry.equals("")){
			bundle.add("industry", industry);
		}
		if(company!=null && !company.equals("")){
			bundle.add("company", company);
		}
		if(companyaddress!=null && !companyaddress.equals("")){
			bundle.add("companyaddress", companyaddress);
		}
		if(job!=null && !job.equals("")){
			bundle.add("job", job);
		}
		if(provide!=null && !provide.equals("")){
			bundle.add("provide", provide);
		}
		if(demand!=null && !demand.equals("")){
			bundle.add("demand", demand);
		}
		if(telephone!=null && !telephone.equals("")){
			bundle.add("telephone", telephone);
		}
		Login g= IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("token",g.token);
		if (g.ypId==null || g.ypId.equals("")) {
			bundle.add("id",BMapApiApp.getInstance().getResources().getString(R.string.ypid));
		}else{
			bundle.add("id",g.ypId);
		}
		String url = SERVER_PREFIX + "/user/api/edit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			LoginResult lr = new LoginResult(reString);
			return lr;
		}
		return null;
	}

	/**
	 * 根据id获取用户资料
	 * @param uid
	 * @return
	 * @throws IMException
	 */
	public LoginResult getUserInfo(String id, String uid, String kai6id) throws IMException{
		IMParameters bundle = new IMParameters();
		//下面是调用聊天服务器
		bundle.add("appkey", APPKEY);
		if (!(id==null || id.equals(""))) {
			bundle.add("id",id);
		}
		if (!(uid==null || uid.equals(""))) {
			bundle.add("uid",uid);
		}
		if (!(kai6id==null || kai6id.equals(""))) {
			bundle.add("kai6id",kai6id);
		}
		bundle.add("userid", String.valueOf(getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX +  "/user/api/detailforother";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new LoginResult(reString);
		}

		return null;
	}


	/**
	 * 16.	设置星标朋友(/user/api/setStar)
	 * fuid	true	int	用户id
	 */
	public LoginResult setStar(String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		if(fuid == null || fuid.equals("")){
			return null;
		}
		bundle.add("fuid", fuid);
		bundle.add("uid", String.valueOf(getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX +  "/user/api/setStar";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new LoginResult(reString);
		}

		return null;
	}
	/**
	 * 
	 * 上传文件
	 * @param f_upload
	 * @param type 1-图片 2-声音
	 * @return
	 * @throws IMException
	 */
	public ChatImg uploadFile(String f_upload, int type) throws IMException{
		ChatImg chatImg = null;
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		List<MorePicture> listpic = new ArrayList<MorePicture>();
		listpic.add(new MorePicture("f_upload", f_upload));
		bundle.addPicture("pic", listpic);

		bundle.add("type", String.valueOf(type));
		String url = SERVER_PREFIX +"/api/index/upload";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("data")){
					String s = json.getString("data");
					if(s!=null && !s.equals("")){
						chatImg = ChatImg.getInfo(s);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return chatImg;
	}


	//四、通讯录
	//1.朋友列表
	/**
	 * ①　朋友列表(/user/api/friendList)
	 * 获取好友列表
	 * page
	 * @return
	 * @throws IMException
	 */
	public GroupList getUserList(int number, int page) throws IMException{
		IMParameters bundle = new IMParameters();
		Login ml = IMCommon.getLoginResult(BMapApiApp.getInstance());
		if (ml.ypId==null || ml.ypId.equals("")) {
			bundle.add("id",BMapApiApp.getInstance().getResources().getString(R.string.ypid));
		}else{
			bundle.add("id",ml.ypId);
		}
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("ka6_id",ml.kai6Id);
		bundle.add("page",page+"");
		bundle.add("o",number+"");
		String url = SERVER_PREFIX +"/user/api/friendList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			List<Login> mUserList = new ArrayList<Login>();
			JSONObject json = null;
			try {
				json = new JSONObject(reString);
				JSONArray jsonList = json.getJSONArray("data");
				if(jsonList.length()==0){
					return new GroupList("0",1,"没有更多数据了");
				}
				for(int i=0;i<jsonList.length();i++){
					mUserList.add(new Login(jsonList.getJSONObject(i)));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			List<Group> mGroupList  = new ArrayList<Group>();
			mGroupList.add(new Group(mUserList));
			return new GroupList("0",0,mGroupList);
		}
		return null;
	}
//	public GroupList getUserList(int number,int page) throws IMException{
//		IMParameters bundle = new IMParameters();
//		Login ml = IMCommon.getLoginResult(BMapApiApp.getInstance());
//		if (ml.ypId==null || ml.ypId.equals("")) {
//			bundle.add("id",BMapApiApp.getInstance().getResources().getString(R.string.ypid));
//		}else{
//			bundle.add("id",ml.ypId);
//		}
//		bundle.add("ka6_id",ml.kai6Id);
//		bundle.add("page",page+"");
//		bundle.add("o",number+"");
//		String url2 = "http://shop.wei20.cn/gouwu/wish3d/a_myfriends.shtml";
//		String reString2 = request(url2, bundle, Utility.HTTPMETHOD_POST,1);
//		try {
//			JSONObject json = new JSONObject(reString2.replace("(","").replace(")","").replace("\\",""));
//			JSONObject jsonList = json.getJSONObject("list");
//			if(jsonList.length()==0){
//				return new GroupList("0",1,"没有更多数据了");
//			}
//			Iterator<String> keys =  jsonList.keys();
//			List<Login> mUserList = new ArrayList<Login>();
//			while(keys.hasNext()){
//				String uid = keys.next();
//				JSONObject o = jsonList.getJSONObject(uid);
//				Login l = new Login(uid,"",o.getString("openid"),o.getString("img"),o.getString("title"),o.getString("minprice"));
//				mUserList.add(l);
//			}
//			List<Group> mGroupList  = new ArrayList<Group>();
//			mGroupList.add(new Group(mUserList));
//			return new GroupList("0",0,mGroupList);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	//2、添加朋友
	// 1.1 添加朋友
	/**
	 * ①好友申请(/user/api/applyAddFriend)
	 * /api/user/to_friend
	 * action to_friend
	 *  uid
	 * @param fuid
	 * http://www.deedkey.com/friend/Index/action?action=to_friend&uid=200269&fuid=53
	 */
	public IMJiaState applyFriends(String userID, String fuid, String reason) throws IMException{
		IMParameters bundle = new IMParameters();
		if((userID == null || userID.equals(""))
				|| (fuid == null || fuid.equals(""))){
			return null;
		}
		bundle.add("uid",userID);
		bundle.add("fuid",fuid);
		bundle.add("content",reason);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/applyAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * ②同意加为好友(/user/api/agreeAddFriend)
	 *  action be_friend
	 * uid
	 * @param fuid
	 */
	public IMJiaState agreeFriends(String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid",fuid);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/agreeAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * ③ 拒绝加为好友(/user/api/refuseAddFriend)
	 * action refuse_f
	 * uid
	 * @param toUid 被拒绝的uid
	 */

	public IMJiaState denyFriends(String toUid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid",toUid);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/refuseAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}	

	/**
	 * ④ 删除好友(/user/api/deleteFriend)
	 *   uid
	 *  @param fuid 好友uid
	 */
	public IMJiaState cancleFriends(String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid",fuid);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/deleteFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}	

	//1.2搜号码
	/**
	 * 
	 * ① 通过手机号或昵称搜索(/user/api/search)
	 * @param userName
	 * @param page
	 * @return
	 * @throws IMException
	 */
	int id = 0;
	public UserList search_number(String search, int page) throws IMException{
		id = id+1;
		IMParameters bundle = new IMParameters();
		bundle.add("search", search);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("page", String.valueOf(page));

		String url = SERVER_PREFIX + "/user/api/search";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		Log.e("search_number","id:"+id);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new UserList(reString,0);
		}

		return null;
	}

	//1.3 从手机通讯录列表添加
	/**
	 * ① 导入手机通讯录(/user/api/importContact)
	 */
	public CheckFriends getContactUserList(String phone) throws IMException{
		if (phone == null || phone.equals("") || phone.contains("null")) {
			return null;
		}
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("phone",phone);
		bundle.add("uid", String.valueOf(getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX + "/user/api/importContact";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new CheckFriends(reString);
		}

		return null;
	}



	//3、新的朋友
	/**
	 * ①　新的朋友(/api/user/newfriend)
	 * @param phone	true	string	格式 手机1,手机2,手机3,手机4
	 * uid 登录用户id
	 * @throws IMException 
	 */

	public UserList getNewFriend(String phone) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		if(phone == null || phone.equals("")
				|| phone.startsWith(",")){
			return null;
		}
		bundle.add("phone", phone);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/newFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			//Log.e("addFriend", reString);
			return new UserList(reString,1);

		}

		return null;
	}


	/**
	 * ①　添加关注与取消关注(/api/publics/follow)
	 * publics_id	true	int	公众号的ID
	 *  uid 登录用户id
	 */
	public IMJiaState addFocus( String subUserID) throws IMException{
		IMParameters bundle = new IMParameters();

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("publics_id", subUserID);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/api/publics/follow";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}





	/**
	 * ① 朋友分组(/api/user/group)
	 * uid 登录用户id
	 * @param type true int 0-名字 1-地区 2-频率 3-添加时间 4-课程 5-行业
	 * @throws IMException 
	 */
	public UserList getContactGroupList(int type) throws IMException{

		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("type", String.valueOf(type));
		String url = SERVER_PREFIX +"/api/user/group";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") ){
			//Log.e("getContactGroupList", reString);
			try {
				return new UserList(reString,0);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return  null;
	}



	/**
	 * 添加关注与取消关注(/api/user/follow)
	 * uid 登录用户id
	 * @param fuid 要关注的用户ID
	 * type 0-取消关注 1-添加关注
	 * appkey
	 * @throws IMException 
	 * 
	 */
	public IMJiaState addfocus(String fuid/*,int type*/) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
	/*	bundle.add("type",String.valueOf(type));*/
		String url = SERVER_PREFIX + "/api/user/follow";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	// dalong


	/**
	 *  
	 * 加入黑名单 /user/api/black

	 * @param blackUid
	 * @return
	 * @throws IMException
	 */
	public IMJiaState addBlock(String blackUid) throws IMException {
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", blackUid);
		String url = SERVER_PREFIX + "/user/api/black";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}



	/**
	 *  
	 * 举报好友 /api/user/jubao
	 * @param fuid
	 * @param content
	 * @param type 1-用户 2-订阅号
	 * @return
	 * @throws IMException
	 */
	public IMJiaState reportedFriend(String fuid, String content, int type) throws IMException {
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("type", String.valueOf(type));
		bundle.add("content", content);
		String url = SERVER_PREFIX +  "/api/user/jubao";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 *  
	 * 获取黑名单列表/user/api/blackList
	 *  page
	 * @return
	 * @throws IMException
	 */
	public UserList getBlockList(/*int page*/) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		/*	bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = SERVER_PREFIX + "/user/api/blackList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			//Log.e("reString", reString);

			return new UserList(reString,0);
		}

		return null;
	}

	/**
	 *  
	 * 取消黑名单 /user/api/black
	 * @param fuid
	 * @return
	 * @throws IMException
	 */
	public IMJiaState cancelBlock(String fuid) throws IMException {
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX +"/user/api/black";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 *  添加收藏(/user/api/favorite)	
	 *  uid true 登陆用户id
	 * 	@throws IMException 
	 *  @param fuid	true	int	被收藏人的uid
	 *   otherid	false	int	如果是收藏的群组的消息，就传入此id
	 *  @param content	true	string	收藏的内容
	 */
	public IMJiaState favoreiteMoving(String fuid, String groupId,
                                      String content) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if((content == null || content.equals(""))
				&& (fuid == null || fuid.equals(""))
				&& (groupId == null || groupId.equals(""))){
			return null;
		}
		bundle.add("content", content);
		if(fuid!=null && !fuid.equals("")){
			Login login  = new Login();
			try{
				login.uid = Integer.parseInt(fuid)+"";
			}catch (NumberFormatException e){
				UserList ul =  search_number(fuid,1);
				if(ul== null || ul.mUserList==null || ul.mUserList.size()==0){
					return null;
				}else{
					login = ul.mUserList.get(0);
				}
			}
			bundle.add("fuid", login.uid);
		}
		if(groupId!=null && !groupId.equals("")){
			bundle.add("otherid", groupId);
		}

		String url = SERVER_PREFIX +"/user/api/favorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.e("favoreiteMoving", reString);
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * http://117.78.2.70/index.php/user/api/favoriteList?uid=200000
	 *  收藏列表(/user/api/favoriteList)	
	 *  @parem uid true 登陆用户id
	 *  http://117.78.2.70/index.php/user/api/favoriteList?appkey=0e93f53b5b02e29ca3eb6f37da3b05b9&uid=200018&page=1, count=20
	 */
	public Favorite favoriteList(int page) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(page!=0){
			bundle.add("page", String.valueOf(page));
		}
		bundle.add("count", "20");
		String url = SERVER_PREFIX +"/user/api/favoriteList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.e("favoriteList", reString);
			return new Favorite(reString);
		}
		return null;
	}



	/**
	 * 删除收藏(/user/api/deleteFavorite)
	 * uid  true 登陆用户id
	 * @parem favoriteid	true	int	收藏记录的id	
	 */

	public IMJiaState canclefavMoving(int favoriteid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(favoriteid == 0){
			return null;
		}
		bundle.add("favoriteid", String.valueOf(favoriteid));

		String url = SERVER_PREFIX +"/user/api/deleteFavorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.e("favoreiteMoving", reString);
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}




	/**
	 * 创建组
	 * @param name			组名
	 * @return
	 * @throws IMException
	 */
	public AddGroup AddGroup(String name) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("teamName", name);
		bundle.add("action", "addTeam");
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "friend/Index/action";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new AddGroup(reString);
		}

		return null;
	}

	/**
	 * 检测更新 /version/api/update
	 * @param version		版本号
	 * @return
	 * @throws IMException
	 */
	public VersionInfo checkUpgrade(String version) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		if(version == null || version.equals("")){
			return null;
		}
		bundle.add("os", "android");
		bundle.add("version", version.substring(1));
		String url = SERVER_PREFIX +"/version/api/update";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new VersionInfo(reString);
		}

		return null;
	}


	/**
	 * /session/api/add
	 * ①　1.	创建临时会话并添加用户
	 *  name  true 会话名称
	 * @param uids  true 所邀请用户ID串  格式：id1,id2,id3,id4
	 * @return
	 * @throws IMException
	 */
	public Room createRoom(String groupname, String uids) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		Log.e("createRoom", "groupName:"+groupname);
		bundle.add("name", groupname);
		bundle.add("uids", uids);
		bundle.add("openid", IMCommon.getLoginResult(BMapApiApp.getInstance()).phone);
		String url = SERVER_PREFIX + "/session/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new Room(reString);
		}

		return null;
	}


	/**
	 * ②　 添加用户到一个会话(/session/api/addUserToSession)
	 *  groupid     true int 群组id
	 * inviteduids true string  参数格式: uid1,uid2,uid3
	 * @return
	 * @throws IMException
	 */
	public IMJiaState inviteUsers(String sessionid, String uids) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/session/api/addUserToSession";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 扫一扫/session/api/join
	 * @param sessionid     true int 群组id
	 * @return
	 * @throws IMException
	 */
	public Room join(String sessionid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/join";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new Room(reString);
		}

		return null;
	}



	/**
	 * ③　把用户从某个群踢出(/session/api/remove)
	 *  groupid			房间ID
	 * @param fuid				被踢用户ID
	 * @return
	 * @throws IMException
	 */
	public IMJiaState kickParticipant(String sessionid, String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", getUserPhone(BMapApiApp.getInstance()));
		bundle.add("sessionid", String.valueOf(sessionid));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX +"/session/api/remove";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 获取某个用户的所在的群（房间列表）
	 * /session/api/userSessionList
	 * @param fuid	false	String	不传则查看自己的。传了则查看别人的
	 * @return
	 * @throws IMException
	 */
	public RoomList getRoomList(String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", IMCommon.getUserPhone(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX +"/session/api/userSessionList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new RoomList(reString);
		}

		return null;
	}
	public BbsList getBbsList(String type, boolean ismy, String title, int page) throws IMException{
		IMParameters bundle = new IMParameters();
		Login login= IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("appkey",APPKEY);
		bundle.add("uid", login.uid);
		if(ismy){
			if(type!=null&&"1".equals(type)){
				bundle.add("quid", login.phone);
			}else{
				bundle.add("quid", login.quId);
			}
		}
		if(type!=null){
			bundle.add("type", type);
		}
		if(title!=null){
			bundle.add("title", title);
		}
		bundle.add("page", page+"");
		String url = SERVER_PREFIX +"/Bbs/api/bbslist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsList(reString);
		}
		return null;
	}
	public BbsList getShareBbsList(String title, String type) throws IMException{
		IMParameters bundle = new IMParameters();
		Login login= IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("appkey",APPKEY);
		bundle.add("uid", login.uid);
		bundle.add("type", type);
		bundle.add("quid", login.phone);
		if(title!=null){
			bundle.add("title", title);
		}
		String url = SERVER_PREFIX +"/Bbs/api/sharebbslist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsList(reString);
		}
		return null;
	}
	public BbsList getBbs(String bid) throws IMException{
		IMParameters bundle = new IMParameters();
		Login login= IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("appkey",APPKEY);
		bundle.add("uid", login.uid);
		bundle.add("bid", bid);
		String url = SERVER_PREFIX +"/Bbs/api/getbbs";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsList(reString);
		}
		return null;
	}
	public IMJiaState addBbs(String title, String content, String file, String type , String money) throws IMException{
		IMParameters bundle = new IMParameters();
		Login login= IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("appkey",APPKEY);
		bundle.add("uid", login.uid);
		if(file!=null && !file.equals("") && file.length()>0){
			List<MorePicture> listpic = new ArrayList<MorePicture>();
			listpic.add(new MorePicture("picture",file));
			bundle.addPicture("pic", listpic);
		}
		if(type!=null){
			bundle.add("type", type);
			bundle.add("quid", login.phone);
		}else{
			bundle.add("quid", login.quId);
		}
		if(money!=null&&!"".equals(money)){
			bundle.add("money", money);
		}
		bundle.add("title", title);
		bundle.add("content", content);
		bundle.add("status", "1");
		String url = SERVER_PREFIX +"/Bbs/api/bbsadd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsList(reString).mState;
		}
		return null;
	}
	public BbsReplyInfoList getBbsReplyList(String bid, String max, String showLouZu) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("bid", bid);
		bundle.add("max", max);
		bundle.add("showlouzu", showLouZu);
		String url = SERVER_PREFIX +"/Bbs/api/bbsreplylist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList getBbsReplyLastList(String bid, String min, String showLouZu) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("bid", bid);
		bundle.add("min", min);
		bundle.add("showlouzu", showLouZu);
		String url = SERVER_PREFIX +"/Bbs/api/bbsreplylastlist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList addBbsReply(BbsReplyInfo msg) throws IMException{
		if(msg==null){
			return null;
		}
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("bid", msg.bid);
		if(!TextUtils.isEmpty(msg.nickname)){
			bundle.add("nickname", msg.nickname);
		}
		if(!TextUtils.isEmpty(msg.headsmall)){
			bundle.add("headsmall", msg.headsmall);
		}
		bundle.add("typefile", String.valueOf(msg.typefile));
		if(!TextUtils.isEmpty(msg.content)){
			bundle.add("content", msg.content);
		}
		if(msg.typefile == MessageType.PICTURE){
			if(!TextUtils.isEmpty(msg.image)){
				bundle.add("image", msg.image);
			}else{
				if(!TextUtils.isEmpty(msg.imgUrlS)){
					List<MorePicture> fileList = new ArrayList<MorePicture>();
					fileList.add(new MorePicture("file_upload", msg.imgUrlS));
					bundle.addPicture("pic", fileList);
				}
			}
			if(msg.imgWidth!=0){
				bundle.add("width", String.valueOf(msg.imgWidth));
			}
			if(msg.imgHeight !=0){
				bundle.add("height", String.valueOf(msg.imgHeight));
			}
		}else if(msg.typefile == MessageType.VOICE){
			if(!TextUtils.isEmpty(msg.voice)){
				bundle.add("voice", msg.voice);
			}else  if(!TextUtils.isEmpty(msg.voiceUrl)){
				List<MorePicture> fileList = new ArrayList<MorePicture>();
				fileList.add(new MorePicture("file_upload", msg.voiceUrl));
				bundle.addPicture("pic", fileList);

			}
		}else if(msg.typefile == MessageType.VIDEO){
			final Video video = Video.getInfo(msg.content);
			if(video!=null){
				List<MorePicture> fileList = new ArrayList<MorePicture>();
				fileList.add(new MorePicture("file_upload", video.url));
				fileList.add(new MorePicture("file_upload2", Utils.createVideoThumbnailImagePath(video.url)));
				bundle.addPicture("pic", fileList);
				bundle.add("videotime", video.time);
			}
		}
		if(msg.mLat != 0){
			bundle.add("lat", String.valueOf(msg.mLat));
		}
		if(msg.mLng != 0){
			bundle.add("lng", String.valueOf(msg.mLng));
		}

		if(!TextUtils.isEmpty(msg.mAddress)){
			bundle.add("address", msg.mAddress);
		}
		bundle.add("voicetime", String.valueOf(msg.voicetime));
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX +"/Bbs/api/bbsreplyadd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList delBbsReply(String ids) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("ids", ids);
		String url = SERVER_PREFIX +"/Bbs/api/bbsreplydel";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList addBbsProhibition(String uid, String bid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		String url = SERVER_PREFIX +"/Bbs/api/bbsprohibitionadd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList bbsProhibitionList(String bid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("bid", bid);
		String url = SERVER_PREFIX +"/Bbs/api/bbsprohibitionlist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList delBbsProhibition(String uid, String bid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		String url = SERVER_PREFIX +"/Bbs/api/bbsprohibitiondel";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsStatus(String id, String uid, String status) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("status", status);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsGetMsg(String uid, String bid, String getmsg) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		bundle.add("getmsg", getmsg);
		String url = SERVER_PREFIX +"/Bbs/api/setGetmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsSpeakStatus(String id, String uid, String speakStatus) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("speakstatus", speakStatus);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsMoney(String id, String uid, String money) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("money", money);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsTitle(String id, String uid, String title) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("title", title);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsContent(String id, String uid, String content) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("content", content);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsVisitors(String id, String uid, String visitors) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("isvisitors", visitors);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList editBbsclosefriendloop(String id, String uid, String closefriendloop) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("id", id);
		bundle.add("uid", uid);
		bundle.add("isclosefriendloop", closefriendloop);
		String url = SERVER_PREFIX +"/Bbs/api/bbsedit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList bbsUserList(String bid, String isallow, int page, String nickname) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("bid", bid);
		bundle.add("isallow", isallow);
		if(nickname!=null && !nickname.equals("")){
			bundle.add("nickname", nickname);
		}
		bundle.add("page", page+"");
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserlist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList addBbsUser(String bid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("bid", bid);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuseradd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList addBbsUserList(String bid, String openids) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("bid", bid);
		bundle.add("openids", openids);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserlistadd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList delBbsUser(String bid, String openid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", openid);
		bundle.add("bid", bid);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserdel";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList updatebbsUser(String bid, String uid, String isallow) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		bundle.add("isallow", isallow);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserupdate";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList updatebbsUserPower(String bid, String uid, String power) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		bundle.add("power", power);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserupdate";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList updatebbsUserdelChat(String bid, String uid, String delchat) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		bundle.add("delchat", delchat);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserupdate";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	public BbsReplyInfoList updatebbsUserdeldynamic(String bid, String uid, String deldynamic) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", uid);
		bundle.add("bid", bid);
		bundle.add("deldynamic", deldynamic);
		String url = SERVER_PREFIX +"/Bbs/api/bbsuserupdate";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new BbsReplyInfoList(reString);
		}
		return null;
	}
	/**
	 * 获取某个房间的用户列表(/api/group/getGroupUserList)
	 * @param groupid			房间ID
	 * @return
	 * @throws IMException
	 */
	public RoomUsrList getRoomUserList(String groupid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("groupid", groupid);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/api/group/getGroupUserList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new RoomUsrList(reString);
		}

		return null;
	}

	/**
	 * ④　删除群(/session/api/delete)
	 * @param sessionid			群组id
	 * @return
	 * @throws IMException
	 */
	public IMJiaState deleteRoom(String sessionid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", String.valueOf(sessionid));
		String url = SERVER_PREFIX + "/session/api/delete";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 退出房间(/session/api/quit)
	 * @param sessionid				房间ID
	 * @return
	 * @throws IMException
	 */
	public IMJiaState exitRoom(String sessionid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("openid", IMCommon.getUserPhone(BMapApiApp.getInstance()));
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/quit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 7.群组聊天
	 * ⑨　修改群资料(/session/api/edit)
	 * uid       true  string 登陆用户id
	 * @param sessionid	true	int	群id
	 * @param name	false	string	群名称
	 *  groupnickname	false	string	群昵称
	 */

	public IMJiaState modifyGroupNickName(String sessionid, String name)
			throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", getUserPhone(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("name", name);
		String url = SERVER_PREFIX + "/session/api/edit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}


	/**
	 * 修改我的群昵称 /session/api/setNickname
	 *  uid true string 登陆用户id
	 *  mynickname	true	string	设置的群昵称
	 * @param sessionid	true	int	群组id
	 */
	public IMJiaState modifyMyNickName(String sessionid, String groupnickname)
			throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", getUserPhone(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("mynickname", groupnickname);
		String url = SERVER_PREFIX + "/session/api/setNickname";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}




	/**
	 * 7.群组聊天
	 * ⑩　设置群类型(/api/group/ispublic)
	 *  uid       true  string 登陆用户id
	 * @param groupid	true	int	群id
	 * @param ispublic	true	int	0-公开群 1-私密群
	 * 
	 */
	public IMJiaState isPublicGroup(String groupid, int ispublic)
			throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("groupid", groupid);
		bundle.add("ispublic", String.valueOf(ispublic));
		String url = SERVER_PREFIX + "/api/group/ispublic";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 7.群组聊天
	 * 11　设置是否接收消息(/session/api/getmsg)
	 * uid       true  string 登陆用户id
	 * @param groupid	true	int	群id
	 * @param isgetmsg	true	int	0-不接收 1-接收
	 * 
	 */
	public IMJiaState isGetGroupMsg(String groupid, int isgetmsg)
			throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", groupid);
		bundle.add("openid", getUserPhone(BMapApiApp.getInstance()));
		bundle.add("isgetmsg", String.valueOf(isgetmsg));
		String url = SERVER_PREFIX + "/session/api/getmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 17.	设置是否接收另一用户的消息(/user/api/setGetmsg)
	 */
	public IMJiaState setMsg(String getmsg, String fopenid)
			throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("fopenid", fopenid);
		bundle.add("getmsg", getmsg);
		String url = SERVER_PREFIX + "/user/api/setGetmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}



	/**
	 * 群聊
	 * 4.	会话详细(/session/api/detail)
	 *   uid       true  string 登陆用户id
	 *  groupid	true	int	群id
	 */

	public Room getRommInfoById(String sessionid)
			throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("openid", IMCommon.getUserPhone(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/session/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new Room(reString);
		}

		return null;
	}






	//六、我
	// 1.获取省市 行业 课程
	/**
	 * ①　省市(/user/apiother/areaList)
	 *  uid
	 * @throws IMException 
	 */
	public CountryList getCityAndContryUser() throws IMException{
		String reString = FeatureFunction.getAssestsFile("AreaCode");
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new CountryList(reString);
		}
		return null;
	}


	/**
	 * 
	 * 6 修改备注名(/user/api/remark )	
	 * @param remark
	 * @return
	 * @throws IMException
	 */
	public IMJiaState remarkFriend(String openid, String remark) throws IMException {
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", openid);
		bundle.add("remark", remark);
		String url = SERVER_PREFIX + "/user/api/remark";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public BranchSearchRests getRedpacketcount(String lbs) throws IMException {
		IMParameters bundle = new IMParameters();
		Login login = IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("lbs", lbs);
		bundle.add("openid", login.kai6Id);
		bundle.add("ypid", login.ypId);
		String url = "https://wxapi.winchat.com.cn/shop_api/hb.shtml";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return BranchSearchRests.getInfo(reString);
		}
		return null;
	}
	public RedpacketDetails getOpenRedpacket(String sid) throws IMException {
		IMParameters bundle = new IMParameters();
		Login login = IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("sid", sid);
		bundle.add("openid", login.kai6Id);
		String url = "https://wxapi.winchat.com.cn/shop_api/hb_more.shtml";
//		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		String reString = GetHttps(url+"?"+"sid="+sid+"&openid="+login.kai6Id);
		Log.e("getOpenRedpacket","reString="+reString);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return RedpacketDetails.getInfo(reString);
		}
		return null;
	}
    public boolean sendRedpacket(String money, String num, String intro1, String intro2,
                                 String intro3, String address) throws IMException {
        IMParameters bundle = new IMParameters();
        Login login = IMCommon.getLoginResult(BMapApiApp.getInstance());
        bundle.add("money", money);
        bundle.add("num", num);
        bundle.add("intro1", intro1);
        bundle.add("intro2", intro2);
        bundle.add("intro3", intro3);
        bundle.add("address", address);
        bundle.add("openid", login.kai6Id);
		bundle.add("id", "585");
        String url = "https://wxapi.winchat.com.cn/shop_api/hb_save.shtml";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
//		String reString = GetHttps(url+"&"+"money="+money+"&num="+num+"&intro1="+intro1+"&intro2="
//				+intro2+"&intro3="+intro3+"&address="+address+"&openid="+login.kai6Id);
//		Log.e("sendRedpacket","reString="+reString);
        if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
            return true;
        }
        return false;
    }
	public GetRedpacketMoney getRedpacketMoney(String sid, String value) throws IMException {
		IMParameters bundle = new IMParameters();
		Login login = IMCommon.getLoginResult(BMapApiApp.getInstance());
		bundle.add("sid", sid);
		bundle.add("openid", login.kai6Id);
		bundle.add("intro2", value);
		String url = "https://wxapi.winchat.com.cn/shop_api/qhb_save.shtml";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
//		String reString = GetHttps(url+"?"+"sid="+sid+"&openid="+login.kai6Id);
		Log.e("getOpenRedpacket","reString="+reString);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return GetRedpacketMoney.getInfo(reString);
		}
		return null;
	}
	private String GetHttps(String string){
		try{
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
			HttpsURLConnection conn = (HttpsURLConnection)new URL(string).openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line);
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(this.getClass().getName(), e.getMessage());
		}
		return null;
	}
	private String PostHttps(String string, String Str){
		try{
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
			HttpsURLConnection conn = (HttpsURLConnection)new URL(string).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length",
					String.valueOf(Str.getBytes("gbk").length));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.getOutputStream().write(Str.getBytes("gbk"));
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line);
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(this.getClass().getName(), e.getMessage());
		}
		return null;
	}
	private class MyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}
	}
	private class MyTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
		}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			// TODO Auto-generated method stub
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	//5. 朋友圈

	/**
	 * 设置用户封面图 post方式请求
	 * /friend/api/setCover
	 * uid	true	string	当前登陆用户ID
	 * action	true	frontCover
	 *   f_upload	true		上传图片
	 *  QiyueException
	 */ 
	public IMJiaState uploadUserBg(String userID, List<MorePicture> listpic) throws IMException{
		IMJiaState status = null;
		IMParameters bundle = new IMParameters();
		if(listpic!=null && listpic.size()>0){
			bundle.addPicture("pic", listpic);
		}
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/setCover";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject jsonObj = new JSONObject(reString);
				if (jsonObj!=null && !jsonObj.equals("") && !jsonObj.equals("null")) {
					status = new IMJiaState(jsonObj);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//return null;
			}
		}
		return status;
	}

	/**
	 *  1.发布分享(/friend/api/add)	
	 *   uid  true 登陆用户id
	 *  @param  picList 上传图片	false	string	最多上传6张，命名picture1 picture2.....
	 *  @param  content	true	string	分享文字内容
	 *  @param  lng	false	string	经度
	 *  @param  lat	false	string	纬度
	 *  @param  address	false	string	经纬度所在的地址
	 *  @param  visible	false	string	不传表示是公开的，传入格式：id1,id2,id3

	 */

	public IMJiaState addShare(List<MorePicture> picList, String content,
                               String lng, String lat, String address, String visible, String type,
                               String bid, String isshow, String fenxiangurl, String urlTitle,
                               String urlImage, String videotime, String shopurl) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if((picList == null || picList.size()<=0)
				&& (content == null || content.equals(""))){
			return null;
		}
		if (fenxiangurl != null  && !fenxiangurl.equals("")) {
			bundle.add("url",fenxiangurl);
			bundle.add("title",urlTitle);
			if (urlImage != null  && !urlImage.equals("")) {
				bundle.add("imageurl",urlImage);
			}
		}
		if(picList!=null && picList.size()>0){
			bundle.addPicture("pic", picList);
		}

		if(content !=null && !content.equals("")){
			bundle.add("content", content);
		}

		if(lng!=null && !lng.equals("")){
			bundle.add("lng",lng);
		}

		if(lat != null && !lat.equals("")){
			bundle.add("lat",lat);
		}

		if(address!=null && !address.equals("")){
			bundle.add("address",address);
		}
		if(type!=null && !type.equals("")){
			bundle.add("type",type);
		}
		if(visible!=null && !visible.equals("")
				&& !visible.startsWith(",")){
			bundle.add("visible",visible);
		}
		if(bid!=null && !bid.equals("")){
			bundle.add("bid",bid);
		}
		if(isshow!=null && !isshow.equals("")){
			bundle.add("isshow",isshow);
		}
		if(videotime!=null && !videotime.equals("")){
			bundle.add("videotime", videotime);
		}
		if(shopurl!=null && !shopurl.equals("")){
			bundle.add("shopurl", shopurl);
		}
		bundle.add("userdj", IMCommon.getLoginResult(BMapApiApp.getInstance()).userDj);
		String url = SERVER_PREFIX + "/friend/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 2. 删除分享(/friend/api/delete)	
	 *  uid string true 登陆用户id
	 * @param fsid int true 分享id
	 */ 

	public IMJiaState deleteShare(int fsid,String deldynamic) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(fsid == 0){
			return null;
		}

		bundle.add("fsid", String.valueOf(fsid));
		bundle.add("deldynamic",deldynamic);
		String url = SERVER_PREFIX + "/friend/api/delete";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {////
				return new IMJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	/**
	 * 3.分享详细(/friend/api/detail)
	 *  uid string true 登陆用户id
	 * @param fsid int true 分享id
	 */ 

	public FriendsLoopItem shareDetail(int fsid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(fsid == 0){
			return null;
		}

		bundle.add("fsid", String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new FriendsLoopItem(reString);

			} catch (Exception e) {
				e.printStackTrace();
				return  null;
			}
		}
		return  null;
	}

	/**
	 * 4. 朋友圈列表(/friend/api/shareList)
	 *  uid true 登录用户id
	 * @param page int 请求的页数
	 */
	public FriendsLoop shareList(int page, String type, String bid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		if(page!=0){
			bundle.add("page", String.valueOf(page));
		}
		if(type!=null && !type.equals("")){
			bundle.add("type",type);
		}
		if(bid!=null && !bid.equals("")){
			bundle.add("bid",bid);
		}
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/shareList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new FriendsLoop(reString);
		}
		return null;
	} 


	/**
	 * 5.朋友相册(/friend/api/userAlbum)	
	 * fuid 
	 */
	public FriendsLoop myHomeList(int page,String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		if(fuid!=null && !fuid.equals(getUserId(BMapApiApp.getInstance()))){
			bundle.add("fuid",fuid);
		}
		if(page!=0){
			bundle.add("page", String.valueOf(page));
		}
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/userAlbum";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new FriendsLoop(reString);
		}
		return null;
	} 

	/**
	 * 6.	添加 取消赞(/friend/api/sharePraise)
	 *  uid true 登陆用户id
	 * @param fsid true int 分享id
	 */

	public IMJiaState sharePraise(int fsid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(fsid == 0){
			return null;
		}

		bundle.add("fsid", String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/sharePraise";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7.回复(/friend/api/shareReply)
	 * uid true 登陆用户id
	 * @param fsid true int 分享id
	 *  fuid true int 回复哪个人
	 */
	public IMJiaState shareReply(int fsid, String toUid, String content) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(fsid == 0 ||( toUid == null || toUid.equals(""))){
			return null;
		}

		bundle.add("content", content);
		bundle.add("fsid", String.valueOf(fsid));
		bundle.add("fuid", toUid);

		String url = SERVER_PREFIX + "/friend/api/shareReply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	/**
	 * 8.	删除回复(/friend/api/deleteReply)
	 *  uid true 登陆用户id
	 * @param replyid	true	int	某条回复的id
	 */

	public IMJiaState deleteReply(int replyid,int fsid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(replyid == 0){
			return null;
		}
		bundle.add("fsid", String.valueOf(fsid));
		bundle.add("replyid", String.valueOf(replyid));
		String url = SERVER_PREFIX + "/friend/api/deleteReply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 9.	设置朋友圈权限(/friend/api/setFriendCircleAuth)
	 * uid true 登陆用户id
	 * @param fuid true 要设置的用户id
	 * @param type true int  1-不看他（她）的朋友圈 2-不让他（她）看我的朋友圈
	 */

	public IMJiaState setFriendCircleAuth(int type,String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(type == 0 || (fuid == null || fuid.equals(""))){
			return null;
		}

		bundle.add("type", String.valueOf(type));
		bundle.add("fuid", fuid);

		String url = SERVER_PREFIX + "/friend/api/setFriendCircleAuth";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	//7.设置

	/**
	 * 
	 * 意见反馈 /user/api/feedback
	 * @param content
	 * @return
	 * @throws IMException
	 */
	public IMJiaState feedback(String content) throws IMException {
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		bundle.add("content", content);
		String url = SERVER_PREFIX + "/user/api/feedback";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new IMJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 7.1 修改密码(/user/api/editPassword)
	 * uid true string 登陆用户id
	 * @param oldpassword true string 旧密码
	 * @param newpassword true string 新密码
	 */
	public IMJiaState editPasswd(String oldpassword, String newpassword) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		if((oldpassword == null || oldpassword.equals(""))
				|| (newpassword == null || newpassword.equals(""))){
			return null;
		}
		Login login= IMCommon.getLoginResult(BMapApiApp.getInstance());
		SharedPreferences mPreferences= BMapApiApp.getInstance().getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
		String fxid=mPreferences.getString(IMCommon.USERNAME, "");
		bundle.add("oldpassword", oldpassword);
		bundle.add("newpassword", newpassword);
		bundle.add("id", login.ypId);
		bundle.add("token", login.token);
		bundle.add("username", fxid);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/editPassword";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 *7.2 帮助中心(/user/apiother/help)
	 *返回的是一个html的页面
	 * @throws IMException 
	 */ 
	public String getHelpHtml() throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/apiother/help";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if(reString != null && !reString.equals("") && !reString.equals("null") ){
			return reString;
		}
		return null;
	}
	/**
	 * 根据姓名获取用户详细(/api/user/getUserByName)
	 * uid 登陆用户id
	 * @param name	true	string	用户姓名
	 */
	public LoginResult getUserByName(String name) throws IMException {
		Log.e("getUserByName","name="+name);
		UserList ul =  search_number(name,1);
		if(ul!=null && ul.mUserList.size()!=0){
			return new LoginResult("0",0,ul.mUserList.get(0));
		}else{
			return null;
		}

	}


	/**
	 * 设置加好友是否需要验证(/user/api/setVerify)
	 * verify int true 0-不验证 1-验证
	 * @return 
	 * @throws IMException
	 */
	public IMJiaState setVerify(int verify) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/user/api/setVerify";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}	




	/**
	 * 发送消息接口
	 * @param messageInfo
	 * @return
	 *  BridgeException
	 * http://117.78.2.70/index.php/user/api/sendMessage?uid=200000
	 * &content=共和国乖乖&fromurl=http://117.78.2.70/Uploads/Picture/
	 * avatar/200000/s_f9f0399347f63dc71c8880d057403f97.jpg
	 * &voicetime=0&tag=0814a62b-5cf3-432d-a878-3da9c99af257
	 * &fromid=200000&fromname=萌萌哒&typechat=200&toname=小黄鸭,海洋天堂,漩涡鸣人&typefile=1&toid=17
	 */
	public MessageResult sendMessage(MessageInfo messageInfo, boolean isForward) throws IMException{
		IMParameters bundle = new IMParameters();
		if(messageInfo == null){
			return null;
		}
		bundle.add("typechat", String.valueOf(messageInfo.typechat));
		bundle.add("tag", messageInfo.tag);
		if(!TextUtils.isEmpty(messageInfo.fromname)){
			bundle.add("fromname", messageInfo.fromname);
		}
		if(!TextUtils.isEmpty(messageInfo.fromid)){
			bundle.add("fromid", messageInfo.fromid);
		}

		if(!TextUtils.isEmpty(messageInfo.fromurl)){
			bundle.add("fromurl", messageInfo.fromurl);
		}
		bundle.add("toid", messageInfo.toid);
		if(!TextUtils.isEmpty(messageInfo.toname)){
			bundle.add("toname", messageInfo.toname);
		}

		if(!TextUtils.isEmpty(messageInfo.tourl)){
			bundle.add("tourl", messageInfo.tourl);
		}
		bundle.add("typefile", String.valueOf(messageInfo.typefile));

		if(!TextUtils.isEmpty(messageInfo.content)){
			bundle.add("content", messageInfo.content);
		}

		if(messageInfo.typefile == MessageType.PICTURE){

			if(isForward && !TextUtils.isEmpty(messageInfo.imageString)){
				bundle.add("image", messageInfo.imageString);
			}else{
				if(!TextUtils.isEmpty(messageInfo.imgUrlS)){
					List<MorePicture> fileList = new ArrayList<MorePicture>();
					fileList.add(new MorePicture("file_upload", messageInfo.imgUrlS));
					bundle.addPicture("pic", fileList);
				}
			}
			if(messageInfo.imgWidth!=0){
				bundle.add("width", String.valueOf(messageInfo.imgWidth));
			}

			if(messageInfo.imgHeight !=0){
				bundle.add("height", String.valueOf(messageInfo.imgHeight));
			}

		}else if(messageInfo.typefile == MessageType.VOICE){
			if(isForward && !TextUtils.isEmpty(messageInfo.voiceString)){
				bundle.add("voice", messageInfo.voiceString);
			}else  if(!TextUtils.isEmpty(messageInfo.voiceUrl)){
				List<MorePicture> fileList = new ArrayList<MorePicture>();
				fileList.add(new MorePicture("file_upload", messageInfo.voiceUrl));
				bundle.addPicture("pic", fileList);

			}
		}else if(messageInfo.typefile == MessageType.VIDEO){
			final Video video = Video.getInfo(messageInfo.getContent());
			if(video!=null){
				List<MorePicture> fileList = new ArrayList<MorePicture>();
				fileList.add(new MorePicture("file_upload", video.url));
				fileList.add(new MorePicture("file_upload2", Utils.createVideoThumbnailImagePath(video.url)));
				bundle.addPicture("pic", fileList);
				bundle.add("videotime", video.time);
			}
		}
		if(!TextUtils.isEmpty(messageInfo.redpacketTitle)){
			bundle.add("redpackettitle", messageInfo.redpacketTitle);
		}
		if(!TextUtils.isEmpty(messageInfo.redpacketUrl)){
			bundle.add("redpacketurl", messageInfo.redpacketUrl);
		}
		if(messageInfo.mLat != 0){
			bundle.add("lat", String.valueOf(messageInfo.mLat));
		}

		if(messageInfo.mLng != 0){
			bundle.add("lng", String.valueOf(messageInfo.mLng));
		}

		if(!TextUtils.isEmpty(messageInfo.mAddress)){
			bundle.add("address", messageInfo.mAddress);
		}
		if(!TextUtils.isEmpty(messageInfo.bid)){
			bundle.add("bid", messageInfo.bid);
		}
		bundle.add("voicetime", String.valueOf(messageInfo.voicetime));
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/sendMessage";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new MessageResult(reString);
		}

		return null;
	}


	//会议
	/**
	 * 1.创建会议(/meeting/api/add)
	 *  uid  		true 	String 登陆用户id
	 * @param picture	false	string	上传logo图片
	 * @param name		true	string	会议标题
	 * @param content	true	string	会议主题
	 * @param start		true	int	开始时间戳
	 * @param end		true	int	结束时间戳
	 * @throws IMException 
	 */
	public IMJiaState createMetting(String picture, String name, String content,
                                    long start, long end) throws IMException{
		IMParameters bundle = new IMParameters();
		if(picture!=null && !picture.equals("")){
			List<MorePicture> listPic = new ArrayList<MorePicture>();
			listPic.add(new MorePicture("picture",picture));
			bundle.addPicture("pic", listPic);
		}
		if((name == null || name.equals("")) || (content == null || content.equals(""))
				|| start == 0 || end == 0){
			return null;	
		}
		bundle.add("name", name);
		bundle.add("content", content);
		bundle.add("start", String.valueOf(start));
		bundle.add("end", String.valueOf(end));

		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}


		return null;
	}


	/**
	 * 2.会议详细(/meeting/api/detail)
	 * @param meetingid	true	string	会议id
	 * @throws IMException 
	 */ 
	public MeetingItem mettingDetail(int meetingid) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(meetingid == 0){
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		String url = SERVER_PREFIX  + "/meeting/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new MeetingItem(reString);
		}
		return null;
	}


	/**
	 * 3. 会议列表(/meeting/api/meetingList)
	 *  uid true String 登陆用户id
	 * @param  type	true	string	type 1-正在进行中 2-往期 3-我的
	 * @param page int 
	 * @throws IMException 
	 */
	public Meeting meetingList(int type,int page) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(type == 0){
			return null;
		}
		bundle.add("type", String.valueOf(type));
		bundle.add("page", String.valueOf(page));
		String url = SERVER_PREFIX  + "/meeting/api/meetingList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") ){
			return new Meeting(reString);

		}
		return null;
	}


	/**
	 * 4. 申请加入会议(/meeting/api/apply)
	 *  uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 * @throws IMException 
	 */
	public IMJiaState applyMeeting(int meetingid,String reasion) throws IMException{
		IMParameters bundle = new IMParameters();
		if( meetingid == 0 || (reasion == null || reasion.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("content", reasion);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/apply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	/**
	 * 5. 同意申请加入会议(/meeting/api/agreeApply)	33
	 *  uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 * @param fuid	true	int	申请用户id
	 */
	public IMJiaState agreeApplyMeeting(int meetingid,String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		if( meetingid == 0 || (fuid == null || fuid.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/agreeApply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}



	/**
	 * 6. 不同意申请加入会议(/meeting/api/disagreeApply)	34
	 *  uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 * @paramfuid	true	int	申请用户id
	 */

	public IMJiaState disagreeApplyMeeting(int meetingid,String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		if( meetingid == 0 || (fuid == null || fuid.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/disagreeApply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7. 邀请加入会议(/meeting/api/invite)	34
	 *  uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 * @param uids	true	int	被邀请用户id
	 */
	public IMJiaState inviteMeeting(int meetingid, String uids) throws IMException{
		IMParameters bundle = new IMParameters();
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		if(meetingid == 0 
				|| (uids == null || uids.equals("")
				|| uids.startsWith(",") || uids.endsWith(","))){
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/meeting/api/invite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}


	/**
	 * 10. 会议的用户申请列表(/meeting/api/meetingApplyList)	36
	 *  uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 * @throws IMException 
	 */
	public UserList meetingApplyList(int page, int meetingid) throws IMException{
		IMParameters bundle = new IMParameters();
		if( meetingid == 0){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("page", String.valueOf(page));
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/meetingApplyList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new UserList(reString,0);
		}
		return null;
	}



	/**
	 * 11. 用户活跃度排行(/meeting/api/huoyue)	37
	 * uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 */
	public UserList huoyueList(int page, int meetingid) throws IMException{
		IMParameters bundle = new IMParameters();
		if( meetingid == 0){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("page", String.valueOf(page));
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/huoyue";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new UserList(reString,0);
		}
		return null;
	}


	/**
	 * 12. 移除用户(/meeting/api/remove)	37
	 *  uid true String 登陆用户id
	 * @param meetingid	true	string	会议id
	 * @param fuid	true	int	要移除的用户
	 */
	public IMJiaState removeMetUser(int meetingid,String fuid) throws IMException{
		IMParameters bundle = new IMParameters();
		if( meetingid == 0 || (fuid == null || fuid.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/remove";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new IMJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public PayReq getPayReq() throws IMException, JSONException {
		String reString = request("http://112.74.73.48:8080/WeixinPay/unifiedOrder.do", null, Utility.HTTPMETHOD_POST,0);
		PayReq request = new PayReq();
		JSONObject json = new JSONObject(reString);
		request.appId = json.getString("appid");
		request.partnerId = json.getString("mch_id");
		request.prepayId= json.getString("prepay_id");
		request.packageValue = "Sign=WXPay";
		request.nonceStr= json.getString("nonce_str");
		request.timeStamp= json.getString("timestamp");
		request.sign= json.getString("sign");
		return request;
	}
	public String geturlShort() throws IMException {
		String openid = IMCommon.getUserPhone(BMapApiApp.getInstance());
		Login l = IMCommon.getLoginResult(BMapApiApp.getInstance());
		String kid=null;
		if (l.ypId==null || l.ypId.equals("")) {
			kid=BMapApiApp.getInstance().getResources().getString(R.string.ypid);
		}else{
			kid=l.ypId;
		}
		String reString = request("http://api.t.sina.com.cn/short_url/shorten.json?source=3271760578&url_long=http://shopxx.wei20.cn/gouwu/wishmb/user_reg.shtml?id="+kid+"%26tid="+l.tid, null, Utility.HTTPMETHOD_POST,0);
		JSONObject json = null;
		try {
			json = new JSONObject(reString);
			reString = json.getString("url_short");
		} catch (JSONException e) {
			e.printStackTrace();
			reString = "http://shopxx.wei20.cn/gouwu/wishmb/user_reg.shtml?id="+kid+"&tid="+l.tid;
		}
		return reString;
	}
	public String getGongGaoMessage(String lbs) throws IMException, JSONException {
		Login l = IMCommon.getLoginResult(BMapApiApp.getInstance());
		if(l == null){
			return null;
		}
		String kid;
		if (l.ypId==null || l.ypId.equals("")) {
			kid=BMapApiApp.getInstance().getResources().getString(R.string.ypid);
		}else{
			kid=l.ypId;
		}
		IMParameters bundle = new IMParameters();
		bundle.add("id", kid);
		bundle.add("token", l.token);
		bundle.add("udj", l.userDj);
		bundle.add("lbs", lbs);
		bundle.add("quid", l.quId);
		bundle.add("ypid", l.ypId);
		String reString = request(SERVER_PREFIX  + "/user/api/getgonggaomessage", bundle, Utility.HTTPMETHOD_POST,0);
		return reString;
	}
	public MenuList getmenu() throws IMException, JSONException {
		Login l = IMCommon.getLoginResult(BMapApiApp.getInstance());
		if(l == null){
			return null;
		}
		String kid;
		if (l.ypId==null || l.ypId.equals("")) {
			kid=BMapApiApp.getInstance().getResources().getString(R.string.ypid);
		}else{
			kid=l.ypId;
		}
		IMParameters bundle = new IMParameters();
		bundle.add("id", kid);
		bundle.add("kai6id", l.kai6Id);
		bundle.add("token", l.token);
		String reString = request(SERVER_PREFIX  + "/user/api/getmenu", bundle, Utility.HTTPMETHOD_POST,0);
		return new MenuList(reString);
	}
	public UserMenuList addusermenu(String phone, String menuname, String menuurl) throws IMException, JSONException {
		IMParameters bundle = new IMParameters();
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", phone);
		bundle.add("menuname", menuname);
		bundle.add("menuurl",menuurl);
		String reString = request(SERVER_PREFIX  + "/user/api/addusermenu", bundle, Utility.HTTPMETHOD_POST,0);
		return new UserMenuList(reString);
	}
	public UserMenuList updateusermenu(String phone, String id, String menuname, String menuurl) throws IMException, JSONException {
		IMParameters bundle = new IMParameters();
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", phone);
		bundle.add("id", id);
		bundle.add("menuname", menuname);
		bundle.add("menuurl",menuurl);
		String reString = request(SERVER_PREFIX  + "/user/api/updateusermenu", bundle, Utility.HTTPMETHOD_POST,0);
		return new UserMenuList(reString);
	}
	public UserMenuList delusermenu(String phone, String id) throws IMException, JSONException {
		IMParameters bundle = new IMParameters();
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", phone);
		bundle.add("id", id);
		String reString = request(SERVER_PREFIX  + "/user/api/delusermenu", bundle, Utility.HTTPMETHOD_POST,0);
		return new UserMenuList(reString);
	}
	public UserMenuList getusermenu(String phone) throws IMException, JSONException {
		IMParameters bundle = new IMParameters();
		bundle.add("uid", IMCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("openid", phone);
		String reString = request(SERVER_PREFIX  + "/user/api/usermenulist", bundle, Utility.HTTPMETHOD_POST,0);
		return new UserMenuList(reString);
	}
}
