package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.coolwin.XYT.Entity.Menu;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopMenuItemBinding;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

/**
 * Created by dell on 2017/7/3.
 */

public class ShopMenuAdapter extends BaseAdapter<Menu> {
    public ShopMenuAdapter(Context context, List<Menu> orders) {
        super(context);
        this.mList = orders;
    }

    @Override
    public int getLayoutId() {
        return R.layout.shop_menu_item;
    }

    @Override
    protected void bindData(MyRecycleViewHolder holder, int position) {
        final Menu menu = mList.get(position);
        final ShopMenuItemBinding binding = (ShopMenuItemBinding) holder.getBinding();
        binding.setName(menu.getTitle());
        Phoenix.with(binding.ivIndex)
                .load(menu.getImageurl());
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(WebViewActivity.WEBURL,menu.getUrl());
                intent.setClass(context, WebViewActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
