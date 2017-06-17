package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    // 头部控件
    private View mHeaderView;
    // 底部控件
    private View mFooterView;
    private boolean isHasHeader = false;
    private boolean isHasFooter = false;
    // item 的三种类型
    public static final int ITEM_TYPE_NORMAL = 0X1111; // 正常的item类型
    public static final int ITEM_TYPE_HEADER = 0X1112; // header
    public static final int ITEM_TYPE_FOOTER = 0X1113; // footer
    public AdapterView.OnItemClickListener mItemClickListener;
    /**
     * 添加头部视图
     * @param header
     */
    public void setHeaderView(View header){
        this.mHeaderView = header;
        isHasHeader = true;
        notifyDataSetChanged();
    }
    /**
     * 添加底部视图
     * @param footer
     */
    public void setFooterView(View footer){
        this.mFooterView = footer;
        isHasFooter = true;
        notifyDataSetChanged();
    }

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
        if(viewType==ITEM_TYPE_FOOTER){
            // 如果是底部类型，返回底部视图
            return new MyRecycleViewHolder(mFooterView);
        }

        if(viewType==ITEM_TYPE_HEADER){
            return new MyRecycleViewHolder(mHeaderView);
        }
        ViewDataBinding dataBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(context), getLayoutId(), parent, false);
        MyRecycleViewHolder  vh = new MyRecycleViewHolder(dataBinding.getRoot());
        vh.setBinding(dataBinding);
        return vh;
    }
    /**
     *  绑定数据
     * @param holder  具体的viewHolder
     * @param position  对应的索引
     */
    protected  void bindData(MyRecycleViewHolder holder, int position){

    }
    /**
     * 获取子item
     * @return
     */
    public  int getLayoutId(){
        return 0;
    }
    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        if(isHasHeader&&isHasFooter){
            // 有头布局和底部时，向前偏移一个，且最后一个不能绑定数据
            if(position==0 ||position==mList.size()+1){
                return;
            }
            bindData(holder,position-1);
        }
        if(position!=0&&isHasHeader&&!isHasFooter){
            // 有顶部，但没有底部
            bindData(holder,position-1);
        }
        if(!isHasHeader&&isHasFooter){
            // 没有顶部，但有底部
            if(position==mList.size()){
                return;
            }
            bindData(holder,position);
        }
        if(!isHasHeader&&!isHasFooter){
            // 没有顶部，没有底部
            bindData(holder,position);
        }
    }
    @Override
    public int getItemViewType(int position) {
        // 根据索引获取当前View的类型，以达到复用的目的
        // 根据位置的索引，获取当前position的类型
        if(isHasHeader&&position==0){
            return ITEM_TYPE_HEADER;
        }
        if(isHasHeader&&isHasFooter&&position==mList.size()+1){
            // 有头部和底部时，最后底部的应该等于size+!
            return ITEM_TYPE_FOOTER;
        }else if(!isHasHeader&&isHasFooter&&position==mList.size()){
            // 没有头部，有底部，底部索引为size
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener){
        this.mItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        int size=0;
        if(mList!=null)
            size =  mList.size();
        if(isHasFooter)
            size ++;
        if(isHasHeader)
            size++;
        return size;
    }
}
