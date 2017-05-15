package com.smapley.vehicle.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;
import com.smapley.base.utils.ThreadSleep;
import com.smapley.base.widget.ListViewForScrollView;
import com.smapley.vehicle.R;
import com.smapley.vehicle.adapter.WithdrawAdapter;
import com.smapley.vehicle.http.Withdraw;
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
 * Created by eric on 2017/3/20.
 * 提现
 */
@ContentView(R.layout.activity_withdraw)
public class WithdrawActivity extends BaseActivity {

    @ViewInject(R.id.withdraw_gold)
    private TextView gold;
    @ViewInject(R.id.withdraw_bank)
    private Spinner bankSP;
    @ViewInject(R.id.withdraw_money)
    private EditText moneyET;
    @ViewInject(R.id.withdraw_name)
    private EditText nameET;
    @ViewInject(R.id.withdraw_card)
    private EditText cardET;
    @ViewInject(R.id.withdraw_code)
    private EditText codeET;

    private String name;
    private String bank;
    private String money;
    private String card;
    private String code;

    @ViewInject(R.id.withdraw_listview)
    private ListViewForScrollView listView;

    private WithdrawAdapter adapter;
    private List<Map> list;
    private boolean isSendCode = false;

    @Override
    public void initArgument() {
        showBack();
        setTitle(R.string.withdraw);
        showBg();

        getData();
    }

    @Override
    public void initView() {
        list = new ArrayList();
        adapter = new WithdrawAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Event({R.id.withdraw, R.id.withdraw_getcode})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.withdraw:
                upData();
                break;
            case R.id.withdraw_getcode:
                getCode(view);
                break;
        }
    }

    private void getCode(final View view) {

        if (!isSendCode) {
            new ThreadSleep().sleep(30, 1000, new ThreadSleep.Callback() {
                @Override
                public void onCallback(int number) {
                    if (number == 0) {
                        isSendCode = false;
                        ((TextView) view).setText(R.string.register_getCode);
                    } else {
                        isSendCode = true;
                        ((TextView) view).setText(number + "s");
                    }
                }
            });
            sendCode();
        }
    }

    private void sendCode() {
        RequestParams params = new RequestParams(BaseConstant.URL_GETCODE);
        params.addBodyParameter("user", (String) SP.getUser(Constant.SP_UKEY));
        x.http().post(params, new BaseCallback<String>() {
            @Override
            public void success(String result) {

            }
        });
    }

    private void upData() {
        if (checkForm()) {
            RequestParams params = new RequestParams(Constant.URL_ADDTIXIAN);
            params.addBodyParameter("name", name);
            params.addBodyParameter("kahao", card);
            params.addBodyParameter("bank", bank);
            params.addBodyParameter("jine", money);
            params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
            x.http().post(params, new BaseCallback<String>() {
                @Override
                public void success(String result) {

                }
            });
        }
    }

    private boolean checkForm() {
        money = moneyET.getText().toString();
        bank = bankSP.getSelectedItem().toString();
        name = nameET.getText().toString();
        card = cardET.getText().toString();
        code = codeET.getText().toString();
        if (StringUtils.isEmpty(money)) {
            showToast(R.string.withdraw_toast_money);
            return false;
        }
        if (bank.equals("请选择收款银行/平台")) {
            showToast(R.string.withdraw_toast_bank);
            return false;
        }
        if (StringUtils.isEmpty(name)) {
            showToast(R.string.withdraw_toast_name);
            return false;
        }
        if (StringUtils.isEmpty(card)) {
            showToast(R.string.withdraw_toast_card);
            return false;
        }
        if(StringUtils.isEmpty(code)){
            showToast(R.string.withdraw_toast_code);
            return false;
        }
        return true;
    }

    public void getData() {
        RequestParams params = new RequestParams(Constant.URL_GETTIXIAN);
        params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
        x.http().post(params, new BaseCallback<Withdraw>() {
            @Override
            public void success(Withdraw result) {
                gold.setText(result.getGold());
                list.clear();
                list.addAll(result.getResult());
                adapter.notifyDataSetChanged();
                if(!list.isEmpty()) {
                    nameET.setText(list.get(0).get("name").toString());
                    cardET.setText(list.get(0).get("kahao").toString());
                    String[] banks = getResources().getStringArray(R.array.bank);
                    String bank = list.get(0).get("bank").toString();
                    for (int i = 0; i < banks.length; i++) {
                        if (banks[i].equals(bank)) {
                            bankSP.setSelection(i);
                        }
                    }
                }
            }
        });
    }
}
