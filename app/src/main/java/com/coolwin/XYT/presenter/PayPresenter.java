package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIPay;
import com.coolwin.XYT.util.UIUtil;
import com.tencent.mm.sdk.modelpay.PayReq;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/6/13.
 */

public class PayPresenter extends BasePresenter<UIPay> {
    PayServlet servlet;
    public interface PayServlet {
        @POST()
        Observable<RetrofitResult<Map<String,String>>> getpay(@Url String url);
    }
    public PayPresenter(){
        servlet = retrofit.create(PayServlet.class);
    }
    public void pay(){
        servlet.getpay(UrlConstants.BASEURL2+"weichatprepay")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<Map<String,String>>>() {
                    @Override
                    public void accept(RetrofitResult<Map<String,String>> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            Map<String,String> data =  retrofitResult.getData();
                            PayReq req = new PayReq();
                            req.appId			= data.get("appid");
                            req.partnerId		= data.get("partnerid");
                            req.prepayId		= data.get("prepayid");
                            req.nonceStr		= data.get("noncestr");
                            req.timeStamp		= data.get("timestamp");
                            req.packageValue	= data.get("package");
                            req.sign			= data.get("sign");
                            req.extData			= "app data";
                            mView.sendpay(req);
                        }else{
                            UIUtil.showMessage(context, retrofitResult.getState().getMsg());
                        }
                        context.finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        context.finish();
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
                        throwable.printStackTrace();
                    }
                });
    }
}
