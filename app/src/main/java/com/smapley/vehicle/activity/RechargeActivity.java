package com.smapley.vehicle.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.smapley.base.activity.BaseActivity;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.vehicle.R;
import com.smapley.vehicle.http.Order;
import com.smapley.vehicle.pay.PayResult;
import com.smapley.vehicle.pay.SignUtils;
import com.smapley.vehicle.utils.Constant;

import org.apache.commons.lang3.StringUtils;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    // 商户PID
    public static final String PARTNER = "2088021078637071";
    // 商户收款账号
    public static final String SELLER = "316344445@qq.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbSGOfIspREM+Bd3MBt9Xxs1egAJFpWMFlBVLuTJaTiiLlg7FwqFOc3sZ4R5faN4BjjuMpqZlMkYlCq2tsqCPyB2LOJEYWCcVKfYICUGDgpxkspt9UB0lDNSGIW64sIKmvcFOS349wv0GRIL+Jp5rFDLDvjZ1f41kWKKVzUfhgVAgMBAAECgYEAiGmG9T3lp4z4jtrWq4XJH70gzDI0rzB9kn0wsmepCLWMjH9JySKWvXr2P85YfORd6KUvooUR/+lMs0GVqd0fOjjfzaE2CXoW2mKd2pkb++KUQ3u1Yc5SVkVwItrPUWOTTHQqBav01OoAceE/FhL9BihaPQ9e2iy6NwxvvXpeOBECQQDx4ju6K8/7XV1yDJGlgoeuJQBAIqYvpTb5AyHzErmIfAqt8BGwp56/TxVu7WyNHAx5HMcchvmpx2OSQ27HzNU/AkEAn59fkRsLBYw1MTsjZqcmdf9qdPjRugZiga9eztOijLz6GhgRPqShOcEfcD1vr7Csm+ZIkBjwD5IE4x8VwYeZqwJAJJGDXh4Jj4MKAZgM3Ozi/lzxsMCMR1++896ZX1pRWmUGaE2HHyH4Sgv2vZJ/esXmzNig8ZsmW5idYRt4wBQjmQJATeVKj9dwo35unt3LQtcjL8Y7P2YFgxCGld7tF2W0F5ZJPt6r27Qfcb3LB80TaduAAHx6wMdKr26EsAmFZnI0DQJAcJsIx7jpl8PemwuI73O8h8alhAIvJ39wlr4R5k/qYLfgdCOmD/as/1RDKjaEhcTswwWTERTDfAqH96fjPFkq9Q==";

    private final int SDK_PAY_FLAG = 1;

    @Override
    public void initArgument() {
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

    @Override
    public void initView() {

    }

    @Event({R.id.charge_weixin, R.id.charge_zhifubao, R.id.charge_item1, R.id.charge_item2, R.id.charge_item3, R.id.charge_item4, R.id.charge_item5, R.id.charge_item6})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.charge_weixin:


                break;
            case R.id.charge_zhifubao:
                recharge();


                break;
            default:
                for (TextView v : viewList) {
                    v.setBackgroundResource(R.mipmap.recharge_tv_nor);
                    v.setTextColor(ContextCompat.getColor(RechargeActivity.this,R.color.teal3));
                }
                view.setBackgroundResource(R.mipmap.recharge_tv_pre);
                ((TextView) view).setTextColor(ContextCompat.getColor(RechargeActivity.this,R.color.white));

                String text = ((TextView) view).getText().toString();
                numET.setText(text.substring(0, text.length() - 1));
                break;
        }
    }

    private void recharge() {
        final String num = numET.getText().toString();
        if (StringUtils.isEmpty(num)) {
            showToast(R.string.recharge_toast_num);
        } else {
            RequestParams params = new RequestParams(Constant.URL_RECHARGE);
            params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
            params.addBodyParameter("gold", num);
            x.http().post(params, new BaseCallback<Order>() {
                @Override
                public void success(Order result) {
                    // 订单
                    String orderInfo = getOrderInfo(getString(R.string.app_name), "0", num, result.getDingdan());
                    // 对订单做RSA 签名
                    String sign = sign(orderInfo);
                    try {
                        // 仅需对sign 做URL编码
                        sign = URLEncoder.encode(sign, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    // 完整的符合支付宝参数规范的订单信息
                    final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                            + getSignType();

                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(RechargeActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };

                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                }
            });
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (resultStatus.equals("9000")) {
                        showToast(R.string.recharge_toast_success);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (resultStatus.equals("8000")) {
                            showToast(R.string.recharge_toast_fail);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showToast(R.string.recharge_toast_stop);

                        }
                    }
                    break;
                }
            }
        }
    };

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price, String id) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + id + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + Constant.URL_ADDGOLD
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
