package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.Entity.constant.StaticData;
import com.coolwin.XYT.R;
import com.coolwin.XYT.activity.SelectCommodityActivity;
import com.coolwin.XYT.databinding.ShopIndexListBinding;
import com.coolwin.XYT.databinding.ShopIndexPicItemBinding;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.library.helper.DownloadImage;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

import static com.coolwin.XYT.Entity.constant.Constants.DATAPOSITION;


/**
 * Created by Administrator on 2017/5/31.
 */

public class MyIndexAdapter extends BaseAdapter{
    public AdapterView.OnItemClickListener mItemClickListener;
    public MyIndexAdapter(Context context, List mList,int widthPixels) {
        super(context);
        this.mList = mList;
        this.widthPixels = widthPixels;
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
            DataModel dataModelist = (DataModel) mList.get(position);
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
            if(dataModelist.datas==null){
                return;
            }
            int dataSize = dataModelist.datas.size();
            for (DataModel.Data data : dataModelist.datas) {
                SimpleDraweeView view = new SimpleDraweeView(context);
                //是否是要新添加的图片
                if(StringUtil.isNull(data.shopImageUrl)){
                    view.setLayoutParams(new LinearLayout.LayoutParams((int)(widthPixels*1.0f/dataSize),
                            (int)(widthPixels*1.0f/dataSize*0.5f)));
                    Phoenix.with(view)
                            .load(R.drawable.smiley_add_btn);
                }else{
                    //判断本地是否缓存网络图片的规格
                    if(StaticData.imageViewOptions.containsKey(data.shopImageUrl)){
                        BitmapFactory.Options options = StaticData.imageViewOptions.get(data.shopImageUrl);
                        int width = (int)(widthPixels*(1.0f/dataSize));
                        int height =  (int)(1.0 *width / options.outWidth * options.outHeight);
                        view.setLayoutParams(new LinearLayout.LayoutParams(width,height));
                    }else{
                        //否则就重新请求
                        DownloadImage downloadImage = new DownloadImage(view,widthPixels,(1.0f/dataSize));
                        downloadImage.execute(data.shopImageUrl);
                    }
                    //加载图片
                    Phoenix.with(view)
                            .load(data.shopImageUrl);
                }
                dataBinding.rootlayout.addView(view);
            }
        }else{
            DataModel.Data data = (DataModel.Data) mList.get(position);
            final ShopIndexPicItemBinding dataBinding = (ShopIndexPicItemBinding) holder.getBinding();
            //加载图片
            Phoenix.with(dataBinding.pic1)
                    .load(data.shopImageUrl);
            dataBinding.updatecommodity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,SelectCommodityActivity.class);
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
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener){
        this.mItemClickListener = onItemClickListener;
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}
