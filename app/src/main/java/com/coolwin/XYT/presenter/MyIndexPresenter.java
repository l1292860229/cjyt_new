package com.coolwin.XYT.presenter;

import com.coolwin.XYT.Entity.DataModel;
import com.coolwin.XYT.interfaceview.UIMyIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public class MyIndexPresenter extends BasePresenter<UIMyIndex> {
    public MyIndexPresenter() {
    }
    public void init(){
        ArrayList<DataModel> datas=new ArrayList<>();
        DataModel data=new DataModel();
        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145044.jpg";
        data.type=DataModel.TYPE_ONETOFOUR;
        datas.add(data);
        data=new DataModel();
        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145054.jpg";
        data.type=DataModel.TYPE_ONETOFOUR;
        datas.add(data);
        data=new DataModel();
        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145125.jpg";
        data.type=DataModel.TYPE_ONETOFOUR;
        datas.add(data);
        data=new DataModel();
        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145131.jpg";
        data.type=DataModel.TYPE_ONETOFOUR;
        datas.add(data);

//        data=new DataModel();
//        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145201.jpg";
//        data.type=DataModel.TYPE_ONETOFOUR;
//        datas.add(data);
//        data=new DataModel();
//        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145153.jpg";
//        data.type=DataModel.TYPE_ONETOFOUR;
//        datas.add(data);
//        data=new DataModel();
//        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145144.jpg";
//        data.type=DataModel.TYPE_ONETOFOUR;
//        datas.add(data);
//        data=new DataModel();
//        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_145138.jpg";
//        data.type=DataModel.TYPE_ONETOFOUR;
//        datas.add(data);
//
//        data=new DataModel();
//        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_151040.jpg";
//        data.type=DataModel.TYPE_ONETOONE;
//        datas.add(data);
//        data=new DataModel();
//        data.imagepath = "http://shop.wei20.cn/UserDocument/sj/Picture/2017517_151028.jpg";
//        data.type=DataModel.TYPE_ONETOONE;
//        datas.add(data);
//        data=new DataModel();
//        data.imagepath = "http://139.224.57.105/im5/Uploads/Picture/share/20161125/4f9fdc6dd92ce1c6f479f74cde4be063.jpg";
//        data.type=DataModel.TYPE_ONETOONE;
//        datas.add(data);
        mView.init(datas);
    }
    public void uploadPicUpdateIndex( List<DataModel> datas){

    }
}
