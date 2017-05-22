package com.coolwin.XYT;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.OpenRedpacketUser;
import com.coolwin.XYT.adapter.RedPacketListAdapter;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;

import java.util.List;

/**
 *发红包
 * @author dongli
 *
 */
public class RedPacketListActivity extends Activity implements View.OnClickListener {
	public Context mContext;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	List<OpenRedpacketUser> openRedpacketUser;
	String money;
	private ImageLoader mImageLoader;
	Login login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		login = IMCommon.getLoginResult(mContext);
		openRedpacketUser = (List<OpenRedpacketUser>) getIntent().getSerializableExtra("data");
		money = getIntent().getStringExtra("money");
		setContentView(R.layout.activity_chat_packet_list);
		mImageLoader = new ImageLoader();
		initComponent();
	}
	ImageView redpackethead;
	TextView redpacketname,closeredpacket,moneyTextView;
	ListView listuser;
	public void initComponent(){
		redpackethead = (ImageView) findViewById(R.id.redpackethead);
		redpacketname = (TextView) findViewById(R.id.redpacketname);
		closeredpacket= (TextView) findViewById(R.id.closeredpacket);
		listuser = (ListView) findViewById(R.id.listuser);
		moneyTextView= (TextView) findViewById(R.id.money);
		closeredpacket.setOnClickListener(this);
		redpacketname.setText(login.nickname);
		mImageLoader.getBitmap(mContext, redpackethead,null, login.headsmall,0,false,false);
		listuser.setAdapter(new RedPacketListAdapter(mContext,openRedpacketUser));
		moneyTextView.setText(money+"元");
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.closeredpacket:
				this.finish();
				break;
			default:
				break;
		}
	}
}
