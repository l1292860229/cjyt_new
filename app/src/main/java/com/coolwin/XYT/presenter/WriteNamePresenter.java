package com.coolwin.XYT.presenter;

import android.view.inputmethod.EditorInfo;

import com.coolwin.XYT.activity.WriteNameActivity;
import com.coolwin.XYT.interfaceview.UIWriteName;

/**
 * Created by Administrator on 2017/1/25.
 */

public class WriteNamePresenter  extends  BasePresenter<UIWriteName>{
    public void init(){
        String name = context.getIntent().getStringExtra(WriteNameActivity.DATANAME);
        String title = context.getIntent().getStringExtra(WriteNameActivity.DATATITLE);
        String type=  context.getIntent().getStringExtra(WriteNameActivity.DATATYPE);
        boolean verify=  context.getIntent().getBooleanExtra(WriteNameActivity.DATAVERIFY,true);
        int inputtype =context.getIntent().getIntExtra(WriteNameActivity.DATAINPUTTYPE, EditorInfo.TYPE_CLASS_TEXT);
        mView.init(name,title,verify);
        if(WriteNameActivity.SINGLE.equals(type)){
            mView.setSingle(inputtype);
        }else if(WriteNameActivity.MULTI.equals(type)){
            mView.setMulti(inputtype);
        }
    }
}
