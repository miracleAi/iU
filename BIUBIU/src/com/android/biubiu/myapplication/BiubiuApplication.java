package com.android.biubiu.myapplication;

import org.xutils.BuildConfig;
import org.xutils.x;

import cn.beecloud.BCPay;
import cn.beecloud.BeeCloud;

import com.avos.avoscloud.AVOSCloud;
import com.android.biubiu.chat.DemoHelper;
import com.android.biubiu.utils.LogUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.melink.bqmmsdk.sdk.BQMM;

import android.R.string;
import android.app.Application;
import android.content.Context;
//import android.support.multidex.MultiDexApplication;
//import android.support.multidex.MultiDex;
import android.support.multidex.MultiDex;
import android.widget.Toast;
//import android.support.multidex.MultiDex;

public class BiubiuApplication extends Application {
    private String TAG = "BiubiuApplication";

//    public static Context applicationContext;
//    private static BiubiuApplication instance;

    /*protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }*/
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
//        applicationContext = this;
//        instance = this;
        final Context context = this;
        new Runnable() {
            @Override
            public void run() {
                initContext(context);
            }
        }.run();
    }

    private void initContext(Context context) {
        x.Ext.init((Application) context);

        x.Ext.setDebug(BuildConfig.DEBUG);


        LogUtil.e(TAG, "APPICATION  start");

        AVOSCloud.initialize(context, "tcd4rj3s3c54bdlkv1vfu5puvu9c2k96ur9kge3qvptqxp8p", "8fpp7j815746jg9x26f0d3c5p76xqkyqm586v2onvx3m2k7a");
        DemoHelper.getInstance().init(context);

        /**
         * 第三方支付相关
         * */
        // 推荐在主Activity里的onCreate函数中初始化BeeCloud.
        BeeCloud.setAppIdAndSecret("3adc89a6-617f-4445-8f23-2b805df90fe4", "2f5add66-01cf-4024-9efe-e4c183f79205");
        // 如果用到微信支付，在用到微信支付的Activity的onCreate函数里调用以下函数.
        // 第二个参数需要换成你自己的微信AppID.
        String initInfo = BCPay.initWechatPay(context, "wxc38cdfe5049cb17e");
        //表情云初始化
        BQMM.getInstance().initConfig(context, "5ca457f4bf624e31ac2dbc1ba3bc398e", "eafc5215121a4075927f4317e6ab67d5");
    }

    /*public static BiubiuApplication getInstance() {
        return instance;
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
