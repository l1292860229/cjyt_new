package com.coolwin.XYT.receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.coolwin.XYT.service.SnsService;

public class PhoneStateChangeListener extends PhoneStateListener {

    private static final String LOGTAG = "PhoneStateChangeListener";

    private final SnsService snsService;

    public PhoneStateChangeListener(SnsService snsService) {
        this.snsService = snsService;
    }

    @Override
    public void onDataConnectionStateChanged(int state) {
        super.onDataConnectionStateChanged(state);
        
        if (state == TelephonyManager.DATA_CONNECTED) {
        	snsService.connect();
        }
    }

    private String getState(int state) {
        switch (state) {
        case 0: // '\0'
            return "DATA_DISCONNECTED";
        case 1: // '\001'
            return "DATA_CONNECTING";
        case 2: // '\002'
            return "DATA_CONNECTED";
        case 3: // '\003'
            return "DATA_SUSPENDED";
        }
        return "DATA_<UNKNOWN>";
    }

}
