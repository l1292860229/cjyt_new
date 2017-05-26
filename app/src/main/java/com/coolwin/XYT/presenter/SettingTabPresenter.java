package com.coolwin.XYT.presenter;

import android.util.Log;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.interfaceview.UISettingTab;
import com.coolwin.XYT.retrofitServlet.SettingTabServlet;
import com.coolwin.XYT.util.UIUtil;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dell on 2017/5/26.
 */

public class SettingTabPresenter extends BasePresenter<UISettingTab> {
    SettingTabServlet servlet;
    public SettingTabPresenter(){
        servlet = retrofit.create(SettingTabServlet.class);
    }
    public void getNewVersion(String version){
        servlet.getNewVersion(version,"android")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<Map<String, String>>>() {
                    @Override
                    public void accept(RetrofitResult<Map<String, String>> mapRetrofitResult) throws Exception {
                        Log.e("accept", "mapRetrofitResult=" + mapRetrofitResult);
                        if (mapRetrofitResult.getState().getCode()==0) {
                           boolean isNew =  UIUtil.compareVersion(UIUtil.getAppVersionName(context),mapRetrofitResult.getData().get("currVersion"));
                            if(isNew){
                                mView.downNewVersion(mapRetrofitResult.getData());
                            }
                        }else{
                            UIUtil.showMessage(context,mapRetrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }
}
