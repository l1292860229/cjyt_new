package com.coolwin.XYT.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigkoo.convenientbanner.holder.Holder;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.constant.StaticData;
import com.coolwin.XYT.R;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.library.helper.DownloadImage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

/**
 * Created by dell on 2017/6/7.
 */

public class NetworkImageHolderView implements Holder<DataModel.BannerList> {
    LinearLayout linearLayout;
    private  int widthPixels;
    public View mView;
    public NetworkImageHolderView(View mView,int widthPixels){
        this.mView = mView;
        this.widthPixels = widthPixels;
    }
    @Override
    public View createView(Context context) {
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }
    @Override
    public void UpdateUI(Context context, final int position, DataModel.BannerList data) {
        SimpleDraweeView view = new SimpleDraweeView(context);
        if(StringUtil.isNull(data.imgUrl)){
            int width = widthPixels;
            view.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(width*0.5f)));
            Phoenix.with(view)
                    .load(R.drawable.smiley_add_btn);
            linearLayout.addView(view);
            return;
        }
        if(StaticData.imageViewOptions.containsKey(data.imgUrl)){
            BitmapFactory.Options options = StaticData.imageViewOptions.get(data.imgUrl);
            int width = widthPixels;
            int height =  (int)(1.0 *width / options.outWidth * options.outHeight);
            if (mView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mView.getLayoutParams();
                if(layoutParams!=null){
                    if(layoutParams.height<height){
                        mView.setLayoutParams(new LinearLayout.LayoutParams(width,height));
                    }
                }
                view.setLayoutParams(new LinearLayout.LayoutParams(width,height));
            }else if(mView.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                if(layoutParams!=null){
                    if(layoutParams.height<height){
                        mView.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
                    }
                }
                view.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
            }
        }else{
            //否则就重新请求
            DownloadImage downloadImage = new DownloadImage(view,widthPixels,1.0f);
            downloadImage.execute(data.imgUrl);
        }
        //加载图片
        Phoenix.with(view)
                .load(data.imgUrl);
        linearLayout.addView(view);
    }
}
