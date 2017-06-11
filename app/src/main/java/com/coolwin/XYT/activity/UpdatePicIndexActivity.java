package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.MyCommodity;
import com.coolwin.XYT.Entity.rxbus.Transmission;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyIndexAdapter;
import com.coolwin.XYT.databinding.PublicRecyclerBinding;

import java.util.ArrayList;
import java.util.List;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;
import static com.coolwin.XYT.Entity.constant.Constants.UPDATEMYINDEXPIC;

/**
 * Created by dell on 2017/6/8.
 */

public class UpdatePicIndexActivity extends  BaseActivity {
    public static final String DATAKEY = "data";
    PublicRecyclerBinding binding;
    DataModel dataModel;
    int position;
    List datas = new ArrayList();
    MyIndexAdapter picAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
        binding =  DataBindingUtil.setContentView(this, R.layout.public_recycler);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("商城图片修改");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("确认");
        dataModel = (DataModel) getIntent().getSerializableExtra(DATAKEY);
        position = getIntent().getIntExtra(DATAPOSITION,0);
        //这个是要修改的图片
        datas.add(dataModel);
        datas.addAll(dataModel.datas);
        picAdapter=new MyIndexAdapter(this,datas,widthPixels);
        binding.ivLayout.setEnableRefresh(false);
        binding.ivLayout.setEnableLoadmore(false);
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivIndex.setAdapter(picAdapter);
    }

    @Override
    public void right_text(View view) {
        Intent intent = new Intent();
        intent.putExtra(DATAKEY,dataModel);
        intent.putExtra(DATAPOSITION,position);
        setResult(RESULT_OK,intent);
        this.finish();
    }

    /**
     *  事件线的来源
     *  1.SelectCommodityActivity->right_text 来源
     * @param msg
     */
    @Subscribe(code = UPDATEMYINDEXPIC,
            threadMode = ThreadMode.MAIN)
    public void receive1000(Transmission msg){
        if (msg==null || msg.object==null) {
            return;
        }
        //接收数据
        List<MyCommodity> subjectses = (List<MyCommodity>) msg.object;
        int startindex=msg.position,endindex=subjectses.size();
        //批量更新选择的商品图片
        for (int i = msg.position; i < dataModel.datas.size(); i++) {
            if(endindex<=(i-startindex)){
                break;
            }
            DataModel.Data data = dataModel.new Data();
            if (subjectses.get(i-startindex).getPicture()!=null && subjectses.get(i-startindex).getPicture().size()>0) {
                data.shopImageUrl = subjectses.get(i-startindex).getPicture().get(0).originUrl;
            }
            data.shopLink = subjectses.get(i-startindex).getShopurl();
            dataModel.datas.remove(i);
            dataModel.datas.add(i,data);
        }
        //更新数据
        datas.clear();
        datas.add(dataModel);
        datas.addAll(dataModel.datas);
        picAdapter.setData(datas);
        picAdapter.notifyItemRangeChanged(0,datas.size());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unRegister(this);
    }
}
