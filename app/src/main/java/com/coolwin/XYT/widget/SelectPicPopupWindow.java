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


public class SelectPicPopupWindow extends PopupWindow {
	//private Button  btn_cancel;
	private View mMenuView;
	private LinearLayout my_fenxiang,my_liulanqi,copy_url;
	public SelectPicPopupWindow(final Activity context, final OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.bottomdialog, null);
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		my_fenxiang = (LinearLayout)mMenuView.findViewById(R.id.fenxiang_layout);
		my_fenxiang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		my_liulanqi = (LinearLayout)mMenuView.findViewById(R.id.waibuliulan);
		my_liulanqi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		copy_url = (LinearLayout)mMenuView.findViewById(R.id.copyurl);
		copy_url.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		});
		//锟斤拷锟矫帮拷钮锟斤拷锟斤拷
		//锟斤拷锟斤拷SelectPicPopupWindow锟斤拷View
		this.setContentView(mMenuView);
		//锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷目锟�		
		this.setWidth(w/4);
		//锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷母锟�		
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷傻锟斤拷
		this.setFocusable(true);
		//锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟藉动锟斤拷效锟斤拷
		this.setAnimationStyle(R.style.mystyle);
		//实锟斤拷一锟斤拷ColorDrawable锟斤拷色为锟斤拷透锟斤拷
		ColorDrawable dw = new ColorDrawable(000000);
		//锟斤拷锟斤拷SelectPicPopupWindow锟斤拷锟斤拷锟斤拷锟斤拷谋锟斤拷锟�		
		//this.setBackgroundDrawable(dw);
		this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.no_top_arrow_bg));
		//mMenuView锟斤拷锟絆nTouchListener锟斤拷锟斤拷锟叫断伙拷取锟斤拷锟斤拷位锟斤拷锟斤拷锟斤拷锟窖★拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷俚锟斤拷锟斤拷锟�		
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
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
