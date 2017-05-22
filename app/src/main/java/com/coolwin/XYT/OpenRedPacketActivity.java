package com.coolwin.XYT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.GetRedpacketMoney;
import com.coolwin.XYT.Entity.RedpacketDetails;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;
import com.coolwin.XYT.net.IMException;

/**
 *发红包
 * @author dongli
 *
 */
public class OpenRedPacketActivity extends Activity implements View.OnClickListener {
	public Context mContext;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	RedpacketDetails redpacketDetails;
	private ImageLoader mImageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		redpacketDetails = (RedpacketDetails) getIntent().getSerializableExtra("data");
		setContentView(R.layout.open_activity_chat_packet);
		mImageLoader = new ImageLoader();
		initComponent();
	}
	ImageView redpackethead;
	TextView redpacketname,closeredpacket,question,prompt;
	EditText answer;
	Button button;
	public void initComponent(){
		redpackethead = (ImageView) findViewById(R.id.redpackethead);
		redpacketname = (TextView) findViewById(R.id.redpacketname);
		closeredpacket= (TextView) findViewById(R.id.closeredpacket);
		question= (TextView) findViewById(R.id.question);
		prompt= (TextView) findViewById(R.id.prompt);
		answer= (EditText) findViewById(R.id.answer);
		button= (Button) findViewById(R.id.submit);
		button.setOnClickListener(this);
		closeredpacket.setOnClickListener(this);
		redpacketname.setText(redpacketDetails.hname);
		mImageLoader.getBitmap(mContext, redpackethead,null, redpacketDetails.hpic,0,false,false);
		question.setText(redpacketDetails.question);
		prompt.setText(redpacketDetails.tips);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.closeredpacket:
				this.finish();
				break;
			case R.id.submit:
				final String answerStr = answer.getText().toString();
				if(answerStr.equals("")){
					Toast.makeText(mContext,"请输入答案", Toast.LENGTH_SHORT).show();
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							GetRedpacketMoney getRedpacketMoney = IMCommon.getIMInfo().getRedpacketMoney(redpacketDetails.id,answerStr);
							if (getRedpacketMoney.msg.equals("err")) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mContext,"答案错误", Toast.LENGTH_SHORT).show();
									}
								});
							}else{
								Intent intent = new Intent(mContext, RedPacketListActivity.class);
								intent.putExtra("data",  getRedpacketMoney.list);
								intent.putExtra("money",getRedpacketMoney.money);
								startActivity(intent);
								overridePendingTransition(0, 0);
								finish();
								overridePendingTransition(0, 0);
							}
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
				break;
			default:
				break;
		}
	}
}
