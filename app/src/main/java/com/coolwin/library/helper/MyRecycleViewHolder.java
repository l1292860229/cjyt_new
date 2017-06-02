package com.coolwin.library.helper;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/6/2.
 */
/**
 * DataBinding的辅助类
 */
public class MyRecycleViewHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;
    public MyRecycleViewHolder(View itemView) {
        super(itemView);
    }
    public ViewDataBinding getBinding() {
        return binding;
    }
    public void setBinding(ViewDataBinding binding) {
        this.binding = binding;
    }
}
