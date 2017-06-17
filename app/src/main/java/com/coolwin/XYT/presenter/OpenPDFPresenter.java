package com.coolwin.XYT.presenter;

import android.os.Environment;

import com.coolwin.XYT.Entity.constant.UrlConstants;
import com.coolwin.XYT.download.DownloadAPI;
import com.coolwin.XYT.interfaceview.UIOpenPDF;
import com.coolwin.XYT.util.UIUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by dell on 2017/6/17.
 */

public class OpenPDFPresenter extends BasePresenter<UIOpenPDF> {
    OpenPDFServlet servlet;
    public interface OpenPDFServlet {
        @POST
        Observable<String> toPDF(@Url String url, @Query("filename") String filename);
    }
    public OpenPDFPresenter(){
        servlet = retrofit.create(OpenPDFServlet.class);
    }
    public void init(String str){
        String filename = str.substring(str.lastIndexOf(".")+1);
        String name = str.substring(str.lastIndexOf("/")+1,str.lastIndexOf("."));
        final File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), name+".pdf");
        if (outputFile.exists()) {
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile));
//            shareIntent.setType("*/*");
//            context.startActivity(Intent.createChooser(shareIntent, "分享到"));
            mView.init(outputFile.getAbsolutePath());
            return;
        }
        if(filename.equals("pdf")){
            mView.showLoading();
            showpdf(str);
        }else{
            mView.showLoading();
            servlet.toPDF(UrlConstants.BASEURL2+"tohtml",str.substring(str.lastIndexOf("/")+1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String retrofitResult) throws Exception {
                            showpdf(retrofitResult);
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
    public void showpdf(String str){
        String name = str.substring(str.lastIndexOf("/")+1);
        final File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), name);
        new DownloadAPI(UrlConstants.BASEURL,null).downloadAPK(str, outputFile, new Action() {
            @Override
            public void run() throws Exception {
                mView.hideLoading();
                downloadCompleted(outputFile);
            }
        });
    }
    private void downloadCompleted(File outputFile) {
        mView.init(outputFile.getAbsolutePath());
    }
}
