package com.coolwin.XYT.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolwin.XYT.BR;
import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.FriendsLoopNoneBinding;
import com.coolwin.XYT.databinding.FriendsLoopOnebigpicBinding;
import com.coolwin.XYT.databinding.FriendsLoopOnepicBinding;
import com.coolwin.XYT.databinding.FriendsLoopPersionBinding;
import com.coolwin.XYT.databinding.FriendsLoopThreepicBinding;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.library.helper.MyRecycleViewHolder;
import com.facebook.fresco.helper.Phoenix;

import java.sql.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/5/31.
 */

public class FriensLoopFragmentAdapter extends BaseAdapter<FriendsLoopItem>{
    public int maxtype=100;
     enum ViewType{
        none,onepic,threepic,oneBigPic,persion
    }
    public FriensLoopFragmentAdapter(Context context, List<FriendsLoopItem> mList) {
        super(context);
        this.mList = mList;
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewType type = ViewType.values()[viewType];
        ViewDataBinding dataBinding;
        switch(type){
            case onepic:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.friends_loop_onepic, parent, false);
                break;
            case threepic:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.friends_loop_threepic, parent, false);
                break;
            case oneBigPic:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.friends_loop_onebigpic, parent, false);
                break;
            case persion:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.friends_loop_persion, parent, false);
                break;
            default:
                dataBinding =  DataBindingUtil.
                        inflate(LayoutInflater.from(context), R.layout.friends_loop_none, parent, false);
                break;
        }
        MyRecycleViewHolder  vh = new MyRecycleViewHolder(dataBinding.getRoot());
        vh.setBinding(dataBinding);
        return vh;
    }


    @Override
    protected void bindData(final MyRecycleViewHolder holder, final int position) {
        final ViewDataBinding dataBinding  = holder.getBinding();
        dataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null) {
                    mItemClickListener.onItemClick(null,dataBinding.getRoot(),holder.getLayoutPosition(),position);
                }
            }
        });
        final FriendsLoopItem friendsLoopItem = mList.get(position);
        if (dataBinding instanceof FriendsLoopOnepicBinding) {
            FriendsLoopOnepicBinding friendsLoopOnepicBinding = (FriendsLoopOnepicBinding) dataBinding;
            friendsLoopOnepicBinding.playVideoTime.setVisibility(View.GONE);
            if(friendsLoopItem.listpic!=null && friendsLoopItem.listpic.size()>0){
                Phoenix.with(friendsLoopOnepicBinding.pic)
                        .load(friendsLoopItem.listpic.get(0).smallUrl);
            }else{
                final Video video = Video.getInfo(friendsLoopItem.video);
                Phoenix.with(friendsLoopOnepicBinding.pic)
                        .load(video.image);
                friendsLoopOnepicBinding.playVideoTime.setText(video.time);
                friendsLoopOnepicBinding.playVideoTime.setVisibility(View.VISIBLE);
            }
        } else if(dataBinding instanceof FriendsLoopThreepicBinding){
            FriendsLoopThreepicBinding friendsLoopThreepicBinding = (FriendsLoopThreepicBinding) dataBinding;
            Phoenix.with(friendsLoopThreepicBinding.pic1)
                    .load(friendsLoopItem.listpic.get(0).smallUrl);
            Phoenix.with(friendsLoopThreepicBinding.pic2)
                    .load(friendsLoopItem.listpic.get(1).smallUrl);
            Phoenix.with(friendsLoopThreepicBinding.pic3)
                    .load(friendsLoopItem.listpic.get(2).smallUrl);
        }else if(dataBinding instanceof FriendsLoopOnebigpicBinding){
            FriendsLoopOnebigpicBinding friendsLoopOnebigpicBinding = (FriendsLoopOnebigpicBinding) dataBinding;
            friendsLoopOnebigpicBinding.playVideoBtn.setVisibility(View.GONE);
            friendsLoopOnebigpicBinding.playVideoTime.setVisibility(View.GONE);
            if(friendsLoopItem.listpic!=null && friendsLoopItem.listpic.size()>0){
                Phoenix.with(friendsLoopOnebigpicBinding.pic)
                        .load(friendsLoopItem.listpic.get(0).smallUrl);
            }else{
                final Video video = Video.getInfo(friendsLoopItem.video);
                Phoenix.with(friendsLoopOnebigpicBinding.pic)
                        .load(video.image);
                friendsLoopOnebigpicBinding.playVideoTime.setText(video.time);
                friendsLoopOnebigpicBinding.playVideoBtn.setVisibility(View.VISIBLE);
                friendsLoopOnebigpicBinding.playVideoTime.setVisibility(View.VISIBLE);
            }
        }else if(dataBinding instanceof FriendsLoopPersionBinding){
            FriendsLoopPersionBinding friendsLoopPersionBinding = (FriendsLoopPersionBinding) dataBinding;
            Phoenix.with(friendsLoopPersionBinding.friendsIcon)
                    .load(friendsLoopItem.headsmall);
            friendsLoopPersionBinding.setCompany(friendsLoopItem.company);
            friendsLoopPersionBinding.setJob(friendsLoopItem.job);
            friendsLoopPersionBinding.playVideoBtn.setVisibility(View.GONE);
            friendsLoopPersionBinding.playVideoTime.setVisibility(View.GONE);
            if(friendsLoopItem.listpic!=null && friendsLoopItem.listpic.size()>0){
                Phoenix.with(friendsLoopPersionBinding.pic)
                        .load(friendsLoopItem.listpic.get(0).smallUrl);
            }else{
                final Video video = Video.getInfo(friendsLoopItem.video);
                Phoenix.with(friendsLoopPersionBinding.pic)
                        .load(video.image);
                friendsLoopPersionBinding.playVideoTime.setText(video.time);
                friendsLoopPersionBinding.playVideoBtn.setVisibility(View.VISIBLE);
                friendsLoopPersionBinding.playVideoTime.setVisibility(View.VISIBLE);
            }
        }else if(dataBinding instanceof FriendsLoopNoneBinding){
            FriendsLoopNoneBinding friendsLoopNoneBinding = (FriendsLoopNoneBinding) dataBinding;
            friendsLoopNoneBinding.url.setVisibility(View.GONE);
            if(StringUtil.isNull(friendsLoopItem.content)){
                friendsLoopNoneBinding.contexttext.setVisibility(View.GONE);
            }else{
                friendsLoopNoneBinding.contexttext.setVisibility(View.VISIBLE);
            }
            if (!StringUtil.isNull(friendsLoopItem.url)) {
                Phoenix.with(friendsLoopNoneBinding.imageUrl)
                        .load(friendsLoopItem.imageurl);
                friendsLoopNoneBinding.setUrltext(friendsLoopItem.title);
                friendsLoopNoneBinding.url.setVisibility(View.VISIBLE);
                friendsLoopNoneBinding.url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(WebViewActivity.WEBURL,friendsLoopItem.url);
                        intent.setClass(context, WebViewActivity.class);
                        context.startActivity(intent);
                    }
                });
            }
            if(friendsLoopItem.listpic!=null && friendsLoopItem.listpic.size()>0){
                friendsLoopNoneBinding.pic.setVisibility(View.VISIBLE);
                Phoenix.with(friendsLoopNoneBinding.pic)
                        .load(friendsLoopItem.listpic.get(0).smallUrl);
            }else{
                friendsLoopNoneBinding.pic.setVisibility(View.GONE);
            }
        }
        dataBinding.setVariable(BR.name,friendsLoopItem.nickname);
        dataBinding.setVariable(BR.context,friendsLoopItem.content);
        dataBinding.setVariable(BR.time,StringUtil.calculaterReleasedTime(context,new Date(friendsLoopItem.createtime*1000)));
        if (friendsLoopItem.replylist!=null) {
            dataBinding.setVariable(BR.commentCount,friendsLoopItem.replylist.size()+"评论");
        }else{
            dataBinding.setVariable(BR.commentCount,"0评论");
        }
        if (friendsLoopItem.praiselist!=null) {
            dataBinding.setVariable(BR.praiseCount,friendsLoopItem.praiselist.size()+"人赞");
        }else{
            dataBinding.setVariable(BR.praiseCount,"0人赞");
        }

    }
    @Override
    public int getItemViewType(int position) {
        FriendsLoopItem friendsLoopItem = mList.get(position);
        if ((friendsLoopItem.listpic==null ||  friendsLoopItem.listpic.size()==0)
                && StringUtil.isNull(friendsLoopItem.video) ) {
            return ViewType.none.ordinal();
        }
        if(StringUtil.isNull(friendsLoopItem.content)){
            return ViewType.none.ordinal();
        }
        if ((friendsLoopItem.praiselist!=null && friendsLoopItem.praiselist.size()>=maxtype)
                && (friendsLoopItem.replylist!=null && friendsLoopItem.replylist.size()>=maxtype)) {
            return ViewType.persion.ordinal();
        }
        if ((friendsLoopItem.praiselist!=null && friendsLoopItem.praiselist.size()>=maxtype)
                || (friendsLoopItem.replylist!=null && friendsLoopItem.replylist.size()>=maxtype)) {
            return ViewType.oneBigPic.ordinal();
        }
        if(!StringUtil.isNull(friendsLoopItem.video)){
            return ViewType.onepic.ordinal();
        }
        if (friendsLoopItem.listpic!=null&& friendsLoopItem.listpic.size()<3) {
            return ViewType.onepic.ordinal();
        }
        if (friendsLoopItem.listpic!=null&& friendsLoopItem.listpic.size()>3) {
            return ViewType.threepic.ordinal();
        }
        return ViewType.none.ordinal();
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}
