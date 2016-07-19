package com.android.biubiu.transport.http;

public class HttpContants {
    //正式服务器
//    public static final String HTTP_ADDRESS = "http://app.iu.imeetu.cc/meetu_maven_new/";
    //测试服务器
    public static final String HTTP_ADDRESS = "http://123.57.26.168:8080/meetu_maven_new/";
    //社区版正式Host
//    public static final String HTTP_COMMUNITY_ADDRESS = "http://app.iu.imeetu.cc/meetu_community/";
    //社区版测试Host
    public static final String HTTP_COMMUNITY_ADDRESS = "http://123.57.26.168:8080/meetu_community/";
    //阿里云
    public static final String A_LI_YUN = "http://oss-cn-beijing.aliyuncs.com";
    /**
     * 注册
     */
    public static final String REGISTER_METHOD = "app/auth/register";
    /**
     * 个人主页
     */
    public static final String MY_PAGER_INFO = "app/info/getUserInfo";
    /**
     * 登录
     */
    public static final String LOGIN = "app/auth/login";
    /**
     * 退出登录/注销
     */
    public static final String EXIT = "app/auth/logout";
    /**
     * 更改密码
     */
    public static final String UPDATE_PASSWORD = "app/auth/updatePassword";
    /**
     * 获取标签
     */
    public static final String GAT_TAGS = "app/info/getTags";
    /**
     * 检测手机号是否已注册
     */
    public static final String IS_REGISTERED = "app/auth/checkPhoneIsRegistered";
    /**
     * 注册时获得上传图片鉴权
     */
    public static final String REGISTER_OSS = "app/auth/getOSSSecurityTokenWithoutT";
    /**
     * 获得上传图片鉴权,需要token
     */
    public static final String REGISTER_OSS_TOKEN = "app/info/getOSSSecurityToken";
    /**
     * 修改头像
     */
    public static final String UPDATE_HEAD = "app/info/updateIcon";
    /**
     * 上传用户墙照片
     */
    public static final String UPLOAD_PHOTO = "app/info/savePhoto";
    /**
     * 删除图片
     */
    public static final String DELETE_PHOTO = "app/info/delPhoto";
    /**
     * 更新用户信息
     */
    public static final String UPDATE_USETINFO = "app/info/updateUserInfo";
    /**
     * 获取用户设置
     */
    public static final String GET_SETTING = "app/info/getSettings";
    /**
     * 更新用户设置
     */
    public static final String UPDATE_SETTING = "app/info/updateSettings";
    /**
     * 更新用户位置信息
     */
    public static final String UPDATE_LACATION = "app/info/updateLocation";
    /**
     * 更新用户活跃时间
     */
    public static final String UPDATE_ACTIVITY_TIME = "app/info/updateUserInfo";
    /**
     * 发送biu
     */
    public static final String SEND_BIU = "app/biu/sendBiu";

    /**
     * 获取默认头像
     */
    public static final String GET_DEFAULT_USERS = "activity/getContents";

    /**
     * 抢biu
     */
    public static final String GRAB_BIU = "app/biubiu/grabBiu";
    /**
     * 更新CHANNELID
     */
    public static final String UPDATE_CHANNEL = "app/biubiu/updateChannelIdAndDeviceType";
    /**
     * 获取biubiu列表
     */
    public static final String GET_BIU_LIST = "app/biubiu/biubiuList";
    /**
     * 获取头像已读状态
     */
    public static final String UPDATE_HEAD_STATE = "app/biubiu/updateIconStatus";
    /**
     * 获取biubiu详情
     */
    public static final String BIU_DETIAL = "app/biu/getBiuDetails";
    /**
     * 修改抢biubiu人状态
     */
    public static final String UPDATE_BIU_STATE = "app/biubiu/updateChatListState";
    /**
     * 获取未登录时biubiu列表
     */
    public static final String GET_UNLOGIN_BIU_LIST = "app/auth/biubiuListUnlogin";
    /**
     * 抢biu时 不想见到他
     */
    public static final String NO_LONGER_MATCH = "app/biubiu/nolongerMatch";

    /**
     * 获取好友列表
     */
    public static final String GET_FRIDENS_LIST = "app/biubiu/getFriendsList";
    /**
     * 解除匹配关系
     */
    public static final String REMOVE_FRIEND = "app/biubiu/removeFriend";
    /**
     * 举报好友
     */
    public static final String REPORT = "app/biubiu/report";
    /**
     * 获取订单编号
     */
    public static final String GET_ORDER_CODE = "app/biubiu/createBill";
    /**
     * 查询订单是否成功
     */
    public static final String QUERY_PAY = "app/biubiu/checkBill";

    /**
     * 获取活动
     */
    public static final String ACTIVITY_GETACTIVITY = HTTP_ADDRESS + "activity/getActivity";

    /**
     * 获取匹配用户列表
     */
    public static final String APP_BIU_GETTARGETBIULIST = HTTP_ADDRESS + "app/biu/getTargetBiuList";
    /**
     * 获取抢biu列表
     */
    public static final String GET_BIU_LIST_NEW = "app/biu/getTargetBiuList";
    /**
     * 新的获取biubiu详情
     */
    public static final String GET_BIU_DETAIL_NEW = "app/biu/getBiuDetails";
    /**
     * 抢biu
     */
    public static final String GRAB_BIU_NEW = "app/biu/grabBiu";
    public static final String APP_BIU_GETGRABBIULIST = HTTP_ADDRESS + "app/biu/getGrabBiuList";

