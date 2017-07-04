package com.coolwin.XYT.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.ab.fragment.AbSampleDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.BbsChatMainActivity;
import com.coolwin.XYT.ChatMainActivity;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.R;
import com.coolwin.XYT.adapter.ChatTabAdapter;
import com.coolwin.XYT.databinding.ChatTabBinding;
import com.coolwin.XYT.databinding.DialogChatBinding;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.interfaceview.UIChatFragment;
import com.coolwin.XYT.net.IMException;
import com.coolwin.XYT.presenter.ChatFragmentPresenter;

import java.util.List;

import static com.coolwin.XYT.R.string.bbs;


/**
 * 聊天Fragment的界面
 */
public class ChatFragment extends BaseFragment<ChatFragmentPresenter> implements UIChatFragment {
	ChatTabBinding binding;
	private List<Session> mSessionList;
	private ChatTabAdapter mAdapter;
	public AbSampleDialogFragment abSampleDialogFragment;
	/**
	 * 导入控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater,R.layout.chat_tab,container,false);
		binding.setBehavior(this);
		return binding.getRoot();
	}
	/**
	 * 初始化控件
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPresenter.init();
	}
	@Override
	public void init(List<Session> sessions) {
		mSessionList = sessions;
		mAdapter = new ChatTabAdapter(context,mSessionList);
		binding.chatsList.setAdapter(mAdapter);
		binding.chatsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Login user = new Login();
				final int pot =position;
				user.uid = mSessionList.get(pot).getFromId();
				user.phone = mSessionList.get(pot).getFromId();
				user.nickname = mSessionList.get(pot).name;
				user.headsmall = mSessionList.get(pot).heading;
				user.mIsRoom = mSessionList.get(pot).type;
				if(mSessionList.get(pot).type==100 && mSessionList.get(pot).bid!=null
						&& !mSessionList.get(pot).bid.equals("0")
						&& !mSessionList.get(pot).bid.equals("")){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BbsList bl= IMCommon.getIMInfo().getBbs(mSessionList.get(pot).bid);
								if(bl.mBbsList.size()!=0){
									Intent intent = new Intent(context, BbsChatMainActivity.class);
									intent.putExtra("data", bl.mBbsList.get(0));
									intent.putExtra("fromid", mSessionList.get(pot).getFromId());
									startActivity(intent);
								}
							} catch (IMException e) {
								e.printStackTrace();
							} catch(Exception e){
								e.printStackTrace();
							}
						}
					}).start();
				}else{
					Intent intent = new Intent(context, ChatMainActivity.class);
					intent.putExtra("bbs", bbs);
					intent.putExtra("data", user);
					startActivity(intent);
				}
			}
		});
		binding.chatsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				DialogChatBinding dialogChatBinding =  DataBindingUtil
						.inflate(LayoutInflater.from(context),R.layout.dialog_chat,null,false);
				itemposition = position;
				if(mSessionList.get(position).isTop !=0 ){
					dialogChatBinding.istop.setText("取消置顶");
				}else{
					dialogChatBinding.istop.setText("置顶聊天");
				}
				dialogChatBinding.setBehavior(ChatFragment.this);
				abSampleDialogFragment = AbDialogUtil.showDialog(dialogChatBinding.getRoot());
				return true;
			}
		});
	}
	private int itemposition;
	public void  isTop(View view){
		if(itemposition>=mSessionList.size()|| itemposition<0){
			return;
		}
		Session session = mSessionList.get(itemposition);
		mPresenter.setTop(session);
		mPresenter.init();
		abSampleDialogFragment.dismiss();
	}
	public void del(View view){
		if(itemposition>=mSessionList.size()|| itemposition<0){
			return;
		}
		mPresenter.del(mSessionList.get(itemposition));
		mPresenter.init();
		abSampleDialogFragment.dismiss();
	}
	public void cannelDialog(View view){
		abSampleDialogFragment.dismiss();
	}
}
