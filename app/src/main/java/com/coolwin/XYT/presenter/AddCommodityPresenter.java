package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIAddCommodity;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

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
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/6/2.
 */

public class AddCommodityPresenter extends BasePresenter<UIAddCommodity> {
    AddCommodityServlet servlet;
    public interface AddCommodityServlet {
        @Multipart
        @POST()
        Observable<RetrofitResult<String>> savecommodity(@Url String url, @Part("ka6id") RequestBody ka6id,
                                                         @Part("token")  RequestBody  token,
                                                         @Part("ypid")  RequestBody  ypid,
                                                         @Part("uid")  RequestBody  uid,
                                                         @Part("title")  RequestBody  title,
                                                         @Part("price")  RequestBody  price,
                                                         @Part("content")  RequestBody  content,
                                                         @Part List<MultipartBody.Part> pic,
                                                         @Part MultipartBody.Part video,
                                                         @Part MultipartBody.Part videoimage,
                                                         @Part("videotime")  RequestBody videotime);
    }
    public AddCommodityPresenter(){
        servlet = retrofit.create(AddCommodityServlet.class);
    }
    public void upload(String commodityTitle,String commodityPrice,String commodityDescription,
                       List<String> imagepath,String video,String videoimage,String videotime){
        List<MultipartBody.Part> stringPartList = new ArrayList<>();
        for (int i = 0; i < imagepath.size(); i++) {
            if(StringUtil.isNull(imagepath.get(i))){
                continue;
            }
            stringPartList.add(getMultipartBodyFilePathPart(imagepath.get(i),"picture"+i));
        }
        MultipartBody.Part videoPart=null;
        if(!StringUtil.isNull(video)){
            videoPart = getMultipartBodyFilePathPart(video,"video");
        }
        MultipartBody.Part videoImagePart=null;
        if(!StringUtil.isNull(videoimage)){
            videoImagePart =  getMultipartBodyFilePathPart(videoimage,"videoimage");
        }
        RequestBody videoTime=null;
        if(!StringUtil.isNull(videoimage)){
            videoTime =  getMultipartRequestBodyPart(videotime);
        }
        mView.showLoading();
        servlet.savecommodity(UrlConstants.BASEURL2+"savecommodity",
                getMultipartRequestBodyPart(login.kai6Id),
                getMultipartRequestBodyPart(login.token),
                getMultipartRequestBodyPart(login.ypId),
                getMultipartRequestBodyPart(login.uid),
                getMultipartRequestBodyPart(commodityTitle),
                getMultipartRequestBodyPart(commodityPrice),
                getMultipartRequestBodyPart(commodityDescription),
                stringPartList,videoPart,
                videoImagePart,videoTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<String>>() {
                    @Override
                    public void accept(RetrofitResult<String> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            UIUtil.showMessage(context, "发布成功");
                            mView.savesuccess();
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
