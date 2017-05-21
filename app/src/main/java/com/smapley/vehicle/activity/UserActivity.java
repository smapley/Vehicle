package com.smapley.vehicle.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.application.BaseApplication;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.vehicle.R;
import com.smapley.vehicle.http.Gold;
import com.smapley.vehicle.utils.Constant;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by wuzhixiong on 2017/4/21.
 */
@ContentView(R.layout.activity_user)
public class UserActivity extends BaseActivity {
    @ViewInject(R.id.user_money)
    private TextView money;

    @Override
    public void initArgument() {
        getData();
    }

    @Override
    public void initView() {
        showBack();
    }

    @Event({R.id.user_logout, R.id.user_set})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_logout:
                BaseApplication.getInstance().loginOut();
                break;
            case R.id.user_set:
                startActivity(new Intent(UserActivity.this, SetActivity.class));
                break;
        }
    }

    public void getData() {
        RequestParams params = new RequestParams(Constant.URL_GETGOLD);
        params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
        x.http().post(params, new BaseCallback<Gold>() {
            @Override
            public void success(Gold result) {
                money.setText("￥" + result.getGold());
            }
        });

    }
}
