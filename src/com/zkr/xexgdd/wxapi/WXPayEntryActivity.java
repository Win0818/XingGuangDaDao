package com.zkr.xexgdd.wxapi;

import org.greenrobot.eventbus.EventBus;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zkr.xexgdd.common.Constant;
import com.zkr.xexgdd.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class WXPayEntryActivity  extends Activity implements IWXAPIEventHandler{
	
	private String TAG = "Wing";
	private IWXAPI api;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID);
		api.handleIntent(getIntent(), this);
		mContext = this;
	}
	@Override
	public void onReq(BaseReq arg0) {
		
	}
	@Override
	public void onResp(BaseResp resp) {
		LogUtil.i(TAG, "微信支付回调>>>>resp>>>" + resp.errCode);
		
		EventBus.getDefault().post("hello");
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0){
				Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_LONG).show();
				EventBus.getDefault().post("wechat_pay_success");
			} else if (resp.errCode == -1){
				Toast.makeText(getApplicationContext(), "支付失败, 请重试", Toast.LENGTH_LONG).show();
				EventBus.getDefault().post("wechat_pay_fail");
			} else if (resp.errCode == -2){
				Toast.makeText(getApplicationContext(), "取消支付", Toast.LENGTH_LONG).show();
				EventBus.getDefault().post("wechat_pay_cancel");
			}
			finish();
		}
	}

}
