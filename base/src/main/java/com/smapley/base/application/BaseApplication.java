package com.smapley.base.application;

import android.app.Application;
import android.util.Log;

import com.smapley.base.utils.ActivityStack;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by eric on 2017/3/13.
 * Application
 */

public class BaseApplication extends Application {


    private static BaseApplication instance;


    //单例模式获取Application实例
    public static BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化xUtils
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        //初始化本地存储
        SP.init(this);

        instance = this;
        Log.d("-----------------", "application started");

    }


    public void loginOut() {
        SP.saveSet(BaseConstant.SP_ISLOGIN, false);
        SP.saveSet(BaseConstant.SP_PAGETOGO, BaseConstant.PAGE_LOGIN);
        SP.clearUser();
        ActivityStack.getInstance().finishActivityButMain();

    }
}
