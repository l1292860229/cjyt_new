package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.interfaceview.UIShopMemberFragment;
import com.coolwin.XYT.org.json.JSONObject;
import com.coolwin.XYT.util.GsonUtil;
import com.coolwin.XYT.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/7/1.
 */

public class ShopMemberFragmentPresenter extends BasePresenter<UIShopMemberFragment> {
    ShopMemberFragmentServlet servlet;
    public interface ShopMemberFragmentServlet {
        @POST()
        Observable<RetrofitResult<List>> getMemberList(@Url String url, @Query("ypid") String ypid,
                                                                  @Query("token") String token,@Query("page") int page);
    }
    public ShopMemberFragmentPresenter() {
        servlet = retrofit.create(ShopMemberFragmentServlet.class);
    }
    public void getData(int page,final GetDataType type){
        if(type==GetDataType.INIT){
            mView.showLoading();
        }
        servlet.getMemberList(UrlConstants.BASEURL2+"getMemberList",login.ypId,login.token,page)
                .map(new Function<RetrofitResult<List>, RetrofitResult<List<Login>>>() {
                    @Override
                    public RetrofitResult<List<Login>> apply(RetrofitResult<List> listRetrofitResult) throws Exception {
                        List<Login> loginList  = new ArrayList<>();
                        for (int i = 0; i < listRetrofitResult.getData().size(); i++) {
                            loginList.add(new Login(new JSONObject(GsonUtil.objectToJson(listRetrofitResult.getData().get(i)))));
                        }
                        RetrofitResult<List<Login>> refit = new RetrofitResult<>();
                        refit.setData(loginList);
                        refit.setState(listRetrofitResult.getState());
                        return refit;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<Login>>>() {
                    @Override
                    public void accept(RetrofitResult<List<Login>> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            switch (type){
                                case INIT:
                                    mView.hideLoading();
                                    mView.init(retrofitResult.getData());
                                    break;
                                case REFRESH:
                                    mView.refreshSucess(retrofitResult.getData());
                                    break;
                                case LOADMORE:
                                    mView.reloadMoreSucess(retrofitResult.getData());
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
