package com.android.biubiu.application;

import org.xutils.BuildConfig;
import org.xutils.x;

import cn.beecloud.BCPay;
import cn.beecloud.BeeCloud;

import com.android.biubiu.component.util.EmoticonUtil;
import com.android.biubiu.crashhandle.UEHandler;
import com.android.biubiu.component.util.CommonUtils;
import com.avos.avoscloud.AVOSCloud;
import com.android.biubiu.component.util.LogUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
//import android.support.multidex.MultiDexApplication;
//import android.support.multidex.MultiDex;
import android.support.multidex.MultiDex;

import java.util.Stack;
//import android.support.multidex.MultiDex;

public class BiubiuApplication extends Application {
    private String TAG = "BiubiuApplication";

//    public static Context applicationContext;
//    private static BiubiuApplication instance;

    /*protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }*/
    private static BiubiuApplication application;
    private final Stack<Activity> mActStack = new Stack<Activity>();

    public static BiubiuApplication getInstance() {
        if (application == null) {
            application = new BiubiuApplication();
        }
        return application;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        application = this;
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
        EmoticonUtil.init(context);
        x.Ext.init((Application) context);

        x.Ext.setDebug(BuildConfig.DEBUG);


        LogUtil.e(TAG, "APPICATION  start");

        AVOSCloud.initialize(context, "tcd4rj3s3c54bdlkv1vfu5puvu9c2k96ur9kge3qvptqxp8p", "8fpp7j815746jg9x26f0d3c5p76xqkyqm586v2onvx3m2k7a");

        /**
         * 第三方支付相关
         * */
        // 推荐在主Activity里的onCreate函数中初始化BeeCloud.
        BeeCloud.setAppIdAndSecret("3adc89a6-617f-4445-8f23-2b805df90fe4", "2f5add66-01cf-4024-9efe-e4c183f79205");
        // 如果用到微信支付，在用到微信支付的Activity的onCreate函数里调用以下函数.
        // 第二个参数需要换成你自己的微信AppID.
        String initInfo = BCPay.initWechatPay(context, "wxc38cdfe5049cb17e");
        UEHandler handler = new UEHandler(context);
        Thread.setDefaultUncaughtExceptionHandler(handler);
        CommonUtils.setScreenWH(context);
    }

    /*public static BiubiuApplication getInstance() {
        return instance;
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 添加activity至Stack
     *
     * @param aInstance 待添加activity
     */
    public void addAppInstance(Activity aInstance) {

        if (!mActStack.empty()) {
            Activity mActivity = mActStack.lastElement();
            String lastSimpleName = mActivity.getPackageName() + "." + mActivity.getClass().getSimpleName();
            String simpleName = aInstance.getPackageName() + "." + aInstance.getClass().getSimpleName();
            if (simpleName.equals(lastSimpleName)) {
                delAppInstance(mActivity, true);
                return;
            }
        }

        mActStack.add(aInstance);
    }

    /**
     * 删除当前指定activity
     *
     * @param aInstance 待删除activity
     * @param bFinish   是否同时finish掉当前Activity，true为是，false为否
     */
    public void delAppInstance(Activity aInstance, boolean bFinish) {
        mActStack.remove(aInstance);
        if (bFinish) {
            aInstance.finish();
        }
    }

    /**
     * 删除当前指定activity
     *
     * @param aInstance 待删除activity
     */
    public void delAppInstanceByAnim(Activity aInstance) {
        mActStack.remove(aInstance);
        aInstance.finish();
        // aInstance.overridePendingTransition(R.anim.activity_show, R.anim.out_to_left);
    }

    /**
     * 删除当前指定activity
     *
     * @param activity 待删除activity类名
     * @param bFinish  是否同时finish掉当前Activity，true为是，false为否
     */
    public void delAppInstance(String activity, boolean bFinish) {
        for (Activity a : mActStack) {
            if (activity.equals(a.getClass().getSimpleName())) {
                mActStack.remove(a);
                if (bFinish) {
                    a.finish();
                }
                return;
            }
        }


    }

    @SuppressWarnings("deprecation")
    private void exitApp() {
        try {
            ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(getPackageName());
        } catch (Exception e) {
        }
    }

    private void exitAppSinceLevel8() {
        try {
            ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(getPackageCodePath());
        } catch (Exception e) {
        }
    }

    public void clearAllActivity() {
        for (int i = 0, size = mActStack.size(); i < size; i++) {
            Activity activity = mActStack.get(i);
            if (null != activity) {
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
        mActStack.clear();
    }

}
