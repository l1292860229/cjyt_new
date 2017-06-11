package com.coolwin.XYT.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.coolwin.XYT.R;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.library.helper.DownloadImage;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

/**
 * Created by Administrator on 2017/6/2.
 */

public  class BaseAdapter<T> extends RecyclerView.Adapter<MyRecycleViewHolder> {
    public Context context;
    public List<T> mList;
    public int widthPixels;
    public BaseAdapter(Context context){
        this.context = context;
    }
    public List<T> getData(){
        return mList;
    }
    public void setData(List<T> model) {
        mList = model;
    }
    public void setImage(String imagepath, SimpleDraweeView simpleDraweeView, float screenRatio){
        if(StringUtil.isNull(imagepath)){
            int width = (int)(widthPixels*screenRatio);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) simpleDraweeView.getLayoutParams();
            layoutParams.height = (int)(width*0.5f);
            layoutParams.width  = width;
            simpleDraweeView.setLayoutParams(layoutParams);
            simpleDraweeView.setImageResource(R.drawable.smiley_add_btn);
        }else{
            DownloadImage downloadImage = new DownloadImage(simpleDraweeView,widthPixels,screenRatio);
            downloadImage.execute(imagepath);
            Phoenix.with(simpleDraweeView)
                    .load(imagepath);
        }
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(mList!=null){
            return mList.size();
        }
        return 0;
    }
}
