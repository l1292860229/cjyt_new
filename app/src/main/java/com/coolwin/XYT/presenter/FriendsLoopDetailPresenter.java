package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.CommentUser;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.interfaceview.UIFriendsLoopDetail;
import com.coolwin.XYT.util.UIUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dell on 2017/6/26.
 */

public class FriendsLoopDetailPresenter extends BasePresenter<UIFriendsLoopDetail> {
    FriendsLoopDetailServlet servlet;
    public interface FriendsLoopDetailServlet {
        @GET("friend/api/sharePraise")
        Observable<RetrofitResult> sharePraise(@Query("uid") String uid,
                                           @Query("fsid") int fsid);
        @GET("friend/api/shareReply")
        Observable<RetrofitResult> shareReply(@Query("uid") String uid,
                                               @Query("content") String content,
                                              @Query("fsid") int fsid,
                                              @Query("fuid") String fuid);
    }
    public FriendsLoopDetailPresenter(){
        servlet = retrofit.create(FriendsLoopDetailServlet.class);
    }
    public void sharePraise(int fsid){
        mView.showLoading();
        servlet.sharePraise(login.uid,fsid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            mView.sharePraise(login.nickname);
                        }else{
                            UIUtil.showMessage(context,retrofitResult.getState().getMsg());
                        }
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
                        throwable.printStackTrace();
                        mView.hideLoading();
                    }
                });
    }
    public void shareReply(final String content, final int fsid, final String fname, final String fuid){
        mView.showLoading();
        servlet.shareReply(login.uid,content,fsid,fuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult>() {
                    @Override
                    public void accept(RetrofitResult retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            CommentUser commentUser = new CommentUser();
                            commentUser.headsmall = login.headsmall;
                            commentUser.nickname = login.nickname;
                            commentUser.fuid = fuid;
                            commentUser.content = content;
                            commentUser.fnickname = fname;
                            commentUser.createtime = System.currentTimeMillis()/1000;
                            mView.shareReply(commentUser);
                        }else{
                            UIUtil.showMessage(context,retrofitResult.getState().getMsg());
                        }
                        mView.hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常,请稍后重试");
                        throwable.printStackTrace();
                        mView.hideLoading();
                    }
                });
    }
}
