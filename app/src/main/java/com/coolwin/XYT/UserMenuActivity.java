package com.coolwin.XYT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.UserMenu;
import com.coolwin.XYT.adapter.UserMenuAdapter;
import com.coolwin.XYT.global.IMCommon;

import java.util.ArrayList;

/**
 * @author dongli
 *
 */
public class UserMenuActivity extends BaseActivity implements OnClickListener {
	final static int REFULDATA = 1;
	private ArrayList<UserMenu> usermenu;
	private ListView mListView;
	private UserMenuAdapter menuAdapter;
	private TextView cancreatecountTextView;
	private Login mlogin;
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_menu_page);
		mContext = this;
		initComponent();
	}
	
	/**
	 * 实例化控件
	 */
	private void initComponent(){
		mlogin = IMCommon.getLoginResult(mContext);
		usermenu = mlogin.mUserMenu;
		mListView = (ListView) findViewById(R.id.usermenulist);
		cancreatecountTextView = (TextView)findViewById(R.id.cancreatecount);
		if(usermenu!=null && usermenu.size()!=0){
			if (usermenu.size()==4) {
				cancreatecountTextView.setText("已达最大上限");
			}else if(usermenu.size()!=0){
				cancreatecountTextView.setText("还可以创建"+(4-usermenu.size())+"个");
			}
			findViewById(R.id.notmenu).setVisibility(View.GONE);
			menuAdapter = new UserMenuAdapter(mContext,usermenu);
			mListView.setAdapter(menuAdapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent usermenuIntent = new Intent();
					usermenuIntent.putExtra("usermenu",usermenu.get(position));
					usermenuIntent.setClass(mContext, AddUserMenuActivity.class);
					startActivityForResult(usermenuIntent,REFULDATA);
				}
			});
			mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setIcon(R.drawable.ic_dialog_alert);
					builder.setTitle("是否删除该菜单");
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							new Thread(){
								@Override
								public void run(){
									if(IMCommon.verifyNetwork(mContext)){
										try {
											IMCommon.getIMInfo().delusermenu(mlogin.phone,usermenu.get(position).id);
											for (int i = mlogin.mUserMenu.size() - 1; i >= 0; i--) {
												if (mlogin.mUserMenu.get(i).id.equals(usermenu.get(position).id)) {
													mlogin.mUserMenu.remove(i);
													break;
												}
											}
											usermenu = mlogin.mUserMenu;
											IMCommon.saveLoginResult(mContext,mlogin);
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													if (usermenu.size()==4) {
														cancreatecountTextView.setText("已达最大上限");
													}else if(usermenu.size()!=0){
														cancreatecountTextView.setText("还可以创建"+(4-usermenu.size())+"个");
													}
													menuAdapter.setData(usermenu);
													menuAdapter.notifyDataSetChanged();
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
					});
					builder.setNegativeButton(R.string.cancel, null);
					builder.create().show();
					return true;
				}
			});
		}
		setTitleContent(R.drawable.back_btn, 0, "自定义菜单栏");
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setImageResource(R.drawable.add_btn);
		mRightBtn.setOnClickListener(this);
		findViewById(R.id.addusermenu).setOnClickListener(this);
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
		case R.id.addusermenu:
		case R.id.right_btn:
			Intent usermenuIntent = new Intent();
			usermenuIntent.setClass(mContext, AddUserMenuActivity.class);
			startActivityForResult(usermenuIntent,REFULDATA);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			switch (requestCode) {
				case REFULDATA:
					usermenu = IMCommon.getLoginResult(mContext).mUserMenu;
					if(usermenu!=null){
						if (usermenu.size()==4) {
							cancreatecountTextView.setText("已达最大上限");
						}else if(usermenu.size()!=0){
							cancreatecountTextView.setText("还可以创建"+(4-usermenu.size())+"个");
						}
						menuAdapter.setData(usermenu);
						menuAdapter.notifyDataSetChanged();
					}
					break;
			}
		}
	}
}
