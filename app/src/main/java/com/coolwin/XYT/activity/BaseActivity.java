package com.coolwin.XYT.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.ab.fragment.AbLoadDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.R;
import com.coolwin.XYT.presenter.BasePresenter;

import java.lang.reflect.ParameterizedType;


/**
 * Created by Administrator on 2017/3/2.
 */

public abstract  class BaseActivity<T extends BasePresenter> extends FragmentActivity {
    public T mPresenter;
    final public static String CLOSE_ACTIVITY="close_activity_action";
    AbLoadDialogFragment loadingfragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将标题栏设置为沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明底部菜单栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
//        colseregisterReceiver();
        mPresenter = getT(this,0);
        if(mPresenter!=null){
            mPresenter.init(this,this);
        }
    }
    public  <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void showLoading(String msg) {
        loadingfragment =  AbDialogUtil.showLoadDialog(BaseActivity.this, R.mipmap.ic_load, msg);
    }

    public void hideLoading() {
        loadingfragment.dismiss();
        loadingfragment.onDestroyView();
    }
    /**
     * 处理通知
     */
//    private BroadcastReceiver closereceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action  = intent.getAction();
//            if (action.equals(CLOSE_ACTIVITY)) {
//                Intent intent1 =new Intent(BaseActivity.this,LoginActivity.class);
//                startActivity(intent1);
//                UIUtil.showMessage(BaseActivity.this,"你的账号在其他设备登陆");
//                Log.e("closereceiver","closereceiver");
//                finish();
//            }
//        }
//    };
//    /**
//     * 注册通知
//     */
//    private void colseregisterReceiver(){
//        IntentFilter filter  = new IntentFilter();
//        filter.addAction(CLOSE_ACTIVITY);
//        registerReceiver(closereceiver,filter);
//    }

    /**
     * 关闭当前View
     * @param view
     */
    public void close(View view){
        finish();
    }

    /**
     * left_btn2 的操作
     * @param view
     */
    public void left_btn2(View view){
        // TODO: 2017/3/20
    }
    /**
     * right_btn 的操作
     * @param view
     */
    public void right_btn(View view){
        // TODO: 2017/3/20
    }
    /**
     * right_btn2 的操作
     * @param view
     */
    public void right_btn2(View view){
        // TODO: 2017/3/20
    }
    /**
     * right_text 的操作
     * @param view
     */
    public void right_text(View view){
        // TODO: 2017/3/20
    }
//    /**
//     * 点表情时将表情添加对应的文本框
//     */
//    @Override
//    public void onEmojiconClicked(Emojicon emojicon) {
//        EmojiconsFragment.input(emojiconEditText, emojicon);
//    }
//    /**
//     * 表情框里面的删除键
//     * @param view
//     */
//    @Override
//    public void onEmojiconBackspaceClicked(View view) {
//        EmojiconsFragment.backspace(emojiconEditText);
//    }

//    /**
//     * 设置表情支持
//     * @param emojiconEditText
//     * @param resourceId Fragment的id
//     */
//    public void setEmojiconFragment(EmojiconEditText emojiconEditText,int resourceId) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(resourceId, EmojiconsFragment.newInstance(false))
//                .commit();
//        this.emojiconEditText =  emojiconEditText;
//    }
//
//    @Override
//    protected void onDestroy() {
//        if(closereceiver!=null){
//            unregisterReceiver(closereceiver);
//        }
//        super.onDestroy();
//    }
}
