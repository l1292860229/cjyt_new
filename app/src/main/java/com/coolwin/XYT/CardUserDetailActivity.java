package com.coolwin.XYT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolwin.XYT.Entity.Card;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.MessageInfo;
import com.coolwin.XYT.Entity.MessageType;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;

import java.util.UUID;

/**
 * 选择的名片用户详情
 * @author dongli
 *
 */
public class CardUserDetailActivity extends BaseActivity {
	
	/*
	 * 定义全局变量
	 */
	private Login mCardLogin,mToLogin,mLogin;
	private ImageView mUserIcon,mToUserIcon;
	private TextView mUserNickName,mUserSign,mToUserNickName,mToUserSign;
	
	private ImageLoader mImageLoader;
	private int mTypeChat = 100; 

	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.card_user_detail_view);
		mCardLogin = (Login) getIntent().getSerializableExtra("user");
		mToLogin = (Login)getIntent().getSerializableExtra("toLogin");
		mLogin = IMCommon.getLoginResult(mContext);
		mTypeChat = getIntent().getIntExtra("typechat",100);
		mImageLoader = new ImageLoader();
		initCompent();
	}
	
	/*
	 * 示例化控件
	 */
	private void initCompent(){
		setTitleContent(R.drawable.back_btn, R.drawable.ok_btn, R.string.recommend_to_friend);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		
		mUserIcon = (ImageView)findViewById(R.id.user_icon);
		mToUserIcon = (ImageView)findViewById(R.id.to_user_icon);
		mUserNickName = (TextView)findViewById(R.id.user_nickname);
		mUserSign = (TextView)findViewById(R.id.user_sign);
		mToUserNickName = (TextView)findViewById(R.id.to_user_nickname);
		mToUserSign = (TextView)findViewById(R.id.to_user_sign);
		if(mCardLogin!=null){
			if(mCardLogin.headsmall!=null && !mCardLogin.headsmall.equals("")){
				mImageLoader.getBitmap(mContext, mUserIcon, null,mCardLogin.headsmall,0,false,true);
			}
			mUserNickName.setText(mCardLogin.nickname);
			mUserSign.setText(mCardLogin.sign);
		}
		
		if(mToLogin!=null){
			if(mToLogin.headsmall!=null && !mToLogin.headsmall.equals("")){
				mImageLoader.getBitmap(mContext, mToUserIcon, null,mToLogin.headsmall,0,false,true);
			}
			mToUserNickName.setText(mToLogin.nickname);
			mToUserSign.setText(mToLogin.sign);
		}
	}

	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			CardUserDetailActivity.this.finish();
			break;
		case R.id.right_btn://确认选择该名片
			if(mCardLogin == null ||  mToLogin == null){
				return;
			}
						
			Card card = new Card(mCardLogin.phone, mCardLogin.headsmall, mCardLogin.nickname, mCardLogin.sign);
			
			MessageInfo msg = new MessageInfo();
			msg.fromid = IMCommon.getUserPhone(mContext);
			msg.tag = UUID.randomUUID().toString();
			msg.fromname = mLogin.nickname;
			msg.fromurl = mLogin.headsmall;
			msg.toid = mToLogin.phone;
			msg.toname = mToLogin.nickname;
			msg.tourl = mToLogin.headsmall;
			msg.typefile = MessageType.CARD;
			msg.typechat = mTypeChat;
			msg.content = Card.getInfo(card);
			msg.time = System.currentTimeMillis();
			msg.readState = 1;
			
			Intent intent = new Intent(ChatMainActivity.ACTION_RECOMMEND_CARD);
			intent.putExtra("cardMsg", msg);
			sendBroadcast(intent);
			sendBroadcast(new Intent(ChooseUserActivity.ACTION_DESTROY_ACTIVITY));
			CardUserDetailActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	

}
