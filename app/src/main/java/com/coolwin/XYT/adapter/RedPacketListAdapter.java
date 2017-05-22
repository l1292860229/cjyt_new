package com.coolwin.XYT.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolwin.XYT.Entity.OpenRedpacketUser;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.ImageLoader;

import java.util.List;

/**
 * 聊天页面适配器
 * @author dongli
 *
 */
public class RedPacketListAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<OpenRedpacketUser> mUserList;
	private Context mContext;
	private ImageLoader mImageLoader;

	public RedPacketListAdapter(Context context, List<OpenRedpacketUser> mUserList){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mImageLoader = new ImageLoader();
		this.mUserList = mUserList;
	}
	@Override
	public int getCount() {
		return mUserList.size();
	}

	@Override
	public Object getItem(int position) {
		return mUserList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	public void setData(List<OpenRedpacketUser> data){
		mUserList = data;
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.redpacketlist_item, null);
			holder=new ViewHolder();
			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.username);
			holder.moneyTextView = (TextView) convertView.findViewById(R.id.content);
			holder.timeTextView = (TextView) convertView.findViewById(R.id.releasetime);
			holder.mHeaderView = (ImageView) convertView.findViewById(R.id.header);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}

		final OpenRedpacketUser openRedpacketUser = mUserList.get(position);
		holder.mUserNameTextView.setText(openRedpacketUser.name);
		holder.moneyTextView.setText(openRedpacketUser.qmoney+"元");
		holder.timeTextView.setText(openRedpacketUser.qtime);
		mImageLoader.getBitmap(mContext,holder.mHeaderView,null,openRedpacketUser.qpic,0,false,false);
		return convertView;
	}


	final static class ViewHolder {  
		TextView mUserNameTextView;
		TextView moneyTextView;
		TextView timeTextView;
		ImageView mHeaderView;
		@Override
		public int hashCode() {
			return this.mUserNameTextView.hashCode() +
					moneyTextView.hashCode() + mHeaderView.hashCode();
		}
	} 

}
