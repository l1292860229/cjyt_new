package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coolwin.XYT.CaptureActivity;
import com.coolwin.XYT.Entity.Menu;
import com.coolwin.XYT.FriensLoopActivity;
import com.coolwin.XYT.MyBbsListActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.XYT.global.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 我的群组数据适配器
 * @author dongli
 *
 */
public class MenuAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	HashMap<Integer, View> hashMap;
	private List<Menu> mData;
	private Context mContext;
	private ImageLoader mImageLoader;
	private String username;
	private String password;
	public MenuAdapter(Context context, List<Menu> data, String username, String password){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		if(data==null){
			data = new ArrayList<Menu>();
		}
		mData = data;
		hashMap= new HashMap<Integer, View>();
		mImageLoader = new ImageLoader();
		this.username = username;
		this.password = password;
	}
	public void setData(List<Menu> data){
		mData = data;
	}
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = hashMap.get(position);
		ViewHolder holder;

		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.menu_list_item, null);
			holder=new ViewHolder();
			holder.layoutGroup = (RelativeLayout) convertView.findViewById(R.id.grouplayout);
			holder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
			holder.layoutGroupBottom = (RelativeLayout) convertView.findViewById(R.id.grouplayoutbottom);
			holder.titleView = (ImageView) convertView.findViewById(R.id.image_url);
			holder.mTitleTextView = (TextView) convertView.findViewById(R.id.titlecontent);
			holder.countTextView= (TextView) convertView.findViewById(R.id.friends_message_count);
			convertView.setTag(holder);
			hashMap.put(position, convertView);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		final Menu menu = mData.get(position);
		String title = menu.getTitle();
		holder.mTitleTextView.setText(menu.getTitle());
		if(title.equals("小鱼圈")){
			holder.layoutGroup.setVisibility(View.VISIBLE);
			holder.titleView.setImageResource(R.drawable.find_friends);
			holder.layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, FriensLoopActivity.class);
					mContext.startActivity(intent);
				}
			});
			if(menu.getCount()!=0){
				holder.countTextView.setVisibility(View.VISIBLE);
				holder.countTextView.setText(menu.getCount()+"");
			}else{
				holder.countTextView.setVisibility(View.GONE);
			}
			return convertView;
		}else if(title.equals("找鱼塘")){
			holder.layoutGroup.setVisibility(View.VISIBLE);
			holder.titleView.setImageResource(R.drawable.foundsq);
			holder.layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent hanyeIntent = new Intent();
					hanyeIntent.putExtra("type","1");
					hanyeIntent.setClass(mContext, MyBbsListActivity.class);
					mContext.startActivity(hanyeIntent);
				}
			});
			return convertView;
		}else if(title.equals("扫一扫")){
			holder.layoutGroup.setVisibility(View.VISIBLE);
			holder.titleView.setImageResource(R.drawable.fx_find_erweima);
			holder.layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent scanIntent = new Intent(mContext, CaptureActivity.class);
					mContext.startActivity(scanIntent);
				}
			});
			return convertView;
		}else if(title.equals("酷发现")){
//			holder.layoutGroup.setVisibility(View.VISIBLE);
			holder.titleView.setImageResource(R.drawable.redpacketmap);
			holder.layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent scanIntent = new Intent(mContext, RedpacketMapActivity.class);
//					mContext.startActivity(scanIntent);
				}
			});
			return convertView;
		}else if(title.equals("附近的店")){
			holder.layoutGroup.setVisibility(View.VISIBLE);
			holder.titleView.setImageResource(R.drawable.fujindedian);
		}else if(title.equals("一元夺宝")){
			holder.titleView.setImageResource(R.drawable.yiyuan);
		}else if(title.equals("购物")){
			holder.layoutGroup.setVisibility(View.VISIBLE);
			holder.titleView.setImageResource(R.drawable.gouwuche);
		}else if(title.equals("游戏")){
			holder.titleView.setImageResource(R.drawable.game);
			holder.layoutGroupBottom.setVisibility(View.VISIBLE);
		}else{
			if (menu.getImageurl()!=null && !menu.getImageurl().equals("")) {
				mImageLoader.getBitmap(mContext, holder.titleView, null, menu.getImageurl(), 0, false, true);
			}
		}
		holder.layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
//				if(menu.getUrl().contains("?")){
//					intent.putExtra("url",menu.getUrl()+"&username="+username+"&password="+password);
//				}else{
//					intent.putExtra("url",menu.getUrl()+"?username="+username+"&password="+password);
//				}
				intent.putExtra("url",menu.getUrl());
				intent.setClass(mContext, WebViewActivity.class);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	final static class ViewHolder {
		RelativeLayout layoutGroup;
		RelativeLayout layoutGroupBottom;
		RelativeLayout layout;
		ImageView titleView;
		TextView mTitleTextView,countTextView;
		@Override
		public int hashCode() {
			return this.titleView.hashCode() + mTitleTextView.hashCode()+layout.hashCode()+layoutGroup.hashCode()+layoutGroupBottom.hashCode();
		}
	} 

}
