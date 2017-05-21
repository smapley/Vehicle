package com.smapley.vehicle.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.utils.ActivityStack;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;
import com.smapley.base.wechatpay.WeChatPay;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void initArgument() {

    }

    @Override
    public void initView() {
        api = WXAPIFactory.createWXAPI(this, WeChatPay.APP_ID);
        api.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        int code = resp.errCode;

        if (code == 0) {
            //显示充值成功的页面和需要的操作
            showToast("充值成功！");
            SP.saveSet(BaseConstant.SP_PAGETOGO, BaseConstant.PAGE_MAIN);
            ActivityStack.getInstance().finishActivityButMain();
        }

        if (code == -1) {
            //错误
            showToast("充值失败！");
            finish();
        }

        if (code == -2) {
            //用户取消
            showToast("取消充值！");
            finish();
        }
    }

}