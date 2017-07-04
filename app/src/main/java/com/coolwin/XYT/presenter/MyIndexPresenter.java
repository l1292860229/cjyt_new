package com.coolwin.XYT.presenter;

import com.ab.util.AbBase64;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.RetrofitResult;
import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.util.GsonUtil;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/6/1.
 */

public class MyIndexPresenter extends BasePresenter<UIMyIndex> {
    MyIndexServlet servlet;
    public interface MyIndexServlet {
        @POST()
        Observable<RetrofitResult> saveshopindex(@Url String url,@Query("ka6id") String ka6id,
                                                 @Query("token") String token,
                                                 @Query("ypid") String ypid,
                                                 @Query("uid") String uid,
                                                 @Query("data") String data);
        @POST()
        Observable<RetrofitResult<String>> getShopIndex(@Url String url,@Query("ka6id") String ka6id,
                                                        @Query("token") String token,
                                                        @Query("ypid") String ypid,
                                                        @Query("uid") String uid);
        @Multipart
        @POST()
        Observable<Map<String,String>> uploadpic(@Url String url, @Part MultipartBody.Part picture);
    }
    public MyIndexPresenter() {
        servlet = retrofit.create(MyIndexServlet.class);
    }
    public void init(){
        mView.showLoading();
        servlet.getShopIndex(UrlConstants.BASEURL2+"getIndex",
                login.kai6Id,login.token,login.ypId,login.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RetrofitResult<String>>() {
                    @Override
                    public void accept(RetrofitResult<String> retrofitResult) throws Exception {
                        if(retrofitResult.getState().getCode()==0){
                            if(StringUtil.isNull(retrofitResult.getData())){
                                return;
                            }
                            String json =  new String(AbBase64.decode(retrofitResult.getData()));
                            List<DataModel> dataList = GsonUtil.parseJsonWithGsonObject(json, new TypeToken<List<DataModel>>() {
                            }.getType());
                            mView.init(dataList);
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
    public void uploadPicUpdateIndex(final List<DataModel> datas){
        mView.showLoading();
        Observable.fromArray(datas)
                // datas -> 单个DataModel
          .flatMap(new Function<List<DataModel>, ObservableSource<DataModel>>() {
              @Override
              public ObservableSource<DataModel> apply(List<DataModel> dataModels) throws Exception {
                  return Observable.fromIterable(dataModels);
              }
              // 单个DataModel ->List<DataModel.Data>
          }).flatMap(new Function<DataModel, ObservableSource<DataModel>>() {
            @Override
            public ObservableSource<DataModel> apply(DataModel dataModel) throws Exception {
                if(dataModel.bannerList!=null && dataModel.bannerList.size()>0){
                    return Observable.fromArray(dataModel.bannerList)
                            .flatMap(new Function<List<DataModel.BannerList>, ObservableSource<DataModel.BannerList>>() {
                                @Override
                                public ObservableSource<DataModel.BannerList> apply(List<DataModel.BannerList> bannerLists) throws Exception {
                                    return Observable.fromIterable(bannerLists);
                                }
                            }).map(new Function<DataModel.BannerList, String>() {
                                @Override
                                public String apply(DataModel.BannerList bannerList) throws Exception {
                                    return bannerList.imgUrl;
                                }
                            }).filter(new Predicate<String>() {
                                @Override
                                public boolean test(String s) throws Exception {
                                    if(s.contains("http")){
                                        return false;
                                    }else{
                                        return true;
                                    }
                                }
                        }).flatMap(new Function<String, ObservableSource<Map<String,String>>>() {
                        @Override
                        public ObservableSource<Map<String,String>> apply(String s) throws Exception {
                            return servlet.uploadpic(UrlConstants.BASEURL2+"upload",getMultipartBodyFilePathPart(s,"picture"));
                        }
                    }).map(new Function<Map<String,String>, String>() {
                        @Override
                        public String apply(Map<String,String> stringRetrofitResult) throws Exception {
                            return stringRetrofitResult.get("originUrl");
                        }
                        //本地图片替换成网络
                    }).toList().flatMapObservable(new Function<List<String>, ObservableSource<DataModel>>() {
                        @Override
                        public ObservableSource<DataModel> apply(List<String> strings) throws Exception {
                            int picindex=0;
                            for (int i = 0; i < datas.size(); i++) {
                                if( datas.get(i).bannerList==null){
                                    continue;
                                }
                                for (int i1 = 0; i1 < datas.get(i).bannerList.size(); i1++) {
                                    if (!datas.get(i).bannerList.get(i1).imgUrl.contains("http")) {
                                        if(picindex<strings.size()){
                                            datas.get(i).bannerList.get(i1).imgUrl = strings.get(picindex++);
                                        }
                                    }
                                }
                            }
                            return Observable.fromIterable(datas);
                        }
                    });
                }else{
                    return Observable.fromArray(dataModel);
                }

            }
        }).filter(new Predicate<DataModel>() {
            @Override
            public boolean test(DataModel dataModel) throws Exception {
                if (dataModel.datas==null) {
                    return false;
                }else{
                    return true;
                }
            }
        }).map(new Function<DataModel, List<DataModel.Data>>() {
            @Override
            public List<DataModel.Data> apply(DataModel dataModel) throws Exception {
                return dataModel.datas;
            }
            //List<DataModel.Data>-> 单个DataModel.Data
        }).flatMap(new Function<List<DataModel.Data>, ObservableSource<DataModel.Data>>() {
            @Override
            public ObservableSource<DataModel.Data> apply(List<DataModel.Data> datas) throws Exception {
                return Observable.fromIterable(datas);
            }
            // 单个DataModel.Data->String
        }).map(new Function<DataModel.Data, String>() {
            @Override
            public String apply(DataModel.Data data) throws Exception {
                return data.shopImageUrl;
            }
            //过滤掉http
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                if(s.contains("http")){
                    return false;
                }else{
                    return true;
                }
            }
            //上传图片
        }).flatMap(new Function<String, ObservableSource<Map<String,String>>>() {
                    @Override
                    public ObservableSource<Map<String,String>> apply(String s) throws Exception {
                        return servlet.uploadpic(UrlConstants.BASEURL2+"upload",getMultipartBodyFilePathPart(s,"picture"));
                    }
                }).map(new Function<Map<String,String>, String>() {
             @Override
             public String apply(Map<String,String> stringRetrofitResult) throws Exception {
                 return stringRetrofitResult.get("originUrl");
             }
             //本地图片替换成网络
         }).toList().flatMapObservable(new Function<List<String>, ObservableSource<RetrofitResult>>() {
            @Override
            public ObservableSource<RetrofitResult> apply(List<String> strings) throws Exception {
                int picindex=0;
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).datas==null) {
                        continue;
                    }
                    for (int i1 = 0; i1 < datas.get(i).datas.size(); i1++) {
                        if (!datas.get(i).datas.get(i1).shopImageUrl.contains("http")) {
                            if(picindex<strings.size()){
                                datas.get(i).datas.get(i1).shopImageUrl = strings.get(picindex++);
                            }
                        }
                    }
                }
                //保存
                return servlet.saveshopindex(UrlConstants.BASEURL2+"saveshopindex",
                        login.kai6Id,login.token,login.ypId,login.uid, AbBase64.encode(GsonUtil.objectToJson(datas).getBytes()));
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<RetrofitResult>() {
            @Override
            public void accept(RetrofitResult retrofitResult) throws Exception {
                if(retrofitResult.getState().getCode()==0){
                    UIUtil.showMessage(context, "提交成功");
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
