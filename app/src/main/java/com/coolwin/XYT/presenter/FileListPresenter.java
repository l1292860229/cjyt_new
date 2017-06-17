package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIFileList;
import com.coolwin.XYT.util.UIUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/6/16.
 */

public class FileListPresenter extends BasePresenter<UIFileList> {
    FileListServlet fileListServlet;
    public interface FileListServlet {
        @POST
        Observable<RetrofitResult<List<String>>> getfile(@Url String url);
    }
    public FileListPresenter (){
        fileListServlet  = retrofit.create(FileListServlet.class);
    }
    public void init(){
        mView.showLoading();
        fileListServlet.getfile(UrlConstants.BASEURL2+"downfile")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<List<String>>>() {
                    @Override
                    public void accept(RetrofitResult<List<String>> listRetrofitResult) throws Exception {
                        if (listRetrofitResult.getState().getCode() == 0) {
                            mView.init(listRetrofitResult.getData());
                        } else {
                            UIUtil.showMessage(context, listRetrofitResult.getState().getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        UIUtil.showMessage(context, "请求异常");
                        throwable.printStackTrace();
                        mView.hideLoading();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideLoading();
                    }
                });
    }
}
