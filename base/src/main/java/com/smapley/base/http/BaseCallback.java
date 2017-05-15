package com.smapley.base.http;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.smapley.base.application.BaseApplication;
import com.smapley.base.utils.ActivityStack;

import org.apache.commons.lang3.StringUtils;
import org.xutils.common.Callback;
import org.xutils.x;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by wuzhixiong on 2017/4/29.
 */
public abstract class
BaseCallback<T> implements Callback.CommonCallback<String>, ParameterizedType {

    public void onSuccess(String result) {
        Log.d("http", result);
        if (!StringUtils.isEmpty(result)) {
            BaseResponse<T> response = deal(result);
            if (!StringUtils.isEmpty(response.getNr())) {
                Toast.makeText(x.app(), response.getNr(), Toast.LENGTH_SHORT).show();
            }
            if (response.getStatus().equals("-99")) {
                BaseApplication.getInstance().loginOut();
            }
            if (response.getStatus().equals("success")) {
                success(response.getData());
            }
        }
    }

    public void onError(Throwable ex, boolean isOnCallback) {
        Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void onCancelled(CancelledException cex) {
        Toast.makeText(x.app(), "Cancelled", Toast.LENGTH_SHORT).show();
    }

    public void onFinished() {

    }

    public abstract void success(T result);


    public BaseResponse<T> deal(String response) {
//            Type gsonType = new ParameterizedType() {//...};//不建议该方式，推荐采用GsonResponsePasare实现ParameterizedType.因为getActualTypeArguments这里涉及获取GsonResponsePasare的泛型集合
        Type gsonType = this;

        BaseResponse<T> commonResponse = new Gson().fromJson(response, gsonType);
        return commonResponse;
    }

    @Override
    public Type[] getActualTypeArguments() {
        Class clz = this.getClass();
        //这里必须注意在外面使用new GsonResponsePasare<GsonResponsePasare.DataInfo>(){};实例化时必须带上{},否则获取到的superclass为Object
        Type superclass = clz.getGenericSuperclass(); //getGenericSuperclass()获得带有泛型的父类
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type getRawType() {
        return BaseResponse.class;
    }
}