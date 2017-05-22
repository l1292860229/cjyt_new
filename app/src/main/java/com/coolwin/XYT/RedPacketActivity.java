package com.coolwin.XYT;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;

/**
 *发红包
 * @author dongli
 *
 */
public class RedPacketActivity extends BaseActivity implements View.OnClickListener {
	private String redpakcetcount,redpakcetmoney,redpakcetquestion,
		redpakcetanswer,redpakcettips;
	EditText etMoneyCount,etMoneyAmount,etQuestion,etAnswer,etTips;
	Button btnPutMoney;
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.wqdsoft.im.BaseActivity#onCreate(android.os.Bundle)
	 */
	private String address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		address = getIntent().getStringExtra("data");
		Log.e("onCreate","address="+address);
		setContentView(R.layout.activity_chat_packet);
		initComponent();
	}
	public void initComponent(){
		setTitleContent(R.drawable.back_btn, 0, "发红包");
		mLeftBtn.setOnClickListener(this);
		etMoneyCount = (EditText) findViewById(R.id.et_money_count);
		etMoneyAmount = (EditText) findViewById(R.id.et_money_amount);
		etQuestion = (EditText) findViewById(R.id.et_question);
		etAnswer = (EditText) findViewById(R.id.et_answer);
		etTips = (EditText) findViewById(R.id.et_tips);
		myTextWatcher myTextWatcher = new myTextWatcher();
		etMoneyCount.addTextChangedListener(myTextWatcher);
		etMoneyAmount.addTextChangedListener(myTextWatcher);
		etQuestion.addTextChangedListener(myTextWatcher);
		etAnswer.addTextChangedListener(myTextWatcher);
		etTips.addTextChangedListener(myTextWatcher);
		btnPutMoney = (Button) findViewById(R.id.btn_group_put_money);
		btnPutMoney.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.left_btn:
				this.finish();
				break;
			case R.id.btn_group_put_money:
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							boolean issuccess =  IMCommon.getIMInfo().sendRedpacket(redpakcetmoney,redpakcetcount,redpakcetquestion,
                                    redpakcetanswer,redpakcettips,address);
							if(issuccess){
								setResult(RESULT_OK);
								finish();
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mContext,"发送失败", Toast.LENGTH_SHORT).show();
									}
								});
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
	class myTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			redpakcetcount = etMoneyCount.getText().toString();
			redpakcetmoney = etMoneyAmount.getText().toString();
			redpakcetquestion = etQuestion.getText().toString();
			redpakcetanswer = etAnswer.getText().toString();
			redpakcettips = etTips.getText().toString();
			btnPutMoney.setEnabled(false);
			if(redpakcetcount==null || redpakcetcount.equals("")){
				return;
			}
			if(redpakcetmoney==null || redpakcetmoney.equals("")){
				return;
			}
			if(redpakcetquestion==null || redpakcetquestion.equals("")){
				return;
			}
			if(redpakcetanswer==null || redpakcetanswer.equals("")){
				return;
			}
			if(redpakcettips==null || redpakcettips.equals("")){
				return;
			}
			btnPutMoney.setEnabled(true);
		}
		@Override
		public void afterTextChanged(Editable s) {}
	}
}
