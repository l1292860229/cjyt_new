package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopIndexBinding;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.library.helper.DownloadImage;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyIndexAdapter extends BaseAdapter<DataModel>{
    public Context context;
    public int widthPixels;
    private AdapterView.OnItemClickListener mOnItemClickListener = null;
    public MyIndexAdapter(Context context, List<DataModel> mList, int widthPixels) {
        this.context = context;
        this.mList = mList;
        this.widthPixels = widthPixels;
    }
    public List<DataModel> getData(){
        return mList;
    }
    public void setData(List<DataModel> model) {
        mList = model;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ShopIndexBinding oneBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(parent.getContext()), R.layout.shop_index, parent, false);
        MyRecycleViewHolder vh = new MyRecycleViewHolder(oneBinding.getRoot());
        vh.setBinding(oneBinding);
        return vh;
    }
    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, final int position) {
        ViewDataBinding dataBinding = holder.getBinding();
        DataModel dataModel =  mList.get(position);
        //为图片控件设置单击事件
        ShopIndexBinding oneBinding = (ShopIndexBinding) dataBinding;
        oneBinding.pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(null,v,position,0);
            }
        });
        //判断是否是第一次添加图片,否就给默认宽高
        if(StringUtil.isNull(dataModel.imagepath)){
            int width = (int)(widthPixels*(dataModel.type*1.0/DataModel.TYPE_ONETOONE));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) oneBinding.pic1.getLayoutParams();
            layoutParams.height = (int)(width*0.5f);
            layoutParams.width  = width;
            oneBinding.pic1.setLayoutParams(layoutParams);
            oneBinding.pic1.setImageResource(R.drawable.smiley_add_btn);
        }else{
            DownloadImage downloadImage = new DownloadImage(oneBinding.pic1,widthPixels,dataModel.type);
            downloadImage.execute(dataModel.imagepath);
            Phoenix.with(oneBinding.pic1)
                    .load(dataModel.imagepath);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return mList.get(position).type;
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            //跟据不同的图类型返回不同的列占比
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position);
                }
            });
        }
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}