package com.coolwin.XYT.interfaceview;

import com.tencent.mm.sdk.modelpay.PayReq;

/**
 * Created by dell on 2017/6/13.
 */

public interface UIPay extends UIPublic {
    void sendpay(PayReq req);
}
