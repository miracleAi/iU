package com.android.biubiu.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by meetu on 2016/5/17.
 */
public class CommonUtils {
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
            return new DecimalFormat("##0.00").format(kilometer);
        }
        return "0";
    }

}
