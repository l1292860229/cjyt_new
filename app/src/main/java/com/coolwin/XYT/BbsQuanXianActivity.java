package com.coolwin.XYT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;

import static com.coolwin.XYT.global.IMCommon.getIMInfo;

/**
 * 论坛详细页
 * @author dongli
 *
 */
public class BbsQuanXianActivity extends BaseActivity implements View.OnClickListener {

	/*
	 * 定义全局变量
	 */
	private static final int MODIFY_REQUEST = 5124;
	private Bbs bbs;
	private Login mLogin;
	private ToggleButton quanguanBtn,delchatBtn,deldynamicBtn;
	private LinearLayout userinfoLayout,delchatlayout,deldynamicLayout,delUser;
	final private int MSG_RESIZE = 0;
	private BbsReplyInfo mbbsReplyInfo;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.bbs_quanxian_page);
		initComponent();
	}
	/*
	 * 示例化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn, 0, "设置");
		mLeftBtn.setOnClickListener(this);
		bbs = (Bbs)getIntent().getSerializableExtra("data");
		mbbsReplyInfo  = (BbsReplyInfo)getIntent().getSerializableExtra("bbsReplyInfo");
		mLogin = IMCommon.getLoginResult(mContext);
		userinfoLayout = (LinearLayout) findViewById(R.id.userinfo);
		delchatlayout = (LinearLayout) findViewById(R.id.delchat_layout);
		deldynamicLayout = (LinearLayout) findViewById(R.id.deldynamic_layout);
		delUser = (LinearLayout) findViewById(R.id.del_user);
		quanguanBtn = (ToggleButton) findViewById(R.id.is_quanguan_btn);
		delchatBtn = (ToggleButton) findViewById(R.id.is_delchat_btn);
		deldynamicBtn= (ToggleButton) findViewById(R.id.is_deldynamic_btn);
		userinfoLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserInfoActivity.class);
				intent.putExtra("uid", mbbsReplyInfo.uid);
				intent.putExtra("type", 2);
				intent.putExtra("bbs", bbs);
				mContext.startActivity(intent);
			}
		});
		delUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							getIMInfo().delBbsUser(bbs.id,mbbsReplyInfo.phone);
							Message ms = new Message();
							ms.what= MSG_RESIZE;
							mHandler.sendMessage(ms);
							Intent intent = new Intent();
							intent.putExtra("uid",mbbsReplyInfo.uid);
							setResult(RESULT_OK,intent);
							BbsQuanXianActivity.this.finish();
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		if (mbbsReplyInfo.power==1) {
			delchatlayout.setVisibility(View.VISIBLE);
			deldynamicLayout.setVisibility(View.VISIBLE);
			if(mbbsReplyInfo.delchat==1){
				delchatBtn.setChecked(true);
			}
			if(mbbsReplyInfo.deldynamic==1){
				deldynamicBtn.setChecked(true);
			}
			quanguanBtn.setChecked(true);
		}
		quanguanBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								getIMInfo().updatebbsUserPower(bbs.id,mLogin.uid,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
					delchatlayout.setVisibility(View.VISIBLE);
					deldynamicLayout.setVisibility(View.VISIBLE);
				}else{
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								getIMInfo().updatebbsUserPower(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
					delchatlayout.setVisibility(View.GONE);
					deldynamicLayout.setVisibility(View.GONE);
				}
			}
		});
		delchatBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								IMCommon.getIMInfo().updatebbsUserdelChat(bbs.id,mLogin.uid,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}else{
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								getIMInfo().updatebbsUserdelChat(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		deldynamicBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								IMCommon.getIMInfo().updatebbsUserdeldynamic(bbs.id,mLogin.uid,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}else{
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsReplyInfoList bl =  getIMInfo().updatebbsUserdeldynamic(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_RESIZE:
					Toast.makeText(mContext,"设置成功" , Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}

	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_btn:
				this.finish();
				break;
				default:
					break;
		}
	}
}
