package com.smapley.base.location;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.smapley.base.application.BaseApplication;

/**
 * Created by wuzhixiong on 2017/5/30.
 */

public class Location {

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;


    public Location init(Callback callback) {
        //初始化定位
        mLocationClient = new AMapLocationClient(BaseApplication.getInstance());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        callback.onResult(aMapLocation);
                        stop();
                        System.out.println(aMapLocation.getLatitude() + "----" + aMapLocation.getLongitude());
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        System.out.println("location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        return this;
    }

    public void start() {
        //初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2000);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void stop() {
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
    }

    public void destory() {
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }


    public interface Callback {
        void onResult(AMapLocation aMapLocation);
    }
}
