package com.coolwin.XYT.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.coolwin.XYT.Entity.AreaCode;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.AreaCodeBinding;

/**
 * Created by dell on 2017/6/13.
 */

public class SelectAreaCodeAdapter extends BaseExpandableListAdapter {
    AreaCode[] areaCodes;
    Context context;
    public SelectAreaCodeAdapter(Context context,AreaCode[] areaCodes){
        Log.e("SelectAreaCodeAdapter","areaCodes");
        this.context = context;
        this.areaCodes = areaCodes;
    }
    //返回一级列表的个数
    @Override
    public int getGroupCount() {
        return areaCodes.length;
    }
    //返回二级列表的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return areaCodes[groupPosition].Cities.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return areaCodes[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return areaCodes[groupPosition].Cities[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AreaCodeBinding binding =  DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.area_code, parent, false);
        binding.setName(areaCodes[groupPosition].State);
        return binding.getRoot();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AreaCodeBinding binding =  DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.area_code, parent, false);
        binding.setName(areaCodes[groupPosition].Cities[childPosition].City);
        return binding.getRoot();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
