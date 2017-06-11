package com.coolwin.library.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.holder.Holder;
import com.coolwin.XYT.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

/**
 * Created by dell on 2017/6/7.
 */

public class LocalImageHolderView implements Holder<Integer> {
    LinearLayout linearLayout;
    private  int widthPixels;
    public LocalImageHolderView(int widthPixels){
        this.widthPixels = widthPixels-100;
    }
    @Override
    public View createView(Context context) {
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        return linearLayout;
    }
    @Override
    public void UpdateUI(Context context, final int position, Integer data) {
        for (int i = 0; i < data; i++) {
            SimpleDraweeView view = new SimpleDraweeView(context);
            int width = (int)(widthPixels*(1f/data));
            view.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(width*0.5f)));
            Phoenix.with(view)
                    .load(R.drawable.smiley_add_btn);
            linearLayout.addView(view);
        }
    }
}
