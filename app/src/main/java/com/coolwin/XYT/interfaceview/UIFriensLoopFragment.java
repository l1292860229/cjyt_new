package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.FriendsLoopItem;

import java.util.List;

/**
 * Created by dell on 2017/6/23.
 */

public interface UIFriensLoopFragment extends UIPublic {
    void init(List<FriendsLoopItem>friendsLoopItems);
    void refreshSucess(List<FriendsLoopItem> friendsLoopItems);
    void reloadMoreSucess(List<FriendsLoopItem> friendsLoopItems);
}
