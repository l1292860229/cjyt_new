package com.coolwin.XYT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;

import static com.coolwin.XYT.R.id.join_persion;
import static com.coolwin.XYT.global.IMCommon.getIMInfo;

/**
 * 论坛详细页
 * @author dongli
 *
 */
public class BbsDetailActivity extends BaseActivity implements View.OnClickListener {

	/*
	 * 定义全局变量
	 */
	private static final int MODIFY_REQUEST = 5124;
	private Bbs bbs;
	private Login mLogin;
	private TextView bbsTitleTextView,bbsContentTextView,bbsNotSpeakPersion,joinPersion,setMoney
			,setTitle,setContent;
	private ToggleButton bbsCloseBtn,bbsNotSpeakBtn,visitorsBtn,closefriendloopBtn,getmsgBtn;
	private LinearLayout bbsSettingLayout,visitorsLayout,closefriendloopLayout,eweimaLayout,getmsglayout;
	final private int MSG_RESIZE = 0;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.bbs_detail_page);
		initComponent();
	}
	/*
	 * 示例化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn, 0, "贴子详细");
		mLeftBtn.setOnClickListener(this);
		bbs = (Bbs)getIntent().getSerializableExtra("data");
		mLogin = IMCommon.getLoginResult(mContext);
		bbsSettingLayout = (LinearLayout) findViewById(R.id.bbs_setting_layout);
		bbsTitleTextView = (TextView) findViewById(R.id.bbstitle);
		bbsContentTextView = (TextView) findViewById(R.id.bbscontent);
		bbsNotSpeakPersion = (TextView) findViewById(R.id.not_speak_persion);
		setTitle = (TextView) findViewById(R.id.set_title);
		setContent = (TextView) findViewById(R.id.set_content);
		joinPersion = (TextView) findViewById(join_persion);
		setMoney = (TextView) findViewById(R.id.set_money);
		visitorsLayout = (LinearLayout) findViewById(R.id.visitors_layout);
		closefriendloopLayout = (LinearLayout) findViewById(R.id.closefriendloop_layout);
		eweimaLayout= (LinearLayout) findViewById(R.id.bbs_eweima);
		getmsglayout= (LinearLayout) findViewById(R.id.getmsglayout);
		eweimaLayout.setOnClickListener(this);
		if(bbs.type==1){
			joinPersion.setVisibility(View.VISIBLE);
			setMoney.setVisibility(View.VISIBLE);
			visitorsLayout.setVisibility(View.VISIBLE);
			closefriendloopLayout.setVisibility(View.VISIBLE);
			eweimaLayout.setVisibility(View.VISIBLE);
			setTitle.setVisibility(View.VISIBLE);
			setContent.setVisibility(View.VISIBLE);
			getmsglayout.setVisibility(View.VISIBLE);
		}
		setMoney.setText("设置加群费(当前费用:￥"+bbs.money+")");
		bbsNotSpeakPersion.setOnClickListener(this);
		joinPersion.setOnClickListener(this);
		setMoney.setOnClickListener(this);
		setTitle.setOnClickListener(this);
		setContent.setOnClickListener(this);
		bbsCloseBtn = (ToggleButton) findViewById(R.id.bbs_close_btn);
		bbsNotSpeakBtn = (ToggleButton) findViewById(R.id.not_speak_btn);
		visitorsBtn= (ToggleButton) findViewById(R.id.visitors_btn);
		closefriendloopBtn= (ToggleButton) findViewById(R.id.closefriendloop_btn);
		getmsgBtn= (ToggleButton) findViewById(R.id.getmsg_btn);
		bbsTitleTextView.setText(bbs.title);
		bbsContentTextView.setText(bbs.content);
		if(bbs.uid.equals(mLogin.uid)){
			bbsSettingLayout.setVisibility(View.VISIBLE);
		}else{
			bbsSettingLayout.setVisibility(View.GONE);
		}
		if(bbs.speakStatus!=0){
			bbsNotSpeakBtn.setChecked(true);
		}
		if(bbs.status!=1){
			bbsCloseBtn.setChecked(true);
		}
		if(bbs.isVisitors==1){
			visitorsBtn.setChecked(true);
		}
		if(bbs.isclosefriedloop==1){
			closefriendloopBtn.setChecked(true);
		}
		if(bbs.getmsg==1){
			getmsgBtn.setChecked(true);
		}
		bbsNotSpeakBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsReplyInfoList bl =  getIMInfo().editBbsSpeakStatus(bbs.id,mLogin.uid,"2");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.speakStatus = 1;
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
								BbsReplyInfoList bl =  getIMInfo().editBbsSpeakStatus(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.speakStatus = 0;
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		getmsgBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsReplyInfoList bl =  IMCommon.getIMInfo().editBbsGetMsg(mLogin.uid,bbs.id,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.getmsg=1;
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
								BbsReplyInfoList bl =  getIMInfo().editBbsGetMsg(mLogin.uid,bbs.id,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.getmsg=0;
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		bbsCloseBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsReplyInfoList bl =  IMCommon.getIMInfo().editBbsStatus(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.status=0;
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
								BbsReplyInfoList bl =  getIMInfo().editBbsStatus(bbs.id,mLogin.uid,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.status=1;
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		visitorsBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsReplyInfoList bl =  IMCommon.getIMInfo().editBbsVisitors(bbs.id,mLogin.uid,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								mHandler.sendMessage(ms);
								bbs.isVisitors = 1;
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
								BbsReplyInfoList bl =  getIMInfo().editBbsVisitors(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								bbs.isVisitors = 0;
								mHandler.sendMessage(ms);
							} catch (IMException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});
		closefriendloopBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsReplyInfoList bl =  IMCommon.getIMInfo().editBbsclosefriendloop(bbs.id,mLogin.uid,"1");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								bbs.isclosefriedloop=1;
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
								BbsReplyInfoList bl =  getIMInfo().editBbsclosefriendloop(bbs.id,mLogin.uid,"0");
								Message ms = new Message();
								ms.what= MSG_RESIZE;
								ms.obj =bl.mState;
								bbs.isclosefriedloop=0;
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
					IMJiaState result = (IMJiaState)msg.obj;
					Toast.makeText(mContext,result.errorMsg , Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}

	};
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
			case R.id.left_btn:
				intent.putExtra("bbs",bbs);
				setResult(RESULT_OK,intent);
				this.finish();
				break;
			case R.id.not_speak_persion:
				intent = new Intent(mContext, bbsProhibitionActivity.class);
				intent.putExtra("bbs",bbs);
				startActivity(intent);
				break;
			case join_persion:
				intent = new Intent(mContext, bbsUserActivity.class);
				intent.putExtra("bbs",bbs);
				intent.putExtra("isallow","1");
				startActivity(intent);
				break;
			case R.id.set_money:
				final EditText et= new EditText(this);
				et.setText(bbs.money);
				et.setInputType(InputType.TYPE_CLASS_NUMBER);
				new AlertDialog.Builder(this)
						.setTitle("请输入")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(et)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								bbs.money = et.getText().toString();
								setMoney.setText("设置加群费(当前费用:￥"+bbs.money+")");
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											IMCommon.getIMInfo().editBbsMoney(bbs.id,mLogin.uid,bbs.money);
										} catch (IMException e) {
											e.printStackTrace();
										}
									}
								}).start();
							}
						})
						.setNegativeButton("取消", null).show();
				break;
			case R.id.set_title:
				final EditText titleet= new EditText(this);
				titleet.setText(bbs.title);
				new AlertDialog.Builder(this)
						.setTitle("请输入")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(titleet)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								bbs.title = titleet.getText().toString();
								bbsTitleTextView.setText(bbs.title);
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											IMCommon.getIMInfo().editBbsTitle(bbs.id,mLogin.uid,bbs.title);
										} catch (IMException e) {
											e.printStackTrace();
										}
									}
								}).start();
							}
						})
						.setNegativeButton("取消", null).show();
				break;
			case R.id.set_content:
				final EditText contentet= new EditText(this);
				contentet.setText(bbs.content);
				new AlertDialog.Builder(this)
						.setTitle("请输入")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(contentet)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								bbs.content = contentet.getText().toString();
								bbsContentTextView.setText(bbs.content);
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											IMCommon.getIMInfo().editBbsContent(bbs.id,mLogin.uid,bbs.content);
										} catch (IMException e) {
											e.printStackTrace();
										}
									}
								}).start();
							}
						})
						.setNegativeButton("取消", null).show();
				break;
			case R.id.bbs_eweima:
				intent = new Intent(mContext, BBSerweimaActivity.class);
				intent.putExtra("title","鱼塘二维码");
				intent.putExtra("headsmall",bbs.headsmall);
				intent.putExtra("titlevalue",bbs.title);
				intent.putExtra("content",bbs.content);
				intent.putExtra("url","http://pre.im/jmac?bid="+bbs.id);
				startActivity(intent);
				break;
				default:
					break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent();
		intent.putExtra("bbs",bbs);
		setResult(RESULT_OK,intent);
	}
}
