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
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.Bbs2Adapter;
import com.coolwin.XYT.adapter.BbsAdapter;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.sortlist.ClearEditText;

import java.util.ArrayList;
import java.util.List;

public class BbsSearchDialog extends Dialog implements OnItemClickListener, View.OnClickListener{

	private Context mContext;
	private ClearEditText mContentEdit;
	private ListView mListView;
	private List<Bbs> mbbsList = new ArrayList<Bbs>();
	private Button mCancleBtn;
	private BbsAdapter mAdapter;
	private Bbs2Adapter mAdapter2;
	private int adapterindex=0;
	private String type;
	private boolean ismy;
	private boolean isshow;
	public BbsSearchDialog(Context context, String type, boolean ismy) {
		super(context, R.style.ContentOverlay);
		mContext = context;
		this.type = type;
		this.ismy = ismy;
	}
	public BbsSearchDialog(Context context, String type, boolean ismy, int adapterindex) {
		super(context, R.style.ContentOverlay);
		mContext = context;
		this.type = type;
		this.ismy = ismy;
		this.adapterindex = adapterindex;
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
								BbsList bbsList = IMCommon.getIMInfo().getBbsList(type,ismy,s.toString(),1);
								if(mbbsList!=null && mbbsList.size()>0){
									mbbsList.clear();
								}
								if(bbsList.mBbsList!=null && bbsList.mBbsList.size()>0){
									mbbsList.addAll(bbsList.mBbsList);
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
		if(adapterindex==0){
			if(mAdapter != null){
				mAdapter.notifyDataSetChanged();
				return;
			}
		}else{
			if(mAdapter2 != null){
				mAdapter2.notifyDataSetChanged();
				return;
			}
		}


		if (mbbsList != null) {
			if(adapterindex==0){
				mAdapter = new BbsAdapter(mContext, mbbsList);
				mListView.setAdapter(mAdapter);
			}else{
				mAdapter2 = new Bbs2Adapter(mContext, mbbsList);
				mListView.setAdapter(mAdapter2);
			}

		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(type.equals("1")){
			if (mbbsList.get(arg2).isjoin==1) {
				Intent intent = new Intent(mContext, BbsChatMainActivity.class);
				intent.putExtra("data", mbbsList.get(arg2));
				mContext.startActivity(intent);
			}else{
				showModifybgDialog(mbbsList.get(arg2));
			}
		}else{
			Intent intent = new Intent(mContext, BbsChatMainActivity.class);
			intent.putExtra("data", mbbsList.get(arg2));
			mContext.startActivity(intent);
		}
		BbsSearchDialog.this.dismiss();
	}
	@Override
	public void onClick(View v) {
		hideKeyBoard();
		BbsSearchDialog.this.dismiss();
	}
	private void showModifybgDialog(final Bbs bbs){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle("你还不是鱼塘成员,是否申请加入");
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
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
		});
		if(bbs.type==1&&bbs.isVisitors ==1){
			builder.setNeutralButton(R.string.look, new DialogInterface.OnClickListener() {
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
				BbsSearchDialog.this.dismiss();
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
