package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyIndexAdapter;
import com.coolwin.XYT.databinding.IndexRecyclerBinding;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.presenter.MyIndexPresenter;
import com.coolwin.library.helper.ItemTouchCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;
import static com.coolwin.XYT.activity.UpdatePicIndexActivity.DATAKEY;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyIndexActivity extends BaseActivity<MyIndexPresenter> implements UIMyIndex {
    public static final int BACKPIC=1;
    public static final int BACKTEMPLATEPIC=2;
    IndexRecyclerBinding binding;
    private MyIndexAdapter mAdapter;
    List<DataModel> datas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.index_recycler);
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
        startActivityForResult(new Intent(context,SelectTemplatePicActivity.class),BACKTEMPLATEPIC);
    }
    public void openLunBo(View view){
        DataModel model = new DataModel();
        model.bannerList = new ArrayList<>();
        model.bannerList.add(model.new BannerList(""));
        datas.add(0,model);
        mAdapter.setData(datas);
        mAdapter.notifyDataSetChanged();
        abSampleDialogFragment.dismiss();
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
                case BACKTEMPLATEPIC:
                    if(data!=null){
                        DataModel dataModel = (DataModel) data.getSerializableExtra(SelectTemplatePicActivity.DATA);
                        if(dataModel.bannerList!=null && dataModel.bannerList.size()>0){
                            datas =  mAdapter.getData();
                            if(datas==null){
                                datas = new ArrayList<>();
                            }
                            if(datas.size()==0){
                                datas.add(dataModel);
                            }else if (datas.get(0).bannerList!=null) {
                                    datas.remove(0);
                                    datas.add(0,dataModel);
                            }else{
                                datas.add(0,dataModel);
                            }
                            mAdapter.setData(datas);
                            mAdapter.notifyItemChanged(0);
                            return;
                        }else{
                            mAdapter.getData().add(dataModel);
                        }
                        mAdapter.notifyItemChanged(mAdapter.getData().size()-1);
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

