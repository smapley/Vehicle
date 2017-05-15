package com.smapley.vehicle.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.smapley.base.activity.BaseActivity;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.vehicle.R;
import com.smapley.vehicle.adapter.PayAdapter;
import com.smapley.vehicle.utils.Constant;

import org.apache.commons.lang3.StringUtils;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzhixiong on 2017/5/6.
 */
@ContentView(R.layout.activity_pay)
public class PayActivity extends BaseActivity {

    @ViewInject(R.id.pay_listview)
    private ListView listView;

    @ViewInject(R.id.pay_number)
    private EditText numberET;


    private PayAdapter adapter;
    private List<Map> list;

    private String data;
    private String bcid;
    private String number;

    private String[] texts = new String[]{"3", "4", "5", "6", "7", "8", "9", "10", "12"};
    private int[] keys = new int[]{R.id.pay_key1, R.id.pay_key2, R.id.pay_key3, R.id.pay_key4, R.id.pay_key5, R.id.pay_key6, R.id.pay_key7, R.id.pay_key8, R.id.pay_key9};
    private boolean isUpdata = false;

    @Override
    public void initArgument() {

        try {
            data = getIntent().getStringExtra("data");
            if (!StringUtils.isEmpty(data)) {
                list.clear();
                list.addAll(new Gson().fromJson(data, List.class));
                if (!list.isEmpty()) {
                    list.get(0).put("check", true);
                    bcid = list.get(0).get("bcid").toString();
                }
                adapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void initView() {

        setTitle(R.string.pay);
        showBack();

        list = new ArrayList();
        adapter = new PayAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).put("check", position == i);
                    if (position == i) {
                        bcid = list.get(i).get("bcid").toString();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Event({R.id.pay_confirm, R.id.pay_key1, R.id.pay_key2, R.id.pay_key3, R.id.pay_key4, R.id.pay_key5, R.id.pay_key6, R.id.pay_key7, R.id.pay_key8, R.id.pay_key9})
    private void onClick(View view) {

        switch (view.getId()) {
            case R.id.pay_confirm:
                number = numberET.getText().toString();
                upData();
                break;
            default:
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i] == view.getId()) {
                        number = texts[i];
                        upData();
                    }
                }
                break;
        }
    }

    private void upData() {
        if ( checkForm()) {
            if(isUpdata){


            }else{
                isUpdata = true;
                RequestParams params = new RequestParams(Constant.URL_PAY);
                params.addBodyParameter("bcid", bcid);
                params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
                params.addBodyParameter("gold", number);
                x.http().post(params, new BaseCallback<String>() {
                    @Override
                    public void success(String result) {
                        isUpdata= false;
                        finish();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        super.onError(ex, isOnCallback);
                        isUpdata= false;
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        super.onCancelled(cex);
                        isUpdata= false;
                    }

                    @Override
                    public void onFinished() {
                        super.onFinished();
                        isUpdata= false;
                    }

                });
            }

        }

    }

    private boolean checkForm() {
        if (StringUtils.isEmpty(bcid)) {
            showToast(R.string.pay_sj);
            return false;
        }
        if (StringUtils.isEmpty(number)) {
            showToast(R.string.pay_number);
            return false;
        }
        return true;
    }
}
