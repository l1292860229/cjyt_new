package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyIndexAdapter;
import com.coolwin.XYT.databinding.IndexRecyclerBinding;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.presenter.SelectTemplatePicPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/31.
 */

public class SelectTemplatePicActivity extends BaseActivity<SelectTemplatePicPresenter> implements UIMyIndex {
    public static final String DATA="data";
    IndexRecyclerBinding binding;
    private MyIndexAdapter mAdapter;
    List<DataModel> datas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.index_recycler);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("选择模板");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        //初始化加载器
        mAdapter=new MyIndexAdapter(this,datas,widthPixels);
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivIndex.setAdapter(mAdapter);
        /**
         * RecycleView 控件的item单击事件
         */
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(DATA,datas.get(position));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        //初始化数据
        mPresenter.init();
    }
    @Override
    public void showLoading() {
        super.showLoading("获取中....");
    }
    @Override
    public void init(List<DataModel> datas) {
        this.datas = datas;
        //在adapter中添加数据
        mAdapter.setData(datas);
        mAdapter.notifyDataSetChanged();
    }
}

