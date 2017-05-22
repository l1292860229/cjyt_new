package com.coolwin.XYT.sortlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.ImageLoader;

import java.util.HashMap;
import java.util.List;

public class UserListAdapter extends BaseAdapter {
	private List<BbsReplyInfo> list = null;
	private Context mContext;
	private ImageLoader mImageLoader;
	public UserListAdapter(Context mContext, List<BbsReplyInfo> list) {
		this.mContext = mContext;
		this.list = list;
		this.mImageLoader = new ImageLoader(list,0);
	}
	public void setData( List<BbsReplyInfo> list){
		this.list = list;
	}
	/**
	 * 获取缓存的图片
	 * @return
	 */
	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageLoader.getImageBuffer();
	}
	public void clearBitmapToMemoryCache(){
		mImageLoader.clearBitmapToMemoryCache();
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<BbsReplyInfo> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		final BbsReplyInfo mContent = list.get(position);
		if (convertView == null || ((ViewHolder) convertView.getTag()).mTag != position) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
			holder.mHeadrIcon = (ImageView)convertView.findViewById(R.id.headerimage);
			holder.mNameTextView = (TextView)convertView.findViewById(R.id.username);

			holder.index = (TextView) convertView.findViewById(R.id.sortKey);
			holder.indexLayout = (RelativeLayout)convertView.findViewById(R.id.grouplayout);
			holder.mContentSplite = (ImageView)convertView.findViewById(R.id.content_splite);
			holder.mSignTextView = (TextView)convertView.findViewById(R.id.prompt);
			holder.newFriendsIcon= (TextView)convertView.findViewById(R.id.new_notify);
			holder.mcompanyTextView= (TextView)convertView.findViewById(R.id.company);
			holder.mjobTextView= (TextView)convertView.findViewById(R.id.job);
			holder.mPower = (ImageView)convertView.findViewById(R.id.power);
			holder.mTag = position;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

			holder.indexLayout.setVisibility(View.GONE);
			holder.index.setVisibility(View.VISIBLE);
			holder.mContentSplite.setVisibility(View.GONE);

		String name = this.list.get(position).nickname;
		if(name == null || name.equals("")){
			name = this.list.get(position).nickname;
		}
		if(mContent.job != null && !mContent.job.equals("")){
			holder.mjobTextView.setVisibility(View.VISIBLE);
			holder.mjobTextView.setText(mContent.job);
		}
		if(mContent.company != null && !mContent.company.equals("")){
			holder.mcompanyTextView.setVisibility(View.VISIBLE);
			holder.mcompanyTextView.setText(mContent.company);
		}
		holder.mNameTextView.setText(name);
		if(this.list.get(position).headsmall!=null && !this.list.get(position).headsmall.equals("")){
			holder.mHeadrIcon.setTag(this.list.get(position).headsmall);
			mImageLoader.getBitmap(holder.mHeadrIcon,null,this.list.get(position).headsmall,0, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.contact_default_header));
		}else{
			holder.mHeadrIcon.setImageResource(R.drawable.contact_default_header);
		}
		if(this.list.get(position).power==2){
			holder.mPower.setVisibility(View.VISIBLE);
			holder.mPower.setImageResource(R.drawable.quanzhu);
		}else if (this.list.get(position).power==1) {
			holder.mPower.setVisibility(View.VISIBLE);
			holder.mPower.setImageResource(R.drawable.guanli);
		}
		//}
		return convertView;

	}



	final static class ViewHolder {
		public int mTag;
		public ImageView mHeadrIcon,mContentSplite,mPower;
		public TextView mNameTextView,mSignTextView,mcompanyTextView,mjobTextView;

		public TextView index;
		public RelativeLayout indexLayout,contactLayout;
		public TextView newFriendsIcon;
	}
}