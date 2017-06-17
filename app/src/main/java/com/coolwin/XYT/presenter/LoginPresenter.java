package com.coolwin.XYT.presenter;

import android.content.Intent;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.MainActivity;
import com.coolwin.XYT.interfaceview.UIPublic;
import com.coolwin.XYT.org.json.JSONObject;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.GsonUtil;
import com.coolwin.XYT.util.UIUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dell on 2017/6/15.
 */

public class LoginPresenter extends BasePresenter<UIPublic> {
    LoginServlet servlet;
    public interface LoginServlet {
        @GET("user/api/loginforother")
        Observable<RetrofitResult> login(@Query("phone") String phone, @Query("password") String password);
    }
    public LoginPresenter(){
        servlet = retrofit.create(LoginServlet.class);
    }
    public void login(final String username, final String password){
        mView.showLoading();
        servlet.login(username,password)
                .map(new Function<RetrofitResult, RetrofitResult<Login>>() {
                    @Override
                    public RetrofitResult<Login> apply(RetrofitResult mapRetrofitResult) throws Exception {
                        RetrofitResult<Login> refit = new RetrofitResult<>();
                        refit.setData(new Login(new JSONObject(GsonUtil.objectToJson(mapRetrofitResult.getData()))));
                        refit.setState(mapRetrofitResult.getState());
                        return refit;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<Login>>() {
                    @Override
                    public void accept(RetrofitResult<Login> listRetrofitResult) throws Exception {
                        if (listRetrofitResult.getState().getCode() == 0) {
                            GetDataUtil.saveLogin(context, listRetrofitResult.getData());
                            GetDataUtil.saveUsername(context, username);
                            GetDataUtil.savePassword(context, password);
                            context.startActivity(new Intent(context, MainActivity.class));
                        } else {
                            UIUtil.showMessage(context, listRetrofitResult.getState().getMsg());
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
