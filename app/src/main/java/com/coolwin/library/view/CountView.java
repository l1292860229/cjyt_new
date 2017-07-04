package com.coolwin.library.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by dell on 2017/7/1.
 * 数字会增长到真实数据的动画的控件
 */

public class CountView extends android.support.v7.widget.AppCompatTextView{
    //动画时长 ms
    int duration = 1500;
    float number;
    public CountView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void showNumberWithAnimation(float startNumber,float endNumber) {
        //修改number属性，会调用setNumber方法
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(this,"number",startNumber,endNumber);
        objectAnimator.setDuration(duration);
        //加速器，从慢到快到再到慢
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }
    public float getNumber() {
        return number;
    }
    public void setNumber(float number) {
        this.number = number;
        setText(String.format("%1$07.2f",number));
    }
}
