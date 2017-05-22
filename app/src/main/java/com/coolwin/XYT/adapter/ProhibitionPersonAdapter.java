package com.coolwin.XYT.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * @author dongli
 *
 */
public class ProhibitionPersonAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	HashMap<Integer, View> hashMap;
	private List<BbsReplyInfo> mData;
	private Context mContext;
	private ImageLoader mImageLoader;
	private boolean mIsDelete = false;
	private boolean mIsShowNickName = true;
	private boolean mIsShowAddBtn;

	public ProhibitionPersonAdapter(Context context, List<BbsReplyInfo> data){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
		hashMap= new HashMap<Integer, View>();
		mImageLoader = new ImageLoader();
	}

	@Override
	public int getCount() {
		int columns = mData.size() / 4;
		if(mData.size() % 4 != 0){
			return (columns + 1) * 4;
		}else {
			mData.size();
		}
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		if(position < mData.size()){
			return mData.get(position);
		}else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setIsDelete(boolean isDelete){
		mIsDelete = isDelete;
	}

	public boolean getIsDelete(){
		return mIsDelete;
	}

	public void setIsShowNickName(boolean isShowNickName){
		mIsShowNickName = isShowNickName;
	}

	public boolean getIsShowNickName(){
		return mIsShowNickName;
	}

	public void setIsShowAddBtn(boolean isShowBtn){
		mIsShowAddBtn = isShowBtn;
	}

	//username
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		convertView = hashMap.get(position);
		ViewHolder holder;  

		if (convertView==null) {  
			convertView=mInflater.inflate(R.layout.chat_detail_person_item, null);
			holder=new ViewHolder();  

			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.username);
			holder.mHeaderView = (ImageView) convertView.findViewById(R.id.header);
			holder.mDeleteBtn = (ImageView) convertView.findViewById(R.id.deletebtn);
			convertView.setTag(holder);  
			hashMap.put(position, convertView);
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}

		holder.mDeleteBtn.setVisibility(View.GONE);

		if(position < mData.size()){
			holder.mHeaderView.setVisibility(View.VISIBLE);
			holder.mUserNameTextView.setVisibility(View.VISIBLE);
			if(mIsDelete){
				holder.mDeleteBtn.setVisibility(View.VISIBLE);
			}

			if(mData.get(position) != null){
				if(mData.get(position).headsmall != null && !mData.get(position).headsmall.equals("")){
					mImageLoader.getBitmap(mContext, holder.mHeaderView, null, mData.get(position).headsmall, 0, false, true);
				}else{
					holder.mHeaderView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.contact_default_header));
				}
				if(mIsShowNickName){
					holder.mUserNameTextView.setVisibility(View.VISIBLE);
					String displayName = mData.get(position).nickname;
					holder.mUserNameTextView.setText(displayName);

				}else{
					holder.mUserNameTextView.setVisibility(View.INVISIBLE);
				}

			}
		}else {
			holder.mHeaderView.setVisibility(View.INVISIBLE);
			holder.mUserNameTextView.setVisibility(View.INVISIBLE);
		}


		return convertView;
	}


	final static class ViewHolder {  
		TextView mUserNameTextView;
		ImageView mHeaderView;
		private ImageView mDeleteBtn;

		@Override
		public int hashCode() {
			return this.mUserNameTextView.hashCode() + mHeaderView.hashCode() + mDeleteBtn.hashCode();
		}
	} 

}
