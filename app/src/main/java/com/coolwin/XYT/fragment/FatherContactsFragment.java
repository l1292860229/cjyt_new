package com.coolwin.XYT.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coolwin.XYT.MyBbsListActivity;
import com.coolwin.XYT.MyGroupListActivity;
import com.coolwin.XYT.NewFriendsActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.widget.MainSearchDialog;
import com.tendcloud.tenddata.TCAgent;

import static com.coolwin.XYT.fragment.ContactsFragment.ACTION_HIDE_NEW_FRIENDS;
import static com.coolwin.XYT.fragment.ContactsFragment.ACTION_SHOW_NEW_FRIENDS;

/**
 * Created by yf on 2016/11/25.
 */

public class FatherContactsFragment extends Fragment implements View.OnClickListener {
    public final static String ACTION_MOVE_LAYOUT = "im_action_move_layout";
    private View mView;
//    private PagerSlidingTabStrip tabs;
    private ViewPager mPager;
    private ContactsFragment cftab0,cftab1,cftab2,cftab3,cftab99;
    private RelativeLayout newFriendsLayout,groupLayout,bbsLayout,hangyeLayout;
    private Context mParentContext;
    private TextView newFriendsMessageCount;
    private LinearLayout topView;
    private RelativeLayout[] mTabs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.activity_father_contact_main, container, false);
        return mView;
    }
    RelativeLayout sousuoLayout;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentContext = (Context)FatherContactsFragment.this.getActivity();
        TCAgent.onPageStart(mParentContext, "FatherContactsFragment");
        registerBoardCast();
        topView = (LinearLayout) mView.findViewById(R.id.topview);
//        tabs = (PagerSlidingTabStrip) mView.findViewById(R.id.tabs);
        mPager=(ViewPager) mView.findViewById(R.id.pager);
        newFriendsLayout = (RelativeLayout) mView.findViewById(R.id.new_friends_layout);
        groupLayout = (RelativeLayout) mView.findViewById(R.id.group_content);
        bbsLayout =  (RelativeLayout) mView.findViewById(R.id.bbs_content);
        hangyeLayout = (RelativeLayout) mView.findViewById(R.id.bbs_hangye_content);
        newFriendsMessageCount = (TextView)mView.findViewById(R.id.new_friends_message_count);
        sousuoLayout  = (RelativeLayout) mView.findViewById(R.id.sousuo);
        sousuoLayout.setOnClickListener(this);
        newFriendsLayout.setOnClickListener(this);
        groupLayout.setOnClickListener(this);
        bbsLayout.setOnClickListener(this);
        hangyeLayout.setOnClickListener(this);
        mPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mPager.setCurrentItem(0);
        mTabs = new RelativeLayout[4];
        MyOnClickListener onClickListener = new MyOnClickListener();
        mTabs[0] = (RelativeLayout) mView.findViewById(R.id.one);
        mTabs[1] = (RelativeLayout) mView.findViewById(R.id.two);
        mTabs[2] = (RelativeLayout) mView.findViewById(R.id.three);
        mTabs[3] = (RelativeLayout) mView.findViewById(R.id.fore);
        mTabs[0].setOnClickListener(onClickListener);
        mTabs[1].setOnClickListener(onClickListener);
        mTabs[2].setOnClickListener(onClickListener);
        mTabs[3].setOnClickListener(onClickListener);
        mTabs[0].setSelected(true);
