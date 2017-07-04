package com.coolwin.XYT.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.coolwin.XYT.Entity.AppDataStatistics;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopIndexItemBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;

import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */

public class ShopIndexFragmentAdapter extends BaseAdapter<AppDataStatistics> {
    public ShopIndexFragmentAdapter(Context context, List<AppDataStatistics> dataStatisticses) {
        super(context);
        this.mList = dataStatisticses;
    }

    @Override
    public int getLayoutId() {
        return R.layout.shop_index_item;
    }

    @Override
    public void bindData(MyRecycleViewHolder holder, int position) {
        AppDataStatistics appDataStatistics = mList.get(position);
        ShopIndexItemBinding binding = (ShopIndexItemBinding) holder.getBinding();
        binding.setKey(appDataStatistics.key);
        binding.setValue(appDataStatistics.value+"");
        if(position%2!=0){
            binding.valuetext.setTextColor(Color.RED);
        }
    }
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position)==ITEM_TYPE_HEADER) {
                        return 5;
                    }
                    if(position%2==0){
                       return 2;
                    }
                    return 3;
                }
            });
        }
    }
}
