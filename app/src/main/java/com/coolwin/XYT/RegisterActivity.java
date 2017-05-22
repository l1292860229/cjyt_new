package com.coolwin.XYT;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.coolwin.XYT.Entity.IMJiaState;
import com.coolwin.XYT.Entity.LoginResult;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.org.json.JSONObject;

import static com.coolwin.XYT.global.IMCommon.getIMInfo;

/**
 * 新用户注册
 * @author dongli
 *
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	public final static int updateCode = 1;
	/**
	 * 定义全局变量
	 */
	private Button mBuyCodeBtn,sendCode;
	private EditText mPwdEdit;
	private String mInputPwd,mInputUsernick,mInputTjr,mInputTelephone;

	private EditText et_usernick;
	private EditText et_tjr;
	private EditText et_telephone;
	private EditText et_code;
	private int mTotalTime = 60;
	private ImageView iv_hide,iv_show;
	private String tempCode;
	/**
	 * 处理消息
	 */
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.REGISTER_REQUEST://检测注册结果
				LoginResult register = (LoginResult)msg.obj;
				if(register == null){
					Toast.makeText(mContext, mContext.getResources().getString(R.string.register_faile), Toast.LENGTH_LONG).show();
					return;
				}
				if(register.mState!=null){
					if(register.mState.code == 0){
						if (register.mLogin != null) {
							register.mLogin.password = mInputPwd;
							IMCommon.saveLoginResult(mContext, register.mLogin);
							IMCommon.setUid(register.mLogin.uid);
							IMCommon.setPhone(register.mLogin.phone);
						}

						Editor editor = mContext.getSharedPreferences(IMCommon.REMENBER_SHARED, 0).edit();
						editor.putBoolean(IMCommon.ISREMENBER, true);
						editor.putString(IMCommon.USERNAME, mInputTelephone);
						editor.putString(IMCommon.PASSWORD, mInputPwd);
						editor.commit();

						setResult(1);//注册成功释放登陆界面
						mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_REGISTER_REQUEST));
						RegisterActivity.this.finish();
					}else{
						Toast.makeText(mContext, register.mState.errorMsg, Toast.LENGTH_LONG).show();
					}
				}
				break;
			case GlobalParam.MSG_CHECK_VALID_ERROR:
				IMJiaState validState = (IMJiaState)msg.obj;
				if(validState == null){
					Toast.makeText(mContext,mContext.getResources().getString(R.string.commit_data_error), Toast.LENGTH_LONG).show();
					return;
				}
				String validErrorMsg = validState.errorMsg;
				if(validErrorMsg ==null || validErrorMsg.equals("")){
					validErrorMsg = mContext.getResources().getString(R.string.commit_data_error);
				}
				Toast.makeText(mContext, validErrorMsg, Toast.LENGTH_LONG).show();
				break;
				case GlobalParam.MSG_LOAD_ERROR:
					String messgage = (String)msg.obj;
					Toast.makeText(mContext, messgage, Toast.LENGTH_LONG).show();
					break;
				case updateCode:
					mTotalTime--;
					sendCode.setText(mTotalTime+"秒");
					sendCode.setEnabled(false);
					if(mTotalTime==0){
						sendCode.setText("验证码");
						sendCode.setEnabled(true);
						mTotalTime = 60;
						return;
					}
					updatecode();
			default:
				break;
			}
		}
	};

	public void updatecode(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					Message message = new Message();
					message.what =updateCode;
					mHandler.sendMessage(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);
		mContext = this;
		initCompent();
	}
	/**
	 * 初始化控件
	 */
	private void initCompent(){
		setTitleContent(R.drawable.back_btn, 0, R.string.register);
		mLeftBtn.setOnClickListener(this);

		mBuyCodeBtn = (Button)findViewById(R.id.buy_code);
		sendCode= (Button)findViewById(R.id.sendcode);
		mBuyCodeBtn.setVisibility(View.VISIBLE);
		mBuyCodeBtn.setOnClickListener(this);
		et_code = (EditText)findViewById(R.id.code);
		mPwdEdit = (EditText)findViewById(R.id.password);
		iv_hide = (ImageView) findViewById(R.id.iv_hide);
		iv_hide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iv_show.setVisibility(View.VISIBLE);
				iv_hide.setVisibility(View.GONE);
				mPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				mPwdEdit.postInvalidate();
				//切换后将EditText光标置于末尾
				CharSequence charSequence = mPwdEdit.getText();
				if (charSequence instanceof Spannable) {
					Spannable spanText = (Spannable) charSequence;
					Selection.setSelection(spanText, charSequence.length());
				}
			}
		});
		iv_show = (ImageView) findViewById(R.id.iv_show);
		iv_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				iv_show.setVisibility(View.GONE);
				iv_hide.setVisibility(View.VISIBLE);
				mPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
				mPwdEdit.postInvalidate();
				//切换后将EditText光标置于末尾
				CharSequence charSequence = mPwdEdit.getText();
				if (charSequence instanceof Spannable) {
					Spannable spanText = (Spannable) charSequence;
					Selection.setSelection(spanText, charSequence.length());
				}
			}
		});
		et_usernick = (EditText) findViewById(R.id.name);
		et_tjr = (EditText) findViewById(R.id.tjr);
		et_telephone = (EditText) findViewById(R.id.telephone);
		et_telephone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				String telephone =et_telephone.getText().toString().trim();
				if(telephone.length()==11){
					sendCode.setEnabled(true);
				}else{
					sendCode.setEnabled(false);
				}
			}
		});
		sendCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updatecode();
				sendCode.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							IMJiaState state = IMCommon.getIMInfo().checkVerCode(et_telephone.getText().toString().trim());
							tempCode = state.validCode;
							if (state.code==1) {
								Message message = new Message();
								message.what = GlobalParam.MSG_CHECK_VALID_ERROR;
								message.obj =state;
								mHandler.sendMessage(message);
							}
						} catch (IMException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	/**
	 * 检测输入内容是否合法
	 */
	private void checkNumber(){
		boolean isCheck = true;
		String hintMsg ="";
		mInputPwd = mPwdEdit.getText().toString().trim();
		mInputUsernick = et_usernick.getText().toString().trim();
		mInputTjr = et_tjr.getText().toString().trim();
		mInputTelephone = et_telephone.getText().toString().trim();
		String code = et_code.getText().toString().trim();
		if(mInputPwd == null || mInputPwd.equals("")){
			isCheck = false;
			hintMsg = mContext.getResources().getString(R.string.please_input_pwd);
		}else if(mInputUsernick == null || mInputUsernick.equals("")){
			isCheck = false;
			hintMsg =mContext.getResources().getString(R.string.please_input_usernick);
		}else if(mInputTjr == null || mInputTjr.equals("")){
			isCheck = false;
			hintMsg =mContext.getResources().getString(R.string.please_input_tjr);
		}else if(mInputTelephone == null || mInputTelephone.equals("")){
			isCheck = false;
			hintMsg =mContext.getResources().getString(R.string.please_input_telephone);
		}else if(!IMCommon.isMobileNum(mInputTelephone)){
			isCheck = false;
			hintMsg =mContext.getResources().getString(R.string.check_phone_number_hint);
		}else if(!code.equals(tempCode)){
			isCheck = false;
			hintMsg ="验证码不正确";
		}
		if (!isCheck && hintMsg!=null && !hintMsg.equals("")) {
			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
		}
		if (isCheck) {
			register();
		}
	}

	/**
	 * 注册
	 */
	private void register(){
		if(!IMCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					IMCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, R.string.commit_dataing);

					String result = getIMInfo().register(mInputPwd,mInputUsernick,mInputTjr,mInputTelephone);
					if (result != null) {
						JSONObject json = new JSONObject(result.replace("(","").replace(")",""));
						if(json.get("success").equals("yes")){
							Message message = new Message();
							message.what = GlobalParam.MSG_LOAD_ERROR;
							message.obj = json.getString("msg");
							mHandler.sendMessage(message);
							RegisterActivity.this.finish();
						}else{
							Message message = new Message();
							message.what = GlobalParam.MSG_LOAD_ERROR;
							message.obj = json.getString("msg");
							mHandler.sendMessage(message);
						}
					}
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				
				}catch (IMException e) {
					e.printStackTrace();
					IMCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}


	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			RegisterActivity.this.finish();
			break;
		case R.id.buy_code:
			checkNumber();
			break;
		default:
			break;
		}
	}

}