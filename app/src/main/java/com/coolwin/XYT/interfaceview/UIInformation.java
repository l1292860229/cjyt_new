package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.MyInformation;

import java.util.List;

/**
 * Created by Administrator on 2017/6/1.
 */

public interface UIInformation extends UIPublic {
    void init(List<MyInformation> data);
    void refreshSuccess(List<MyInformation> data);
    void loadMoreSucess(List<MyInformation> data);
}
