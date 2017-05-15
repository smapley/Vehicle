package com.smapley.base.activity;

import android.Manifest;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import com.smapley.base.R;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.http.BaseResponse;
import com.smapley.base.http.LoginResponse;
import com.smapley.base.utils.ActivityStack;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;
import com.smapley.base.widget.LinearlayoutView;

import org.apache.commons.lang3.StringUtils;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Eric on 2017/4/20.
 */
public class LoginActivity extends BaseActivity implements LinearlayoutView.KeyBoardStateListener {

    private ImageView logo;
    private LinearlayoutView layout;
    private EditText phoneET;
    private EditText passwordET;

    private String phone;
    private String password;

    private boolean showPass = false;

    @Override
    public void initArgument() {
        isExit = true;

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_login);
        layout = (LinearlayoutView) findViewById(R.id.login_layout);
        layout.setKeyBoardStateListener(this);
        logo = (ImageView) findViewById(R.id.login_logo);
        phoneET = (EditText) findViewById(R.id.login_phone);
        passwordET = (EditText) findViewById(R.id.login_password);
        findViewById(R.id.login_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
            }
        });
        findViewById(R.id.login_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        findViewById(R.id.login_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPass = !showPass;
                ((ImageView) v).setImageResource(showPass ? R.mipmap.login_pass_open : R.mipmap.login_pass_close);
                passwordET.setInputType(showPass ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            }
        });
    }


    private void doLogin() {
        if (checkForm()) {
            RequestParams params = new RequestParams(BaseConstant.URL_LOGIN);
            params.addBodyParameter("user", phone);
            params.addBodyParameter("mi", MD5.md5(password));
            x.http().post(params, new BaseCallback<LoginResponse>() {
                @Override
                public void success(LoginResponse result) {
                    afterLogin(result);
                }
            });
        }
    }

    private void afterLogin(LoginResponse result) {
        SP.saveUser(BaseConstant.SP_USERNAME, phone);
        SP.saveUser(BaseConstant.SP_PASSWORD, password);
        SP.saveUser(BaseConstant.SP_UKEY, result.getUkey());
        SP.saveSet(BaseConstant.SP_ISLOGIN, true);
        SP.saveSet(BaseConstant.SP_PAGETOGO, BaseConstant.PAGE_MAIN);
        ActivityStack.getInstance().finishActivityButMain();
    }

    private boolean checkForm() {
        phone = phoneET.getText().toString();
        password = passwordET.getText().toString();
        if (StringUtils.isEmpty(phone)) {
            showToast(getString(R.string.login_phone));
        } else if (StringUtils.isEmpty(password)) {
            showToast(getString(R.string.login_pass));
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void stateChange(int state) {
        switch (state) {
            case LinearlayoutView.KEYBOARD_HIDE:
                logo.setVisibility(View.VISIBLE);
                break;
            case LinearlayoutView.KEYBOARD_SHOW:
                logo.setVisibility(View.GONE);
                break;
        }
    }
}
