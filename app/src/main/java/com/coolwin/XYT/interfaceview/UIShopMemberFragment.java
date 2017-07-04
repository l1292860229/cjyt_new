package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.Login;

import java.util.List;

/**
 * Created by dell on 2017/7/1.
 */

public interface UIShopMemberFragment extends UIPublic {
    void init(List<Login> mlogin);
    void refreshSucess(List<Login> mlogin);
    void reloadMoreSucess(List<Login> mlogin);
}
