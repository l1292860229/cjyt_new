package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.activity.MyOrderInfoActivity;
import com.coolwin.XYT.interfaceview.UIMyOrderInfo;
import com.coolwin.XYT.util.UIUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/7/3.
 */

public class MyOrderInfoPresenter extends BasePresenter<UIMyOrderInfo> {
    MyOrderInfoServlet servlet;
    public interface MyOrderInfoServlet {
        @POST()
        Observable<RetrofitResult> upateOrderListStatus(@Url String url, @Query("ypid") String ypid,
                                                             @Query("s") String s,
                                                             @Query("token") String token, @Query("orderid") String orderid);
    }
    public MyOrderInfoPresenter() {
        servlet = retrofit.create(MyOrderInfoServlet.class);
    }
    public void setData(final String s, String orderid){
        mView.showLoading();
        servlet.upateOrderListStatus(UrlConstants.BASEURL2+"upateOrderListStatus",login.ypId,s,login.token,orderid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult retrofitResult) throws Exception {
                        if (retrofitResult.getState().getCode() == 0) {
                            String value="";
                            if (s.equals(MyOrderInfoActivity.NODO)) {
                                value="未处理";
                            }else if(s.equals(MyOrderInfoActivity.COMPLETE)){
                                value="已完成";
                            }else if(s.equals(MyOrderInfoActivity.SHIP)){
                                value="已发货";
                            }else if(s.equals(MyOrderInfoActivity.CANCEL)){
                                value="已取消";
                            }
                            mView.setData(value);
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
