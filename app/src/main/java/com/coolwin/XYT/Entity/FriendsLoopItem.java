package com.coolwin.XYT.Entity;

import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FriendsLoopItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 259457862436629649L;
	public int id;
	public String uid;
	public String nickname;
	public String headsmall;
	public String content;
	public List<Picture> listpic;
	public String url;
	public String title;
	public String imageurl;
	public String job;
	public String video;
	public String company;
	public double lng;
	public double lat;
	public String address;
	public int replys;
	public int praises;
	public int favorite;
	public long createtime;
	public int ispraise;
    public String shopurl;
	//点赞列表
	public List<CommentUser> replylist;
	//评论列表
	public List<CommentUser> praiselist;
	public int showView;
	public String type;
	public FriendsLoopItem() {
		super();
	}
	
	

	public FriendsLoopItem(long createtime) {
		super();
		this.createtime = createtime;
	}



	public FriendsLoopItem(JSONObject json) {
		super();
		if(json == null ){
			return;
		}

		try {
			id = json.getInt("id");
			uid = json.getString("uid");
			nickname = json.getString("nickname");
			type = json.getString("type");
			headsmall = json.getString("headsmall");
			content = json.getString("content");
			if(!json.isNull("shareurl")){
				String shareurl = json.getString("shareurl");
				if(shareurl!=null && !shareurl.equals("")
						&& shareurl.startsWith("{")){
					JSONObject o = new JSONObject(json.getString("shareurl").replace("\\\"","\""));
					url = o.getString("url");
					title = o.getString("title");
					imageurl= o.getString("imageurl");
				}
			}
			if(!json.isNull("picture")){
				String pictureString = json.getString("picture");
				if(pictureString!=null && !pictureString.equals("")
						&& pictureString.startsWith("[")){
					JSONArray array = json.getJSONArray("picture");
					if(array!=null && array.length()>0){
						listpic = new ArrayList<Picture>();
						for (int i = array.length()-1; i >=0; i--) {
							listpic.add(Picture.getInfo(array.getString(i)));
						}
					}
				}
			}
			lng = json.getDouble("lng");
			lat = json.getDouble("lat");
			job = json.getString("job");
			company = json.getString("company");
			address = json.getString("address");
			praises = json.getInt("praises");
			replys = json.getInt("replys");
			ispraise = json.getInt("ispraise");
			createtime = json.getLong("createtime");
			video = json.getString("video");
            shopurl= json.getString("shopurl");
			if(!json.isNull("replylist")){
				String replyString = json.getString("replylist");
				if(replyString!=null && !replyString.equals("")
						&& replyString.startsWith("[")){
					JSONArray array = json.getJSONArray("replylist");
					if(array!=null && array.length()>0){
						replylist = new ArrayList<CommentUser>();
						for (int i = array.length()-1; i >=0  ; i--) {
							replylist.add(new CommentUser(array.getJSONObject(i)));
						}

					}
				}

			}
			if(!json.isNull("praiselist")){
				String replyString = json.getString("praiselist");
				if(replyString!=null && !replyString.equals("")
						&& replyString.startsWith("[")){
					JSONArray array = json.getJSONArray("praiselist");
					if(array!=null && array.length()>0){
						praiselist = new ArrayList<CommentUser>();
						for (int i = array.length()-1; i >=0; i--) {
							praiselist.add(new CommentUser(array.getJSONObject(i)));
						}

					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public FriendsLoopItem(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}

		try {
			JSONObject parentJson = new JSONObject(reqString);
			if(!parentJson.isNull("data")){
				JSONObject json = parentJson.getJSONObject("data");

				id = json.getInt("id");
				uid = json.getString("uid");
				nickname = json.getString("nickname");
				headsmall = json.getString("headsmall");
				content = json.getString("content");
				type = json.getString("type");
				if(!json.isNull("picture")){
					String pictureString = json.getString("picture");
					if(pictureString!=null && !pictureString.equals("")
							&& pictureString.startsWith("[")){
						JSONArray array = json.getJSONArray("picture");
						if(array!=null && array.length()>0){
							listpic = new ArrayList<Picture>();
							for (int i = 0; i < array.length(); i++) {
								listpic.add(Picture.getInfo(array.getString(i)));
							}
						}
					}
				}
				lng = json.getDouble("lng");
				lat = json.getDouble("lat");
				address = json.getString("address");
				praises = json.getInt("praises");
				replys = json.getInt("replys");
				ispraise = json.getInt("ispraise");
				createtime = json.getLong("createtime");
				if(!json.isNull("data")){
					JSONObject childObj = json.getJSONObject("data");
					if(!childObj.isNull("replylist")){
						String replyString = childObj.getString("replylist");
						if(replyString!=null && !replyString.equals("")
								&& replyString.startsWith("[")){
							JSONArray array = childObj.getJSONArray("replylist");
							if(array!=null && array.length()>0){
								replylist = new ArrayList<CommentUser>();
								for (int i = array.length()-1; i >=0  ; i--) {
									replylist.add(new CommentUser(array.getJSONObject(i)));
								}
								

							}
						}

					}
					if(!childObj.isNull("praiselist")){
						String replyString = childObj.getString("praiselist");
						if(replyString!=null && !replyString.equals("")
								&& replyString.startsWith("[")){
							JSONArray array = childObj.getJSONArray("praiselist");
							if(array!=null && array.length()>0){
								praiselist = new ArrayList<CommentUser>();
								for (int i = array.length()-1; i >=0  ; i--) {
									praiselist.add(new CommentUser(array.getJSONObject(i)));
								}

							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "FriendsLoopItem{" +
				"id=" + id +
				", uid='" + uid + '\'' +
				", nickname='" + nickname + '\'' +
				", headsmall='" + headsmall + '\'' +
				", content='" + content + '\'' +
				", listpic=" + listpic +
				", url='" + url + '\'' +
				", title='" + title + '\'' +
				", imageurl='" + imageurl + '\'' +
				", job='" + job + '\'' +
				", company='" + company + '\'' +
				", lng=" + lng +
				", lat=" + lat +
				", address='" + address + '\'' +
				", replys=" + replys +
				", praises=" + praises +
				", favorite=" + favorite +
				", createtime=" + createtime +
				", ispraise=" + ispraise +
				", replylist=" + replylist +
				", praiselist=" + praiselist +
				", showView=" + showView +
				", type='" + type + '\'' +
				'}';
	}
}
