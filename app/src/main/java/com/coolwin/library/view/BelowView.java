package com.coolwin.library.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by dell on 2017/6/22.
 * 这个是弹出框
 */

public class BelowView {
    private View convertView;
    private Context context;
    private PopupWindow pw;
    private int animationStyle;

    public BelowView(Context c, View convertView) {
        this.context = c;
        this.convertView = convertView;
    }

    public BelowView(Context c, int resource) {
        this.context = c;
        this.convertView = View.inflate(c, resource, (ViewGroup)null);
    }

    public void showBelowView(View view, boolean CanceledOnTouchOutside, int xoff, int yoff) {
        this.pw = new PopupWindow(this.convertView, -2, -2, true);
        this.pw.setOutsideTouchable(CanceledOnTouchOutside);
        if(this.animationStyle != 0) {
            this.pw.setAnimationStyle(this.animationStyle);
        }

        this.pw.setBackgroundDrawable(new ColorDrawable(0));
        this.pw.showAsDropDown(view, xoff, yoff);
    }

    public void setAnimation(int animationStyle) {
        this.animationStyle = animationStyle;
    }

    public View getBelowView() {
        return this.convertView;
    }

    public void dismissBelowView() {
        if(this.pw != null) {
            this.pw.dismiss();
        }

    }
}
