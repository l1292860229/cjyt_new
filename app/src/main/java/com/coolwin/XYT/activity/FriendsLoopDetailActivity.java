package com.coolwin.XYT.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;

import com.coolwin.XYT.Entity.CommentUser;
import com.coolwin.XYT.Entity.FriendsLoopItem;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Picture;
import com.coolwin.XYT.Entity.Video;
import com.coolwin.XYT.Entity.constant.StaticData;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.CommentMovingItemBinding;
import com.coolwin.XYT.databinding.ShareDetailViewBinding;
import com.coolwin.XYT.interfaceview.UIFriendsLoopDetail;
import com.coolwin.XYT.presenter.FriendsLoopDetailPresenter;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;
import com.coolwin.XYT.webactivity.WebViewActivity;
import com.coolwin.library.helper.DownloadImage;
import com.coolwin.library.view.ObservableScrollView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 分享详情
 */
public class FriendsLoopDetailActivity extends BaseActivity<FriendsLoopDetailPresenter> implements UIFriendsLoopDetail {

	public static final String DATA="data";
	FriendsLoopItem friendsLoopItem;
	ShareDetailViewBinding binding;
	Login mlogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this,R.layout.share_detail_view);
		friendsLoopItem  = (FriendsLoopItem) getIntent().getSerializableExtra(DATA);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		binding.titleLayout.title.setText("详情");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
		binding.titleLayout.llBar.setVisibility(View.GONE);
		mlogin = GetDataUtil.getLogin(context);

		//如始化数据
		binding.setName(friendsLoopItem.nickname);
		binding.setTime(StringUtil.calculaterReleasedTime(context,new Date(friendsLoopItem.createtime*1000)));
		binding.setContent(friendsLoopItem.content);
		binding.setJobtext(friendsLoopItem.job);
		binding.setCompanytext(friendsLoopItem.company);
		Phoenix.with(binding.friendsIcon)
				.load(friendsLoopItem.headsmall);
		Phoenix.with(binding.friendsIcon2)
				.load(friendsLoopItem.headsmall);
		//初始化视频
		if(!StringUtil.isNull(friendsLoopItem.video)){
			Video video = Video.getInfo(friendsLoopItem.video);
			MediaController mediaCtlr = new MediaController(this);
			mediaCtlr.setVisibility(View.INVISIBLE); //设置隐藏
			binding.video.setVisibility(View.VISIBLE);
			binding.video.setMediaController(mediaCtlr);
			binding.video.setVideoURI(Uri.parse(video.url));
			binding.video.start();
			binding.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
					mp.setLooping(true);
				}
			});
		}
		//初始化图片信息
		if(friendsLoopItem.listpic!=null && friendsLoopItem.listpic.size()>0){
			List<Picture> pictures = friendsLoopItem.listpic;
			for (Picture picture : pictures) {
				SimpleDraweeView view = new SimpleDraweeView(context);
				view.setImageResource(R.drawable.plugin_camera_no_pictures);
				//判断本地是否缓存网络图片的规格
				if(StaticData.imageViewOptions.containsKey(picture.originUrl)){
					BitmapFactory.Options options = StaticData.imageViewOptions.get(picture.originUrl);
					int width = widthPixels-StringUtil.dip2px(context,30);
					int height =  (int)(1.0 *width / options.outWidth * options.outHeight);
					view.setLayoutParams(new LinearLayout.LayoutParams(width,height));
				}else{
					//否则就重新请求
					DownloadImage downloadImage = new DownloadImage(view,widthPixels,1);
					downloadImage.execute(picture.originUrl);
				}
				//加载图片
				Phoenix.with(view)
						.load(picture.originUrl);
				binding.piclist.addView(view);
			}
		}
		//初始化链接
		if(!StringUtil.isNull(friendsLoopItem.url)){
			binding.url.setVisibility(View.VISIBLE);
			binding.setUrltext(friendsLoopItem.title);
			Phoenix.with(binding.imageUrl)
					.load(friendsLoopItem.imageurl);
			binding.url.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra(WebViewActivity.WEBURL,friendsLoopItem.url);
					intent.setClass(context, WebViewActivity.class);
					context.startActivity(intent);
				}
			});
		}
		//初始化赞信息
		if(friendsLoopItem.praiselist!=null && friendsLoopItem.praiselist.size()>0){
			setSharePraise();
		}
		//改变图标
		if(friendsLoopItem.ispraise == 1){
			binding.zanIcon.setImageResource(R.drawable.friends_loop_zan_icon);
		}
		binding.zanIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(friendsLoopItem.ispraise == 1){
					UIUtil.showMessage(context,"你已经赞过了");
				}else{
					mPresenter.sharePraise(friendsLoopItem.id);
				}
			}
		});
		//初始化评论信息
		if(friendsLoopItem.replylist!=null && friendsLoopItem.replylist.size()>0){
			setShareReply();
		}
		binding.scrollview.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView observableScrollView, int x, int y, int oldx, int oldy) {
				if((y-oldy>20) && binding.menubutton2.getVisibility()==View.VISIBLE){
					binding.menubutton2.setVisibility(View.GONE);
					binding.menubutton1.setVisibility(View.VISIBLE);
					UIUtil.closeKeybord(binding.edit,context);
					fuid = null;
					fname = null;
					binding.edit.setHint("说点什么");
				}
			}
		});
	}
	public void showShareReply(View view){
        binding.menubutton1.setVisibility(View.GONE);
        binding.menubutton2.setVisibility(View.VISIBLE);
		binding.edit.requestFocus();
		UIUtil.openKeybord(binding.edit,context);
    }
	String fuid,fname;
	public void sendReply(View view){
		String content = binding.edit.getText().toString();
		if(StringUtil.isNull(content)){
			return;
		}
		mPresenter.shareReply(content,friendsLoopItem.id,fname,fuid);
		binding.menubutton2.setVisibility(View.GONE);
		binding.menubutton1.setVisibility(View.VISIBLE);
		UIUtil.closeKeybord(binding.edit,context);
		fuid = null;
		fname = null;
		binding.edit.setHint("说点什么");
	}
	@Override
	public void showLoading() {
		super.showLoading("提交中...");
	}

	@Override
	public void sharePraise(String name) {
		if(friendsLoopItem.praiselist==null){
			friendsLoopItem.praiselist = new ArrayList<>();
		}
		friendsLoopItem.ispraise = 1;
		binding.zanIcon.setImageResource(R.drawable.friends_loop_zan_icon);
		CommentUser commentUser = new CommentUser();
		commentUser.nickname = name;
		friendsLoopItem.praiselist.add(commentUser);
		setSharePraise();
		UIUtil.showMessage(context,"赞了一下对方");
	}

	@Override
	public void shareReply(CommentUser commentUser) {
		if(friendsLoopItem.replylist==null){
			friendsLoopItem.replylist = new ArrayList<>();
		}
		friendsLoopItem.replylist.add(commentUser);
		binding.commentLayout.removeAllViews();
		setShareReply();
	}
	private void setSharePraise(){
		List<CommentUser> comments  = friendsLoopItem.praiselist;
		int size = comments.size()>10?10:comments.size();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(comments.get(i).nickname).append(",");
		}
		sb.append("等"+comments.size()+"个人点赞");
		binding.setZantext(sb.toString());
	}
	private void setShareReply(){
		List<CommentUser> comments  = friendsLoopItem.replylist;
		LayoutInflater mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		CommentMovingItemBinding commentMovingItemBinding;
		for (final CommentUser comment : comments) {
			commentMovingItemBinding = DataBindingUtil.inflate(mInflater,R.layout.comment_moving_item,null,false);
			commentMovingItemBinding.setName(comment.nickname);
			if(StringUtil.isNull(comment.fnickname) || comment.fuid.equals(comment.uid)){
				commentMovingItemBinding.contextview.setText(comment.content);
			}else{
				String str = "回复"+comment.fnickname+":"+comment.content;
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(new ClickableSpan() {
					@Override
					public void updateDrawState(TextPaint ds) {
						super.updateDrawState(ds);
						ds.setColor(getResources().getColor(R.color.application_friends_loop_user_name_lun));
						ds.setUnderlineText(false);      //设置下划线
					}
					@Override
					public void onClick(View widget) {
					}
				},2,comment.fnickname.length()+2 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				commentMovingItemBinding.contextview.setText(spannableString);
			}
			commentMovingItemBinding.setTimetext(StringUtil.calculaterReleasedTime(context,new Date(friendsLoopItem.createtime*1000)));
			Phoenix.with(commentMovingItemBinding.userIcon)
					.load(comment.headsmall);
			commentMovingItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					fuid = comment.uid;
					fname = comment.nickname;
					binding.edit.setHint("回复:"+comment.nickname);
					showShareReply(v);
				}
			});
			binding.commentLayout.addView(commentMovingItemBinding.getRoot());
		}
	}
}
