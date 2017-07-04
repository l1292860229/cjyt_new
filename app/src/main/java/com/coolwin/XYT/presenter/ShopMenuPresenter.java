package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.Menu;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.interfaceview.UIShopMenu;
import com.coolwin.XYT.util.UIUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/7/3.
 */

public class ShopMenuPresenter extends BasePresenter<UIShopMenu> {
    ShopMenuServlet servlet;
    public interface ShopMenuServlet {
        @GET
        Observable<RetrofitResult<List<Menu>>> getmenuList(@Url String url,
                                                           @Query("id") String id,@Query("kai6id")  String kai6id,
                                                           @Query("token") String token,@Query("ypid2") String ypid2);
    }
    public ShopMenuPresenter() {
        servlet = retrofit.create(ShopMenuServlet.class);
    }
    public void getData(String url){
        mView.showLoading();
        servlet.getmenuList(url,login.ypId,login.kai6Id,login.token,login.ypId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<Menu>>>() {
                    @Override
                    public void accept(RetrofitResult<List<Menu>> retrofitResult) throws Exception {
                        if (retrofitResult.getState().getCode() == 0) {
                            mView.init(retrofitResult.getData());
                        } else {
                            UIUtil.showMessage(context, retrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
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
