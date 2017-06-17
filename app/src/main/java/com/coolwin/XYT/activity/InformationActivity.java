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
import com.coolwin.XYT.Entity.MyInformation;
import com.coolwin.XYT.Entity.enumentity.GetDataType;
import com.coolwin.XYT.Entity.enumentity.InformationType;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.InformationAdapter;
import com.coolwin.XYT.databinding.InformationBinding;
import com.coolwin.XYT.interfaceview.UIInformation;
import com.coolwin.XYT.presenter.InformationPresenter;
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

public class InformationActivity extends BaseActivity<InformationPresenter>  implements UIInformation {
    public static final String DATATYPE="type";
    InformationBinding binding;
    InformationAdapter commodityAdapter;
    private int page=1;
    private InformationType informationType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.information);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        informationType = (InformationType) getIntent().getSerializableExtra(DATATYPE);
        binding.titleLayout.title.setText("我的"+informationType.getValue());
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("添加");
        commodityAdapter = new InformationAdapter(context,new ArrayList<MyInformation>());
        binding.ivCommodity.setAdapter(commodityAdapter);
        binding.ivLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                page = 1;
                mPresenter.getData(page, GetDataType.REFRESH,informationType.getValue());
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                page++;
                mPresenter.getData(page, GetDataType.LOADMORE,informationType.getValue());
            }
        });
        mPresenter.getData(page, GetDataType.INIT,informationType.getValue());
        commodityAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AbDialogUtil.showAlertDialog(context, "是否删除?", "你确定要删除么?", new AbAlertDialogFragment.AbDialogOnClickListener() {
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
        Intent intent = new Intent(this,AddInformationActivity.class);
        intent.putExtra(AddInformationActivity.DATATYPE,informationType);
        startActivity(intent);
    }
    @Override
    public void showLoading() {
    }
    /**
     *  事件线的来源
     *  1.AddInformationActivity->savesuccess 来源
     */
    @Subscribe(code = COMMODITY,
            threadMode = ThreadMode.MAIN)
    public void receive1001(){
        page = 1;
        mPresenter.getData(page, GetDataType.REFRESH,informationType.getValue());
    }
    @Override
    public void init(List<MyInformation> data) {
        binding.ivCommodity.setLayoutManager(new LinearLayoutManager(context));
        commodityAdapter.setData(data);
        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshSuccess(List<MyInformation> data) {
        binding.ivLayout.finishRefreshing();
        commodityAdapter.setData(data);
        commodityAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMoreSucess(List<MyInformation> data) {
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
