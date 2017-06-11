package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyIndexAdapter;
import com.coolwin.XYT.databinding.AddIndexPictureBinding;
import com.coolwin.XYT.databinding.PublicRecyclerBinding;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.presenter.MyIndexPresenter;
import com.coolwin.library.helper.ItemTouchCallBack;
import com.coolwin.library.helper.LocalImageHolderView;

import java.util.ArrayList;
import java.util.List;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;
import static com.coolwin.XYT.activity.UpdatePicIndexActivity.DATAKEY;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyIndexActivity extends BaseActivity<MyIndexPresenter> implements UIMyIndex {
    public static final int BACKPIC=1;
    PublicRecyclerBinding binding;
    private MyIndexAdapter mAdapter;
    List<DataModel> datas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.public_recycler);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的商城首页");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn2.setText("添加");
        binding.titleLayout.rightTextBtn.setText("保存");
        //初始化加载器
        mAdapter=new MyIndexAdapter(this,datas,widthPixels);
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivIndex.setAdapter(mAdapter);
        //创建一个条目触摸回调
        ItemTouchCallBack itemTouchCallBack = new ItemTouchCallBack(mAdapter);
        //设置给条目触摸的帮助类
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
        //设置关联recycleview
        itemTouchHelper.attachToRecyclerView(binding.ivIndex);
        /**
         * RecycleView 控件的item单击事件
         */
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                Intent intent = new Intent(context,UpdatePicIndexActivity.class);
                intent.putExtra(DATAKEY,(DataModel)mAdapter.getData().get(position));
                intent.putExtra(DATAPOSITION,position);
                startActivityForResult(intent,BACKPIC);
            }
        });
        //初始化数据
        mPresenter.init();
    }

    /**
     * 添加图片规格
     * @param view
     */
    @Override
    public void right_text2(View view) {
        AddIndexPictureBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.add_index_picture,null,false);
        binding.setBehavior(this);
        final List<Integer> countInt = new ArrayList<>();
        countInt.add(1);
        countInt.add(2);
        countInt.add(3);
        countInt.add(4);
        binding.convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new LocalImageHolderView(widthPixels);
            }
        },countInt)
        //设置指示器的方向
        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        binding.convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DataModel model = new DataModel();
                int index =  countInt.get(position);
                model.datas = new ArrayList<>();
                for (int i = 0; i < index; i++) {
                    model.datas.add(model.new Data());
                }
                mAdapter.getData().add(model);
                mAdapter.notifyItemInserted(mAdapter.getData().size()-1);
                abSampleDialogFragment.dismiss();
            }
        });
        abSampleDialogFragment = AbDialogUtil.showDialog(binding.getRoot());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case BACKPIC:
                    if(data!=null){
                        DataModel dataModel = (DataModel) data.getSerializableExtra(DATAKEY);
                        int position = data.getIntExtra(DATAPOSITION,0);
                        mAdapter.getData().remove(position);
                        mAdapter.getData().add(position,dataModel);
                        mAdapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    /**
     * 点击保存后的操作
     * @param view
     */
    @Override
    public void right_text(View view) {
        mPresenter.uploadPicUpdateIndex(mAdapter.getData());
    }
    @Override
    public void showLoading() {
        super.showLoading("提交中....");
    }
    @Override
    public void init(List<DataModel> datas) {
        this.datas = datas;
        //在adapter中添加数据
        mAdapter.setData(datas);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void close(View view) {
        showTiXing();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            showTiXing();
        }
        return false;

    }
    public void showTiXing(){
        AbDialogUtil.showAlertDialog(context, "提示", "是否确定退出?\n未保存的修改将丢失", new AbAlertDialogFragment.AbDialogOnClickListener() {
            @Override
            public void onPositiveClick() {
                MyIndexActivity.this.finish();
            }
            @Override
            public void onNegativeClick() {
            }
        });
    }
}

