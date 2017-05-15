package com.smapley.base.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.smapley.base.application.BaseApplication;
import com.smapley.base.utils.ActivityStack;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.xutils.x;

/**
 * Created by eric on 2017/3/13.
 * 基础Activity
 */

public abstract class BaseActivity extends MyActionBarActivity {

    protected boolean isExit = false;

    protected RxPermissions rxPermissions;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().addActivity(this);
        x.view().inject(this);
        rxPermissions = new RxPermissions(this);
        initState();
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
        initArgument();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        //Activity堆栈管理
        ActivityStack.getInstance().removeActivity(this);
        super.onDestroy();

    }


    public abstract void initArgument();

    public abstract void initView();

    protected void showToast(int msg) {
        showToast(getString(msg));
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 沉浸式状态栏
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    protected void showDialog(String msg, DialogListener dialogListener) {
        showDialog("提示", msg, dialogListener);
    }

    protected void showDialog(String title, String msg, final DialogListener dialogListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.onNetural(dialog, which);
            }
        });
        builder.show();
    }

    public abstract class DialogListener {
        protected abstract void onNetural(DialogInterface dialog, int which);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isExit) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                showDialog("确定要退出吗？", new DialogListener() {
                    @Override
                    protected void onNetural(DialogInterface dialog, int which) {
                        SP.saveSet(BaseConstant.SP_PAGETOGO, BaseConstant.PAGE_MAIN);
                        ActivityStack.getInstance().finishAllActivity();
                    }
                });
            }
            return false;
        } else {
            super.onKeyDown(keyCode, event);
            return true;
        }
    }

    protected boolean checkLogin() {
        if ((Boolean) SP.getSet(BaseConstant.SP_ISLOGIN, false)) {
            return true;
        } else {
            BaseApplication.getInstance().loginOut();
            return false;
        }
    }

}
