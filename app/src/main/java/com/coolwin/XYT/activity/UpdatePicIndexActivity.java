package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ab.util.AbDialogUtil;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.MyInformation;
import com.coolwin.XYT.Entity.rxbus.Transmission;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.MyIndexAdapter;
import com.coolwin.XYT.adapter.NetworkImageHolderView;
import com.coolwin.XYT.databinding.DialogPicUpdateordelBinding;
import com.coolwin.XYT.databinding.PublicRecyclerBinding;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;
import static com.coolwin.XYT.Entity.constant.Constants.UPDATEMYINDEXPIC;
import static com.coolwin.XYT.Entity.constant.Constants.UPDATEMYINDEXPIC_PIC;

/**
 * Created by dell on 2017/6/8.
 */

public class UpdatePicIndexActivity extends  BaseActivity {
    public static final String DATAKEY = "data";
    public static final int  IMAGEPATHLUNBO = 1;
    public static final int  IMAGEPATH = 2;
    PublicRecyclerBinding binding;
    DataModel dataModel;
    int position;
    List datas = new ArrayList();
    MyIndexAdapter picAdapter;
    CBViewHolderCreator cbViewHolderCreator;
    int picPosition;
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
        binding.ivLayout.setEnableLoadmore(false);
        binding.ivLayout.setEnableRefresh(false);
        dataModel = (DataModel) getIntent().getSerializableExtra(DATAKEY);
        if(dataModel.datas!=null){
            showDatas();
        }
        if(dataModel.bannerList!=null){
            showBannerList();
        }
    }
    public void showBannerList(){
        if(dataModel.bannerList.size()<4 &&
                !StringUtil.isNull(dataModel.bannerList.get(dataModel.bannerList.size()-1).imgUrl)){
            dataModel.bannerList.add(dataModel.new BannerList(""));
        }
        binding.ivIndex.setVisibility(View.GONE);
        binding.convenientBanner.setVisibility(View.VISIBLE);
        binding.convenientBanner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
        binding.convenientBanner.setBackgroundColor(Color.WHITE);
        cbViewHolderCreator = new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new NetworkImageHolderView(binding.convenientBanner,widthPixels);
            }
        };
        binding.convenientBanner.setPages(cbViewHolderCreator,dataModel.bannerList)
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        binding.convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (StringUtil.isNull(dataModel.bannerList.get(position).imgUrl)) {
                    //打开相册
                    GalleryFinalHelper.openGallerySingle(0, false, true, new GalleryFinal.OnHanlderResultCallback() {
                        @Override
                        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                            if (resultList.size()>0) {
                                Intent intent = new Intent(context,CropPicActivity.class);
                                intent.putExtra(CropPicActivity.DATA,resultList.get(0).getPhotoPath());
                                startActivityForResult(intent,IMAGEPATHLUNBO);
                            }
                        }
                        @Override
                        public void onHanlderFailure(int requestCode, String errorMsg) {
                            UIUtil.showMessage(context,errorMsg);
                        }
                    });
                }else{
                    picPosition = position;
                    DialogPicUpdateordelBinding dialogPicUpdateordelBinding =  DataBindingUtil
                            .inflate(LayoutInflater.from(context),R.layout.dialog_pic_updateordel,null,false);
                    dialogPicUpdateordelBinding.setBehavior(UpdatePicIndexActivity.this);
                    abSampleDialogFragment = AbDialogUtil.showDialog(dialogPicUpdateordelBinding.getRoot());
                }
            }
        });
    }
    public void showDatas(){
        position = getIntent().getIntExtra(DATAPOSITION,0);
        //这个是要修改的图片
        datas.add(dataModel);
        datas.addAll(dataModel.datas);
        picAdapter=new MyIndexAdapter(this,datas,widthPixels);
        picAdapter.setItemCanClick(true);
        binding.ivLayout.setEnableRefresh(false);
        binding.ivLayout.setEnableLoadmore(false);
        binding.ivIndex.setLayoutManager(new LinearLayoutManager(context));
        binding.ivIndex.setAdapter(picAdapter);
    }
    public void openCamera(View view){
        GalleryFinalHelper.openGallerySingle(0, false, true, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                if (resultList.size()>0) {
                    Intent intent = new Intent(context,CropPicActivity.class);
                    intent.putExtra(CropPicActivity.DATA,resultList.get(0).getPhotoPath());
                    startActivityForResult(intent,IMAGEPATH);
                }
            }
            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                UIUtil.showMessage(context,errorMsg);
            }
        });
        abSampleDialogFragment.dismiss();
    }
    public void delPic(View view){
        dataModel.bannerList.remove(picPosition);
        if(dataModel.bannerList.size()<4 &&
                !StringUtil.isNull(dataModel.bannerList.get(dataModel.bannerList.size()-1).imgUrl)){
            dataModel.bannerList.add(dataModel.new BannerList(""));
        }
        binding.convenientBanner.setPages(cbViewHolderCreator,dataModel.bannerList);
        abSampleDialogFragment.dismiss();
    }

    @Override
    public void right_text(View view) {
        if (dataModel.bannerList!=null&&dataModel.bannerList.size()>1) {
            if(StringUtil.isNull(dataModel.bannerList.get(dataModel.bannerList.size()-1).imgUrl)){
                dataModel.bannerList.remove(dataModel.bannerList.size()-1);
            }
        }
        if(binding.convenientBanner.getVisibility()==View.VISIBLE){
            binding.convenientBanner.notifyDataSetChanged();
        }
        Intent intent = new Intent();
        intent.putExtra(DATAKEY,dataModel);
        intent.putExtra(DATAPOSITION,position);
        setResult(RESULT_OK,intent);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String path = data.getStringExtra(CropPicActivity.DATA);
            switch (requestCode){
                case IMAGEPATHLUNBO:
                    dataModel.bannerList.add(dataModel.bannerList.size()-1,
                            dataModel.new BannerList(path));
                    if(dataModel.bannerList.size()==5){
                        dataModel.bannerList.remove(dataModel.bannerList.size()-1);
                    }else{
                        if(!StringUtil.isNull(dataModel.bannerList.get(dataModel.bannerList.size()-1).imgUrl)){
                            dataModel.bannerList.add(dataModel.new BannerList(""));
                        }
                    }
                    binding.convenientBanner.setPages(cbViewHolderCreator,dataModel.bannerList);
                    break;
                case IMAGEPATH:
                    dataModel.bannerList.remove(picPosition);
                    dataModel.bannerList.add(picPosition,dataModel.new BannerList(path));
                    binding.convenientBanner.setPages(cbViewHolderCreator,dataModel.bannerList);
                    break;
            }
        }
    }

    /**
     *  事件线的来源
     *  1.SelectInformationActivity->right_text 来源
     * @param msg
     */
    @Subscribe(code = UPDATEMYINDEXPIC,
            threadMode = ThreadMode.MAIN)
    public void receive1000(Transmission msg){
        if (msg==null || msg.object==null) {
            return;
        }
        //接收数据
        List<MyInformation> subjectses = (List<MyInformation>) msg.object;
        StringBuffer str = new StringBuffer();
        for (MyInformation subjectse : subjectses) {
            str.append(subjectse.getShopurl()).append(",");
        }
        if(subjectses.size()>0){
            str.deleteCharAt(str.length()-1);
        }
        dataModel.datas.get(msg.position).shopLink = str.toString();
        picAdapter.setData(datas);
        picAdapter.notifyItemRangeChanged(0,datas.size());
    }
    /**
     *  事件线的来源
     *  1.CropPicActivity->right_text 来源
     * @param msg
     */
    @Subscribe(code = UPDATEMYINDEXPIC_PIC,
            threadMode = ThreadMode.MAIN)
    public void receive10001(Transmission msg) {
        if (msg == null || msg.object == null) {
            return;
        }
        //接收数据
        List<String> subjectses = (List<String>) msg.object;
        int startindex = msg.position, endindex = subjectses.size();
        //批量更新选择的商品图片
        for (int i = msg.position; i < dataModel.datas.size(); i++) {
            if (endindex <= (i - startindex)) {
                break;
            }
            DataModel.Data data = dataModel.new Data();
            if (subjectses.size() > 0) {
                data.shopImageUrl = subjectses.get(i - startindex);
            }
            data.shopLink = dataModel.datas.get(i).shopLink;
            dataModel.datas.remove(i);
            dataModel.datas.add(i, data);
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
