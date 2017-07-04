package com.coolwin.XYT.interfaceview;

import com.coolwin.XYT.Entity.Session;

import java.util.List;

/**
 * Created by dell on 2017/6/22.
 */

public interface UIChatFragment extends UIPublic {
    void init(List<Session> sessions);
}
