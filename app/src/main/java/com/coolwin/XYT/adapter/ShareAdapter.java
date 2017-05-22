package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.ChatMainActivity;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.ShareUrl;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.net.IMException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShareAdapter<T> extends BaseAdapter {
	List<T> tempList;
	private Context mContext;
	private ImageLoader mImageLoader;
	private String mUrl,urltitle,ImagePath;
	public ShareAdapter(Context mContext, List<T> tempList, String mUrl, String urltitle, String ImagePath) {
		this.mContext = mContext;
		this.tempList = tempList;
		this.mImageLoader = new ImageLoader();
		this.mUrl = mUrl;
		this.urltitle = urltitle;
		this.ImagePath = ImagePath;
	}
	/**
	 * 获取缓存的图片
	 * @return
	 */
	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageLoader.getImageBuffer();
	}


	public int getCount() {
		return this.tempList.size();
	}

	public Object getItem(int position) {
		return tempList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null || ((ViewHolder) convertView.getTag()).mTag != position) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
			holder.mHeaderView = (ImageView)convertView.findViewById(R.id.headerimage);
			holder.mGroupHeaderLayout = (LinearLayout) convertView.findViewById(R.id.group_header);
			holder.mNameTextView = (TextView)convertView.findViewById(R.id.username);
			holder.groupLayout = (RelativeLayout) convertView.findViewById(R.id.grouplayout);
			holder.userDetailLayout= (RelativeLayout) convertView.findViewById(R.id.user_detail_layout);
			holder.mTag = position;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		T t = tempList.get(position);
		String nickname="";
		String headsmall="";
		String[] headUrlArray=new String[0];
		if (t instanceof Session) {
			final Session session = ((Session)t);
			nickname = session.name;
			if(session.heading!=null && !session.heading.equals("")){
				headUrlArray = session.heading.split(",");
			}else{
				headUrlArray = new String[]{IMCommon.getLoginResult(mContext).headsmall};
			}
			holder.userDetailLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (session.type==100 && session.bid!=null
							&& !session.bid.equals("0")
							&& !session.bid.equals("")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									BbsList bl= IMCommon.getIMInfo().getBbs(session.bid);
									if(bl.mBbsList.size()!=0){
										Intent intent = new Intent(mContext, BbsChatMainActivity.class);
										intent.putExtra("data", bl.mBbsList.get(0));
										intent.putExtra("shareUrlMsg",createUrlBbsReplyInfo( bl.mBbsList.get(0).id,new ShareUrl(mUrl,urltitle,ImagePath)));
										mContext.startActivity(intent);
									}
								} catch (IMException e) {
									e.printStackTrace();
								} catch(Exception e){
								}
							}
						}).start();
					}else{
						Login user = new Login();
						user.phone =session.getFromId();
						user.nickname = session.name;
						user.headsmall = session.heading;
						user.mIsRoom = session.type;
						Intent intent = new Intent(mContext, ChatMainActivity.class);
						intent.putExtra("data", user);
						intent.putExtra("shareUrlMsg",createUrlMsg(session.type,session.getFromId(),session.name,session.heading,new ShareUrl(mUrl,urltitle,ImagePath)));
						mContext.startActivity(intent);
					}
				}
			});
		}else if(t instanceof Bbs){
			final Bbs bbs = ((Bbs)t);
			nickname = bbs.title;
			headsmall = bbs.headsmall;
			holder.userDetailLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						Intent intent = new Intent(mContext, BbsChatMainActivity.class);
						intent.putExtra("data", bbs);
						intent.putExtra("shareUrlMsg",createUrlBbsReplyInfo(bbs.id,new ShareUrl(mUrl,urltitle,ImagePath)));
						mContext.startActivity(intent);
				}
			});
		}else if(t instanceof Room){
			final Room room = (Room)t;
			nickname = room.groupName;
			ArrayList<String> headsmallList = new ArrayList<String>();
			for (Login login : room.mUserList) {
				headsmallList.add(login.headsmall);
			}
			headUrlArray= new String[headsmallList.size()];
			headsmallList.toArray(headUrlArray);
			StringBuffer sb = new StringBuffer();
			for (String s : headsmallList) {
				sb.append(s).append(",");
			}
			final String headsmal = sb.deleteCharAt(sb.length()-1).toString();
			holder.userDetailLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Login user = new Login();
					user.phone =room.groupId;
					user.nickname = room.groupName;
					user.headsmall = headsmal;
					user.mIsRoom = 300;
					Intent intent = new Intent(mContext, ChatMainActivity.class);
					intent.putExtra("data", user);
					intent.putExtra("shareUrlMsg",createUrlMsg(300,room.groupId, room.groupName,headsmal,new ShareUrl(mUrl,urltitle,ImagePath)));
					mContext.startActivity(intent);
				}
			});
		}
		holder.groupLayout.setVisibility(View.GONE);
		holder.mNameTextView.setText(nickname);
		if(headsmall!=null && !headsmall.equals("")){
			holder.mHeaderView.setTag(headsmall);
			mImageLoader.getBitmap(holder.mHeaderView,null,headsmall,0, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.contact_default_header));
		}else{
			holder.mHeaderView.setImageResource(R.drawable.contact_default_header);
		}
		List<String> headUrlList = new ArrayList<String>();
		if(headUrlArray != null && headUrlArray.length!= 0){
			int count = 4;
			if(headUrlArray.length < 4){
				count = headUrlArray.length;
			}
			if(holder.mGroupHeaderLayout.getChildCount() != 0){
				holder.mGroupHeaderLayout.removeAllViews();
			}
			if(count == 1){
				holder.mGroupHeaderLayout.setVisibility(View.GONE);
				holder.mHeaderView.setVisibility(View.VISIBLE);
				mImageLoader.getBitmap(mContext, holder.mHeaderView, null, headUrlArray[0], 0, false, true);
				headUrlList.add(headUrlArray[0]);
			}else {
				holder.mGroupHeaderLayout.setVisibility(View.VISIBLE);
				holder.mHeaderView.setVisibility(View.GONE);

				boolean single = count % 2 == 0 ? false : true;
				int row = !single ? count / 2 : count / 2 + 1;
				for (int i = 0; i < row; i++) {
					LinearLayout outLayout = new LinearLayout(mContext);
					outLayout.setOrientation(LinearLayout.HORIZONTAL);
					int width = FeatureFunction.dip2px(mContext, 23);
					outLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, width));
					int padding = FeatureFunction.dip2px(mContext, 1);
					if(single && i == 0){
						LinearLayout layout = new LinearLayout(mContext);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
						layout.setPadding(padding, padding, padding, padding);
						layout.setLayoutParams(params);
						ImageView imageView = new ImageView(mContext);
						imageView.setImageResource(R.drawable.contact_default_header);
						mImageLoader.getBitmap(mContext, imageView, null, headUrlArray[0], 0, false, true);
						headUrlList.add(headUrlArray[0]);
						imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
						layout.addView(imageView);
						outLayout.setGravity(Gravity.CENTER_HORIZONTAL);
						outLayout.addView(layout);
					}else {
						for (int j = 0; j < 2; j++) {
							LinearLayout layout = new LinearLayout(mContext);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
							layout.setPadding(padding, padding, padding, padding);
							layout.setLayoutParams(params);
							ImageView imageView = new ImageView(mContext);
							if(single){
								headUrlList.add(headUrlArray[(2 * i + j - 1)]);
								if(headUrlArray[(2 * i + j - 1)] == null || headUrlArray[(2 * i + j - 1)].equals("")){
									imageView.setImageResource(R.drawable.contact_default_header);
								}else{
									mImageLoader.getBitmap(mContext, imageView, null, headUrlArray[(2 * i + j - 1)], 0, false, true);
								}
							}else {
								headUrlList.add(headUrlArray[(2 * i + j)]);
								if(headUrlArray[(2 * i + j)] == null || headUrlArray[(2 * i + j)].equals("")){
									imageView.setImageResource(R.drawable.contact_default_header);
								}else{
									mImageLoader.getBitmap(mContext, imageView, null, headUrlArray[(2 * i + j)], 0, false, true);
								}
							}
							imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
							layout.addView(imageView);
							outLayout.addView(layout);
						}
					}
					holder.mGroupHeaderLayout.addView(outLayout);
				}
			}
		}
		return convertView;

	}
	private MessageInfo createUrlMsg(int typeChat, String toid, String toname, String tourl, ShareUrl shareUrl){
		Login login = IMCommon.getLoginResult(mContext);
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.fromid = IMCommon.getUserPhone(mContext);
		messageInfo.tag = UUID.randomUUID().toString();
		messageInfo.fromname = login.nickname;
		messageInfo.fromurl = login.headsmall;
		messageInfo.toid = toid;
		messageInfo.toname = toname;
		messageInfo.tourl = tourl;
		messageInfo.typefile = MessageType.SHAREURL;
		messageInfo.content =ShareUrl.getInfo(shareUrl);
		messageInfo.time = System.currentTimeMillis();
		messageInfo.readState = 1;
		messageInfo.typechat = typeChat;
		return messageInfo;
	}
	private BbsReplyInfo createUrlBbsReplyInfo(String bid, ShareUrl shareUrl){
		Login login = IMCommon.getLoginResult(mContext);
		BbsReplyInfo messageInfo = new BbsReplyInfo();
		messageInfo.bid = bid;
		messageInfo.uid = IMCommon.getUserId(mContext);
		messageInfo.nickname = login.nickname;
		messageInfo.headsmall = login.headsmall;
		messageInfo.typefile = MessageType.SHAREURL;
		messageInfo.content =ShareUrl.getInfo(shareUrl);
		messageInfo.time = System.currentTimeMillis();
		return messageInfo;
	}
	final static class ViewHolder {
		public int mTag;
		public TextView mNameTextView;
		public RelativeLayout groupLayout;
		ImageView mHeaderView;
		LinearLayout mGroupHeaderLayout;
		RelativeLayout userDetailLayout;
	}
}