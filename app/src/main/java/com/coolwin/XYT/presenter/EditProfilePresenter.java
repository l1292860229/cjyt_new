package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.interfaceview.UIEditProfile;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;

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
 * Created by dell on 2017/6/13.
 */

public class EditProfilePresenter extends BasePresenter<UIEditProfile> {
    EditProfileServlet servlet;
    public interface EditProfileServlet {
        @Multipart
        @POST("user/api/edit")
        Observable<RetrofitResult<Login>> updateinfo(@Part("headsmall") RequestBody headsmall,
                                              @Part("uid") RequestBody  uid,
                                              @Part("nickname") RequestBody  nickname,
                                              @Part("gender") RequestBody  gender,
                                              @Part("sign") RequestBody  sign,
                                              @Part("province") RequestBody  province,
                                              @Part("city") RequestBody  city,
                                              @Part("companywebsite") RequestBody  companywebsite,
                                              @Part("industry") RequestBody  industry,
                                              @Part("company") RequestBody  company,
                                              @Part("companyaddress") RequestBody  companyaddress,
                                              @Part("job") RequestBody  job,
                                              @Part("provide") RequestBody  provide,
                                              @Part("demand") RequestBody  demand,
                                              @Part("telephone") RequestBody  telephone,
                                              @Part("token") RequestBody  token,
                                              @Part("id") RequestBody  id,
                                              @Part MultipartBody.Part pic);
    }
    public EditProfilePresenter(){
        servlet = retrofit.create(EditProfileServlet.class);
    }

    public void  editProfile(final Login mLogin, final String image){
        MultipartBody.Part imagepath=null;
        if(!StringUtil.isNull(image)){
            imagepath = getMultipartBodyFilePathPart(image,"picture");
        }
        mView.showLoading();
        servlet.updateinfo(getMultipartRequestBodyPart(mLogin.headsmall),
                getMultipartRequestBodyPart(mLogin.uid),
                getMultipartRequestBodyPart(mLogin.nickname),
                getMultipartRequestBodyPart(mLogin.gender==2?"男":"女"),
                getMultipartRequestBodyPart(mLogin.sign),
                getMultipartRequestBodyPart(mLogin.provinceid),
                getMultipartRequestBodyPart(mLogin.cityid),
                getMultipartRequestBodyPart(mLogin.companywebsite),
                getMultipartRequestBodyPart(mLogin.industry),
                getMultipartRequestBodyPart(mLogin.company),
                getMultipartRequestBodyPart(mLogin.companyaddress),
                getMultipartRequestBodyPart(mLogin.job),
                getMultipartRequestBodyPart(mLogin.provide),
                getMultipartRequestBodyPart(mLogin.demand),
                getMultipartRequestBodyPart(mLogin.telephone),
                getMultipartRequestBodyPart(mLogin.token),
                getMultipartRequestBodyPart(mLogin.ypId),imagepath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<Login>>() {
                    @Override
                    public void accept(RetrofitResult<Login> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            UIUtil.showMessage(context, "修改成功");
                            mLogin.headsmall = retrofitResult.getData().headsmall;
                            mLogin.headlarge = retrofitResult.getData().headlarge;
                            GetDataUtil.saveLogin(context,mLogin);
                            context.finish();
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
