package com.coolwin.XYT.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.BbsQuanXianActivity;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.R;
import com.coolwin.XYT.UserInfoActivity;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.sortlist.ClearEditText;
import com.coolwin.XYT.sortlist.UserListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BbsReplyInfoSearchDialog extends Dialog implements OnItemClickListener, View.OnClickListener{

	private Context mContext;
	private ClearEditText mContentEdit;
	private ListView mListView;
	private List<BbsReplyInfo> mbbsList = new ArrayList<BbsReplyInfo>();
	private Button mCancleBtn;
	private UserListAdapter mAdapter;
	private boolean isshow;
	private Bbs bbs;
	private boolean isvisitors;
	public BbsReplyInfoSearchDialog(Context context, Bbs bbs, boolean isvisitors) {
		super(context, R.style.ContentOverlay);
		mContext = context;
		this.bbs = bbs;
		this.isvisitors = isvisitors;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_search_dialog);
		initComponent();
	}

	private void initComponent() {
		mCancleBtn = (Button)findViewById(R.id.cancle_btn);
		mCancleBtn.setOnClickListener(this);
		mContentEdit = (ClearEditText) findViewById(R.id.searchcontent);
		 mContentEdit.setOnClearClickLister(new ClearEditText.OnClearClick() {
			@Override
			public void onClearListener() {

			}
		});

		mContentEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (s.toString() != null && !s.toString().equals("")) {
					isshow=true;
					new Thread(){
						public void run() {
							try {
								BbsReplyInfoList result = IMCommon.getIMInfo().bbsUserList(bbs.id,"2",1,s.toString());
								if(mbbsList!=null && mbbsList.size()>0){
									mbbsList.clear();
								}
								if(result.mBbsReplyInfoList!=null && result.mBbsReplyInfoList.size()>0){
									mbbsList.addAll(result.mBbsReplyInfoList);
								}
								mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LISTVIEW_DATA);
							} catch (IMException e) {
								e.printStackTrace();
							}catch (Exception e) {
								e.printStackTrace();
							}
						};
					}.start();
				}else if(s.toString().equals("")){
					isshow=false;
					mbbsList.clear();
					updateListView();
					mListView.setVisibility(View.GONE);
				}
			}
		});

		mListView = (ListView) findViewById(R.id.contact_list);
		mListView.setVisibility(View.GONE);
		mListView.setDivider(null);
		mListView.setCacheColorHint(0);
		mListView.setOnItemClickListener(this);
		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));
		/*mListView.setOnTouchListener(this);*/
	}
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case GlobalParam.MSG_SHOW_LISTVIEW_DATA:
					if(isshow){
						updateListView();
						if(mbbsList == null || mbbsList.size()>0){
							mListView.setVisibility(View.VISIBLE);
						}else{
							mListView.setVisibility(View.GONE);
						}
					}
					break;
				default:
					break;
			}
		}

	};
	private void updateListView() {

		if(mbbsList != null && mbbsList.size() != 0){
			mListView.setVisibility(View.VISIBLE);
		}

		if(mAdapter != null){
			mAdapter.notifyDataSetChanged();
			return;
		}

		if (mbbsList != null) {
			mAdapter = new UserListAdapter(mContext, mbbsList);
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Login mLogin = IMCommon.getLoginResult(mContext);
		if (mLogin.uid.equals(bbs.uid) && !mbbsList.get(arg2).uid.equals(mLogin.uid)) {
			Intent intent = new Intent(mContext, BbsQuanXianActivity.class);
			intent.putExtra("bbsReplyInfo", mbbsList.get(arg2));
			intent.putExtra("data", bbs);
			mContext.startActivity(intent);
		}else{
			Intent intent = new Intent(mContext, UserInfoActivity.class);
			intent.putExtra("uid", mbbsList.get(arg2).uid);
			intent.putExtra("type", 2);
			if(mbbsList.get(arg2).id==null||mbbsList.get(arg2).id.equals("")){
				intent.putExtra("o", 0);
			}
			intent.putExtra("bbs", bbs);
			intent.putExtra("isvisitors", isvisitors);
			mContext.startActivity(intent);
		}
		BbsReplyInfoSearchDialog.this.dismiss();
	}
	@Override
	public void onClick(View v) {
		hideKeyBoard();
		BbsReplyInfoSearchDialog.this.dismiss();
	}
	private void showModifybgDialog(final Bbs bbs){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle("你还不是鱼塘成员,是否申请加入");
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(!bbs.money.equals("0")){
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setIcon(R.drawable.ic_dialog_alert);
					builder.setTitle("你申请的群不是免费的,是否支付费用");
					builder.setPositiveButton(R.string.ok, new OnClickListener() {
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
					builder.setNegativeButton(R.string.cancel, new OnClickListener() {
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
		});
		if(bbs.type==1&&bbs.isVisitors ==1){
			builder.setNeutralButton(R.string.look, new OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = new Intent(mContext, BbsChatMainActivity.class);
					intent.putExtra("data", bbs);
					intent.putExtra("isvisitors", true);
					mContext.startActivity(intent);
				}
			});
		}
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}
	
	private void hideKeyBoard(){
		if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
			InputMethodManager manager= (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}  
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(mbbsList == null || mbbsList.size()<=0){
				hideKeyBoard();
				BbsReplyInfoSearchDialog.this.dismiss();
			}
		}  
		return super.onTouchEvent(event);
	}
	
	/*@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){  
			if(mSearchList == null || mSearchList.size()<=0){
				hideKeyBoard();
				SearchDialog.this.dismiss();
			}
		}  
		return super.onTouchEvent(event);  
	}*/
}
