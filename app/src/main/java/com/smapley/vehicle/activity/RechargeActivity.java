package com.smapley.vehicle.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.alipay.AliPay;
import com.smapley.base.wechatpay.WeChatPay;
import com.smapley.vehicle.R;
import com.smapley.vehicle.utils.Constant;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.apache.commons.lang3.StringUtils;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 2017/3/19.
 * 充值
 */
@ContentView(R.layout.activity_recharge)
public class RechargeActivity extends BaseActivity {

    @ViewInject(R.id.charge_num)
    private EditText numET;

    @ViewInject(R.id.charge_item1)
    private TextView item1;
    @ViewInject(R.id.charge_item2)
    private TextView item2;
    @ViewInject(R.id.charge_item3)
    private TextView item3;
    @ViewInject(R.id.charge_item4)
    private TextView item4;
    @ViewInject(R.id.charge_item5)
    private TextView item5;
    @ViewInject(R.id.charge_item6)
    private TextView item6;

    private List<TextView> viewList;

    private AliPay aliPay;
    private WeChatPay weChatPay;

    @Override
    public void initArgument() {

    }

    @Override
    public void initView() {
        aliPay = new AliPay(this);
        weChatPay = new WeChatPay(this);

        showBack();
        setTitle(R.string.recharge);
        showBg();

        viewList = new ArrayList<>();
        viewList.add(item1);
        viewList.add(item2);
        viewList.add(item3);
        viewList.add(item4);
        viewList.add(item5);
        viewList.add(item6);
    }

    @Event({R.id.charge_weixin, R.id.charge_zhifubao, R.id.charge_item1, R.id.charge_item2, R.id.charge_item3, R.id.charge_item4, R.id.charge_item5, R.id.charge_item6})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.charge_weixin:
                recharge(Constant.WECHATPAY);
                break;
            case R.id.charge_zhifubao:
                recharge(Constant.ALIPAY);
                break;
            default:
                for (TextView v : viewList) {
                    v.setBackgroundResource(R.mipmap.recharge_tv_nor);
                    v.setTextColor(ContextCompat.getColor(RechargeActivity.this, R.color.teal3));
                }
                view.setBackgroundResource(R.mipmap.recharge_tv_pre);
                ((TextView) view).setTextColor(ContextCompat.getColor(RechargeActivity.this, R.color.white));

                String text = ((TextView) view).getText().toString();
                numET.setText(text.substring(0, text.length() - 1));
                break;
        }
    }

    private void recharge(int type) {
        final String num = numET.getText().toString();
        if (StringUtils.isEmpty(num)) {
            showToast(R.string.recharge_toast_num);
        } else {
            switch (type) {
                case Constant.ALIPAY:
                    aliPay.recharge(getString(R.string.app_name), num,  new AliPay.Callback() {
                        @Override
                        public void onSuccess() {
                            showToast(R.string.recharge_toast_success);
                            finish();
                        }

                        @Override
                        public void onFail() {
                            showToast(R.string.recharge_toast_fail);
                        }

                        @Override
                        public void onCancel() {
                            showToast(R.string.recharge_toast_stop);
                        }
                    });
                    break;
                case Constant.WECHATPAY:
                    weChatPay.recharge(num);
                    break;
            }
        }

    }

}
