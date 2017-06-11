package com.coolwin.XYT.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.ab.fragment.AbLoadDialogFragment;
import com.ab.fragment.AbSampleDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.R;
import com.coolwin.XYT.presenter.BasePresenter;

import java.lang.reflect.ParameterizedType;


/**
 * Created by Administrator on 2017/3/2.
 */

public abstract  class BaseActivity<T extends BasePresenter> extends FragmentActivity {
    public T mPresenter;
    public static final  int CLOSEACTIVITY = 100;
    public AbLoadDialogFragment loadingfragment;
    public Context context;
    public AbSampleDialogFragment abSampleDialogFragment;
    int widthPixels;
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
        //初始化Presenter类
        mPresenter = getT(this,0);
        if(mPresenter!=null){
            mPresenter.init(this,this);
        }
        //全局上下文赋值
        context = this;
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
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
    /**
     * right_text2 的操作
     * @param view
     */
    public void right_text2(View view){
        // TODO: 2017/3/20
    }

    /**
     * 取消浮动窗口
     * @param view
     */
    public void cannelDialog(View view){
        if(abSampleDialogFragment!=null){
            abSampleDialogFragment.dismiss();
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消事件总线

    }
}
