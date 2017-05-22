package com.coolwin.XYT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.global.ImageLoader;

/**邀请进入论坛
 * @author dongli
 *
 */
public class JoinBBSActivity extends BaseActivity implements View.OnClickListener {
	private Bbs bbs;
	private ImageView headsmallView;
	private TextView titleView,contentView,bbsMoneyView;
	private Button joinbtn,isjoinbtn;
	private ImageLoader mImageLoader;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mImageLoader = new ImageLoader();
		setContentView(R.layout.join_bbs_page);
		initComponent();
	}
	/*
	 * 示例化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn, 0, 0);
		mLeftBtn.setOnClickListener(this);
		bbs = (Bbs)getIntent().getSerializableExtra("data");
		headsmallView = (ImageView) findViewById(R.id.headsmall);
		titleView = (TextView) findViewById(R.id.bbs_title);
		contentView = (TextView) findViewById(R.id.bbs_content);
		bbsMoneyView= (TextView) findViewById(R.id.bbs_money);
		joinbtn = (Button) findViewById(R.id.join);
		isjoinbtn = (Button) findViewById(R.id.isjoin);
		titleView.setText(bbs.title);
		contentView.setText(bbs.content);
		if (bbs.headsmall!=null && !bbs.headsmall.equals("")) {
			mImageLoader.getBitmap(mContext, headsmallView, null, bbs.headsmall, 0, false, true);
		}
		bbsMoneyView.setText("￥"+bbs.money);
		if(bbs.isjoin==1){
			isjoinbtn.setVisibility(View.VISIBLE);
			joinbtn.setVisibility(View.GONE);
		}else{
			joinbtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showModifybgDialog(bbs);
				}
			});
		}

	}
	private void showModifybgDialog(final Bbs bbs){
		if(!bbs.money.equals("0")){
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setIcon(R.drawable.ic_dialog_alert);
			builder.setTitle("你申请的群不是免费的,是否支付费用");
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					new Thread(){
						@Override
						public void run(){
							if(IMCommon.verifyNetwork(mContext)){
								try {
									IMCommon.getIMInfo().addBbsUser(bbs.id);
									return;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}.start();
					Toast.makeText(mContext,"申请成功", Toast.LENGTH_LONG).show();
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			builder.create().show();
		}else{
			new Thread(){
				@Override
				public void run(){
					if(IMCommon.verifyNetwork(mContext)){
						try {
							IMCommon.getIMInfo().addBbsUser(bbs.id);
							return;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			Toast.makeText(mContext,"申请成功", Toast.LENGTH_LONG).show();
		}
	}
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
