package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UICommodity;
import com.coolwin.XYT.util.UIUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/6/1.
 */

public class CommodityPresenter extends BasePresenter<UICommodity> {
    public interface CertificationServlet {
        @GET
        Observable<MyCommodity> getInfo(@Url String url);
    }
    CertificationServlet servlet;
    public CommodityPresenter(){
        servlet = retrofit.create(CertificationServlet.class);
    }
    public void init(){
        servlet.getInfo(UrlConstants.COMMODITY_SHOP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MyCommodity>() {
                    @Override
                    public void accept(MyCommodity myCommodity) throws Exception {
                        mView.init(myCommodity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context,"请求异常");
                        throwable.printStackTrace();
                    }
                });
    }
}
