package com.android.biubiu.callback;

import org.json.JSONObject;

/**
 * Created by meetu on 2016/5/27.
 */
public interface HttpCallback {
    void callback(JSONObject object,String error);
}
