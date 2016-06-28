package com.android.biubiu.transport.xg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.biubiu.MainActivity;
import com.android.biubiu.activity.biu.BiuBiuReceiveActivity;
import com.android.biubiu.bean.BiuBean;
import com.android.biubiu.bean.UserFriends;
import com.android.biubiu.common.Constant;
import com.android.biubiu.push.PushInterface;
import com.android.biubiu.sqlite.UserDao;
import com.android.biubiu.transport.xg.constant.XGConstant;
import com.android.biubiu.transport.xg.model.HeadVerify;
import com.android.biubiu.transport.xg.model.XGMessage;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.reflect.TypeToken;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import cc.imeetu.iu.R;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by yanghj on 16/6/16.
 */
public class XGMessageReceiver extends XGPushBaseReceiver {
    private UserDao mUserDao;
    private static PushInterface mPushInterface;

    public static void setPushInterface(PushInterface pushInterface) {
        mPushInterface = pushInterface;
    }

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
        System.out.println("token = " + xgPushRegisterResult.getToken());
    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.d(XGConstant.TAG, "content = " + xgPushTextMessage.getContent() + " customContent = " + xgPushTextMessage.getCustomContent()
                + " title = " + xgPushTextMessage.getTitle());
        Log.d("mytest1",xgPushTextMessage.toString());
        String customContent = xgPushTextMessage.getCustomContent();
        if (!TextUtils.isEmpty(customContent)) {
            XGMessage message = CommonUtils.parseJsonToObj(customContent, new TypeToken<XGMessage>() {
            });
            exeCount(context,message);
            int type = Integer.parseInt(message.getMessageType());
            boolean isOpen = SharePreferanceUtils.getInstance().isAppOpen(context, SharePreferanceUtils.IS_APP_OPEN, false);
            boolean isOpenVoice = SharePreferanceUtils.getInstance().isOpenVoice(context, SharePreferanceUtils.IS_OPEN_VOICE, true);
            boolean isShock = SharePreferanceUtils.getInstance().isOpenVoice(context, SharePreferanceUtils.IS_SHOCK, true);
            boolean isPlaySound = false;
            BiuBean bean;
            switch (type) {
                case XGConstant.MSG_TYPE_MATCH:
                case XGConstant.MSG_TYPE_GRAB:
                    String lastSound = SharePreferanceUtils.getInstance().getBiuSoundTime(context, SharePreferanceUtils.BIU_SOUND_TIME, "0");
                    if ((System.currentTimeMillis() - Long.parseLong(lastSound)) < 5 * 1000) {
                        isPlaySound = false;
                    } else {
                        isPlaySound = true;
                    }
                    if (mUserDao == null) {
                        mUserDao = new UserDao(context);
                    }
                    break;
                case XGConstant.HEAD_VERIFY:
                    HeadVerify verify = message.getIconState();
                    Constant.headState = verify.getIconStatus();
                    Intent i = new Intent(Constant.HEAD_VERIFY_ACTION);
                    context.sendBroadcast(i, Constant.PUBLISH_POST_ACTION_PERMISSION);
                    showNotifyVerify(context, Integer.parseInt(Constant.headState), verify.getTime() * 1000);
                    break;
            }
            switch (type) {
                case XGConstant.MSG_TYPE_MATCH:
                    bean = message.getSendBiu();
                    if (isOpen) {
                        if (isPlaySound) {
                            SharePreferanceUtils.getInstance().putShared(context, SharePreferanceUtils.BIU_SOUND_TIME, String.valueOf(System.currentTimeMillis()));
                            if (isOpenVoice) {
                                playSound(context);
                            }
                            if (isShock) {
                                shock(context);
                            }
                        }
                        if (mPushInterface != null) {
                            mPushInterface.updateView(bean, type);
                        }
                    } else {
                        showNotification(context, isShock, isOpenVoice, bean, type);
                    }
                    break;
                case XGConstant.MSG_TYPE_GRAB:
                    bean = message.getGrabBiu();
                    saveUserFriend(bean);
                    if (isOpen) {
                        if (isOpenVoice) {
                            playSound(context);
                        }
                        if (isShock) {
                            shock(context);
                        }
                        if (mPushInterface != null) {
                            mPushInterface.updateView(bean, type);
                        }
                    } else {
                        showNotification(context, isShock, isOpenVoice, bean, type);
                    }
                    break;
            }
        }
    }

    private void exeCount(Context context,XGMessage message) {
        int totleCount = message.getBadge();
        int comCount = message.getComBiuCount();
        int notifyCount = message.getNoticeCount();
        ShortcutBadger.applyCount(context, totleCount);
        Log.d("mytest1","zhixing"+totleCount);
        if(comCount>0){
            Intent i = new Intent(Constant.COM_BIU_ACTION);
            i.putExtra("com_count",comCount);
            context.sendBroadcast(i);
        }
        if(notifyCount>0){
            Intent i = new Intent(Constant.NOTIFU_ACTION);
            i.putExtra("notify_count",notifyCount);
            context.sendBroadcast(i);
        }
    }

    private void showNotification(Context context, boolean isShock, boolean isOpenVoice, BiuBean bean, int type) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getResources().getString(R.string.receive_biu_tip))
                .setTicker(context.getResources().getString(R.string.receive_biu_tip))//通知首次出现在通知栏，带上升动画效果的
                .setAutoCancel(true)
                .setWhen(bean.getTime())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                //.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(cc.imeetu.iu.R.drawable.icon);
        try {
            if (XGConstant.MSG_TYPE_MATCH == type) {
                StringBuilder sb = new StringBuilder();
                sb.append(bean.getNickname()).append(" ");
                if (bean.getSex().equals(Constants.SEX_MALE)) {
                    sb.append(context.getResources().getString(R.string.male)).append(" ");
                } else {
                    sb.append(context.getResources().getString(R.string.female)).append(" ");
                }
                sb.append(context.getResources().getString(R.string.age, bean.getAge()));
                mBuilder.setContentText(sb.toString());
            } else {
                mBuilder.setContentText(context.getResources().getString(R.string.grab_biu_tip));
            }
        } catch (NullPointerException e) {
        }
        if (isShock) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (isOpenVoice) {
            playSound(context);
        }
        Intent resultIntent;
        // 此处应根据biubiu是否结束来判断进入首页还是biu详情页
        if (SharePreferanceUtils.getInstance().isBiuEnd(context, SharePreferanceUtils.IS_BIU_END, true)) {
            resultIntent = new Intent(context.getApplicationContext(), BiuBiuReceiveActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultIntent.putExtra("userCode", String.valueOf(bean.getUserCode()));
        } else {
            resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void showNotifyVerify(Context context, int type, long time) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setAutoCancel(true)
                .setWhen(time)
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setSmallIcon(cc.imeetu.iu.R.drawable.icon);
        if (type == Constants.HEAD_VERIFYSUC) {
            mBuilder.setContentTitle(context.getResources().getString(R.string.head_egis))
                    .setContentText(context.getResources().getString(R.string.head_egis_info))
                    .setTicker(context.getResources().getString(R.string.head_egis_info));
        } else if (type == Constants.HEAD_VERIFYFAIL) {
            mBuilder.setContentTitle(context.getResources().getString(R.string.head_no_egis))
                    .setContentText(context.getResources().getString(R.string.head_no_egis_info1))
                    .setTicker(context.getResources().getString(R.string.head_no_egis_info1));
        }
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        Intent resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        playSound(context);
        mNotificationManager.notify(0, mBuilder.build());
    }


    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

    }

    /**
     * 保存用户信息
     *
     * @param bean
     */
    public void saveUserFriend(/*int code, String name, String url*/BiuBean bean) {
        UserFriends item = new UserFriends();
        item.setUserCode(String.valueOf(bean.getUserCode()));
        item.setIcon_thumbnailUrl(bean.getIconUrl());
        item.setNickname(bean.getNickname());
        mUserDao.insertOrReplaceUser(item);
    }

    //播放自定义的声音
    public void playSound(Context context) {
        String uri = "android.resource://" + context.getPackageName() + "/" + R.raw.yaho;  //自己把铃声放在raw文件夹下就行了
        Uri no = Uri.parse(uri);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), no);
        r.play();
    }

    public void shock(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }
}
