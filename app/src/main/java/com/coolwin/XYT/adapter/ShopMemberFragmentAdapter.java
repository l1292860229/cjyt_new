package com.coolwin.XYT.adapter;

import android.content.Context;
import android.view.View;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.ShopMemberItemBinding;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */

public class ShopMemberFragmentAdapter extends BaseAdapter<Login> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public ShopMemberFragmentAdapter(Context context, List<Login> logins) {
        super(context);
        this.mList = logins;
    }
    @Override
    public int getLayoutId() {
        return R.layout.shop_member_item;
    }

    @Override
    public void bindData(MyRecycleViewHolder holder, int position) {
        Login login = mList.get(position);
        ShopMemberItemBinding binding = (ShopMemberItemBinding) holder.getBinding();
        binding.setNametext(login.nickname);
        binding.setDjtext("等级:"+login.userDj);
        binding.setProvincetext(login.provinceid);
        binding.setTimetext(simpleDateFormat.format(new Date(login.createtime)));
        if(login.headsmall.contains("http")){
            Phoenix.with(binding.userIcon)
                    .load(login.headsmall);
        }else{
            Phoenix.with(binding.userIcon)
                    .load("http://shopxx.wei20.cn/gouwu/wish3d/"+login.headsmall);
        }
        if(StringUtil.isNull(login.provinceid.trim())){
            binding.province.setVisibility(View.GONE);
        }
    }
}
