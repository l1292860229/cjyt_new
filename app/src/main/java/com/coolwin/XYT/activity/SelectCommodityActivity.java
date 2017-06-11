package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.Entity.rxbus.Transmission;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.SelectCommodityAdapter;
import com.coolwin.XYT.databinding.PublicRecyclerBinding;
import com.coolwin.XYT.interfaceview.UICommodity;
import com.coolwin.XYT.presenter.CommodityPresenter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import gorden.rxbus2.RxBus;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;
import static com.coolwin.XYT.Entity.constant.Constants.UPDATEMYINDEXPIC;

/**
 * Created by dell on 2017/6/8.
 */

public class SelectCommodityActivity extends  BaseActivity<CommodityPresenter>  implements UICommodity {
    PublicRecyclerBinding binding;
    SelectCommodityAdapter selectCommodityAdapter;
    int position;
    int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.public_recycler);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("选择");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("确认");
        position = getIntent().getIntExtra(DATAPOSITION,0);
        selectCommodityAdapter = new SelectCommodityAdapter(context,new ArrayList<MyCommodity>());
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivIndex.setAdapter(selectCommodityAdapter);
        binding.ivLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                mPresenter.getData(page,CommodityPresenter.REFRESH);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                mPresenter.getData(page,CommodityPresenter.LOADMORE);
            }
        });
        //这个是展示的图片
        mPresenter.getData(1,CommodityPresenter.INIT);
    }
    @Override
    public void right_text(View view) {
        Transmission msg = new Transmission();
        msg.position = position;
        msg.object = selectCommodityAdapter.getCheckedSubjects();
        //发送更新的事件线 UpdatePicIndexActivity->receive1000
        RxBus.get().send(UPDATEMYINDEXPIC,msg);
        this.finish();
    }
    @Override
    public void showLoading() {

    }

    @Override
    public void init(List<MyCommodity> data) {
        selectCommodityAdapter.setData(data);
        selectCommodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshSuccess(List<MyCommodity> data) {
        binding.ivLayout.finishRefreshing();
        selectCommodityAdapter.setData(data);
        selectCommodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMoreSucess(List<MyCommodity> data) {
        binding.ivLayout.finishLoadmore();
        selectCommodityAdapter.getData().addAll(data);
        selectCommodityAdapter.notifyDataSetChanged();
    }
}
