package com.coolwin.XYT.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.coolwin.XYT.R;

/**
 * Created by yf on 2016/12/6.
 */
public class ItemLongClickedPopWindow extends PopupWindow {
    /**
     * 图片项目弹出菜单 * @value * {@value} *
     */
    public static final int IMAGE_VIEW_POPUPWINDOW = 5;
    /**
     * 超链接项目弹出菜单 * @value * {@value} *
     */
    public static final int ACHOR_VIEW_POPUPWINDOW = 6;
    private LayoutInflater itemLongClickedPopWindowInflater;
    private View itemLongClickedPopWindowView;
    private Context context;
    private int type;
    /**
     * 构造函数 * @param context 上下文 * @param width 宽度 * @param height 高度 *
     */
    public ItemLongClickedPopWindow(Context context, int type, int width, int height) {
        super(context);
        this.context = context;
        this.type = type;
        //创建
        this.initTab();
        //设置默认选项
        setWidth(width);
        setHeight(height);
        setContentView(this.itemLongClickedPopWindowView);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    //实例化
    private void initTab() {
        this.itemLongClickedPopWindowInflater = LayoutInflater.from(this.context);
        switch (type) {
            case ACHOR_VIEW_POPUPWINDOW: //超链接
               break;
            case IMAGE_VIEW_POPUPWINDOW: //图片
                this.itemLongClickedPopWindowView = this.itemLongClickedPopWindowInflater.inflate(R.layout.list_item_longclicked_img, null);
                break;
        }
    }

    public View getView(int id) {
        return this.itemLongClickedPopWindowView.findViewById(id);
    }
}
