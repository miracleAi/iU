package com.android.biubiu.bean.base;

/**
 * Created by yanghj on 16/6/1.
 */
public class Data<T> extends SimpleData {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
