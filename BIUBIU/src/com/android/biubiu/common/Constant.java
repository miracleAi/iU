package com.android.biubiu.common;

import com.hyphenate.easeui.EaseConstant;

public class Constant extends EaseConstant {
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";

    public static final String ACTIVITY_NAME = "act_name";
    public static final String ACTIVITY_URL = "act_url";
    public static final String ACTIVITY_COVER = "act_cover";
    public static int biubiCnt;
    public static volatile String headState;

    //发布页面相关传递参数
    public static final String PUBLISH_TYPE = "publish_type";
    public static final String PUBLISH_TEXT = "publish_text";
    public static final String PUBLISH_IMG = "publish_img";
    public static final String PUBLISH_IMG_PATH = "publish_img_path";

    //标签页面相关传递参数
    public static final String TO_TAG_TYPE = "to_tag_type";
    public static final String TAG_TYPE_PUBLISH = "tag_type_publish";
    public static final String TAG_TYPE_SELSET = "tag_type_select";

    public static final String POSTS = "posts";
    public static final String TAG = "tag";
    public static final String FROM_POSTLIST_BY_TAG = "from_postlist_by_tag";
    public static final String FROM_COMM_NOTIFY_PAGE = "from_comm_notify_page";
    public static final String POSTS_ID = "posts_id";
    public static final int EXIT_APP_SUCCESS = 10005;

    public static final int DELETE_RESULT_CODE = EXIT_APP_SUCCESS + 1;
    public static final int PRAISE_RESULT_CODE = DELETE_RESULT_CODE + 1;
    public static final int COMMENT_RESULT_CODE = PRAISE_RESULT_CODE + 1;

    public static final String PUBLISH_POST_ACTION = "publish_post_action";
    public static final String PUBLISH_POST_ACTION_PERMISSION = "cc.imeetu.iu.PUBLISH_POST_ACTION_PERMISSION";
    public static final String HEAD_VERIFY_ACTION = "head_verify_action";
    public static final String HEAD_VERIFY_PERMISSION = "cc.imeetu.iu.HEAD_VERIFY_PERMISSION";

    public static final int BANNER_ANIM_INTERVAL = 2;
    public static int screenWidth, screenHeight;
    public static final int DEVICE_TYPE = 3;
}
