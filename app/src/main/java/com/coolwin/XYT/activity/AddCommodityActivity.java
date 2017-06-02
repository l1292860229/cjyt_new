package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.AddCommodityPicAdapter;
import com.coolwin.XYT.databinding.AddCommodityBinding;
import com.coolwin.XYT.interfaceview.UIAddCommodity;
import com.coolwin.XYT.presenter.AddCommodityPresenter;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.library.helper.ItemTouchCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by Administrator on 2017/6/2.
 */

public class AddCommodityActivity extends BaseActivity<AddCommodityPresenter> implements UIAddCommodity {
    AddCommodityBinding binding;
    AddCommodityPicAdapter picAdapter;
    public List<String> picList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.add_commodity);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        binding.titleLayout.title.setText("添加商品");
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        binding.titleLayout.rightTextBtn.setText("添加");
        binding.ivPic.setLayoutManager(new GridLayoutManager(context,4));
        init();
    }
    @Override
    public void showLoading() {
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
                    GalleryFinalHelper.openGalleryMuti(0, false, true, (5-picAdapter.getData().size()), new GalleryFinal.OnHanlderResultCallback() {
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
                                UIUtil.showMessage(context,"最大选择4张");
                            }else{
                                UIUtil.showMessage(context,errorMsg);
                            }
                        }
                    });
                }
            }
        });
    }
}
