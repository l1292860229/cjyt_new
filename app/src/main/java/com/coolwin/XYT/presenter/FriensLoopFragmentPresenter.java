package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.interfaceview.UIFriensLoopFragment;
import com.coolwin.XYT.org.json.JSONArray;
import com.coolwin.XYT.util.GsonUtil;
import com.coolwin.XYT.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dell on 2017/6/23.
 */

public class FriensLoopFragmentPresenter extends BasePresenter<UIFriensLoopFragment> {
    FriensLoopFragmentServlet servlet;
    public interface FriensLoopFragmentServlet {
        @GET("friend/api/shareList")
        Observable<RetrofitResult> getData(@Query("uid") String uid,
                                                                  @Query("page") int page);
    }
    public FriensLoopFragmentPresenter(){
        servlet = retrofit.create(FriensLoopFragmentServlet.class);
    }
    public void getData(int page,final GetDataType type){
        if(type==GetDataType.INIT){
            mView.showLoading();
        }
        servlet.getData(login.uid,page)
                .map(new Function<RetrofitResult, RetrofitResult<List<FriendsLoopItem>>>() {
                    @Override
                    public RetrofitResult<List<FriendsLoopItem>> apply(RetrofitResult mapRetrofitResult) throws Exception {
                        RetrofitResult<List<FriendsLoopItem>> refit = new RetrofitResult<>();
                        JSONArray childJson = new JSONArray(GsonUtil.objectToJson(mapRetrofitResult.getData()));
                        ArrayList<FriendsLoopItem> childList = new ArrayList<>();
                        for (int i = 0; i<childJson.length(); i++) {
                            childList.add(new FriendsLoopItem(childJson.getJSONObject(i)));
                        }
                        refit.setData(childList);
                        refit.setState(mapRetrofitResult.getState());
                        return refit;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<FriendsLoopItem>>>() {
                    @Override
                    public void accept(RetrofitResult<List<FriendsLoopItem>> retrofitResult) throws Exception {
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
                            UIUtil.showMessage(context,retrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(type==GetDataType.INIT){
                            mView.hideLoading();
                        }
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
                        throwable.printStackTrace();
                    }
                });
    }
}
