package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.MediaController;

import com.ab.util.AbDialogUtil;
import com.cocomeng.geneqiaovideorecorder.CameraActivity;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.AddCommodityPicAdapter;
import com.coolwin.XYT.databinding.AddCommodityBinding;
import com.coolwin.XYT.databinding.DialogPicVideoBinding;
import com.coolwin.XYT.interfaceview.UIAddCommodity;
import com.coolwin.XYT.presenter.AddCommodityPresenter;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.library.helper.FullyGridLayoutManager;
import com.coolwin.library.helper.ItemTouchCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import gorden.rxbus2.RxBus;

import static com.coolwin.XYT.Entity.constant.Constants.COMMODITY;


/**
 * Created by Administrator on 2017/6/2.
 */

public class AddCommodityActivity extends BaseActivity<AddCommodityPresenter> implements UIAddCommodity {
    public static final int VIDEORESULTCODE=1;
    AddCommodityBinding binding;
    AddCommodityPicAdapter picAdapter;
    public List<String> picList = new ArrayList<>();
    String videopath;
    String videoimagepath;
    String videotime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.add_commodity);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("添加商品");
        binding.titleLayout.llBar.setVisibility(View.GONE);
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("添加");
        binding.ivPic.setLayoutManager(new FullyGridLayoutManager(context,4));
        init();
    }
    @Override
    public void showLoading() {
        super.showLoading("正在提交...");
    }
    public void init(){
        picList.add("");
        picAdapter = new AddCommodityPicAdapter(context,picList);
        binding.ivPic.setAdapter(picAdapter);
        //设置关联recycleview
        new ItemTouchHelper(new ItemTouchCallBack(picAdapter))
                .attachToRecyclerView(binding.ivPic);
        picAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (StringUtil.isNull(picAdapter.getData().get(position))) {
                   DialogPicVideoBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_pic_video, parent, false);
                    binding.setBehavior(AddCommodityActivity.this);
                    abSampleDialogFragment  = AbDialogUtil.showDialog(binding.getRoot());
                }
            }
        });
    }

    @Override
    public void right_text(View view) {
        mPresenter.upload(binding.commoditytitle.getText().toString(),
                binding.commodityprice.getText().toString(),
                binding.commoditydescription.getText().toString(),picList,
                videopath,videoimagepath,videotime);
    }
    /**
     * 打开相片选择
     * @param view
     */
    public void openCamera(View view){
        GalleryFinalHelper.openGalleryMuti(0, false, true, (10-picAdapter.getData().size()), new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                if (resultList.size()>0) {
                    for (PhotoInfo photoInfo : resultList) {
                        picList.add(photoInfo.getPhotoPath());
                        picAdapter.setData(picList);
                        picAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                if (errorMsg.contains("大于0")) {
                    UIUtil.showMessage(context,"最大选择9张");
                }else{
                    UIUtil.showMessage(context,errorMsg);
                }
            }
        });
        abSampleDialogFragment.dismiss();
    }
    /**
     * 打开视频录制
     * @param view
     */
    public void openVideo(View view){
        CameraActivity.startActivity(AddCommodityActivity.this,VIDEORESULTCODE);
        abSampleDialogFragment.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case VIDEORESULTCODE:
                    if(data!=null){
                        videopath = data.getStringExtra(CameraActivity.VOIDPATH);
                        videoimagepath = data.getStringExtra(CameraActivity.VOIDIMAGEPATH);
                        videotime = data.getStringExtra(CameraActivity.VOIDPATHTIME);
                        binding.video.setVisibility(View.VISIBLE);
                        MediaController mediaCtlr = new MediaController(this);
                        mediaCtlr.setVisibility(View.INVISIBLE); //设置隐藏
                        binding.video.setMediaController(mediaCtlr);
                        binding.video.setVideoURI(Uri.parse(videopath));
                        binding.video.start();
                        binding.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setVolume(0f, 0f);
                                mp.start();
                                mp.setLooping(true);
                            }
                        });
                    }
                    break;
            }
        }
    }

    @Override
    public void savesuccess() {
        //发送更新的事件线 CommodityActivity->receive1001
        RxBus.get().send(COMMODITY);
        this.finish();
    }
}
