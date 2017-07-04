package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.Bbs;

import java.util.List;

/**
 * Created by dell on 2017/6/15.
 */

public interface UIBbsListFragment extends UIPublic {
    void init(List<Bbs> bbses);
    void refreshSucess(List<Bbs> bbses);
    void reloadMoreSucess(List<Bbs> bbses);
    void searchData(List<Bbs> bbses);
}
