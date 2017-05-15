package com.smapley.base.activity;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smapley.base.R;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.http.BaseResponse;
import com.smapley.base.http.LoginResponse;
import com.smapley.base.utils.ActivityStack;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;
import com.smapley.base.utils.ThreadSleep;
import com.smapley.base.widget.LinearlayoutView;

import org.apache.commons.lang3.StringUtils;
import org.xutils.common.util.MD5;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by wuzhixiong on 2017/4/20.
 */
public class RegisterActivity extends BaseActivity implements LinearlayoutView.KeyBoardStateListener{

    private LinearlayoutView layout;
    private ImageView logo;

    private EditText passwordET;
    private EditText phoneET;
    private EditText codeET;

    private String password;
    private String phone;
    private String code;

    private boolean isSendCode = false;

    private boolean showPass = false;


    @Override
    public void initArgument() {
        isExit = true;
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_register);
        layout = (LinearlayoutView)findViewById(R.id.register_layout);
        layout.setKeyBoardStateListener(this);
        logo = (ImageView)findViewById(R.id.register_logo);
        phoneET = (EditText) findViewById(R.id.register_phone);
        passwordET = (EditText) findViewById(R.id.register_password);
        codeET = (EditText) findViewById(R.id.register_code);
        findViewById(R.id.register_getCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode(v);
            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
        findViewById(R.id.register_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        findViewById(R.id.register_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPass = !showPass;
                ((ImageView) v).setImageResource(showPass ? R.mipmap.login_pass_open : R.mipmap.login_pass_close);
                passwordET.setInputType(showPass ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

            }
        });

    }

    private void getCode(final View view) {
        phone = phoneET.getText().toString();
        if (StringUtils.isEmpty(phone)) {
            showToast(R.string.login_phone);
        } else {
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
    }

    private void sendCode() {
        RequestParams params = new RequestParams(BaseConstant.URL_GETCODE);
        params.addBodyParameter("user", phone);
        x.http().post(params, new BaseCallback<LoginResponse>() {
            @Override
            public void success(LoginResponse result) {

            }
        });
    }

    private void doRegister() {
        if (checkForm()) {
            RequestParams params = new RequestParams(BaseConstant.URL_REGISTER);
            password = MD5.md5(password);
            params.addBodyParameter("user", phone);
            params.addBodyParameter("mi", password);
            params.addBodyParameter("yzm", code);
            x.http().post(params, new BaseCallback<LoginResponse>() {
                @Override
                public void success(LoginResponse result) {
                    afterRegister(result);
                }
            });
        }
    }

    private void afterRegister(LoginResponse result) {
        SP.saveUser(BaseConstant.SP_USERNAME, phone);
        SP.saveUser(BaseConstant.SP_PASSWORD, password);
        SP.saveUser(BaseConstant.SP_UKEY, result.getUkey());
        SP.saveSet(BaseConstant.SP_ISLOGIN, true);
        SP.saveSet(BaseConstant.SP_PAGETOGO, BaseConstant.PAGE_MAIN);
        ActivityStack.getInstance().finishActivityButMain();
    }

    private boolean checkForm() {
        password = passwordET.getText().toString();
        phone = phoneET.getText().toString();
        code = codeET.getText().toString();
        if (StringUtils.isEmpty(phone)) {
            showToast(getString(R.string.login_phone));
        } else if (StringUtils.isEmpty(password)) {
            showToast(getString(R.string.login_pass));
        } else if (StringUtils.isEmpty(code)) {
            showToast(getString(R.string.register_code));
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
