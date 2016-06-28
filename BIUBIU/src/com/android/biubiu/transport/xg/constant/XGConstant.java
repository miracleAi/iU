package com.android.biubiu.transport.xg.constant;

/**
 * Created by yanghj on 16/6/16.
 */
public class XGConstant {
    public static final String TAG = "TPush";

    //推送消息类型 0--推送匹配
    public static final int MSG_TYPE_MATCH = 101;
    //推送消息类型 1--你的biubiu被抢了
    public static final int MSG_TYPE_GRAB = MSG_TYPE_MATCH + 1;
    //社区biu
    public static final int COM_BIU = MSG_TYPE_GRAB + 1;

    //头像审核状态
    public static final int HEAD_VERIFY = 201;
    //点赞 评论通知
    public static final int NOTIFY_COUNT = 301;
}
