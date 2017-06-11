package com.coolwin.library.helper;

/**
 * Created by Administrator on 2017/6/2.
 */

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.adapter.BaseAdapter;

import java.util.Collections;

/**
 * RecycleView item 的拖拽处理
 */
public class ItemTouchCallBack extends ItemTouchHelper.Callback {
    private final BaseAdapter adapter;
    public ItemTouchCallBack(BaseAdapter adapter) {
        this.adapter = adapter;
    }
    /**
     * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，
     * 如果是网格类RecyclerView则还应该多有LEFT和RIGHT
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            //final int swipeFlags = 0;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;//代表处理滑动删除,将执行onSwiped()方法
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //final int swipeFlags = 0;//为0 代表不处理滑动删除
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;//代表处理滑动删除,将执行onSwiped()方法
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //得到当拖拽的viewHolder的Position
        int fromPosition = viewHolder.getAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adapter.getData(), i, i + 1);//交换位置
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adapter.getData(), i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);//刷新
        return true;
    }

    /**
     * 当item被左右滑动时调用
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        AbDialogUtil.showAlertDialog(adapter.context, "是否删除?", "你确定要删除该模块么?", new AbAlertDialogFragment.AbDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                adapter.getData().remove(position);
                adapter.notifyItemRemoved(position);
            }
            @Override
            public void onNegativeClick() {
                adapter.notifyItemChanged(position);
            }
        });
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     * 相当于长按后执行了：mItemTouchHelper.startDrag(holder);//开始拖动
     * @return 返回true：长按item时，就可以实现拖动效果
     *          返回false：长按item事件就失效了，也就不能直接实现拖动效果了
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 左右滑动时是否可以执行onSwiped()方法
     * @return 返回true:代表左右滑动item时，可以删除
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * 开始拖拽时调用
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);//为拖拽的item设置背景颜色
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 拖拽结束时调用
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
    }
}
