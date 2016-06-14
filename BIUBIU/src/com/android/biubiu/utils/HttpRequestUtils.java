package com.android.biubiu.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.biubiu.callback.HttpCallback;
import com.android.biubiu.common.Constant;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by meetu on 2016/5/27.
 */
public class HttpRequestUtils {
    //修改头像状态
    public static void commonRequest(final Context context, JSONObject requestObject, String httpUrl, final HttpCallback callback) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams(httpUrl);
        try {
            requestObject.put("device_code",SharePreferanceUtils.getInstance().getDeviceId(context, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token",SharePreferanceUtils.getInstance().getToken(context, SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {

            e.printStackTrace();
        }
        Log.d("mytest","request"+requestObject.toString());
        params.addBodyParameter("data",requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                Log.d("mytest","error"+arg0.getMessage());
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                Log.d("mytest", "result--"+result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if(!state.equals("200")){
                        if (state.equals("303")) {
                            Toast.makeText(context, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                            SharePreferanceUtils.getInstance().putShared(context, SharePreferanceUtils.TOKEN, "");
                            SharePreferanceUtils.getInstance().putShared(context, SharePreferanceUtils.USER_NAME, "");
                            SharePreferanceUtils.getInstance().putShared(context, SharePreferanceUtils.USER_HEAD, "");
                            SharePreferanceUtils.getInstance().putShared(context, SharePreferanceUtils.USER_CODE, "");
                        }
                        callback.callback(null,"");
                    }else{
                        JSONObject data = jsons.getJSONObject("data");
                        if(data != null){
                            callback.callback(data,"");
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
