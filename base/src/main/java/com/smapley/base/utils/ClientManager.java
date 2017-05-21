package com.smapley.base.utils;

import com.inuker.bluetooth.library.BluetoothClient;
import com.smapley.base.application.BaseApplication;

/**
 * Created by wuzhixiong on 2017/5/15.
 */

public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {
        if (mClient == null) {
            synchronized (ClientManager.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(BaseApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}