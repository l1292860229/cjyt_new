package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.fragment.ProfileFragment;
import com.coolwin.XYT.interfaceview.UICertification;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.UIUtil;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by dell on 2017/5/24.
 */

public class CertificationPresenter extends BasePresenter<UICertification>{
    public interface CertificationServlet {
        @Multipart
        @POST("user/api/userattest")
        Observable<RetrofitResult<Map<String,String>>> uploadUserAttext(@Part("uid") RequestBody uid,
                                                                        @Part MultipartBody.Part businesscard,
                                                                        @Part MultipartBody.Part signboard,
                                                                        @Part MultipartBody.Part idcard);
    }
    CertificationServlet service;
    public CertificationPresenter() {
        service = retrofit.create(CertificationServlet.class);
    }
    public void uploadUserAttext(String picture1Path, String picture2Path, String  picture3Path){
        mView.showLoading();
        service.uploadUserAttext(getMultipartRequestBodyPart(login.uid)
                ,getMultipartBodyFilePathPart(picture1Path,"picture1"),
                getMultipartBodyFilePathPart(picture2Path,"picture2"),
                getMultipartBodyFilePathPart(picture3Path,"picture3"))
                .doOnNext(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            login.isattest = ProfileFragment.ATTEST_ING;
                            GetDataUtil.saveLogin(context,login);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            mView.uploadsuccess();
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
