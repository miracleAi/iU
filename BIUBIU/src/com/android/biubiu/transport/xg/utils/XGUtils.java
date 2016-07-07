package com.android.biubiu.transport.xg.utils;

import android.content.Context;
import android.util.Log;

import com.android.biubiu.transport.xg.constant.XGConstant;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by yanghj on 16/6/16.
 */
public class XGUtils {
    private int mRetryTime;
    private Context mCon;
    private static XGUtils xgUtils;

    private XGUtils(Context context) {
        mCon = context;
    }

    public static XGUtils getInstance(Context context) {
        if (xgUtils == null) {
            synchronized (XGUtils.class) {
                if (xgUtils == null) {
                    xgUtils = new XGUtils(context);
                }
            }
        }
        return xgUtils;
    }

    public void registerPush() {
        XGPushManager.registerPush(mCon, SharePreferanceUtils.getInstance().getUserCode(mCon,
                SharePreferanceUtils.USER_CODE, ""), new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.d(XGConstant.TAG, "注册成功，设备token为：" + o);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                Log.d(XGConstant.TAG, "注册失败，错误码：" + i + ",错误信息：" + s);
                if (mRetryTime > 2) {
                    Log.d(XGConstant.TAG, "三次注册失败");
                    mRetryTime = 0;
                } else {
                    registerPush();
                    mRetryTime++;
                }
            }
        });
    }

    public void unRegisterPush() {
        XGPushManager.unregisterPush(mCon);
    }
}
