package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.MyCommodity;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public interface UICommodity extends UIPublic {
    void init(List<MyCommodity> data);
    void refreshSuccess(List<MyCommodity> data);
    void loadMoreSucess(List<MyCommodity> data);
}
