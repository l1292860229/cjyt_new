package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.Order;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.interfaceview.UIOrderFragment;
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

public class OrderFragmentPresenter extends BasePresenter<UIOrderFragment> {
    OrderFragmentServlet servlet;
    public interface OrderFragmentServlet {
        @POST()
        Observable<RetrofitResult<List<Order>>> getOrderList(@Url String url, @Query("ypid") String ypid,
                                                             @Query("s") String s,
                                                             @Query("token") String token, @Query("page") int page);
    }
    public OrderFragmentPresenter() {
        servlet = retrofit.create(OrderFragmentServlet.class);
    }
    public void getData(int page,String s,final GetDataType type){
        if(type==GetDataType.INIT){
            mView.showLoading();
        }
        servlet.getOrderList(UrlConstants.BASEURL2+"getOrderList",login.ypId,s,login.token,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<Order>>>() {
                    @Override
                    public void accept(RetrofitResult<List<Order>> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            switch (type){
                                case INIT:
                                    mView.hideLoading();
                                    mView.init(retrofitResult.getData());
                                    break;
                                case REFRESH:
                                    mView.refreshsuccess(retrofitResult.getData());
                                    break;
                                case LOADMORE:
                                    mView.loadsuccess(retrofitResult.getData());
                                    break;
                            }
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
