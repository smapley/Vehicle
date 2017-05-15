package com.smapley.base.http;



/**
 * Created by wuzhixiong on 2017/4/28.
 */
public class BaseResponse<T> {

    private String status ;
    private String nr;
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
