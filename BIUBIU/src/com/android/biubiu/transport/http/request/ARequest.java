package com.android.biubiu.transport.http.request;

/**
 * Created by yanghj on 16/7/6.
 */
public abstract class ARequest {
    public abstract String getUrl();

    protected String filter(String name, String value) {
        return value;
    }
}
