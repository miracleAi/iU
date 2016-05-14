package com.android.biubiu.common;

import java.util.HashMap;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.text.StaticLayout;

public class Umutils {
    /**
     * 进入注册页面
     */
    public static String IN_REGISTER_ACTIVITY = "in_register_activity";

    /**
     * 注册成功
     */
    public static String RIGISTER_SUCCESS = "rigister_success";

    /**
     * 收biu 总数
     */
    public static String RECEIVE_BIU_TOTAL = "receive_biu_total";
    /**
     * 发biu 总数
     */
    public static String SEND_BIU_TOTAL = "send_biu_total";
    /**
     * 抢biu成功
     */
    public static String GRAB_BIU_SUCCESS = "grab_biu_success";
    /**
     * 点击注册完成按钮
     */
    public static String REGISTER_BEFORE = "register_finish_before";
    /**
     * 点击发送验证码
     */
    public static String REGISTER_SMS_SEND = "register_sms_send";
    /**
     * 验证码发送成功
     */
    public static String REGISTER_SMS_SEND_SUCCESS = "register_sms_send_success";
    /**
     * 验证码验证成功
     */
    public static String REGISTER_SMS_VERIFY = "register_sms_verify";

    /**
     * 活动弹窗,弹出次数
     */
    public static String ACTY_DIALOG_POP = "acty_dialog_pop";
    /**
     * 活动弹窗，点开次数
     */
    public static String ACTY_DIALOG_OPEN = "acty_dialog_open";
    /**
     * 活动弹窗，关闭次数
     */
    public static String ACTY_DIALOG_CLOSE = "acty_dialog_close";
    /**
     * 活动列表页面，进入次数
     */
    public static String ACTY_LIST_INTO = "acty_list_into";
    /**
     * 活动列表页面，活动打开总次数
     */
    public static String ACTY_LIST_OPEN_TOTAL = "acty_list_open_total";
    /**
     * 内容头像展示次数
     */
    public static String PROFILE_CON_SHOW = "profile_con_show";
    /**
     * 内容头像总打开次数
     */
    public static String PROFILE_CON_OPEN_TOTAL = "profile_con_open_total_android";


    /**
     * 统计事件
     *
     * @param context
     * @param id      id 为事件ID
     * @param m       map 为当前事件的属性和取值
     * @param value
     */
    public static void onEvent(Context context, String id, HashMap<String, String> m, int value) {
        m.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, m);
    }

    /**
     * 计数事件
     *
     * @param context
     * @param eventId 对应后台的 计数事件
     */
    public static void count(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }


}
