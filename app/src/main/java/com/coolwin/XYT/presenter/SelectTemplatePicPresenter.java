package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.util.UIUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/6/1.
 */

public class SelectTemplatePicPresenter extends BasePresenter<UIMyIndex> {
    SelectTemplatePicServlet servlet;
    public interface SelectTemplatePicServlet {
        @POST()
        Observable<RetrofitResult<List<DataModel>>> gettemplatepic(@Url String url);
    }
    public SelectTemplatePicPresenter() {
        servlet = retrofit.create(SelectTemplatePicServlet.class);
    }
    public void init(){
        mView.showLoading();
        servlet.gettemplatepic(UrlConstants.BASEURL2+"gettemplatepic")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<DataModel>>>() {
                    @Override
                    public void accept(RetrofitResult<List<DataModel>> retrofitResult) throws Exception {
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
