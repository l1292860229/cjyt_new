package com.coolwin.XYT.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.coolwin.library.helper.MyRecycleViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/2.
 */

public class BaseAdapter<T> extends RecyclerView.Adapter<MyRecycleViewHolder> {
    public List<T> mList;
    public List<T> getData(){
        return mList;
    }
    public void setData(List<T> model) {
        mList = model;
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
        return 0;
    }
}
