package com.coolwin.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by dell on 2017/6/27.
 * 有滑动监听ScrollView
 */
public class ObservableScrollView extends ScrollView {
    private ScrollViewListener mScrollViewListener=null;
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        mScrollViewListener = scrollViewListener;
    }
    public ObservableScrollView(Context context) {
        super(context);
    }
    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ObservableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView observableScrollView,int x,int y ,int oldx, int oldy);
    }
}