//        tabs.setViewPager(mPager);
//        setTabsValue();
    }
    public class  MyOnClickListener implements View.OnClickListener{
        private int index,currentTabIndex;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.one:
                    index = 0;
                    break;
                case R.id.two:
                    index = 1;
                    break;
                case R.id.three:
                    index = 2;
                    break;
                case R.id.fore:
                    index = 3;
                    break;
            }
            showView();
        }
        public void showView(){
            if (currentTabIndex != index) {
                mPager.setCurrentItem(index);
            }
            mTabs[currentTabIndex].setSelected(false);
            // set current tab selected
            mTabs[index].setSelected(true);
            currentTabIndex = index;
        }
    }

    private void registerBoardCast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalParam.ACTION_CANCLE_NEW_ORDER);
        filter.addAction(GlobalParam.ACTION_CANCLE_NEW_SERVICE);
        filter.addAction(ACTION_MOVE_LAYOUT);
        mParentContext.registerReceiver(mReceiver, filter);
    }
    /**
     * 处理通知
     */
    int moveheight=0;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if(intent.getAction().equals(ACTION_SHOW_NEW_FRIENDS)){//显示新的朋友
                    newFriendsMessageCount.setVisibility(View.VISIBLE);
                }else if(intent.getAction().equals(ACTION_HIDE_NEW_FRIENDS)){//隐藏新的朋友
                    newFriendsMessageCount.setVisibility(View.GONE);
                }else if(intent.getAction().equals(ACTION_MOVE_LAYOUT)){//滚动ui
                    int move =  intent.getIntExtra("move",0);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    moveheight+=move;
                    if(moveheight<0){
                        moveheight = 0;
                    }else if(moveheight>=topView.getHeight()){
                        moveheight = topView.getHeight();
                    }
                    lp.setMargins(0, -moveheight, 0, 0);
                    topView.setLayoutParams(lp);
                }
            }
        }
    };
//    private void setTabsValue() {
//        // 设置Tab是自动填充满屏幕的
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        tabs.setShouldExpand(true);
//        // 设置Tab的分割线是透明的
//        tabs.setDividerColor(Color.TRANSPARENT);
//        // 设置Tab底部线的高度
//        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
//        // 设置Tab Indicator的高度
//        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,40, dm));
//        // 设置Tab标题文字的大小
//        tabs.setTextSize((int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_SP, 16, dm));
//        // 设置Tab Indicator的颜色
//        tabs.setIndicatorColor(Color.parseColor("#FF4A51"));
//        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
//        tabs.setSelectedTextColor(Color.parseColor("#ffffff"));
//        // 取消点击Tab时的背景色
//        tabs.setTextColor(Color.parseColor("#FF4A51"));
//        tabs.settabBackground(R.drawable.btn_yuanjiao_while);
//    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch (id){
            case R.id.sousuo:
                MainSearchDialog dialog = new MainSearchDialog(mParentContext,0);
                dialog.show();
                break;
            case R.id.new_friends_layout:
                Intent newFriendIntent = new Intent();
                newFriendIntent.setClass(mParentContext, NewFriendsActivity.class);
                startActivity(newFriendIntent);
                break;
            case R.id.group_content:
                Intent groupListIntent = new Intent();
                groupListIntent.setClass(mParentContext, MyGroupListActivity.class);
                startActivity(groupListIntent);
                break;
            case R.id.bbs_content:
                Intent bbsListIntent = new Intent();
                bbsListIntent.putExtra("type","0");
                bbsListIntent.setClass(mParentContext, MyBbsListActivity.class);
                startActivity(bbsListIntent);
                break;
            case R.id.bbs_hangye_content:
                Intent hanyeIntent = new Intent();
                hanyeIntent.putExtra("type","1");
                hanyeIntent.setClass(mParentContext, MyBbsListActivity.class);
                startActivity(hanyeIntent);
                break;

        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        //		private final String[] titles = mContext.getResources().getStringArray(R.array.main_fragment_array);
        private final String[] titles = new String[]{"全部人脉","我的好友","好友的朋友","朋友的朋友"};
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
					if (cftab0 == null) {
						cftab0 = ContactsFragment.newInstance(0);
					}
					return cftab0;
				case 1:
					if (cftab1 == null) {
						cftab1 = ContactsFragment.newInstance(1);
					}
					return cftab1;
				case 2:
					if (cftab2 == null) {
                        cftab2 = ContactsFragment.newInstance(2);
					}
					return cftab2;
                case 3:
                    if (cftab3 == null) {
                        cftab3 = ContactsFragment.newInstance(3);
                    }
                    return cftab3;
                case 4:
                    if (cftab99 == null) {
                        cftab99 = ContactsFragment.newInstance(99);
                    }
                    return cftab99;
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
    public void onDestroy() {
        TCAgent.onPageEnd(mParentContext, "FatherContactsFragment");
        super.onDestroy();
    }
}
