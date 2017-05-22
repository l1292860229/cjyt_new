package com.coolwin.XYT;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.fragment.ContactsFragment;
import com.coolwin.XYT.fragment.NewContactsFragment;
import com.coolwin.XYT.global.FeatureFunction;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.SystemContactGlobal;
import com.coolwin.XYT.sortlist.SortAdapter;
import com.coolwin.XYT.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的朋友
 * @author dongli
 *
 */
public class NewFriendsActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
//	private ListView mListView;
//	private List<Login> mUserList = new ArrayList<Login>();
//	private NewFriendAdapter mAdapter;
	public List<Login> mSourceDateList=new ArrayList<Login>();
	private SortAdapter mAdapter;
	private SystemContactGlobal mSystemContactGlobal;
	private PagerSlidingTabStrip tabs;
	private ViewPager mPager;

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
//			case GlobalParam.MSG_GET_CONTACT_DATA:
//				IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,
//						mContext.getResources().getString(R.string.get_dataing));
//				break;
			case GlobalParam.MSG_CHECK_STATE:
				IMJiaState returnStatus = (IMJiaState)msg.obj;
				if (returnStatus == null) {
					Toast.makeText(mContext, "提交数据失败!", Toast.LENGTH_LONG).show();
					return;
				}
				if(returnStatus.code!=0){
					Toast.makeText(mContext, returnStatus.errorMsg, Toast.LENGTH_LONG).show();
					return;
				}
				String controlId= returnStatus.uid;
				break;
			case GlobalParam.MSG_LOAD_ERROR:
				hideProgressDialog();
				int excuteType = msg.arg1;
				String prompt = (String) msg.obj;
				if(prompt != null && !prompt.equals("")){
					Toast.makeText(mContext, prompt, Toast.LENGTH_LONG).show();
				}else {
					if(excuteType == 1){
						Toast.makeText(mContext,mContext.getResources().getString(R.string.add_block_failed), Toast.LENGTH_LONG).show();
					}else if(excuteType == 2){
						Toast.makeText(mContext,mContext.getResources().getString(R.string.delete_friend_failed), Toast.LENGTH_LONG).show();
					}else if(excuteType == 3){
						Toast.makeText(mContext,mContext.getResources().getString(R.string.no_search_user), Toast.LENGTH_LONG).show();
					}
				}

				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				List<Login> tempList = (List<Login>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mSourceDateList.addAll(tempList);
					mAdapter = new SortAdapter(mContext,mSourceDateList);
					//mListView.setAdapter(mAdapter);
				}
				break;
			default:
				break;
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		mContext = this;
		IMCommon.saveContactTip(mContext, 0);
		mContext.sendBroadcast(new Intent(ContactsFragment.ACTION_HIDE_NEW_FRIENDS));
		mContext.sendBroadcast(new Intent(GlobalParam.ACTION_HIDE_CONTACT_NEW_TIP));
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(GlobalParam.ACTION_REFRESH_NEW_FRIENDS);
		//registerReceiver(mReceiver, filter);
		initCompent();
	}

//	BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent != null) {
//				if(intent.getAction().equals(GlobalParam.ACTION_REFRESH_NEW_FRIENDS)){
//					if(mUserList!=null && mUserList.size()>0){
//						mUserList.clear();
//					}
//					if(IMCommon.getNewFriendItemResult(mContext)!=null
//							&& IMCommon.getNewFriendItemResult(mContext).size()>0){
//						mUserList.addAll(IMCommon.getNewFriendItemResult(mContext));
//					}
//					if(mAdapter!=null){
//						mAdapter.notifyDataSetChanged();
//					}else{
//						updateListView();
//					}
//				}
//			}
//		}
//	};



	@Override
	protected void onDestroy() {
		super.onDestroy();
		//unregisterReceiver(mReceiver);
	}


	private void initCompent(){
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setVisibility(View.VISIBLE);
		mPager=(ViewPager) findViewById(R.id.pager);
		mPager.setVisibility(View.VISIBLE);
		setTitleContent(R.drawable.back_btn, R.drawable.add_contact_btn, R.string.new_friends);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		LinearLayout.LayoutParams addBtnParams = new LinearLayout.LayoutParams(
				FeatureFunction.dip2px(mContext,52),FeatureFunction.dip2px(mContext, 55));
		addBtnParams.gravity = Gravity.CENTER_VERTICAL;
		mRightBtn.setLayoutParams(addBtnParams);
		//mSystemContactGlobal = new SystemContactGlobal(mContext,mHandler);
		mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mPager.setCurrentItem(0);
		tabs.setIndicatorColor(Color.rgb(255,74,81));
		tabs.setViewPager(mPager);
		tabs.setShouldExpand(true);
		tabs.setBackgroundColor(Color.rgb(239,239,244));
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			NewFriendsActivity.this.finish();
			break;
		case R.id.right_btn:
			Intent intent = new Intent();
			intent.setClass(mContext, AddActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//arg2 = arg2-1;
		if (0<=arg2 && arg2<mSourceDateList.size()) {
			Intent intent = new Intent();
			intent.setClass(mContext, UserInfoActivity.class);
			intent.putExtra("type",2);
			intent.putExtra("o",0);
			intent.putExtra("uid",mSourceDateList.get(arg2).uid);
			startActivityForResult(intent, 1);
		}
	}


	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		//		private final String[] titles = mContext.getResources().getStringArray(R.array.main_fragment_array);
		private final String[] titles = new String[]{"人脉","一度","二度","三度"};
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}
		@Override
		public int getCount() {
			return titles.length;
		}
		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return NewContactsFragment.newInstance(0);
				case 1:
					return NewContactsFragment.newInstance(1);
				case 2:
					return NewContactsFragment.newInstance(2);
				case 3:
					return NewContactsFragment.newInstance(3);
				case 4:
					return NewContactsFragment.newInstance(99);
				default:
					return null;
			}
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.e("destroyItem", "destroyItem");
			super.destroyItem(container, position, object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.e("instantiateItem", "instantiateItem");
			return super.instantiateItem(container, position);
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode == 2){
			if(data == null){
				return;
			}
			String uid = data.getStringExtra("uid");
			int changeType = data.getIntExtra("changeType",0);
		}
	}


}
