package com.coolwin.XYT;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.Entity.Bbs;
import com.coolwin.XYT.Entity.BbsList;
import com.coolwin.XYT.Entity.Room;
import com.coolwin.XYT.Entity.RoomList;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.Entity.ShareUrl;
import com.coolwin.XYT.adapter.ShareAdapter;
import com.coolwin.XYT.global.IMCommon;
import com.coolwin.XYT.net.IMException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yf on 2017/3/15.
 */

public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private Context mContext;
    private TextView friendsTV,hangyeTV,qunlianTV;
    private String mUrl,urltitle,ImagePath;
    private LinearLayout selectLayout;
    private int type=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mUrl = getIntent().getStringExtra("url");
        urltitle = getIntent().getStringExtra("title");
        ImagePath = getIntent().getStringExtra("imagePath");
        type = getIntent().getIntExtra("type",0);
        setContentView(R.layout.sharefriends);
        listView = (ListView) findViewById(R.id.chats_list);
        friendsTV = (TextView) findViewById(R.id.friendlist);
        friendsTV.setOnClickListener(this);
        hangyeTV = (TextView) findViewById(R.id.hangyelist);
        hangyeTV.setOnClickListener(this);
        qunlianTV = (TextView) findViewById(R.id.qunlianlist);
        qunlianTV.setOnClickListener(this);
        selectLayout = (LinearLayout) findViewById(R.id.select_layout);
        init();
    }
    public void init(){
        setTitleContent(R.drawable.back_btn, 0, R.string.select);
        mLeftBtn.setOnClickListener(this);
        switch(type){
            case 0:
                initSession();
                break;
            case 1:
                initHangyequan();
                selectLayout.setVisibility(View.GONE);
                break;
            case 2:
                initQunlian();
                selectLayout.setVisibility(View.GONE);
                break;
        }
    }
    private void initSession(){
        SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
        SessionTable table = new SessionTable(db);
        final List<Session> tempList;
        tempList = table.query(null,false);
        listView.setAdapter( new ShareAdapter(mContext, tempList,mUrl,urltitle,ImagePath));
        listView.setDivider(null);
        listView.setCacheColorHint(0);
        listView.setHeaderDividersEnabled(false);
    }

    private void initHangyequan(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BbsList bbsList = null;
                try {
                    bbsList = IMCommon.getIMInfo().getShareBbsList(null,"1");
                } catch (IMException e) {
                    e.printStackTrace();
                }
                final List<Bbs> mBbsList = new ArrayList<Bbs>();
                mBbsList.addAll(bbsList.mBbsList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(new ShareAdapter(mContext, mBbsList,mUrl,urltitle,ImagePath));
                    }
                });
            }
        }).start();
    }
    private void initQunlian(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RoomList roomList = null;
                try {
                    roomList = IMCommon.getIMInfo().getRoomList(null);
                } catch (IMException e) {
                    e.printStackTrace();
                }
                final List<Room> mRoomList = new ArrayList<Room>(roomList.mRoomList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter( new ShareAdapter(mContext, mRoomList,mUrl,urltitle,ImagePath));
                    }
                });
            }
        }).start();
    }
    /**
     * 按钮点击事件
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("url",mUrl);
        intent.putExtra("title", urltitle);
        intent.putExtra("imagePath", ImagePath);
        super.onClick(v);
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                return;
            case R.id.friendlist:
                intent.setClass(mContext, ChooseUserActivity.class);
                intent.putExtra("shareurl_msg", new ShareUrl(mUrl,urltitle,ImagePath));
                break;
            case R.id.hangyelist:
                intent.setClass(mContext, ShareActivity.class);
                intent.putExtra("type",1);
                break;
            case R.id.qunlianlist:
                intent.setClass(mContext, ShareActivity.class);
                intent.putExtra("type",2);
                break;
            default:
                break;
        }
        startActivity(intent);
    }
}
