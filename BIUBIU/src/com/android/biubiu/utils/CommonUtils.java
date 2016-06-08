package com.android.biubiu.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.biubiu.common.Constant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by meetu on 2016/5/17.
 */
public class CommonUtils {
    private static Gson mGson = new Gson();

    public static boolean isAppOnForeground(Context context) {
        ActivityManager mActManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // Returns a list of application processes that are running on the device
        List<ActivityManager.RunningAppProcessInfo> appProcesses = mActManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(context.getPackageName())
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static String formatDistance(int meter) {
        if (meter != 0) {
            double kilometer = meter / 1000.00;
            if (kilometer > 10.00) {
                return String.valueOf((int) (kilometer));
            }
            return new DecimalFormat("##0.00").format(kilometer);
        }
        return "0";
    }

    public static <T> T parseJsonToObj(String jsonStr, TypeToken<T> token) {
        return mGson.fromJson(jsonStr, token.getType());
    }

    public static <T> String parseObjToJson(Object src, TypeToken<T> token) {
        return mGson.toJson(src, token.getType());
    }

    public static void setScreenWH(Context con) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        Constant.screenWidth = metrics.widthPixels;
        Constant.screenHeight = metrics.heightPixels;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static boolean unifyResponse(int stateCode, Context con) {
        if (stateCode == 303) {
            Toast.makeText(con, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
            SharePreferanceUtils.getInstance().putShared(con, SharePreferanceUtils.TOKEN, "");
            SharePreferanceUtils.getInstance().putShared(con, SharePreferanceUtils.USER_NAME, "");
            SharePreferanceUtils.getInstance().putShared(con, SharePreferanceUtils.USER_HEAD, "");
            SharePreferanceUtils.getInstance().putShared(con, SharePreferanceUtils.USER_CODE, "");
            return false;
        }
        if (stateCode != 200) {
            return false;
        }
        return true;
    }
}
