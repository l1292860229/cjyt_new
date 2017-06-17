package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.AreaCode;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.SelectAreaCodeAdapter;
import com.coolwin.XYT.databinding.SelectAreaCodeBinding;
import com.coolwin.XYT.util.GetDataUtil;

/**
 * Created by dell on 2017/6/13.
 */

public class SelectAreaCodeActivity extends BaseActivity{
    public static final String DATAPROVINCE = "province";
    public static final String DATACITY = "city";
    SelectAreaCodeBinding binding;
    AreaCode[] areaCodes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.select_area_code);
        areaCodes = GetDataUtil.getAreaCode(context);
        binding.expandableListView.setAdapter(new SelectAreaCodeAdapter(context,areaCodes));
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("选择省份");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent();
                intent.putExtra(DATAPROVINCE,areaCodes[groupPosition].State);
                intent.putExtra(DATACITY,areaCodes[groupPosition].Cities[childPosition].City);
                setResult(RESULT_OK,intent);
                SelectAreaCodeActivity.this.finish();
                return false;
            }
        });
    }
}
