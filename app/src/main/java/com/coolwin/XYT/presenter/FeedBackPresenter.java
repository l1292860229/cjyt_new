package com.coolwin.XYT.presenter;

import android.util.Log;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.interfaceview.UIFeedBack;
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
 * Created by Administrator on 2017/5/27.
 */

public class FeedBackPresenter extends BasePresenter<UIFeedBack> {
    public interface FeedBackServlet{
        @GET("user/api/feedback")
        Observable<RetrofitResult<Map<String,String>>> sendFeedBack(@Query("content") String content,@Query("uid") String uid);
    }
    FeedBackServlet servlet;
    public FeedBackPresenter(){
        servlet = retrofit.create(FeedBackServlet.class);
    }
    public void sendFeedBack(final String content){
        mView.showLoading();
        servlet.sendFeedBack(content,login.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<Map<String, String>>>() {
                    @Override
                    public void accept(RetrofitResult<Map<String, String>> mapRetrofitResult) throws Exception {
                        Log.e("sendFeedBack","mapRetrofitResult="+mapRetrofitResult);
                        if (mapRetrofitResult.getState().getCode() == 0) {
                            UIUtil.showMessage(context, "谢谢你的反馈,我们会做得更好");
                            mView.closeFeedBack();
                        } else {
                            UIUtil.showMessage(context, mapRetrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mView.hideLoading();
                        UIUtil.showMessage(context, "请求失败,请稍后重试");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.closeFeedBack();
                    }
                });
    }
}
