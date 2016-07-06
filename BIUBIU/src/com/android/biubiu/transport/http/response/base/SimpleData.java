package com.android.biubiu.transport.http.response.base;

/**
 * Created by yanghj on 16/5/31.
 * 最外层的response对象
 */
public class SimpleData {
    private String state;
    private String error;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
