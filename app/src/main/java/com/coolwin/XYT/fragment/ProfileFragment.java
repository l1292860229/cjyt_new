package com.coolwin.XYT.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.AddActivity;
import com.coolwin.XYT.EditProfileActivity;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.FeedBackActivity;
import com.coolwin.XYT.MainActivity;
import com.coolwin.XYT.MyAlbumActivity;
import com.coolwin.XYT.MyBbsListActivity;
import com.coolwin.XYT.MyFavoriteActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.SettingTab;
import com.coolwin.XYT.UserMenuActivity;
import com.coolwin.XYT.WebViewActivity;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.widget.MainSearchDialog;
import com.coolwin.XYT.widget.SelectAddPopupWindow;
import com.tendcloud.tenddata.TCAgent;

import static com.coolwin.XYT.global.IMCommon.getLoginResult;
import static com.tendcloud.tenddata.TalkingDataSMS.mContext;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    Activity context;
    Login login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = ProfileFragment.this.getActivity();
        TCAgent.onPageStart(context, "ProfileFragment");
        mView =inflater.inflate(R.layout.fragment_profile, container, false);
        return mView;
    }
    RelativeLayout mTitleLayout;
    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }

    private void initView(){
        ImageView ivAvatar= (ImageView) getView().findViewById(R.id.iv_avatar);
        TextView tvNick= (TextView) getView().findViewById(R.id.tv_name);
        TextView tvFxid= (TextView) getView().findViewById(R.id.tv_fxid);
        login = getLoginResult(context);
        SharedPreferences mPreferences= context.getSharedPreferences(IMCommon.REMENBER_SHARED, 0);
        String fxid=mPreferences.getString(IMCommon.USERNAME, "");
        if(login!=null ){
            if(login.headsmall!=null && !login.headsmall.equals("")){
                ImageLoader mImageLoader  = new ImageLoader();
                mImageLoader.getBitmap(context, ivAvatar,null,login.headsmall,0,false,true);
            }
            if(login.nickname!=null && !login.nickname.equals("")){
                tvNick.setText(login.nickname);
            }
        }
        if(TextUtils.isEmpty(fxid)){
            fxid="未设置";
        }
        fxid="鱼塘号:"+fxid;
        tvFxid.setText(fxid);
    }
    private void setListener(){
        getView().findViewById(R.id.re_myinfo).setOnClickListener(this);
        getView().findViewById(R.id.re_setting).setOnClickListener(this);
        getView().findViewById(R.id.re_xiangce).setOnClickListener(this);
        getView().findViewById(R.id.re_shoucang).setOnClickListener(this);
        getView().findViewById(R.id.re_tuiguangzhongxin).setOnClickListener(this);
        getView().findViewById(R.id.re_tuiguangeweima).setOnClickListener(this);
        getView().findViewById(R.id.re_huiyanshenji).setOnClickListener(this);
        getView().findViewById(R.id.usermenu_layout).setOnClickListener(this);
        getView().findViewById(R.id.yaoqing_layout).setOnClickListener(this);
        getView().findViewById(R.id.my_service_icon).setOnClickListener(this);
     }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        String kid=null;
        if (login.ypId==null || login.ypId.equals("")) {
            kid=getResources().getString(R.string.ypid);
        }else{
            kid=login.ypId;
        }
        switch (v.getId()){
            case R.id.my_service_icon:
                Intent myintent = new Intent(context, MyBbsListActivity.class);
                myintent.putExtra("type","1");
                startActivity(myintent);
                break;
            case R.id.re_myinfo:
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.re_setting:
                Intent settingIntent = new Intent();
                settingIntent.setClass(context, SettingTab.class);
                startActivity(settingIntent);
                break;
            case R.id.re_xiangce:
                Intent albumIntent = new Intent();
                albumIntent.setClass(context, MyAlbumActivity.class);
                startActivity(albumIntent);
                break;
            case R.id.re_shoucang:
                Intent collectionIntent = new Intent();
                collectionIntent.setClass(context, MyFavoriteActivity.class);
                startActivity(collectionIntent);
                break;
            case R.id.re_tuiguangzhongxin:
                Intent tuiguangzhongxinIntent = new Intent();
                tuiguangzhongxinIntent.putExtra("url","http://shop.wei20.cn/gouwu/wish3d/my_tg77.shtml?id="+kid+"&token="+login.token+"&lbs="+ MainActivity.lbs);
                tuiguangzhongxinIntent.setClass(context, WebViewActivity.class);
                startActivity(tuiguangzhongxinIntent);
                break;
            case R.id.re_tuiguangeweima:
                Intent tuiguangeweimaIntent = new Intent();
                tuiguangeweimaIntent.putExtra("url","http://shop.wei20.cn/upic/2pic.shtml?id="+kid+"&fromusername="+login.phone+"&lbs="+ MainActivity.lbs);
                tuiguangeweimaIntent.setClass(context, WebViewActivity.class);
                startActivity(tuiguangeweimaIntent);
                break;
            case R.id.re_huiyanshenji:
                if(login.quId.equals(login.kai6Id)){
                    Toast.makeText(context,"你已经是渠道商", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!login.userDj.equals("0")){
                    Toast.makeText(context,"你已经是特级会员", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent huiyuanshenjiIntent = new Intent();
                huiyuanshenjiIntent.putExtra("url","http://shop.wei20.cn/gouwu/wish3d/member_hy.shtml?id="+kid+"&k=%E7%89%B9%E6%9D%83%E4%BC%9A%E5%91%98&token="+login.token+"&lbs="+ MainActivity.lbs);
                huiyuanshenjiIntent.setClass(context, WebViewActivity.class);
                startActivity(huiyuanshenjiIntent);
                break;
            case R.id.usermenu_layout:
                Intent usermenuIntent = new Intent();
                usermenuIntent.setClass(context, UserMenuActivity.class);
                startActivity(usermenuIntent);
                break;
            case R.id.search_btn:
                MainSearchDialog dialog = new MainSearchDialog(context,0);
                dialog.show();
                break;
            case R.id.yaoqing_layout:
                Intent yaoqingIntent = new Intent();
                yaoqingIntent.setClass(context, AddActivity.class);
                startActivity(yaoqingIntent);
                break;
            case R.id.add_btn:
                uploadImage2(context,mTitleLayout);
                break;
        }
    }
    SelectAddPopupWindow menuWindow2; // 弹出框
    public void uploadImage2(final Activity context, View view) {
        if (menuWindow2 != null && menuWindow2.isShowing()) {
            menuWindow2.dismiss();
            menuWindow2 = null;
        }
        menuWindow2 = new SelectAddPopupWindow(context, itemsOnClick2);
        // 显示窗口

        // 计算坐标的偏移量
        int xoffInPixels = menuWindow2.getWidth() - view.getWidth()+10;
        menuWindow2.showAsDropDown(view, -xoffInPixels, 0);
    }
    // 为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick2 = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.feedback:
                    Intent feedbackIntent = new Intent(mContext, FeedBackActivity.class);
                    startActivity(feedbackIntent);
                    break;
                default:
                    break;
            }
            if (menuWindow2 != null && menuWindow2.isShowing()) {
                menuWindow2.dismiss();
                menuWindow2 = null;
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        TCAgent.onPageEnd(context, "ProfileFragment");
    }
}
