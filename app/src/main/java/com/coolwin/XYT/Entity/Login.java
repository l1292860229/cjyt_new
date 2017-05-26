package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Login implements Serializable {

	private static final long serialVersionUID = 14634155464564L;

	/*	
	 * 
	 {
  "data": {
    "uid": "200601",
    "phone": "13689084790",
    "password": "855180",
    "nickname": "",
    "headsmall": "",
    "headlarge": "",
    "gender": "2",
    "sign": "",
    "province": "",
    "city": "",
    "isfriend": 0,
    "createtime": "1407311351"
  },
  "state": {
    "code": 0,
    "msg": "",
    "debugMsg": "",
    "url": "User\/Api\/regist"
  }
}
	 */
	public String uid; //UID 用户的唯一ID
	public int nameType; //0-普通用户 1-操作栏 2-星标朋友
	public int userType; // 0-普通用户 1-黑名单用户 2-星标朋友
	public String sort;//排序字段
	public String sortName;//排序字段名称
	public String phone;//用户的手机号码
	public String openfirePwd;//operfire密码
	public String nickname;//用户昵称
	public String token;//用户身份标识

	public String headsmall;//小头像
	public String headlarge;//大头像
	public int gender;//性别 0-男 1-女 2-未填写
	public String url;
	public String kai6Id;
	public String linkname="";
	public String quId;
	public String userDj;
	public String ypId;
	public String tid;
	/**
	 * 0-看 1-不看 当前用户是否看另个用户的朋友圈
	 */
	public int fauth1 ; 
	/**
	 * 0-看 1-不看 当前用户不让另个用户查看我的朋友圈
	 */
	public int fauth2; 
	public int isGetMsg;//0-不接收 1-接收
	public String sign; //个性签名
	public int newFriends; //1-有新的朋友 0-无新的朋友
	/**
	 * 省id
	 */
	public String provinceid;//省id
	/**
	 * 市id
	 */
	public String cityid;//市id
	public int isfriend;
	public long createtime;//用户创建时间

	public String password = "";//本地用户密码
	
	public String content;//申请理由
	public String companywebsite;//公司主页
	public String industry;//行业
	public String company;//所在地址
	public String companyaddress;//公司地址
	public String job;//职位
	public String provide ;//提供
	public String demand  ;//需求
	public String telephone  ;//需求

	public String name;//用户的姓名
	public String userPic;

	public ArrayList<UserMenu> mUserMenu;
	public String mUserMenuStr;
	public String userMenuTime;
	
	public String remark; //备注名
	public String urltitle;//中间按钮提示信息

	public String isattest;

	/*public int isfollow;
	public int followers;
	public int fansers;*/
	public List<Picture> picList;

	/*public int visit;*/
	

	public boolean isShow = false;
	public int groupId = -999;
	public String groupName = "";
	public int isAccount;
	
	//隐私设置
	public boolean isAcceptNew= true;
	public boolean isOpenVoice= true;
	public boolean isOpenShake = true;
	
	/**
	 * 验证朋友请求
	 */
	public boolean isValidFriendAppley = true;
	
	/**
	 * 回复即添加对方为朋友
	 */
	public boolean isReplyAndFriend = true;
	/**
	 * 向我推荐通讯录朋友
	 */
	public boolean isTuiJianContact=true;

	//搜索用户时用到的字段
	public int mIsRoom;
	public int isOwner;
	public List<String> headUrlList;
	
	public String cover;
	
	public Room room;
	
	
	/*new Login(String.valueOf(contactId),number,
			String.valueOf(photoId),name,index,"",0,*/
	
	
	
	
	public Login(){}
	public Login(String uid, String sort, String phone, String headsmall,
                 String nickname) {
		super();
		this.uid = uid;
		this.sort = sort;
		this.phone = phone;
		this.headsmall = headsmall;
		this.nickname = nickname;
	}
	public Login(String uid, String sort, String phone, String headsmall,
                 String nickname, String provinceid) {
		super();
		this.uid = uid;
		this.sort = sort;
		this.phone = phone;
		this.headsmall = headsmall;
		this.nickname = nickname;
		this.provinceid = provinceid;
	}
	public Login(String sort, String headsmall,
                 String nickname, String remarkName, int nameType) {
		super();
		this.sort = sort;
		this.headsmall = headsmall;
		this.nickname = nickname;
		this.remark = remarkName;
		this.nameType = nameType;
	}
	public Login(String sort, String headsmall,
                 String nickname, String remarkName, int nameType, int newFriends) {
		super();
		this.sort = sort;
		this.headsmall = headsmall;
		this.nickname = nickname;
		this.remark = remarkName;
		this.nameType = nameType;
		this.newFriends = newFriends;
	}
	
	//newFriends

	public Login(String uid, String name, int mIsRoom, int isOwner, List<String> headUrlList) {
		super();
		this.uid = uid;
		this.name = name;
		this.mIsRoom = mIsRoom;
		this.isOwner = isOwner;
		this.headUrlList = headUrlList;
	}
	
	
	public Login(String uid, String name, int mIsRoom, int isOwner, String headSmallImag) {
		super();
		this.uid = uid;
		this.nickname = name;
		this.mIsRoom = mIsRoom;
		this.isOwner = isOwner;
		this.headsmall = headSmallImag;
	}
	
	public Login(Room room, String nickName, String headSmall, int isRoom) {
		super();
		this.room = room;
		this.nickname = nickName;
		this.headsmall = headSmall;
		this.mIsRoom = isRoom;
	}
	
	

	public Login(JSONObject json) {
		try {
			if(!json.isNull("uid")){
				uid = json.getString("uid");
			}
			if(!json.isNull("sort")){
				sort = json.getString("sort");
				if(sort.matches("[A-Z]") || sort.matches("[a-z]")){
					sort=sort.toUpperCase();
				}else{
					sort = "#";
				}
			}

			if(!json.isNull("phone")){
				phone = json.getString("phone");
			}
			if(!json.isNull("content")){
				content = json.getString("content");
				sign = content;
			}
			
			if(!json.isNull("password")){
				openfirePwd = json.getString("password");
			}
			if(!json.isNull("nickname")){
				nickname = json.getString("nickname");
			}
			if(!json.isNull("token")){
				token = json.getString("token");
			}
			if(!json.isNull("url")){
				url = json.getString("url");
			}
			if(!json.isNull("urltitle")){
				urltitle = json.getString("urltitle");
			}
			if(!json.isNull("ka6id")){
				kai6Id = json.getString("ka6id");
			}
			if(!json.isNull("quid")){
				quId = json.getString("quid");
			}
			if(!json.isNull("userdj")){
				userDj = json.getString("userdj");
			}
			if(!json.isNull("ypid")){
				ypId = json.getString("ypid");
			}
			if(!json.isNull("tid")){
				tid = json.getString("tid");
			}
			if(!json.isNull("headsmall")){
				headsmall = json.getString("headsmall");
			}

			if(!json.isNull("headlarge")){
				headlarge = json.getString("headlarge");
			}
			
			if(!json.isNull("gender")){
				gender = json.getInt("gender");
			}
			
			if(!json.isNull("sign")){
				sign = json.getString("sign");
			}
			/*if(sign == null || sign.equals("")){
				sign = "还未签名  签一个";
			}*/

			
			if(!json.isNull("province")){
				provinceid = json.getString("province");
			}

			if(!json.isNull("city")){
				cityid = json.getString("city");
			}
			if(!json.isNull("isfriend")){
				isfriend = json.getInt("isfriend");
			}
			
			if(!json.isNull("isblack")){
				userType = json.getInt("isblack");
			}
			
			if(!json.isNull("getmsg")){
				isGetMsg = json.getInt("getmsg");
			}
			
			if(!json.isNull("createtime")){
				createtime = json.getLong("createtime");
			}
		
			if(!json.isNull("cover")){
				this.cover = json.getString("cover");
			}

			
			//以下内容暂不需要
			if(!json.isNull("name")){
				name = json.getString("name");
			}
			if(!json.isNull("companywebsite")){
				companywebsite = json.getString("companywebsite");
			}
			if(!json.isNull("industry")){
				industry = json.getString("industry");
			}
			if(!json.isNull("company")){
				company = json.getString("company");
			}
			if(!json.isNull("companyaddress")){
				companyaddress = json.getString("companyaddress");
			}
			if(!json.isNull("job")){
				job = json.getString("job");
			}
			if(!json.isNull("provide")){
				provide = json.getString("provide");
			}
			if(!json.isNull("demand")){
				demand = json.getString("demand");
			}
			if(!json.isNull("telephone")){
				telephone = json.getString("telephone");
			}
			if(!json.isNull("isattest")){
				isattest = json.getString("isattest");
			}
			picList = new ArrayList<Picture>();
			if(!json.isNull("picture1")){
				String picUrl = json.getString("picture1");
				if(picUrl!=null && !picUrl.equals("")){
					picList.add(new Picture(picUrl,""));
				}
				
			}
			if(!json.isNull("picture2")){
				String picUrl = json.getString("picture2");
				if(picUrl!=null && !picUrl.equals("")){
					picList.add(new Picture(picUrl,""));
				}
				
			}
			if(!json.isNull("picture3")){
				String picUrl = json.getString("picture3");
				if(picUrl!=null && !picUrl.equals("")){
					picList.add(new Picture(picUrl,""));
				}
			}
			
			
			if(!json.isNull("picture")){
				picList = new ArrayList<Picture>();
				JSONArray jsonArray = json.getJSONArray("picture");
				if(jsonArray.length()>0){
					userPic = json.getString("picture");
					for (int i = 0; i < jsonArray.length(); i++) {
						picList.add(Picture.getInfo(jsonArray.getString(i)));
					}
				}
			}
			if(!json.isNull("usermenu")){
				mUserMenu = new ArrayList<UserMenu>();
				mUserMenuStr = json.getString("usermenu");
				if(mUserMenuStr.contains("[")){
					JSONArray jsonArray = json.getJSONArray("usermenu");
					if(jsonArray.length()>0){
						for (int i = 0; i < jsonArray.length(); i++) {
							mUserMenu.add(UserMenu.getInfo(jsonArray.getString(i)));
						}
					}
				}
			}
			if(!json.isNull("isstar")){
				int isStar = json.getInt("isstar");
				if(isStar == 1){
					userType = 2;
				}
			}
			
			if(!json.isNull("verify")){
				this.isValidFriendAppley = json.getInt("verify")==1?true:false;
			}
			
			if(!json.isNull("fauth1")){
				this.fauth1 = json.getInt("fauth1");
			}
			if(!json.isNull("fauth2")){
				this.fauth2 = json.getInt("fauth2");
			}
			
			
			if(!json.isNull("createtime")){
				createtime = json.getLong("createtime");
			}
		
			
			
			if(!json.isNull("remark")){
				remark = json.getString("remark");
			}
			
			
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void setmUserMenu(String mUserMenuStr){
		if(mUserMenuStr==null||mUserMenuStr.equals("") || !mUserMenuStr.startsWith("[")){
			return;
		}
		if(mUserMenu==null){
			mUserMenu = new ArrayList<UserMenu>();
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(mUserMenuStr);
			if( jsonArray.length()>0){
				for (int i = 0; i < jsonArray.length(); i++) {
					mUserMenu.add(UserMenu.getInfo(jsonArray.getString(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		return "Login{" +
				"uid='" + uid + '\'' +
				", nameType=" + nameType +
				", userType=" + userType +
				", sort='" + sort + '\'' +
				", sortName='" + sortName + '\'' +
				", phone='" + phone + '\'' +
				", openfirePwd='" + openfirePwd + '\'' +
				", nickname='" + nickname + '\'' +
				", token='" + token + '\'' +
				", headsmall='" + headsmall + '\'' +
				", headlarge='" + headlarge + '\'' +
				", gender=" + gender +
				", url='" + url + '\'' +
				", kai6Id='" + kai6Id + '\'' +
				", linkname='" + linkname + '\'' +
				", quId='" + quId + '\'' +
				", userDj='" + userDj + '\'' +
				", ypId='" + ypId + '\'' +
				", tid='" + tid + '\'' +
				", fauth1=" + fauth1 +
				", fauth2=" + fauth2 +
				", isGetMsg=" + isGetMsg +
				", sign='" + sign + '\'' +
				", newFriends=" + newFriends +
				", provinceid='" + provinceid + '\'' +
				", cityid='" + cityid + '\'' +
				", isfriend=" + isfriend +
				", createtime=" + createtime +
				", password='" + password + '\'' +
				", content='" + content + '\'' +
				", companywebsite='" + companywebsite + '\'' +
				", industry='" + industry + '\'' +
				", company='" + company + '\'' +
				", companyaddress='" + companyaddress + '\'' +
				", job='" + job + '\'' +
				", provide='" + provide + '\'' +
				", demand='" + demand + '\'' +
				", telephone='" + telephone + '\'' +
				", name='" + name + '\'' +
				", userPic='" + userPic + '\'' +
				", mUserMenu=" + mUserMenu +
				", remark='" + remark + '\'' +
				", picList=" + picList +
				", isShow=" + isShow +
				", groupId=" + groupId +
				", groupName='" + groupName + '\'' +
				", isAccount=" + isAccount +
				", isAcceptNew=" + isAcceptNew +
				", isOpenVoice=" + isOpenVoice +
				", isOpenShake=" + isOpenShake +
				", isValidFriendAppley=" + isValidFriendAppley +
				", isReplyAndFriend=" + isReplyAndFriend +
				", isTuiJianContact=" + isTuiJianContact +
				", mIsRoom=" + mIsRoom +
				", isOwner=" + isOwner +
				", headUrlList=" + headUrlList +
				", cover='" + cover + '\'' +
				", room=" + room +
				'}';
	}
}
