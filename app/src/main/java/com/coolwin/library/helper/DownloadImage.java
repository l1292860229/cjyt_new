package com.coolwin.library.helper;

/**
 * Created by Administrator on 2017/6/2.
 */

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import com.coolwin.XYT.Entity.constant.StaticData;
import com.coolwin.XYT.util.StringUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 这个是为了获取图片的尺寸
 */
public class DownloadImage extends AsyncTask<String, Void, BitmapFactory.Options> {
    WeakReference<SimpleDraweeView> imageViewWeakReference;
    public int widthPixels;
    public float screenRatio;
    public DownloadImage(SimpleDraweeView imageView1,int widthPixels,float screenRatio) {
        imageViewWeakReference = new WeakReference<>(imageView1);
        this.widthPixels = widthPixels;
        this.screenRatio = screenRatio;
    }
    @Override
    protected void onPostExecute(BitmapFactory.Options options) {
        super.onPostExecute(options);
        if(imageViewWeakReference!=null){
            SimpleDraweeView imageView1 = imageViewWeakReference.get();
            if(imageView1 != null && options != null){
                int width = (int)(widthPixels*screenRatio);
                int height =  (int)(1.0 *width / options.outWidth * options.outHeight);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
                layoutParams.width  = width;
                layoutParams.height = height;
                imageView1.setLayoutParams(layoutParams);
            }
        }
    }
    @Override
    protected BitmapFactory.Options doInBackground(String... params) {
        //判断参数是否为空,
        if (StringUtil.isNull(params[0])) {
            return null;
        }else if(!params[0].contains("http")){//是否为本地图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            StaticData.imageViewOptions.put(params[0],options);
            BitmapFactory.decodeFile(params[0],options);
            return options;
        }
        URL url = null;
        try {
            url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                StaticData.imageViewOptions.put(params[0],options);
                return options;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
