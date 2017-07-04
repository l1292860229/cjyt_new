package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.interfaceview.UIBbsListFragment;
import com.coolwin.XYT.util.UIUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dell on 2017/6/15.
 */

public class BbsListFragmentPresenter extends BasePresenter<UIBbsListFragment> {
    BbsListFragmentServlet bbsListFragmentServlet;
    public interface BbsListFragmentServlet {
        @GET("Bbs/api/bbslist")
        Observable<RetrofitResult<List<Bbs>>> getData(@Query("uid") String uid,
                                                     @Query("quid") String quid,
                                                     @Query("type") String type,
                                                     @Query("title") String title,
                                                     @Query("page") int page);
    }
    public BbsListFragmentPresenter(){
        bbsListFragmentServlet = retrofit.create(BbsListFragmentServlet.class);
    }
    public void getData(int page, final GetDataType type){
        if(type==GetDataType.INIT){
            mView.showLoading();
        }
        bbsListFragmentServlet.getData(login.uid,login.phone,"1",null,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<Bbs>>>() {
                    @Override
                    public void accept(RetrofitResult<List<Bbs>> retrofitResult) throws Exception {
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
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
                        throwable.printStackTrace();
                    }
                });
    }
    public void searchData(String title){
        bbsListFragmentServlet.getData(login.uid,login.phone,"1",title,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<Bbs>>>() {
                    @Override
                    public void accept(RetrofitResult<List<Bbs>> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            mView.searchData(retrofitResult.getData());
                        }else{
                            UIUtil.showMessage(context,retrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
                        throwable.printStackTrace();
                    }
                });
    }
}
