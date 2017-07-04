package com.coolwin.XYT.adapter;

import android.content.Context;
import android.view.View;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.MyBbsListBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;


/**
 * Created by Administrator on 2017/5/31.
 */

public class MyBbsListFragmentAdapter extends BaseAdapter<Bbs>{
    public MyBbsListFragmentAdapter(Context context, List mList) {
        super(context);
        this.mList = mList;
    }
    @Override
    public int getLayoutId() {
        return R.layout.my_bbs_list;
    }
    @Override
    protected void bindData(final MyRecycleViewHolder holder, int position) {
        final MyBbsListBinding dataBinding  = (MyBbsListBinding) holder.getBinding();
        Bbs bbs = mList.get(position);
        dataBinding.setContent(bbs.content);
        dataBinding.setTitle(bbs.title);
        dataBinding.setPropleCount(bbs.peopleCount+"人");
        Phoenix.with(dataBinding.header)
                .load(bbs.headsmall);
        dataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener!=null){
                    mItemClickListener.onItemClick(null,dataBinding.getRoot(),holder.getLayoutPosition(),holder.getLayoutPosition());
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}
