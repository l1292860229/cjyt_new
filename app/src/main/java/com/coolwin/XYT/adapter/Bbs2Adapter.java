package com.coolwin.XYT.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;

import java.util.HashMap;
import java.util.List;


/**
 * 我的群组数据适配器
 * @author dongli
 *
 */
public class Bbs2Adapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	HashMap<Integer, View> hashMap;
	private List<Bbs> mData;
	private Context mContext;
	private ImageLoader mImageLoader;

	public Bbs2Adapter(Context context, List<Bbs> data){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
		hashMap= new HashMap<Integer, View>();
		mImageLoader = new ImageLoader();
	}
	public  void setData(List<Bbs> data){
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
			convertView=mInflater.inflate(R.layout.bbs_list_item2, null);
			holder=new ViewHolder();
			//设置内容view的大小为屏幕宽度,这样按钮就正好被挤出屏幕外
			holder.content = convertView.findViewById(R.id.ll_content);
			holder.mHeaderView = (ImageView) convertView.findViewById(R.id.header);
			holder.mTitleTextView = (TextView) convertView.findViewById(R.id.title);
			holder.mContentTextView = (TextView) convertView.findViewById(R.id.content);
			holder.mPeopleTextView = (TextView) convertView.findViewById(R.id.peoplecount);
			convertView.setTag(holder);
			hashMap.put(position, convertView);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		final Bbs bbs = mData.get(position);
		if (bbs.headsmall!=null && !bbs.uheadsmall.equals("")) {
			mImageLoader.getBitmap(mContext, holder.mHeaderView, null, bbs.headsmall, 0, false, true);
		}
		holder.mTitleTextView.setText(bbs.title);
		holder.mContentTextView.setText(bbs.content);
		holder.mPeopleTextView.setText(bbs.peopleCount+"人");
		holder.content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bbs.isVisitors==1){
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					intent.putExtra("isvisitors", true);
					mContext.startActivity(intent);
					return;
				}
				if(bbs.isjoin==0){
					showModifybgDialog(bbs);
				}else if(bbs.isjoin==1){
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					mContext.startActivity(intent);
				}else if(bbs.isjoin==2){
					Toast.makeText(mContext,"你的申请已经提交,等待管理员审核", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return convertView;
	}

	final static class ViewHolder {
		public View content;
		ImageView mHeaderView;
		TextView mTitleTextView;
		TextView mContentTextView;
		TextView mPeopleTextView;
		@Override
		public int hashCode() {
			return this.mHeaderView.hashCode() + mTitleTextView.hashCode() + mContentTextView.hashCode() +
					 mHeaderView.hashCode() +mPeopleTextView.hashCode();
		}
	}
	private void showModifybgDialog(final Bbs bbs){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle("你还不是鱼塘成员,是否申请加入");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(!bbs.money.equals("0")){
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setIcon(R.drawable.ic_dialog_alert);
					builder.setTitle("你申请的群不是免费的,是否支付费用");
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							new Thread(){
								@Override
								public void run(){
									if(IMCommon.verifyNetwork(mContext)){
										try {
											IMCommon.getIMInfo().addBbsUser(bbs.id);
											return;
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}.start();
							Toast.makeText(mContext,"申请成功", Toast.LENGTH_LONG).show();
						}
					});
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					});
					builder.create().show();
				}else{
					new Thread(){
						@Override
						public void run(){
							if(IMCommon.verifyNetwork(mContext)){
								try {
									IMCommon.getIMInfo().addBbsUser(bbs.id);
									return;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}.start();
					Toast.makeText(mContext,"申请成功", Toast.LENGTH_LONG).show();
				}
			}
		});
		if(bbs.type==1&&bbs.isVisitors ==1){
			builder.setNeutralButton(R.string.look, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					intent.putExtra("isvisitors", true);
					mContext.startActivity(intent);
				}
			});
		}
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}

}
