package com.coolwin.XYT.activity;


import android.os.Bundle;

import com.coolwin.XYT.interfaceview.UIPay;
import com.coolwin.XYT.presenter.PayPresenter;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class PayActivity extends BaseActivity<PayPresenter> implements UIPay {
	private IWXAPI api;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, "wx6e8948c5d050b521");
		mPresenter.pay();
	}
	@Override
	public void showLoading() {
	}
	@Override
	public void sendpay(PayReq req) {
		api.sendReq(req);
	}
}
