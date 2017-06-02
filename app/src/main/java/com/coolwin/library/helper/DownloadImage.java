package com.coolwin.library.helper;

/**
 * Created by Administrator on 2017/6/2.
 */

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.util.StringUtil;

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
    WeakReference<ImageView> imageViewWeakReference;
    public int widthPixels;
    public int type;
    public DownloadImage(ImageView imageView1,int widthPixels,int type) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView1);
        this.widthPixels = widthPixels;
        this.type = type;
    }
    @Override
    protected void onPostExecute(BitmapFactory.Options options) {
        super.onPostExecute(options);
        if(imageViewWeakReference!=null){
            ImageView imageView1 = imageViewWeakReference.get();
            if(imageView1 != null && options != null){
                int width = (int)(widthPixels*(type*1.0/ DataModel.TYPE_ONETOONE));
                int height =  (int)(1.0 *width / options.outWidth * options.outHeight);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView1.getLayoutParams();
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
