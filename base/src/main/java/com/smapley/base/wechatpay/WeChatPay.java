package com.smapley.base.wechatpay;

import android.app.Activity;
import android.util.Log;

import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.commons.lang3.StringUtils;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by wuzhixiong on 2017/5/16.
 */

public class WeChatPay {
    //微信支付appID
    public static final String APP_ID = "wx720b96b5857118c0";
    //  API密钥，在商户平台设置
    public static final String API_KEY = "APwXEDRpiMFfaKYPtyfhphiqzniun9GE";
    //第三方APP和微信通信的openapi接口
    private IWXAPI api;

    public WeChatPay(Activity activity) {
        //注册到微信
        api = WXAPIFactory.createWXAPI(activity, null);
        api.registerApp(APP_ID);
    }

    //微信支付
    public void recharge(String num) {
        RequestParams params = new RequestParams(BaseConstant.URL_WECHATPAYORDER);
        params.addBodyParameter("ukey", (String) SP.getUser(BaseConstant.SP_UKEY));
        params.addBodyParameter("gold", num);
        x.http().post(params, new BaseCallback<Map>() {
            @Override
            public void success(Map result) {
                if (result != null && result.get("result") != null) {
                    Map<String, Object> data = (Map) result.get("result");
                    if (data.get("prepayid") != null && !StringUtils.isEmpty(data.get("prepayid").toString())) {
                        sendReqPay(data);
                    }
                }

            }


        });
    }

    private void sendReqPay(Map<String, Object> data) {
        PayReq request = new PayReq();
        request.appId = data.get("appid").toString();
        request.partnerId = data.get("partnerid").toString();
        request.prepayId = data.get("prepayid").toString();
        request.packageValue = data.get("package").toString();
        request.nonceStr = data.get("noncestr").toString();
        request.timeStamp = data.get("timestamp").toString();
        SortedMap<String, Object> map = new TreeMap<>(data);
        map.remove("sign");
        request.sign = signParams(map);
        Log.e( "sendReqPay: ", request.sign);
        api.sendReq(request);
    }

    private String signParams(SortedMap<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(API_KEY);
        return MD5.md5(sb.toString()).toUpperCase();
    }
}
