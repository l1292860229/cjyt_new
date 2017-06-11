package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.CommodityAdapter;
import com.coolwin.XYT.databinding.CommodityBinding;
import com.coolwin.XYT.interfaceview.UICommodity;
import com.coolwin.XYT.presenter.CommodityPresenter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;

import static com.coolwin.XYT.Entity.constant.Constants.COMMODITY;

/**
 * Created by Administrator on 2017/5/31.
 */

public class CommodityActivity extends BaseActivity<CommodityPresenter>  implements UICommodity {
    CommodityBinding binding;
    CommodityAdapter commodityAdapter;
    private int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.commodity);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的商品");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("添加");
        commodityAdapter = new CommodityAdapter(context,new ArrayList<MyCommodity>());
        binding.ivCommodity.setAdapter(commodityAdapter);
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
        mPresenter.getData(page,CommodityPresenter.INIT);
        commodityAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AbDialogUtil.showAlertDialog(context, "是否删除?", "你确定要删除该商品么?", new AbAlertDialogFragment.AbDialogOnClickListener() {
                    @Override
                    public void onPositiveClick() {
                        mPresenter.deleteData(commodityAdapter.getData().get(position).getId()+"");
                        commodityAdapter.getData().remove(position);
                        commodityAdapter.notifyItemRemoved(position);
                    }
                    @Override
                    public void onNegativeClick() {
                    }
                });
                return false;
            }
        });
        RxBus.get().register(this);
    }
    @Override
    public void right_text(View view) {
        startActivity(new Intent(this,AddCommodityActivity.class));
    }
    @Override
    public void showLoading() {
    }
    /**
     *  事件线的来源
     *  1.AddCommodityActivity->savesuccess 来源
     */
    @Subscribe(code = COMMODITY,
            threadMode = ThreadMode.MAIN)
    public void receive1001(){
        page = 1;
        mPresenter.getData(page,CommodityPresenter.REFRESH);
    }
    @Override
    public void init(List<MyCommodity> data) {
        binding.ivCommodity.setLayoutManager(new LinearLayoutManager(context));
        commodityAdapter.setData(data);
        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshSuccess(List<MyCommodity> data) {
        binding.ivLayout.finishRefreshing();
        commodityAdapter.setData(data);
        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMoreSucess(List<MyCommodity> data) {
        binding.ivLayout.finishLoadmore();
        commodityAdapter.getData().addAll(data);
        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        RxBus.get().unRegister(this);
        super.onDestroy();
    }
}
