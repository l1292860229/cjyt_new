package com.coolwin.XYT.presenter;

import com.ab.util.AbBase64;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.util.GsonUtil;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.google.gson.reflect.TypeToken;

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
 * Created by Administrator on 2017/6/1.
 */

public class MyIndexPresenter extends BasePresenter<UIMyIndex> {
    MyIndexServlet servlet;
    public interface MyIndexServlet {
        @POST()
        Observable<RetrofitResult> saveshopindex(@Url String url,@Query("ka6id") String ka6id,
                                                 @Query("token") String token,
                                                 @Query("ypid") String ypid,
                                                 @Query("uid") String uid,
                                                 @Query("data") String data);
        @POST()
        Observable<RetrofitResult<String>> getShopIndex(@Url String url,@Query("ka6id") String ka6id,
                                                        @Query("token") String token,
                                                        @Query("ypid") String ypid,
                                                        @Query("uid") String uid);
    }
    public MyIndexPresenter() {
        servlet = retrofit.create(MyIndexServlet.class);
    }
    public void init(){
        mView.showLoading();
        servlet.getShopIndex(UrlConstants.BASEURL2+"getIndex",
                login.kai6Id,login.token,login.ypId,login.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<String>>() {
                    @Override
                    public void accept(RetrofitResult<String> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            if(StringUtil.isNull(retrofitResult.getData())){
                                return;
                            }
                            String json =  new String(AbBase64.decode(retrofitResult.getData()));
                            List<DataModel> dataList = GsonUtil.parseJsonWithGsonObject(json, new TypeToken<List<DataModel>>() {
                            }.getType());
                            mView.init(dataList);
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
    public void uploadPicUpdateIndex( List<DataModel> datas){
        mView.showLoading();
        servlet.saveshopindex(UrlConstants.BASEURL2+"saveshopindex",
                login.kai6Id,login.token,login.ypId,login.uid, AbBase64.encode(GsonUtil.objectToJson(datas).getBytes()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            UIUtil.showMessage(context, "提交成功");
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
