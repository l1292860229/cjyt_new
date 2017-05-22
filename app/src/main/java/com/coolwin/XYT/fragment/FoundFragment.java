package com.coolwin.XYT.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Menu;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MenuAdapter;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.org.json.JSONException;
import com.coolwin.XYT.sortlist.PinYin;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现Fragment的界面
 * @author dl
 */
public class FoundFragment extends Fragment implements OnClickListener {
	
	/**
	 * 定义全局变量
	 */
	private View mView;

	private ListView menuLayout;
	private Context mParentContext;
//	private TextView mNewsFriendsLoopIcon;
	private List<Menu> mMenuList = new ArrayList<Menu>();
	private MenuAdapter menuAdapter;
	private Login login;
	private String username;
	private String password;
	private int friendloopcount=0;
	/**
	 * 导入控件
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentContext = (Context)FoundFragment.this.getActivity();
		SharedPreferences mPreferences= mParentContext.getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
		username=mPreferences.getString(IMCommon.USERNAME, "");
		password=mPreferences.getString(IMCommon.PASSWORD, "");
		login = IMCommon.getLoginResult(mParentContext);
		PinYin.main();
	}

	public void getMenu(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mMenuList = IMCommon.getIMInfo().getmenu().mMenuList;
					if(mMenuList==null){
						mMenuList = new ArrayList<Menu>();
					}
					List<Menu> mMenuListTemp = new ArrayList<Menu>();
					String kid = "";
					if(login.ypId==null||login.ypId.equals("")){
						kid=getResources().getString(R.string.ypid);
					}else{
						kid=login.ypId;
					}
					friendloopcount = IMCommon.getFriendsLoopTip(mParentContext);
					mMenuListTemp.add(new Menu("小鱼圈","",friendloopcount));
					//mMenuListTemp.add(new Menu("扫一扫",""));
					//mMenuListTemp.add(new Menu("酷发现",""));
					mMenuListTemp.add(new Menu("找鱼塘",""));
					//mMenuListTemp.add(new Menu("附近的店","http://shop.wei20.cn/gouwu/o2o/home.shtml?id="+kid+"&token="+login.token+"&lbs="+MainActivity.lbs));
					//mMenuListTemp.add(new Menu("购物","http://"+login.url+"&token="+login.token));
					//mMenuListTemp.add(new Menu("游戏","http://shop.wei20.cn/hd/zhuangpang.shtml?id="+kid+"&sid=531&token="+login.token));
					mMenuListTemp.addAll(mMenuList);
					mMenuList = mMenuListTemp;
					Message message=  new Message();
					mHandler.sendMessage(message);
				} catch (IMException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(menuAdapter!=null){
				menuAdapter.setData(mMenuList);
				menuAdapter.notifyDataSetChanged();
			}else{
				menuAdapter =new MenuAdapter(mParentContext,mMenuList,username,password);
				menuLayout.setAdapter(menuAdapter);
			}
		}
	};
	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.chat_tab_header, container, false);
		return mView;
	}

	
	/**
	 * 初始化界面
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TCAgent.onPageStart(mParentContext, "FoundFragment");
		menuLayout = (ListView)mView.findViewById(R.id.menu);
		register();
		getMenu();
		mMenuList = new ArrayList<Menu>();
		String kid = "";
		if(login.ypId==null||login.ypId.equals("")){
			kid=getResources().getString(R.string.ypid);
		}else{
			kid=login.ypId;
		}
		friendloopcount = IMCommon.getFriendsLoopTip(mParentContext);
		mMenuList.add(new Menu("小鱼圈","",friendloopcount));
		mMenuList.add(new Menu("找鱼塘",""));
		//mMenuList.add(new Menu("扫一扫",""));
		//mMenuList.add(new Menu("酷发现",""));
		//mMenuList.add(new Menu("附近的店","http://shop.wei20.cn/gouwu/o2o/home.shtml?id="+kid+"&token="+login.token+"&lbs="+ MainActivity.lbs));
		//mMenuList.add(new Menu("购物","http://"+login.url+"&token="+login.token+"&lbs="+MainActivity.lbs));
		//mMenuList.add(new Menu("游戏","http://shop.wei20.cn/hd/zhuangpang.shtml?id="+kid+"&sid=531&token="+login.token+"&lbs="+MainActivity.lbs));
		menuAdapter =new MenuAdapter(mParentContext,mMenuList,username,password);
		menuLayout.setAdapter(menuAdapter);
	}

	/**
	 * 注册界面通知
	 */
	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP);
		filter.addAction(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP);
		mParentContext.registerReceiver(mReBoradCast, filter);
	}
	
	/**
	 * 处理通知
	 */
	BroadcastReceiver mReBoradCast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent!=null){
				String action = intent.getAction();
				if(action.equals(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP)){
					if(mMenuList==null || mMenuList.size()==0){
						return;
					}
					friendloopcount = IMCommon.getFriendsLoopTip(mParentContext);
					for (int i = mMenuList.size() - 1; i >= 0; i--) {
						if (mMenuList.get(i).getTitle().equals("小鱼圈")) {
							mMenuList.get(i).setCount(friendloopcount);
							break;
						}
					}
					menuAdapter.setData(mMenuList);
					menuAdapter.notifyDataSetChanged();
				}else if(action.equals(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP)){
					if(mMenuList==null || mMenuList.size()==0){
						return;
					}
					if(IMCommon.getIsReadFoundTip(mParentContext)){
						for (int i = mMenuList.size() - 1; i >= 0; i--) {
							if (mMenuList.get(i).getTitle().equals("小鱼圈")) {
								mMenuList.get(i).setCount(0);
								break;
							}
						}
						menuAdapter.setData(mMenuList);
						menuAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
	}

	/**
	 * 销毁页面
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mReBoradCast!=null){
			mParentContext.unregisterReceiver(mReBoradCast);
		}
		TCAgent.onPageEnd(mParentContext, "FoundFragment");
	}

}
