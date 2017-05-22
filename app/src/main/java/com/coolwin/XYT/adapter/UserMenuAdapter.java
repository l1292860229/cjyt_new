package com.coolwin.XYT.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coolwin.XYT.Entity.UserMenu;
import com.coolwin.XYT.R;

import java.util.List;


public class UserMenuAdapter extends BaseAdapter {
	private List<UserMenu> usermenuList;
	private LayoutInflater mInflater;
	private Context mContext;

	public UserMenuAdapter(Context context, List<UserMenu> usermenuList) {
		super();
		this.usermenuList = usermenuList;
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}
	public void setData(List<UserMenu> usermenuList){
		this.usermenuList = usermenuList;
	}
	@Override
	public int getCount() {
		return usermenuList.size();
	}

	@Override
	public UserMenu getItem(int position) {
		return usermenuList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView==null) {
        	convertView=mInflater.inflate(R.layout.usermenu_item, null);
            holder=new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.usermenu);
            holder.mlayout = (RelativeLayout) convertView.findViewById(R.id.usermenu_layout);
			holder.mUrlTextView = (TextView) convertView.findViewById(R.id.usermenuurl);
            convertView.setTag(holder);
        }else {
        	holder=(ViewHolder) convertView.getTag();  
		}
		holder.mTextView.setText(usermenuList.get(position).menuname);
		holder.mUrlTextView.setText(usermenuList.get(position).menuurl);
		return convertView;
	}

	
	final static class ViewHolder {  
        TextView mTextView,mUrlTextView;
		RelativeLayout mlayout;
        
        @Override
        public int hashCode() {
			return this.mlayout.hashCode() + mTextView.hashCode()+mUrlTextView.hashCode();
        }
    } 
}
