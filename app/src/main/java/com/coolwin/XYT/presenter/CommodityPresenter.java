package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UICommodity;
import com.coolwin.XYT.util.UIUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/6/1.
 */

public class CommodityPresenter extends BasePresenter<UICommodity> {
    public static final int INIT = 0;
    public static final int REFRESH = 1;
    public static final int LOADMORE = 2;
    public interface CertificationServlet {
        @POST
        Observable<RetrofitResult<List<MyCommodity>>> getInfo(@Url String url,
                                                             @Query("ka6id") String ka6id, @Query("token") String token, @Query("ypid") String ypid,
                                                             @Query("uid") String uid, @Query("page") String page);
        @POST
        Observable<RetrofitResult> delete(@Url String url,@Query("id") String id,
                                                              @Query("ka6id") String ka6id, @Query("token") String token, @Query("ypid") String ypid,
                                                              @Query("uid") String uid);
    }
    CertificationServlet servlet;
    public CommodityPresenter(){
        servlet = retrofit.create(CertificationServlet.class);
    }
    public void getData(int page, final int type){
        servlet.getInfo(UrlConstants.BASEURL2+"getcommodity",login.kai6Id,login.token,login.ypId,login.uid,page+"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<MyCommodity>>>() {
                    @Override
                    public void accept(RetrofitResult<List<MyCommodity>> listRetrofitResult) throws Exception {
                        if (listRetrofitResult.getState().getCode()==0) {
                            switch (type){
                                case INIT:
                                    mView.init(listRetrofitResult.getData());
                                    break;
                                case REFRESH:
                                    mView.refreshSuccess(listRetrofitResult.getData());
                                    break;
                                case LOADMORE:
                                    mView.loadMoreSucess(listRetrofitResult.getData());
                                    break;
                            }
                        }else{
                            UIUtil.showMessage(context,listRetrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常");
                        throwable.printStackTrace();
                    }
                });
    }
    public void deleteData(String id){
        servlet.delete(UrlConstants.BASEURL2+"delcommodity",id,login.kai6Id,login.token,login.ypId,login.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult listRetrofitResult) throws Exception {
                        if (listRetrofitResult.getState().getCode()==0) {
                            UIUtil.showMessage(context,"删除成功");
                        }else{
                            UIUtil.showMessage(context,listRetrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常");
                        throwable.printStackTrace();
                    }
                });
    }
}
