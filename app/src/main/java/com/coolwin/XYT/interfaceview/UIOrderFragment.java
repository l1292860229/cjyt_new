package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.Order;

import java.util.List;

/**
 * Created by Administrator on 2017/2/28.
 */

public interface UIOrderFragment extends UIPublic {
    void init(List<Order> userInfos);
    void loadsuccess(List<Order> userInfos);
    void refreshsuccess(List<Order> userInfos);
}
