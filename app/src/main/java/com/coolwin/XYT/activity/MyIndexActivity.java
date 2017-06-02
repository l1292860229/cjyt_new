package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.ab.fragment.AbSampleDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyIndexAdapter;
import com.coolwin.XYT.databinding.AddIndexPictureBinding;
import com.coolwin.XYT.databinding.MyIndexBinding;
import com.coolwin.XYT.databinding.UpdateIndexPictureUrlBinding;
import com.coolwin.XYT.databinding.UpdateIndexUrlBinding;
import com.coolwin.XYT.interfaceview.UIMyIndex;
import com.coolwin.XYT.presenter.MyIndexPresenter;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.library.helper.ItemTouchCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by Administrator on 2017/5/31.
 */

public class MyIndexActivity extends BaseActivity<MyIndexPresenter> implements UIMyIndex {
    public static final int REQUEST_CODE_GALLERY = 2;
    MyIndexBinding binding;
    private MyIndexAdapter mAdapter;
    List<DataModel> datas = new ArrayList<>();
    AbSampleDialogFragment abSampleDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.my_index);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("我的商城首页");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn2.setText("添加");
        binding.titleLayout.rightTextBtn.setText("保存");
        //获得屏幕宽度
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        //初始化加载器
        mAdapter=new MyIndexAdapter(this,datas,outMetrics.widthPixels);
        binding.ivIndex.setLayoutManager(new GridLayoutManager(this,12));
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
                UpdateIndexUrlBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.update_index_url,null,false);
                binding.setBehavior(MyIndexActivity.this);
                //图片中是包含外链,是就显示,否就不显示
                if(StringUtil.isNull(mAdapter.getData().get(position).openurl)){
                    binding.picUrl.setVisibility(View.GONE);
                }else{
                    binding.picUrl.setText(mAdapter.getData().get(position).openurl);
                }
                abSampleDialogFragment = AbDialogUtil.showDialog(binding.getRoot());
                binding.getRoot().setTag(position);
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
        abSampleDialogFragment = AbDialogUtil.showDialog(binding.getRoot());
    }

    /**
     * 这些方法是跟不同类型,添加不同规格的图片控件
     * @param view
     */
    public void p4ratio4(View view){
        setType(DataModel.TYPE_ONETOONE);
    }
    public void p1ratio4(View view){
        setType(DataModel.TYPE_ONETOFOUR);
    }
    public void p1ratio3(View view){
        setType(DataModel.TYPE_ONETOTHREE);
    }
    public void p1ratio2(View view){
        setType(DataModel.TYPE_ONETOTWO);
    }
    public void p2ratio3(View view){
        setType(DataModel.TYPE_TWOTOTHREE);
    }
    public void p3ratio4(View view){
        setType(DataModel.TYPE_THREETOFOUR);
    }
    /**
     * 跟据不同的类型添加不同的图片规格
     * @param type
     */
    public void setType(int type){
        DataModel data=new DataModel();
        data.type=type;
        datas.add(data);
        mAdapter.setData(datas);
        mAdapter.notifyDataSetChanged();
        abSampleDialogFragment.dismiss();
    }
    /**
     * 打开图片要更换的外链地址
     * @param view
     */
    public void openUpdateUrl(View view){
        int position  = (int) abSampleDialogFragment.getContentView().getTag();
        abSampleDialogFragment.dismiss();
        UpdateIndexPictureUrlBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.update_index_picture_url,null,false);
        binding.setBehavior(this);
        binding.getRoot().setTag(position);
        binding.picUrl.setText(mAdapter.getData().get(position).openurl);
        abSampleDialogFragment = AbDialogUtil.showDialog(binding.getRoot());
    }

    /**
     *
     * 选择本地图片
     * @param view
     */
    public void openGallery(View view){
        final int position  = (int) abSampleDialogFragment.getContentView().getTag();
        abSampleDialogFragment.dismiss();
        /**
         * 打开相册
         */
        GalleryFinalHelper.openGallerySingle(REQUEST_CODE_GALLERY, false, true, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                mAdapter.getData().get(position).imagepath = resultList.get(0).getPhotoPath();
                mAdapter.setData(datas);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                UIUtil.showMessage(context,"加载图片失败,原因:"+errorMsg);
            }
        });
    }


    /**
     * 这个是确定更换图片外链
     * @param view
     */
    public void sureDialog(View view){
        final int position  = (int) abSampleDialogFragment.getContentView().getTag();
        EditText et = (EditText) abSampleDialogFragment.getContentView().findViewById(R.id.pic_url);
        mAdapter.getData().get(position).openurl = et.getText().toString();
        mAdapter.notifyDataSetChanged();
        abSampleDialogFragment.dismiss();
    }

    /**
     * 取消浮动窗口
     * @param view
     */
    public void cannelDialog(View view){
        abSampleDialogFragment.dismiss();
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
}

