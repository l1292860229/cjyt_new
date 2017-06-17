package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.Entity.MyInformation;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.CommodityItemBinding;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class SelectInformationAdapter extends BaseAdapter<MyInformation>{
    public List<MyInformation> checkedSubjects = new ArrayList<>();
    public SelectInformationAdapter(Context context, List<MyInformation> mList) {
        super(context);
        this.mList = mList;
    }
    public List<MyInformation> getCheckedSubjects() {
        return checkedSubjects;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CommodityItemBinding dataBinding =  DataBindingUtil.
                inflate(LayoutInflater.from(context), R.layout.commodity_item, parent, false);
        MyRecycleViewHolder  vh = new MyRecycleViewHolder(dataBinding.getRoot());
        vh.setBinding(dataBinding);
        return vh;

    }
    @Override
    public void onBindViewHolder(final MyRecycleViewHolder holder, final int position) {
        final MyInformation subjects =  mList.get(position);
        final CommodityItemBinding dataBinding = (CommodityItemBinding) holder.getBinding();
        dataBinding.check.setVisibility(View.VISIBLE);
        dataBinding.check.setChecked(false);
        dataBinding.setName(subjects.getTitle());
        //加载图片
        if (subjects.getPicture()!=null && subjects.getPicture().size()>0) {
            Phoenix.with(dataBinding.pic1)
                    .load(subjects.getPicture().get(0).smallUrl);
        }
        dataBinding.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataBinding.check.isChecked()){
                    dataBinding.check.setChecked(false);
                    checkedSubjects.remove(subjects);
                }else{
                    dataBinding.check.setChecked(true);
                    checkedSubjects.add(subjects);
                }
            }
        });
        for (MyInformation checkedSubject : checkedSubjects) {
            if (checkedSubject.equals(subjects)) {
                dataBinding.check.setChecked(true);
            }
        }
    }
}
