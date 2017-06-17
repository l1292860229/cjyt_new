package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.interfaceview.UIRegister;
import com.coolwin.XYT.util.UIUtil;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dell on 2017/6/15.
 */

public class RegisterPresenter extends BasePresenter<UIRegister> {
    RegisterServlet servlet;
    public interface RegisterServlet {
        @GET("user/api/registforother")
        Observable<Map<String,String>> register(@Query("username") String username,
                                                                @Query("name") String name,
                                                                @Query("tjr") String tjr,
                                                                @Query("password") String password,
                                                                @Query("id") String id);
        @GET("user/apiother/getCode")
        Observable<RetrofitResult<Map<String,String>>> sendCode(@Query("phone") String phone);
    }
    public RegisterPresenter(){
        servlet = retrofit.create(RegisterServlet.class);
    }
    public void sendCode(String telephone){
        servlet.sendCode(telephone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<Map<String,String>>>() {
                    @Override
                    public void accept(RetrofitResult<Map<String,String>> listRetrofitResult) throws Exception {
                        if (listRetrofitResult.getState().getCode()==0) {
                            mView.setCode(listRetrofitResult.getData().get("code"));
                        }else{
                            UIUtil.showMessage(context,listRetrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常");
                        throwable.printStackTrace();
                    }
                });
    }

    public void register(String name,String tjr,String telephone,String password){
        mView.showLoading();
        servlet.register(telephone,name,tjr,password,"585")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, String>>() {
                    @Override
                    public void accept(Map<String, String> string) throws Exception {
                        UIUtil.showMessage(context, string.get("msg"));
                        if ("yes".equals(string.get("success"))) {
                            context.finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常");
                        mView.hideLoading();
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideLoading();
                    }
                });
    }
}
