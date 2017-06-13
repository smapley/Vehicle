package com.smapley.base.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.smapley.base.location.Location;
import com.smapley.base.utils.ActivityStack;
import com.smapley.base.utils.BaseConstant;
import com.smapley.base.utils.SP;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

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
        Log.d("-----------------", "application started" + sHA1(this));

    }


    public void loginOut() {
        SP.saveSet(BaseConstant.SP_ISLOGIN, false);
        SP.saveSet(BaseConstant.SP_PAGETOGO, BaseConstant.PAGE_LOGIN);
        SP.clearUser();
        ActivityStack.getInstance().finishActivityButMain();

    }

    public String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
