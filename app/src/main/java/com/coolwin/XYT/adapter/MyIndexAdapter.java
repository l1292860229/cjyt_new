package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.constant.StaticData;
import com.coolwin.XYT.Entity.enumentity.InformationType;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.CropPicActivity;
import com.coolwin.XYT.activity.SelectInformationActivity;
import com.coolwin.XYT.databinding.ShopIndexListBinding;
import com.coolwin.XYT.databinding.ShopIndexPicItemBinding;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.library.helper.DownloadImage;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;


/**
 * Created by Administrator on 2017/5/31.
 */

public class MyIndexAdapter extends BaseAdapter{
    public boolean itemCanClick=false;
    public MyIndexAdapter(Context context, List mList, int widthPixels) {
        super(context);
        this.mList = mList;
        this.widthPixels = widthPixels;
    }
    public void setItemCanClick(boolean itemCanClick){
        this.itemCanClick = itemCanClick;
    }
    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding=null;
        switch (viewType){
            case 0:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.shop_index_list, parent, false);
                break;
            case 1:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.shop_index_pic_item, parent, false);
                break;
        }
        MyRecycleViewHolder  vh = new MyRecycleViewHolder(dataBinding.getRoot());
        vh.setBinding(dataBinding);
        return vh;
    }
    @Override
    public void onBindViewHolder(final MyRecycleViewHolder holder, final int position) {
        if (holder.getBinding() instanceof ShopIndexListBinding) {
            final DataModel dataModelist = (DataModel) mList.get(position);
            ShopIndexListBinding dataBinding = (ShopIndexListBinding) holder.getBinding();
            dataBinding.rootlayout.removeAllViews();
            dataBinding.rootlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null){
                        mItemClickListener.onItemClick(null,v,holder.getLayoutPosition(),holder.getLayoutPosition());
                    }
                }
            });
            //非空判断
            if(dataModelist.datas==null && dataModelist.bannerList==null){
                return;
            }
            if(dataModelist.bannerList!=null && dataModelist.bannerList.size()>0){
                final ConvenientBanner convenientBanner = new ConvenientBanner(context);
                convenientBanner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
                convenientBanner.setCanLoop(true);
                convenientBanner.startTurning(3000);
                convenientBanner.setPages(new CBViewHolderCreator() {
                    @Override
                    public Object createHolder() {
                        return new NetworkImageHolderView(convenientBanner,widthPixels);
                    }
                },dataModelist.bannerList)
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
                convenientBanner.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if(mItemClickListener!=null){
                            mItemClickListener.onItemClick(null,convenientBanner,holder.getLayoutPosition(),0);
                        }

                    }
                });
                dataBinding.rootlayout.addView(convenientBanner);
                setCanMoreTop(false);
                return;
            }
            final int dataSize = dataModelist.datas.size();
            for (int i = 0; i < dataModelist.datas.size(); i++) {
                 SimpleDraweeView view = new SimpleDraweeView(context);
                //是否是要新添加的图片
                if(StringUtil.isNull(dataModelist.datas.get(i).shopImageUrl)){
                    view.setLayoutParams(new LinearLayout.LayoutParams((int)(widthPixels*1.0f/dataSize),
                            (int)(widthPixels*1.0f/dataSize*0.5f)));
                    Phoenix.with(view)
                            .load(R.drawable.smiley_add_btn);
                }else{
                    //判断本地是否缓存网络图片的规格
                    if(StaticData.imageViewOptions.containsKey(dataModelist.datas.get(i).shopImageUrl)){
                        BitmapFactory.Options options = StaticData.imageViewOptions.get(dataModelist.datas.get(i).shopImageUrl);
                        int width = (int)(widthPixels*(1.0f/dataSize));
                        int height =  (int)(1.0 *width / options.outWidth * options.outHeight);
                        view.setLayoutParams(new LinearLayout.LayoutParams(width,height));
                    }else{
                        //否则就重新请求
                        DownloadImage downloadImage = new DownloadImage(view,widthPixels,(1.0f/dataSize));
                        downloadImage.execute(dataModelist.datas.get(i).shopImageUrl);
                    }
                    //加载图片
                    Phoenix.with(view)
                            .load(dataModelist.datas.get(i).shopImageUrl);
                }
                if (itemCanClick) {
                    final int pos  = i;
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GalleryFinalHelper.openGallerySingle(0, false, true, new GalleryFinal.OnHanlderResultCallback() {
                                @Override
                                public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                                    if (resultList.size()>0) {
                                        Intent intent = new Intent(context,CropPicActivity.class);
                                        intent.putExtra(CropPicActivity.DATA,resultList.get(0).getPhotoPath());
                                        intent.putExtra(CropPicActivity.POSITION,pos);
                                        context.startActivity(intent);
                                    }
                                }
                                @Override
                                public void onHanlderFailure(int requestCode, String errorMsg) {
                                    UIUtil.showMessage(context,errorMsg);
                                }
                            });
                        }
                    });
                }
                dataBinding.rootlayout.addView(view);
            }
        }else{
            DataModel.Data data = (DataModel.Data) mList.get(position);
            final ShopIndexPicItemBinding dataBinding = (ShopIndexPicItemBinding) holder.getBinding();
            dataBinding.setImageurl(data.shopLink);
            //加载图片
            Phoenix.with(dataBinding.pic1)
                    .load(data.shopImageUrl);
            dataBinding.updatecommodity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,SelectInformationActivity.class);
                    intent.putExtra(SelectInformationActivity.DATATYPE, InformationType.commodity);
                    intent.putExtra(DATAPOSITION,holder.getLayoutPosition()-1);
                    context.startActivity(intent);
                }
            });
            dataBinding.updateinformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,SelectInformationActivity.class);
                    intent.putExtra(SelectInformationActivity.DATATYPE, InformationType.Information);
                    intent.putExtra(DATAPOSITION,holder.getLayoutPosition()-1);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) instanceof DataModel) {
            return 0;
        }else{
            return 1;
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}