    /**
     * 接受biu
     */
    public static final String APP_BIU_ACCEPTBIU = HTTP_ADDRESS + "app/biu/acceptBiu";
    /**
     * 结束biu
     */
    public static final String APP_BIU_ENDBIU = HTTP_ADDRESS + "app/biu/endBiu";
    /**
     * 获取biu币的数量
     */
    public static final String GET_UM_COUNT = HTTP_ADDRESS + "app/biu/getVC";
    /**
     * 获取标签
     */
    public static final String GET_TAGS = HTTP_COMMUNITY_ADDRESS + "app/community/tag/getTagList";
    /**
     * 根据关键字获取标签
     */
    public static final String GET_TAGS_BY_NAME = HTTP_COMMUNITY_ADDRESS + "app/community/tag/getTagByName";
    /**
     * 创建标签
     */
    public static final String CREATE_TAG = HTTP_COMMUNITY_ADDRESS + "app/community/tag/createTag";

    /**
     * 发现首页接口,包括帖子列表和banner
     */
    public static final String POST_GETPOSTLISTBYTYPE = HTTP_COMMUNITY_ADDRESS + "app/community/post/getPostListByType";
    /**
     * 发布帖子
     */
    public static final String PUBLISH_POST = HTTP_COMMUNITY_ADDRESS + "app/community/post/createPost";
    /**
     * 帖子详情
     */
    public static final String POST_DETAIL = HTTP_COMMUNITY_ADDRESS + "app/community/post/getPostDetail";
    /**
     * 发布评论
     */
    public static final String COMMENT_CREATECOMMENT = HTTP_COMMUNITY_ADDRESS + "app/community/comment/createComment";
    /**
     * 点赞/取消赞
     */
    public static final String PRAISE_DOPRAISE = HTTP_COMMUNITY_ADDRESS + "app/community/praise/doPraise";
    /**
     * 获取个人动态
     */
    public static final String USER_DYNAMIC = HTTP_COMMUNITY_ADDRESS + "app/community/post/getMyPostList";
    /**
     * 社区抢biu
     */
    public static final String GRAB_COM_BIU = HTTP_COMMUNITY_ADDRESS + "app/community/combiu/grabComBiu";
    /* 举报
    */
    public static final String REPORT_CREATEREPORT = HTTP_COMMUNITY_ADDRESS + "app/community/report/createReport";
    /**
     * 删除帖子
     */
    public static final String POST_DELETEPOST = HTTP_COMMUNITY_ADDRESS + "app/community/post/deletePost";
    /**
     * 删除评论
     */
    public static final String COMMENT_DELETECOMMENT = HTTP_COMMUNITY_ADDRESS + "app/community/comment/deleteComment";
    /**
     * 获取全局通知
     */
    public static final String GET_ALL_STATUS = HTTP_COMMUNITY_ADDRESS + "app/overall/getStatus";
    /* 获取社区biu列表
    */
    public static final String COMBIU_GETCOMBIULIST = HTTP_COMMUNITY_ADDRESS + "app/community/combiu/getComBiuList";
    /**
     * 话题分类下的帖子
     */
    public static final String POST_GETPOSTLISTBYTAG = HTTP_COMMUNITY_ADDRESS + "app/community/post/getPostListByTag";
    /**
     * 接受社区biu我的人
     */
    public static final String COMBIU_ACCEPTCOMBIU = HTTP_COMMUNITY_ADDRESS + "app/community/combiu/acceptComBiu";
    /**
     * 清空社区biu我的人
     */
    public static final String COMBIU_DELETECOMBIU = HTTP_COMMUNITY_ADDRESS + "app/community/combiu/deleteComBiu";
    /**
     * 请求通知列表
     */
    public static final String NOTIFY_GETNOTIFYLIST = HTTP_COMMUNITY_ADDRESS + "app/community/notify/getNotifyList";
    /**
     * 清空通知
     */
    public static final String NOTIFY_DELETNOTIFIES = HTTP_COMMUNITY_ADDRESS + "app/community/notify/deletNotifies";
    /**
     * 一半
     */
    public static final String HALF_USERS = HTTP_COMMUNITY_ADDRESS + "app/cp/getOtherHalf/v1";
    /**
     * 广场
     */
    public static final String SQUARE_REQUEST = HTTP_COMMUNITY_ADDRESS + "app/cp/dutytype/getSquareInfo/v1";
    /**
     * 我关心的人
     */
    public static final String MY_CARE_REQUEST = HTTP_COMMUNITY_ADDRESS + "app/cp/care/getCareList/v1";
    /**
     * 身份认证
     */
    public static final String VERIFY_REQUEST = HTTP_COMMUNITY_ADDRESS + "app/cp/verifyUserIdentity/v1";
    /**
     * 获取cp条件
     */
    public static final String CP_TERM = HTTP_COMMUNITY_ADDRESS + "app/cp/checkCp/v1";
    /**
     * 申请cp
     */
    public static final String CP_APPLY = HTTP_COMMUNITY_ADDRESS + "app/cp/applyCp/v1";
    /**
     * 关心和取消关心
     */
    public static final String PAGER_CARE = HTTP_COMMUNITY_ADDRESS + "app/cp/care/doCare/v1";

}
