package com.coolwin.XYT;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.UserMenu;
import com.coolwin.XYT.Entity.UserMenuList;
import com.coolwin.XYT.global.IMCommon;

/**
 * 关于我们页面
 * @author dongli
 *
 */
public class AddUserMenuActivity extends BaseActivity implements OnClickListener {

	public UserMenu userMenu;
	public EditText menuname,menuurl;
	private Login mlogin;
	private String menunameStr,menuurlStr;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_user_menu_page);
		mContext = this;
		initComponent();
	}
	
	/**
	 * 实例化控件
	 */
	private void initComponent(){
		mlogin = IMCommon.getLoginResult(mContext);
		userMenu = (UserMenu) getIntent().getSerializableExtra("usermenu");
		menuname= (EditText) findViewById(R.id.menuname);
		menuurl= (EditText) findViewById(R.id.menuurl);
		if(userMenu!=null){
			setTitleContent(R.drawable.back_btn, 0, "修改");
			menuname.setText(userMenu.menuname);
			menuurl.setText(userMenu.menuurl);
		}else{
			setTitleContent(R.drawable.back_btn, 0, "添加");
		}
		mLeftBtn.setOnClickListener(this);
		mRightTextBtn.setVisibility(View.VISIBLE);
		mRightTextBtn.setText("确定");
		mRightTextBtn.setOnClickListener(this);
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.right_text_btn:
			menunameStr=menuname.getText().toString();
			menuurlStr=menuurl.getText().toString();
			if(menunameStr==null || menunameStr.equals("")){
				Toast.makeText(mContext,"请输入名称", Toast.LENGTH_SHORT).show();
				return;
			}
			if(menuurlStr==null || menuurlStr.equals("")){
				Toast.makeText(mContext,"请输入网址", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!(menuurlStr.startsWith("http://")|| menuurlStr.startsWith("https://"))){
				Toast.makeText(mContext,"请输入正确网址,http或https开头", Toast.LENGTH_SHORT).show();
				return;
			}
			if(userMenu!=null){
				new Thread(){
					@Override
					public void run(){
						if(IMCommon.verifyNetwork(mContext)){
							try {
								IMCommon.getIMInfo().updateusermenu(mlogin.phone,userMenu.id,menunameStr,menuurlStr);
								for (int i = mlogin.mUserMenu.size() - 1; i >= 0; i--) {
									if (mlogin.mUserMenu.get(i).id.equals(userMenu.id)) {
										mlogin.mUserMenu.get(i).menuname = menunameStr;
										mlogin.mUserMenu.get(i).menuurl = menuurlStr;
										IMCommon.saveLoginResult(mContext,mlogin);
										break;
									}
								}
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setResult(RESULT_OK);
										AddUserMenuActivity.this.finish();
									}
								});
								return;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
			}else{
				if(mlogin.mUserMenu!=null && mlogin.mUserMenu.size()==4){
					Toast.makeText(mContext,"自定义菜单已达上限", Toast.LENGTH_SHORT).show();
					return;
				}
				new Thread(){
					@Override
					public void run(){
					if(IMCommon.verifyNetwork(mContext)){
						try {
							UserMenuList userMenuList=  IMCommon.getIMInfo().addusermenu(mlogin.phone,menunameStr,menuurlStr);
							if (userMenuList!=null) {
								mlogin.mUserMenu = userMenuList.mMenuList;
								IMCommon.saveLoginResult(mContext,mlogin);
							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									setResult(RESULT_OK);
									AddUserMenuActivity.this.finish();
								}
							});
							return;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					}
				}.start();
			}
			break;
		default:
			break;
		}
	}
}
