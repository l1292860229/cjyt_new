package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.MyInformation;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.interfaceview.UIInformation;
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

public class InformationPresenter extends BasePresenter<UIInformation> {
    public interface CertificationServlet {
        @POST
        Observable<RetrofitResult<List<MyInformation>>> getInfo(@Url String url,
                                                                @Query("ka6id") String ka6id, @Query("token") String token,
                                                                @Query("ypid") String ypid, @Query("uid") String uid,
                                                                @Query("page") String page,@Query("type") String type);
        @POST
        Observable<RetrofitResult> delete(@Url String url,@Query("id") String id,
                                                              @Query("ka6id") String ka6id, @Query("token") String token,
                                                                @Query("ypid") String ypid,@Query("uid") String uid,@Query("type") String type);
    }
    CertificationServlet servlet;
    public InformationPresenter(){
        servlet = retrofit.create(CertificationServlet.class);
    }
    public void getData(int page, final GetDataType type, String typevalue){
        servlet.getInfo(UrlConstants.BASEURL2+"getinformation",login.kai6Id,login.token,login.ypId,login.uid,page+"",typevalue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<MyInformation>>>() {
                    @Override
                    public void accept(RetrofitResult<List<MyInformation>> listRetrofitResult) throws Exception {
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
    public void deleteData(String id,String type){
        servlet.delete(UrlConstants.BASEURL2+"delinformation",id,login.kai6Id,login.token,login.ypId,login.uid,type)
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
