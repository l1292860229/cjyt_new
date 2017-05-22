package com.coolwin.XYT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsReplyInfo;
import com.coolwin.XYT.Entity.BbsReplyInfoList;
import com.coolwin.XYT.adapter.ProhibitionPersonAdapter;
import com.coolwin.XYT.global.GlobalParam;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;


/*
 * 我的群详情
 */
public class bbsProhibitionActivity extends BaseActivity implements OnItemClickListener {

	private MyGridView mGridView;
	private ProhibitionPersonAdapter mAdapter;
	private List<BbsReplyInfo> mList = new ArrayList<BbsReplyInfo>();
	private Bbs bbs;
	private final  int PROHIBITION_DATA_SUCCESS=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bbs_prohibition_page);
		mContext = this;
		bbs = (Bbs) getIntent().getSerializableExtra("bbs");
		initCompent();
	}
	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0, R.string.prohibition_material);
		mLeftBtn.setOnClickListener(this);
		mGridView = (MyGridView) findViewById(R.id.gridview);
		mGridView.setOnItemClickListener(this);
		getData(bbs.id);
	}
	private void showItem(){
		mAdapter = new ProhibitionPersonAdapter(mContext, mList);
		mGridView.setAdapter(mAdapter);
	}
	private void getData(final String bid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(IMCommon.verifyNetwork(mContext)){
					try {
						BbsReplyInfoList result = IMCommon.getIMInfo().bbsProhibitionList(bid);
						if(result != null && result.mState != null){
							if(result.mState.code == 0&& result.mBbsReplyInfoList!=null ){
								mList = result.mBbsReplyInfoList;
								Message msg = new Message();
								msg.what =PROHIBITION_DATA_SUCCESS;
								mHandler.sendMessage(msg);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	private void delBbsProhibition(final String uid, final String bid){
		new Thread(){
			@Override
			public void run(){
				if(IMCommon.verifyNetwork(mContext)){
					try {
						IMCommon.getIMInfo().delBbsProhibition(uid,bid);
						Message message = new Message();
						message.what = GlobalParam.MSG_CHECK_FAVORITE_STATUS;
						message.obj = uid;
						mHandler.sendMessage(message);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}
		}.start();
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case PROHIBITION_DATA_SUCCESS:
					showItem();
					break;
				case GlobalParam.MSG_CHECK_FAVORITE_STATUS:
					String uid = (String)msg.obj;
					for (int i = mList.size() - 1; i >= 0; i--) {
						if (mList.get(i).uid.equals(uid)) {
							mList.remove(i);
						}
					}
					if (mAdapter != null) {
						Toast.makeText(mContext, "解除禁言成功", Toast.LENGTH_LONG).show();
						mAdapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
			}
		}

	};
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			bbsProhibitionActivity.this.finish();
			break;
		default:
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2 < mList.size()){
			final int id = arg2;
			new AlertDialog.Builder(bbsProhibitionActivity.this).setTitle("提示")//设置对话框标题
					.setMessage("确定要解除禁言?")//设置显示的内容
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
						@Override
						public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
							delBbsProhibition(mList.get(id).uid,bbs.id);
						}
					}).setNegativeButton("返回",null).show();//在按键响应事件中显示此对话框
		}
	}
}
