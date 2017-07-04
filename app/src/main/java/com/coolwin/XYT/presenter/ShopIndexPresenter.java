package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.AppDataStatistics;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIShopIndexFrament;
import com.coolwin.XYT.util.UIUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/7/1.
 */

public class ShopIndexPresenter extends BasePresenter<UIShopIndexFrament> {
    ShopIndexPicServlet servlet;
    public interface ShopIndexPicServlet {
        @POST()
        Observable<RetrofitResult<List<AppDataStatistics>>> getDataStatistics(@Url String url, @Query("ypid") String ypid,
                                                                                    @Query("token") String token);
    }
    public ShopIndexPresenter() {
        servlet = retrofit.create(ShopIndexPicServlet.class);
    }
    public void init(){
        mView.showLoading();
        servlet.getDataStatistics(UrlConstants.BASEURL2+"getDataStatistics",login.ypId,login.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<AppDataStatistics>>>() {
                    @Override
                    public void accept(RetrofitResult<List<AppDataStatistics>> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            mView.init(retrofitResult.getData());
                        }else{
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
