package com.coolwin.XYT.widget;



import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.coolwin.XYT.R;

public class SelectAddPopupWindow extends PopupWindow {

    private View mMenuView;
    private LinearLayout mChatLayout,mAddFriendLayout,mShaoLayout,mfeedbackLayout;

    public SelectAddPopupWindow(final Activity context, final OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.addxml, null);
        mChatLayout = (LinearLayout)mMenuView.findViewById(R.id.my_chat_layout);
        mChatLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
                dismiss();
            }
        });
        
        mAddFriendLayout = (LinearLayout)mMenuView.findViewById(R.id.create_chat_layout);
        mAddFriendLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
                dismiss();
            }
        });
        
        mShaoLayout = (LinearLayout)mMenuView.findViewById(R.id.sao_layout);
        mShaoLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
                dismiss();
            }
        });

        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        //设置按钮监听
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth((int)(w/2.5));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.mystyle);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        //this.setBackgroundDrawable(dw);
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.picred));
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
            
            public boolean onTouch(View v, MotionEvent event) {
                
                int height = mMenuView.findViewById(R.id.pop_layout2).getTop();
                int y=(int) event.getY();
                if(event.getAction()== MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }               
                return true;
            }
        });
    }
}
