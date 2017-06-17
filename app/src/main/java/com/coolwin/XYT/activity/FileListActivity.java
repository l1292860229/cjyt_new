package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.FileListAdapter;
import com.coolwin.XYT.databinding.PublicRecyclerBinding;
import com.coolwin.XYT.interfaceview.UIFileList;
import com.coolwin.XYT.presenter.FileListPresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dell on 2017/6/16.
 */

public class FileListActivity extends BaseActivity<FileListPresenter> implements UIFileList {
    PublicRecyclerBinding binding;
    List<String> filePathList = new ArrayList<>();
    FileListAdapter fileListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.public_recycler);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("文件列表");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.ivLayout.setEnableLoadmore(false);
        binding.ivLayout.setEnableRefresh(false);
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        fileListAdapter = new FileListAdapter(context,filePathList);
        binding.ivIndex.setAdapter(fileListAdapter);
        mPresenter.init();
    }

    @Override
    public void showLoading() {
        super.showLoading("查询中...");
    }

    @Override
    public void init(List<String> strings) {
        filePathList = strings;
        fileListAdapter.setData(filePathList);
        fileListAdapter.notifyDataSetChanged();
    }
}
