package com.coolwin.XYT.presenter;

import android.database.sqlite.SQLiteDatabase;

import com.coolwin.XYT.DB.DBHelper;
import com.coolwin.XYT.DB.MessageTable;
import com.coolwin.XYT.DB.SessionTable;
import com.coolwin.XYT.Entity.Session;
import com.coolwin.XYT.interfaceview.UIChatFragment;

import java.util.List;

/**
 * Created by dell on 2017/6/22.
 */

public class ChatFragmentPresenter extends BasePresenter<UIChatFragment> {
    SQLiteDatabase db;
    public void init(){
        db = DBHelper.getInstance(context).getReadableDatabase();
        SessionTable table = new SessionTable(db);
        List<Session> tempList = table.query(null,false);
        mView.init(tempList);
    }
    public void del(Session session){
		MessageTable messageTable = new MessageTable(db);
		messageTable.delete(session.getFromId(), session.type);
		SessionTable sessionTable = new SessionTable(db);
		sessionTable.delete(session.getFromId(),session.type);
    }
    public void setTop(Session session){
        if(session.isTop == 0){// 不置顶 -置顶
            SessionTable sessionTable = new SessionTable(db);
            session.isTop =  sessionTable.getTopSize()+1;
            sessionTable.update(session, session.type);
        }else{
            SessionTable sessionTable = new SessionTable(db);
            List<Session> exitsSesList = sessionTable.getTopSessionList();
            if(exitsSesList!=null && exitsSesList.size()>0){
                for (int i = 0; i < exitsSesList.size(); i++) {
                    Session ses = exitsSesList.get(i);
                    if(ses.isTop>1){
                        ses.isTop = ses.isTop-1;
                        sessionTable.update(ses, ses.type);
                    }
                }
            }
            session.isTop = 0;
            sessionTable.update(session, session.type);
        }
    }
}
