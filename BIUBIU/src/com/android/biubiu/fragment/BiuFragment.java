package com.android.biubiu.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;

import com.android.biubiu.MainActivity;
import com.android.biubiu.MatchSettingActivity;
import com.android.biubiu.activity.LoginOrRegisterActivity;
import com.android.biubiu.activity.act.ActivityListActivity;
import com.android.biubiu.activity.act.WebviewActivity;
import com.android.biubiu.activity.biu.BiuBiuReceiveActivity;
import com.android.biubiu.activity.biu.BiuBiuSendActivity;
import com.android.biubiu.activity.biu.ReceiveBiuListActivity;
import com.android.biubiu.bean.BiuBean;
import com.android.biubiu.bean.DotBean;
import com.android.biubiu.bean.UserBean;
import com.android.biubiu.callback.BiuBooleanCallback;
import com.android.biubiu.chat.ChatActivity;
import com.android.biubiu.chat.Constant;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.common.Umutils;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.push.MyPushReceiver;
import com.android.biubiu.push.PushInterface;
import com.android.biubiu.sqlite.BiubiuDao;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.BiuUtil;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.DensityUtil;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.HttpUtils;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.android.biubiu.utils.UploadImgUtils;
import com.android.biubiu.view.BiuView;
import com.android.biubiu.view.HomeBgView;
import com.android.biubiu.view.TaskView;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.avos.avoscloud.LogUtil.log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.imeetu.iu.R;

@SuppressLint("NewApi")
public class BiuFragment extends Fragment implements PushInterface,FragmentIndicator.OnClickListener {
    View view;
    //启动发biubiu页面的requestcode
    public static final int SEND_BIU_REQUEST = 1001;
    //屏幕宽高
    int width = 0;
    int height = 0;
    Handler taskHandler;
    Handler infoHandler;
    //圆心坐标
    float x0 = 0;
    float y0 = 0;
    float y1 = 0;
    //三个圆的半径
    float circleR1;
    float circleR2;
    float circleR3;
    //三个头像layout宽度
    float userD1;
    float userD2;
    float userD3;
    //三个圈放置view数
    int n1 = 4;
    int n2 = 6;
    int n3 = 8;
    //所有头像数
    int allCount = n1+n2+n3;
    //倒计时总时间和当前时间
    int totalTime = 90;
    int currentTime = 0;
    //放三个圆圈的分割点
    ArrayList<DotBean> c1DotList = new ArrayList<DotBean>();
    ArrayList<DotBean> c2DotList = new ArrayList<DotBean>();
    ArrayList<DotBean> c3DotList = new ArrayList<DotBean>();
    //存放三个圈上的用户列表
    ArrayList<BiuBean> user1List = new ArrayList<BiuBean>();
    ArrayList<BiuBean> user2List = new ArrayList<BiuBean>();
    ArrayList<BiuBean> user3List = new ArrayList<BiuBean>();
    //屏幕上所有用户
    ArrayList<Integer> allUserCodeList = new ArrayList<Integer>();
    //新增用户
    BiuBean newUserBean;
    //存放外圈临界角度
    ArrayList<Double> edgeAngleList = new ArrayList<Double>();
    //标识viewTag
    private int retivIdTag = 1;
    private int tvIdTag = 2;
    private int gifvIdTag = 3;
    private int imvHeadIdTag = 4;
    private int imvDotIdTag = 5;
    //线圈view
    BiuView biuView;
    //背景动画
    HomeBgView backgroundGif;
    //中间biubiu layout
    RelativeLayout biuLayout;
    //头像IMv
    ImageView userBiuImv;
    //倒计时view
    TaskView taskView;
    //放置接受用户信息layout
    AbsoluteLayout userGroupLayout;
    private TextView nameTv;
    private TextView sexTv;
    private TextView ageTv;
    private TextView starTv;
    private TextView schoolTv;
    private LinearLayout infoLayout;
    ImageOptions imageOptions;
    ImageView userBiuBg;
    Animation animationAlpha;
    Animation animationHide;
    Animation animationUserBg;

    SchoolDao schoolDao;
    private static final int SELECT_PHOTO = 1002;
    private static final int CROUP_PHOTO = 1003;
    private static final int ACTIVITY_LIST = 1004;
    Bitmap userheadBitmap = null;
    String headPath = "";
    LinearLayout loadingLayout;
    GifView loadGif;
    TextView loadTv;
    public static boolean isUploadingPhoto = false;
    private TopTitleView mTopTitle;
    private PopupWindow mAdPopup;
    private String mAdUrl, mAdName, mAdCover;
    private String mUserCode;

    BiubiuDao biuDao;
    //请求biu列表是否成功
    private boolean isBiuLoaded = false;
    //是否正在请求biu列表
    private boolean isBiuLoading = false;
    private int inveralTime = 1;
    private BiuBean grabBiuBean;
    private Handler showBiuHandler;
    private Handler mInvalidHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (CommonUtils.isAppOnForeground(getActivity())) {
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, false);
                if (!LoginUtils.isLogin(getActivity())) {
                    clearView();
                }
            } else {
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, true);
                clearView();
                biuDao.deleteAll();
                isBiuLoading = false;
                isBiuLoaded = false;
            }
        }
    };
    //biu 列表是否还有数据需要请求
    private boolean isBiuHasNext = false;
    //private ArrayList<BiuBean> grabBiuList = new ArrayList<BiuBean>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.biu_fragment_layout, null);
        schoolDao = new SchoolDao();
        biuDao = new BiubiuDao(getActivity());
        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, true);
        init();
        drawBiuView();
        setBiuLayout();
        if (SharePreferanceUtils.getInstance().isScanBeginGuid(getActivity(), SharePreferanceUtils.IS_SCAN_BEGINGUID, false)) {
            getAd();
        }
        return view;
    }

    /**
     * 获得广告
     */
    public void getAd() {
        mUserCode = SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, "");
        RequestParams params = new RequestParams(HttpContants.ACTIVITY_GETTAGS);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtil.e("mytest", "s = " + s);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(s);
                    String state = jsons.getString("state");
                    if (state.equals("303")) {
                        Toast.makeText(getActivity(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_NAME, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_HEAD, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_CODE, "");
                        LogUtil.d("mytest", "tok---" + SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
                        exitHuanxin();
                        return;
                    }
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    JSONObject activity = data.getJSONObject("activity");
                    int status = activity.getInt("status");
                    if (status == 1) {
                        mTopTitle.setRightLayoutVisible();
                        mTopTitle.setRightOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent activities = new Intent(getActivity(), ActivityListActivity.class);
                                startActivityForResult(activities, ACTIVITY_LIST);
                            }
                        });
                        mTopTitle.setRightImage(R.drawable.biu_btn_activity_nor);
                        JSONObject dialog = activity.getJSONObject("dialog");
                        mAdUrl = dialog.getString("url");
                        mAdName = dialog.getString("name");
                        mAdCover = dialog.getString("cover");
                        //判断是否弹出广告dialog
                        String url = SharePreferanceUtils.getInstance().getAdUrl(getActivity(), mUserCode);
                        if (TextUtils.isEmpty(url) || !url.equals(mAdUrl)) {//没弹过
                            SharePreferanceUtils.getInstance().saveAdUrl(getActivity(), mUserCode, mAdUrl);
                            showPopup();
                            Umutils.count(getActivity(), Umutils.ACTY_DIALOG_POP);
                        }

                        int updateAt = activity.getInt("updateAt");
                        int cacheUpdate = SharePreferanceUtils.getInstance().getUpdateAd(getActivity(), mUserCode);
                        if (updateAt != cacheUpdate) {
                            boolean haveView = SharePreferanceUtils.getInstance().getHaveToView(getActivity(), mUserCode);
                            if (haveView) {
                                SharePreferanceUtils.getInstance().saveHaveToView(getActivity(), mUserCode, false);
                            }
                            mTopTitle.setRightImage(R.drawable.biu_btn_activity_light);
                            SharePreferanceUtils.getInstance().saveUpdateAd(getActivity(), mUserCode, updateAt);
                        } else {
                            boolean haveView = SharePreferanceUtils.getInstance().getHaveToView(getActivity(), mUserCode);
                            if (!haveView) {
                                mTopTitle.setRightImage(R.drawable.biu_btn_activity_light);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isBiuEnd = SharePreferanceUtils.getInstance().isBiuEnd(getActivity(), SharePreferanceUtils.IS_BIU_END, true);
        if (LoginUtils.isLogin(getActivity())) {
            showBiuHandler.post(shouBiuR);
            //如果返回时biu已结束，则清掉抢biu列表的相关状态
            if(isBiuEnd){
                userBiuImv.setImageResource(R.drawable.biu_btn_biu);
                userBiuImv.setVisibility(View.VISIBLE);
            }else{
                if(null == grabBiuBean){
                    userBiuImv.setImageResource(R.drawable.biu_btn_unfinished);
                    userBiuImv.setVisibility(View.VISIBLE);
                }
            }
            //主要解决登陆后请求biu列表
            if(!isBiuLoading && !isBiuLoaded){
                getBiuList(0);
            }
        }else{
            //退出登录后清掉页面信息
            biuDao.deleteAll();
            isBiuLoading = false;
            isBiuLoaded = false;
           clearView();
            userBiuImv.setImageResource(R.drawable.biu_btn_biu);
            userBiuImv.setVisibility(View.VISIBLE);
            //获取未登录时的biubiu列表
            getBiuListUnlogin();
        }
        if (SharePreferanceUtils.getInstance().isExchange(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, true)) {
            //接口通信赋值
            MyPushReceiver.setUpdateBean(this);
            //检查是否提交了channelID
            if (!SharePreferanceUtils.getInstance().getShared(getActivity(), SharePreferanceUtils.IS_COMMIT_CHANNEL, false)) {
                HttpUtils.commitChannelId(getActivity());
            }
            initUserGroup();
            if (LoginUtils.isLogin(getActivity())) {
                //执行加载biu列表 抢biu人列表 各种状态
                if(!isBiuLoading && !isBiuLoaded){
                    getBiuList(0);
                }
            } else {
                //获取未登录时的biubiu列表
                getBiuListUnlogin();
            }
            if (!isBiuEnd) {
                getGrabBiuUser();
            }else{
                userBiuImv.setImageResource(R.drawable.biu_btn_biu);
                userBiuImv.setVisibility(View.VISIBLE);
            }
        }
    }
    private void clearView(){
        userGroupLayout.removeAllViews();
        user1List.clear();
        user2List.clear();
        user3List.clear();
        allUserCodeList.clear();
    }

    private void init() {
        showBiuHandler = new Handler();
        width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        loadGif = (GifView) view.findViewById(R.id.load_gif);
        loadTv = (TextView) view.findViewById(R.id.loading_tv);
        taskHandler = new Handler();
        infoHandler = new Handler();
        x0 = width / 2;
        y0 = width * 417 / 720;

        biuView = (BiuView) view.findViewById(R.id.biu_view);
        biuLayout = (RelativeLayout) view.findViewById(R.id.biu_layout);
        userBiuImv = (ImageView) view.findViewById(R.id.user_biu);
        backgroundGif = (HomeBgView) view.findViewById(R.id.home_view);
        backgroundGif.setWidth(width);
        backgroundGif.startAnim();
        taskView = (TaskView) view.findViewById(R.id.task_view);
        userGroupLayout = (AbsoluteLayout) view.findViewById(R.id.user_group_layout);
        nameTv = (TextView) view.findViewById(R.id.name_tv);
        sexTv = (TextView) view.findViewById(R.id.sex_tv);
        ageTv = (TextView) view.findViewById(R.id.age_tv);
        starTv = (TextView) view.findViewById(R.id.star_tv);
        schoolTv = (TextView) view.findViewById(R.id.school_tv);
        infoLayout = (LinearLayout) view.findViewById(R.id.info_layout);
        userBiuBg = (ImageView) view.findViewById(R.id.user_biu_bg);
        animationAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        animationHide = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_anim);
        animationUserBg = AnimationUtils.loadAnimation(getActivity(), R.anim.user_gif_alpha);

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.photo_fail)
                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(true)
                .build();

        mTopTitle = (TopTitleView) view.findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), MatchSettingActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });

        mTopTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginUtils.isLogin(getActivity())){
                    ((MainActivity) getActivity()).reverse();
                }
            }
        });
    }

    private void showPopup() {
        if (mAdPopup == null) {
            View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.ad_layout, null);
            ImageOptions imageOptions = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    .setLoadingDrawableId(R.drawable.biu_banner)
                    .setFailureDrawableId(R.drawable.biu_banner)
                    .build();
            ImageView iv = (ImageView) contentView.findViewById(R.id.ad_imageview);
            x.image().bind(iv, mAdCover, imageOptions);
            contentView.findViewById(R.id.ad_imageview).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), WebviewActivity.class);
                    i.putExtra(com.android.biubiu.common.Constant.ACTIVITY_NAME, mAdName);
                    i.putExtra(com.android.biubiu.common.Constant.ACTIVITY_URL, mAdUrl);
                    i.putExtra(com.android.biubiu.common.Constant.ACTIVITY_COVER, mAdCover);
                    startActivity(i);
                    Umutils.count(getActivity(), Umutils.ACTY_DIALOG_OPEN);
                    mAdPopup.dismiss();
                }
            });
            mAdPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mAdPopup.setTouchable(true);
            mAdPopup.setOutsideTouchable(true);
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            mAdPopup.setBackgroundDrawable(new ColorDrawable(0x88000000));
            contentView.findViewById(R.id.close_imageview).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Umutils.count(getActivity(), Umutils.ACTY_DIALOG_CLOSE);
                    mAdPopup.dismiss();
                }
            });
            mAdPopup.setAnimationStyle(R.style.popwin_anim_style);
        }
        if (!mAdPopup.isShowing()) {
            mAdPopup.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        } else {
            mAdPopup.dismiss();
        }
    }

    private void drawBiuView() {
        // TODO Auto-generated method stub
        circleR1 = width * 111 / 360;
        circleR2 = width * 81 / 180;
        circleR3 = width * 49 / 90;
        //设置半径
        biuView.setCircleR(circleR1, circleR2, circleR3);
        //设置圆心
        biuView.setDot(x0, y0);
    }

    private void setBiuLayout() {
        /**
         * 外层动画
         */
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width * 125 / 360, width * 125 / 360);
        params.leftMargin = width * 235 / 720;
        params.topMargin = width * 146 / 360;
        biuLayout.setLayoutParams(params);
        RelativeLayout.LayoutParams animParams = new RelativeLayout.LayoutParams(width * 125 / 360, width * 125 / 360);
        userBiuBg.setLayoutParams(animParams);
        /***
         * 头像及倒计时
         */
        RelativeLayout.LayoutParams imvParams = new RelativeLayout.LayoutParams(width * 65 / 360, width * 65 / 360);
        imvParams.leftMargin = width * 30 / 360;
        imvParams.topMargin = width * 30 / 360;
        userBiuImv.setLayoutParams(imvParams);
        userBiuBg.startAnimation(animationAlpha);
        //倒计时view
        drawTaskView();
        /**
         * 外层biubiu点击
         * */
        biuLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int flag = 0;
                String headFlag = com.android.biubiu.common.Constant.headState;
                if (!LoginUtils.isLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
                    startActivity(intent);
                    return;
                }
                if (currentTime > 0) {
                    return;
                }
                String sendTimeStr = SharePreferanceUtils.getInstance().getBiuTime(getActivity(), SharePreferanceUtils.SEND_BIU_TIME, "");
                if (!TextUtils.isEmpty(sendTimeStr)) {
                    long time = System.currentTimeMillis() - Long.parseLong(sendTimeStr);
                    if (time / 1000 > 90) {
                        //启动发送biubiu界面
                        if (!TextUtils.isEmpty(headFlag)) {
                            switch (Integer.parseInt(headFlag)) {
                                case Constants.HEAD_VERIFYSUC_UNREAD:
                                case Constants.HEAD_VERIFYFAIL_UNREAD:
                                case Constants.HEAD_VERIFYFAIL:
                                case Constants.HEAD_VERIFYFAIL_UPDATE:
                                    showShenHeDaiog(Integer.parseInt(headFlag));
                                    break;
                                default:
                                    if(SharePreferanceUtils.getInstance().isBiuEnd(getActivity(),SharePreferanceUtils.IS_BIU_END,true)){
                                        Intent intent = new Intent(getActivity(), BiuBiuSendActivity.class);
                                        startActivityForResult(intent, SEND_BIU_REQUEST);
                                    }else{
                                        Intent intent = new Intent(getActivity(), ReceiveBiuListActivity.class);
                                        startActivity(intent);
                                    }
                                    break;
                            }
                        } else {
                            if(SharePreferanceUtils.getInstance().isBiuEnd(getActivity(),SharePreferanceUtils.IS_BIU_END,true)){
                                Intent intent = new Intent(getActivity(), BiuBiuSendActivity.class);
                                startActivityForResult(intent, SEND_BIU_REQUEST);
                            }else{
                                Intent intent = new Intent(getActivity(), ReceiveBiuListActivity.class);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "距离上次发biu还不到90秒哦！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //启动发送biubiu界面
                    if (!TextUtils.isEmpty(headFlag)) {
                        switch (Integer.parseInt(headFlag)) {
                            case Constants.HEAD_VERIFYSUC_UNREAD:
                            case Constants.HEAD_VERIFYFAIL_UNREAD:
                            case Constants.HEAD_VERIFYFAIL:
                            case Constants.HEAD_VERIFYFAIL_UPDATE:
                                showShenHeDaiog(Integer.parseInt(headFlag));
                                break;
                            default:
                                if(SharePreferanceUtils.getInstance().isBiuEnd(getActivity(),SharePreferanceUtils.IS_BIU_END,true)){
                                    Intent intent = new Intent(getActivity(), BiuBiuSendActivity.class);
                                    startActivityForResult(intent, SEND_BIU_REQUEST);
                                }else{
                                    Intent intent = new Intent(getActivity(), ReceiveBiuListActivity.class);
                                    startActivity(intent);
                                }
                                break;
                        }
                    } else {
                        if(SharePreferanceUtils.getInstance().isBiuEnd(getActivity(),SharePreferanceUtils.IS_BIU_END,true)){
                            Intent intent = new Intent(getActivity(), BiuBiuSendActivity.class);
                            startActivityForResult(intent, SEND_BIU_REQUEST);
                        }else{
                            Intent intent = new Intent(getActivity(), ReceiveBiuListActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void showShenHeDaiog(final int flag) {
        String title = "";
        String msg = "";
        String strBtn1 = "";
        String strBtn2 = "";
        switch (flag) {
            case Constants.HEAD_VERIFYSUC_UNREAD:
                title = getResources().getString(R.string.head_egis);
                msg = getResources().getString(R.string.head_egis_info);
                strBtn1 = "我知道了";
                break;
            case Constants.HEAD_VERIFYFAIL_UNREAD:
            case Constants.HEAD_VERIFYFAIL:
                title = getResources().getString(R.string.head_no_egis);
                msg = getResources().getString(R.string.head_no_egis_info1);
                strBtn1 = "取消";
                strBtn2 = "重新上传";
                break;
            case Constants.HEAD_VERIFYFAIL_UPDATE:
                title = getResources().getString(R.string.head_no_egis);
                msg = getResources().getString(R.string.head_no_egis_info2);
                strBtn1 = "取消";
                strBtn2 = "重新上传";
                break;
            default:
                break;
        }
        if (flag == Constants.HEAD_VERIFYSUC_UNREAD) {
            CommonDialog.singleBtnDialog(getActivity(), title, msg, strBtn1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    HttpUtils.commitIconState(getActivity(), flag);
                    Intent intent = new Intent(getActivity(), BiuBiuSendActivity.class);
                    startActivityForResult(intent, SEND_BIU_REQUEST);
                }
            });
        } else {
            CommonDialog.doubleBtnDialog(getActivity(), title, msg, strBtn1, strBtn2, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    switch (flag) {
                        case Constants.HEAD_VERIFYFAIL_UNREAD:
                            HttpUtils.commitIconState(getActivity(), flag);
                            break;
                        case Constants.HEAD_VERIFYFAIL_UPDATE:
                            HttpUtils.commitIconState(getActivity(), flag);
                            Intent intent = new Intent(getActivity(), BiuBiuSendActivity.class);
                            startActivityForResult(intent, SEND_BIU_REQUEST);
                            break;
                        default:
                            break;
                    }
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    switch (flag) {
                        case Constants.HEAD_VERIFYFAIL_UNREAD:
                            showHeadDialog();
                            break;
                        case Constants.HEAD_VERIFYFAIL:
                            showHeadDialog();
                            break;
                        case Constants.HEAD_VERIFYFAIL_UPDATE:
                            showHeadDialog();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }

    }

    public void showHeadDialog() {
        CommonDialog.headDialog(getActivity(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                /*Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						"image/*");*/
                startActivityForResult(intent, SELECT_PHOTO);
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }

    //设置倒计时按钮
    private void drawTaskView() {
        // TODO Auto-generated method stub
        taskView.setDot(x0, y0);
        taskView.setRadius(width * 65 / 720, 2);
        taskView.setTotal(totalTime);
    }

    //放置接收到的用户
    private void initUserGroup() {
        //给三个头像layout赋值
        userD1 = width * 58 / 360;
        userD2 = width * 34 / 360;
        userD3 = width * 25 / 360;
        //计算分割区域
        c1DotList.clear();
        c1DotList.addAll(BiuUtil.caculateCircle(n1, circleR1, x0, y0));
        c2DotList.clear();
        c2DotList.addAll(BiuUtil.caculateCircle(n2, circleR2, x0, y0));
        edgeAngleList.clear();
        edgeAngleList.addAll(BiuUtil.getEdgeAngle(width, userD3, circleR3, x0, y0));
        c3DotList.clear();
        //将角度按照从小到大排列
        Collections.sort(edgeAngleList, new SorByAngle());
        c3DotList.addAll(BiuUtil.caculateCircleBig(edgeAngleList, userD3, circleR3, x0, y0, n3));
    }

    @Override
    public void onTabClick() {
        if(!LoginUtils.isLogin(getActivity()) && ((MainActivity)getActivity()).isReverse()){
            ((MainActivity)getActivity()).reverseBack();
        }
        if(LoginUtils.isLogin(getActivity())){
            resumeDraw();
        }
    }

    @Override
    public void onLeaveTab() {
        if(LoginUtils.isLogin(getActivity())){
            pauseDraw();
        }
    }

    //按照角度
    class SorByAngle implements Comparator {
        public int compare(Object o1, Object o2) {
            Double s1 = (Double) o1;
            Double s2 = (Double) o2;
            if (s1 < s2)
                return 1;
            return 0;
        }
    }

    //判断是否存在
    private boolean isOnCircle(BiuBean userBean) {
        if (allUserCodeList.contains(userBean.getUserCode())) {
            return true;
        }
        return false;
    }

    //往第一个圈上放view
    private void addCircle1View(BiuBean userBean) {
        //如果数据库取到的biu屏幕上已存在 则舍弃
        if (isOnCircle(userBean)) {
            return;
        }
        boolean haveSpace = false;
        for (int i = 0; i < n1; i++) {
            DotBean bean = c1DotList.get(i);
            if (!bean.isAdd()) {
                c1DotList.get(i).setAdd(true);
                //添加区域标记
                userBean.setIndex(i);
                haveSpace = true;
                //计算放置位置
                double randomAngle = BiuUtil.getRandomAngle(n1, i, userD1, circleR1);
                int xLocation = BiuUtil.getLocationX(randomAngle, userD1, circleR1, x0);
                int yLocation = BiuUtil.getLocationY(randomAngle, userD1, circleR1, y0);
                userBean.setX(xLocation);
                userBean.setY(yLocation);
                user1List.add(userBean);
                allUserCodeList.add(userBean.getUserCode());
                createCir1NewView(xLocation, yLocation, (int) userD1, (int) userD1, userBean);
                showInfoLayout(userBean);
                break;
            }
        }
        if (!haveSpace) {
            //将内圈中的用户按照时间顺序排列
            Collections.sort(user1List, new SorByTime());
            if (user1List.size() > 0) {
                BiuBean moveBean = user1List.get(0);
                user1List.remove(0);
                moveOneToTwo(moveBean);
            }
        }
    }

    //从第1个圈移到第2个圈
    private void moveOneToTwo(BiuBean oneUserBean) {
        boolean haveSpace = false;
        for (int i = 0; i < n2; i++) {
            DotBean dotBean = c2DotList.get(i);
            if (!dotBean.isAdd()) {
                haveSpace = true;
                //内圈最早的移到第二圈,计算移到第二圈的位置x2,y2
                double randomAngle = BiuUtil.getRandomAngle(n2, i, userD2, circleR2);
                int x2 = BiuUtil.getLocationX(randomAngle, userD2, circleR2, x0);
                int y2 = BiuUtil.getLocationY(randomAngle, userD2, circleR2, y0);
                moveUserView(oneUserBean.getX(), oneUserBean.getY(),
                        x2, y2, oneUserBean, userD2, userD1, userD2);
                oneUserBean.setX(x2);
                oneUserBean.setY(y2);
                c2DotList.get(i).setAdd(true);
                //改变空闲标记，将最新插入内圈
                c1DotList.get(oneUserBean.getIndex()).setAdd(false);
                //移动后改变区域标记
                oneUserBean.setIndex(i);
                user2List.add(oneUserBean);
                allUserCodeList.add(oneUserBean.getUserCode());
                addCircle1View(newUserBean);
                break;
            }
        }
        if (!haveSpace) {
            Collections.sort(user2List, new SorByTime());
            BiuBean twoUserBean = user2List.get(0);
            user2List.remove(0);
            moveTwoToThree(oneUserBean, twoUserBean);
        }
    }

    //从第2个圈移到第3个圈
    private void moveTwoToThree(BiuBean oneUserBean, BiuBean twoUserBean) {
        boolean haveSpace = false;
        for (int i = 0; i < n3; i++) {
            DotBean dotBean = c3DotList.get(i);
            if (!dotBean.isAdd()) {
                haveSpace = true;
                //从第2个圈移到第3个圈,计算移到第二圈的位置x3,y3
                //将角度按照从小到大排列
                Collections.sort(edgeAngleList, new SorByAngle());
                double randomAngle = BiuUtil.getRandomAngleBig(edgeAngleList, n3, i, userD1, circleR3);
                int x3 = BiuUtil.getLocationX(randomAngle, userD3, circleR3, x0);
                int y3 = BiuUtil.getLocationY(randomAngle, userD3, circleR3, y0);
                moveUserView(twoUserBean.getX(), twoUserBean.getY(),
                        x3, y3, twoUserBean, userD3, userD1, userD2);
                twoUserBean.setX(x3);
                twoUserBean.setY(y3);
                c3DotList.get(i).setAdd(true);
                //改变空闲标记，内圈移到第二圈
                c2DotList.get(twoUserBean.getIndex()).setAdd(false);
                //移动后改变区域标记
                twoUserBean.setIndex(i);
                user3List.add(twoUserBean);
                allUserCodeList.add(twoUserBean.getUserCode());
                moveOneToTwo(oneUserBean);
                break;
            }
        }
        if (!haveSpace) {
            //移除外圈最早的
            Collections.sort(user3List, new SorByTime());
            BiuBean delBean = user3List.get(0);
            //RelativeLayout rl = (RelativeLayout) userGroupLayout.findViewWithTag(retivTag+delBean.getId());
            RelativeLayout rl = (RelativeLayout) userGroupLayout.findViewById(retivIdTag + delBean.getUserCode());
            userGroupLayout.removeView(rl);
            //改变空闲标记，第二圈移到外圈
            c3DotList.get(delBean.getIndex()).setAdd(false);
            user3List.remove(0);
            moveTwoToThree(oneUserBean, twoUserBean);
        }
    }

    //显示底部的信息view
    private void showInfoLayout(BiuBean bean) {
        // TODO Auto-generated method stub
        infoHandler.removeCallbacks(infoR);
        infoLayout.setVisibility(View.VISIBLE);
        nameTv.setText(bean.getNickname());
        sexTv.setText(bean.getSex());
        if (bean.getSex().equals(Constants.SEX_MALE)) {
            sexTv.setText("男生");
        } else {
            sexTv.setText("女生");
        }
        ageTv.setText(bean.getAge() + "岁");
        starTv.setText(bean.getStarsign());
        if (bean.getSchool() != null && !bean.getSchool().equals("")) {
            if (schoolDao.getschoolName(bean.getSchool()) != null) {
                schoolTv.setText(schoolDao.getschoolName(bean.getSchool()).get(0).getUnivsNameString());
            }

        }
        infoHandler.postDelayed(infoR, 5000);
    }

    Runnable infoR = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            infoLayout.setVisibility(View.GONE);
        }
    };


    //按照时间排序
    class SorByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            BiuBean s1 = (BiuBean) o1;
            BiuBean s2 = (BiuBean) o2;
            if (s1.getTime() < s2.getTime())
                return 1;
            return 0;
        }
    }

    //创建第一圈上新的view,宽高为要创建view的宽高
    private void createCir1NewView(int xLocation, int yLocation, int lWidth, int lHeight, final BiuBean bean) {
        final RelativeLayout rl = new RelativeLayout(getActivity());
        //rl.setTag(retivTag+bean.getId());
        rl.setId(retivIdTag + bean.getUserCode());
        AbsoluteLayout.LayoutParams llParams = new AbsoluteLayout.LayoutParams(
                lWidth,
                lHeight, xLocation, yLocation);
        rl.setBackgroundResource(R.drawable.photo_fail);
        userGroupLayout.addView(rl, llParams);
        final ImageView gifIv = new ImageView(getActivity());
        RelativeLayout.LayoutParams gifP = new RelativeLayout.LayoutParams(
                lWidth,
                lHeight);
        rl.addView(gifIv, gifP);
        //layout与头像宽高差 像素
        int margin = DensityUtil.dip2px(getActivity(), (58 - 46));
        if (lWidth != userD1) {
            margin = (int) (margin * lWidth / userD1);
        }
        ImageView imageViewbg = new ImageView(getActivity());
        RelativeLayout.LayoutParams imagebg = new RelativeLayout.LayoutParams(
                lWidth - margin,
                lHeight - margin);
        imageViewbg.setId(bean.getUserCode());

        //imageViewbg.setTag(imvHeadTag+bean.getId());
        imageViewbg.setId(imvHeadIdTag + bean.getUserCode());

        imagebg.addRule(RelativeLayout.CENTER_IN_PARENT);
        rl.addView(imageViewbg, imagebg);
        if (lWidth != userD1) {
            margin = margin + DensityUtil.dip2px(getActivity(), 2);
        } else {
            margin = margin + DensityUtil.dip2px(getActivity(), 4);
        }
        ImageView imageView = new ImageView(getActivity());
        RelativeLayout.LayoutParams imageP = new RelativeLayout.LayoutParams(
                lWidth - margin,
                lHeight - margin);
        imageView.setId(imvHeadIdTag + bean.getUserCode());
        //imageView.setTag(imvHeadTag+bean.getId());
        imageP.addRule(RelativeLayout.CENTER_IN_PARENT);
        rl.addView(imageView, imageP);
        final ImageView imageViewL = new ImageView(getActivity());
        //红点layout直径
        int dotD = DensityUtil.dip2px(getActivity(), 8);
        if (lWidth != userD1) {
            dotD = (int) (dotD * lWidth / userD1);
        }
        RelativeLayout.LayoutParams imagePL = new RelativeLayout.LayoutParams(dotD, dotD);
        //基于头像底部 右侧 偏移d/4
        //imagePL.leftMargin = dotD/4;
        imagePL.rightMargin = DensityUtil.dip2px(getActivity(), 0);
        imagePL.bottomMargin = DensityUtil.dip2px(getActivity(), 2);
        //imageViewL.setTag(imvDotTag+bean.getId());
        imageViewL.setId(imvDotIdTag + bean.getUserCode());
        imagePL.addRule(RelativeLayout.ALIGN_BOTTOM, imageView.getId());
        imagePL.addRule(RelativeLayout.ALIGN_RIGHT, imageViewbg.getId());
        rl.addView(imageViewL, imagePL);
        /*TextView tv = new TextView(getActivity());
        RelativeLayout.LayoutParams tvP = new RelativeLayout.LayoutParams(LayoutParams .WRAP_CONTENT,LayoutParams .WRAP_CONTENT);
		tvP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); 
		tvP.addRule(RelativeLayout.CENTER_HORIZONTAL); 
		tv.setText("11%");
		tv.setMaxLines(1);
		tv.setTextSize(8);
		tv.setGravity(Gravity.CENTER); 
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTag(tvTag+bean.getId());
		rl.addView(tv, tvP);
		if(lWidth!=userD1){
			tv.setVisibility(View.GONE);
		}*/
        imageViewbg.setImageResource(R.drawable.biu_imageview_photo_s);
        imageView.setImageResource(R.drawable.photo_fail);
        x.image().bind(imageView, bean.getIconUrl(), imageOptions);
        gifIv.setImageResource(R.drawable.biu_imageview_photo_circle);
        gifIv.startAnimation(animationUserBg);
        imageViewL.setImageResource(R.drawable.biu_imageview_photo_news_s);
        imageViewL.setVisibility(View.VISIBLE);
        rl.setBackgroundResource(0);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                gifIv.startAnimation(animationHide);
                gifIv.setVisibility(View.GONE);
            }
        }, 1000);
        rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (LoginUtils.isLogin(getActivity())) {
                    imageViewL.setVisibility(View.GONE);
                    Intent intent = new Intent(getActivity(), BiuBiuReceiveActivity.class);
                    intent.putExtra("userCode", String.valueOf(bean.getUserCode()));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    //移动view
    public void moveUserView(double startX, double startY, double endX, double endY, BiuBean userBean, float viewD, float viewD1, float viewD2) {
        //final RelativeLayout rl = (RelativeLayout) userGroupLayout.findViewWithTag(retivTag+userBean.getId());
        final RelativeLayout rl = (RelativeLayout) userGroupLayout.findViewById(retivIdTag + userBean.getUserCode());
        float scale = 0;
        float d = 0;
        if (rl.getWidth() == viewD2) {
            scale = viewD2 / viewD2;
            d = viewD * (1 - scale) / 2;
        } else {
            scale = viewD / viewD1;
            d = viewD1 * (1 - scale) / 2;
        }
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(rl, "x", (float) startX, (float) (endX - d));
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(rl, "y", (float) startY, (float) (endY - d));
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(rl, "scaleX", 1.0f, scale);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(rl, "scaleY", 1.0f, scale);
        ObjectAnimator anim5 = ObjectAnimator.ofFloat(rl, "alpha", 1.0f, 0.8f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(500);
        animSet.playTogether(anim1, anim2, anim3, anim4, anim5);
        animSet.start();
        /*TextView tv = (TextView) rl.findViewWithTag(tvTag+userBean.getId());
        tv.setVisibility(View.GONE);*/
    }

    //加入所有list中的view
    protected void addAllView(ArrayList<BiuBean> list, boolean isLogin) {
        int length = 0;
        if (list.size() > allCount) {
            length = allCount;
        } else {
            length = list.size();
        }
        for (int i = 0; i < length; i++) {
            BiuBean userBean = list.get(i);
            boolean haveOneSpace = false;
            boolean haveTwoSpace = false;
            for (int j = i; j < n1; j++) {
                DotBean bean = c1DotList.get(j);
                if (!bean.isAdd()) {
                    haveOneSpace = true;
                    //添加区域标记
                    userBean.setIndex(j);
                    //计算放置位置
                    double randomAngle = BiuUtil.getRandomAngle(n1, j, userD1, circleR1);
                    int xLocation = BiuUtil.getLocationX(randomAngle, userD1, circleR1, x0);
                    int yLocation = BiuUtil.getLocationY(randomAngle, userD1, circleR1, y0);
                    userBean.setX(xLocation);
                    userBean.setY(yLocation);
                    createCir1NewView(xLocation, yLocation, (int) userD1, (int) userD1, userBean);
                    c1DotList.get(i).setAdd(true);
                    user1List.add(userBean);
                    break;
                }
            }
            if (haveOneSpace) {
                continue;
            }
            for (int k = i - n1; k < n2; k++) {
                DotBean dotBean = c2DotList.get(k);
                if (!dotBean.isAdd()) {
                    haveTwoSpace = true;
                    //计算第二圈的位置x2,y2
                    double randomAngle = BiuUtil.getRandomAngle(n2, k, userD2, circleR2);
                    int x2 = BiuUtil.getLocationX(randomAngle, userD2, circleR2, x0);
                    int y2 = BiuUtil.getLocationY(randomAngle, userD2, circleR2, y0);
                    userBean.setX(x2);
                    userBean.setY(y2);
                    createCir1NewView(x2, y2, (int) userD2, (int) userD2, userBean);
                    userBean.setIndex(k);
                    c2DotList.get(k).setAdd(true);
                    user2List.add(userBean);
                    break;
                }
            }
            if (haveTwoSpace) {
                continue;
            }
            for (int l = i - n1 - n2; l < n3; l++) {
                DotBean dotBean = c3DotList.get(l);
                if (!dotBean.isAdd()) {
                    //计算第3圈的位置x3,y3
                    //将角度按照从小到大排列
                    Collections.sort(edgeAngleList, new SorByAngle());
                    double randomAngle = BiuUtil.getRandomAngleBig(edgeAngleList, n3, l, userD1, circleR3);
                    int x3 = BiuUtil.getLocationX(randomAngle, userD3, circleR3, x0);
                    int y3 = BiuUtil.getLocationY(randomAngle, userD3, circleR3, y0);
                    createCir1NewView(x3, y3, (int) userD3, (int) userD3, userBean);
                    userBean.setX(x3);
                    userBean.setY(y3);
                    c3DotList.get(l).setAdd(true);
                    //移动后改变区域标记
                    userBean.setIndex(l);
                    user3List.add(userBean);
                    break;
                }

            }
        }
    }

    private void getGrabBiuUser() {
        RequestParams params = new RequestParams(HttpContants.APP_BIU_GETGRABBIULIST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                log.d("mytest", "suc" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");

                    JSONArray userArray = data.getJSONArray("users");
                    Gson gson = new Gson();
                    ArrayList<BiuBean> list = gson.fromJson(userArray.toString(), new TypeToken<List<BiuBean>>() {
                    }.getType());
                    if (null != list && list.size() > 0) {
                            userBiuImv.setImageResource(R.drawable.photo_fail);
                            userBiuImv.setVisibility(View.VISIBLE);
                            Collections.sort(list, new SorByTime());
                            /*grabBiuList.clear();
                            //将最新的9个放入列表
                            if (list.size() > 9) {
                                grabBiuList.addAll(list.subList(0, 9));
                            } else {
                                grabBiuList.addAll(list);
                            }*/
                            updateBiuView(list.get(0));
                    }else{
                        userBiuImv.setImageResource(R.drawable.biu_btn_unfinished);
                        userBiuImv.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                isBiuLoading = false;
                isBiuLoaded = false;
                log.d("mytest", "error");
            }

            @Override
            public void onCancelled(CancelledException e) {
                log.d("mytest", "cancel");
            }

            @Override
            public void onFinished() {
                log.d("mytest", "finish");
            }
        });
    }

    //biubiu被抢后显示view
    private void updateBiuView(BiuBean bean) {
        grabBiuBean = bean;
        currentTime = 0;
        taskHandler.removeCallbacks(taskR);
        taskView.setVisibility(View.GONE);
        userBiuImv.setImageResource(R.drawable.photo_fail);
        userBiuImv.setVisibility(View.VISIBLE);
         x.image().bind(userBiuImv, bean.getIconUrl(), imageOptions);
       /* if (grabBiuList.size() > 8) {
            grabBiuList.remove(8);
            grabBiuList.add(bean);
        } else {
            grabBiuList.add(bean);
        }*/
    }

    //新的获取biu列表
    private void getBiuList(final long requestTime) {
        isBiuLoading = true;
        isBiuLoaded = false;
        RequestParams params = new RequestParams(HttpContants.APP_BIU_GETTARGETBIULIST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("num", 20);
            requestObject.put("last_date", requestTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                log.d("mytest", "suc" + result);
                isBiuLoading = false;
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        isBiuLoaded = false;
                        return;
                    }
                    isBiuLoaded = true;
                    JSONObject data = jsons.getJSONObject("data");
                    String headFlag = data.getString("iconStatus");
                    com.android.biubiu.common.Constant.headState = headFlag;
                    if (headFlag.equals("2") || headFlag.equals("4")) {
                        showShenHeDaiog(Integer.parseInt(headFlag));
                    }
                    String recSex = data.getString("s_sex");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.RECEIVE_SEX, recSex);
                    inveralTime = data.getInt("biu_time_interval");
                    String next = data.getString("has_next");
                    if (next.equals("0")) {
                        isBiuHasNext = false;
                    } else {
                        isBiuHasNext = true;
                    }
                    JSONArray userArray = data.getJSONArray("users");
                    Gson gson = new Gson();
                    ArrayList<BiuBean> list = gson.fromJson(userArray.toString(), new TypeToken<List<BiuBean>>() {
                    }.getType());
                    if (null != list) {
                        if (list.size() > 0) {
                            if (requestTime == 0) {
                                biuDao.deleteAll();
                            }
                            biuDao.addBiuList(list, SharePreferanceUtils.getInstance().getReceiveSex(getActivity(), SharePreferanceUtils.RECEIVE_SEX, ""));
                        } else {
                            biuDao.updateAllBiuState();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                isBiuLoading = false;
                isBiuLoaded = false;
                log.d("mytest", "error");
            }

            @Override
            public void onCancelled(CancelledException e) {
                log.d("mytest", "cancel");
            }

            @Override
            public void onFinished() {
                log.d("mytest", "finish");
            }
        });
    }

    /**
     * 退出环信登录
     */
    public void exitHuanxin() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                //清空本地token
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.HX_USER_NAME, "");
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.HX_USER_PASSWORD, "");
            }

            @Override
            public void onProgress(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
            }
        });

    }


    //获取未登录时的biubiu列表
    private void getBiuListUnlogin() {
        // TODO Auto-generated method stub
        user1List.clear();
        user2List.clear();
        user3List.clear();
        allUserCodeList.clear();
        userGroupLayout.removeAllViews();
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.GET_UNLOGIN_BIU_LIST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                LogUtil.d("mytest", "getall--" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    JSONArray userArray = data.getJSONArray("users");
                    Gson gson = new Gson();
                    ArrayList<BiuBean> list = gson.fromJson(userArray.toString(), new TypeToken<List<BiuBean>>() {
                    }.getType());
                    if (null != list && list.size() > 0) {
                        addAllView(list, false);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        showBiuHandler.removeCallbacks(shouBiuR);
        mInvalidHandler.sendEmptyMessageDelayed(0,1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, true);
        biuDao.deleteAll();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEND_BIU_REQUEST:
                if (resultCode == BiuBiuSendActivity.RESULT_OK) {
                    //开始倒计时
                    currentTime = totalTime;
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.SEND_BIU_TIME, System.currentTimeMillis() + "");
                    taskView.setVisibility(View.VISIBLE);
                    userBiuImv.setVisibility(View.GONE);
                    taskView.updeteTask(currentTime);
                    taskHandler.post(taskR);
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_BIU_END, false);
                }
                break;
            case CROUP_PHOTO:
                try {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        userheadBitmap = extras.getParcelable("data");
                        if (userheadBitmap != null) {
                            headPath = saveHeadImg(userheadBitmap);
                            uploadPhoto(headPath);
                        }
                    }
                } catch (NullPointerException e) {
                    // TODO: handle exception
                }
                break;
            case SELECT_PHOTO:
                if (data != null) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case ACTIVITY_LIST:
                mTopTitle.setRightImage(R.drawable.biu_btn_activity_nor);
                break;
            default:
                break;
        }
    }

    private void uploadPhoto(String headPath) {
        // TODO Auto-generated method stub
        isUploadingPhoto = true;
        showLoadingLayout("正在上传……");
        UploadImgUtils.uploadPhoto(getActivity(), headPath, new BiuBooleanCallback() {

            @Override
            public void callback(boolean result) {
                // TODO Auto-generated method stub
                isUploadingPhoto = false;
                if (result) {
                    com.android.biubiu.common.Constant.headState = "3";
                    dismissLoadingLayout();
                } else {
                    Toast.makeText(getActivity(), "上传照片失败", Toast.LENGTH_SHORT).show();
                    dismissLoadingLayout();
                }
            }
        });
    }

    public void showLoadingLayout(String loadingStr) {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        }
        if (loadGif == null) {
            loadGif = (GifView) view.findViewById(R.id.load_gif);
            loadGif.setGifImage(R.drawable.loadingbbbb);
            loadGif.setShowDimension(DensityUtil.dip2px(getActivity(), 30), DensityUtil.dip2px(getActivity(), 30));
            loadGif.setGifImageType(GifImageType.COVER);
        }
        if (loadTv == null) {
            loadTv = (TextView) view.findViewById(R.id.loading_tv);
        }
        loadTv.setText(loadingStr);
        loadGif.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    //加载完毕隐藏
    public void dismissLoadingLayout() {
        if (loadingLayout == null) {
            loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        }
        loadingLayout.setVisibility(View.GONE);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        // 调用拍照的裁剪功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽和搞的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROUP_PHOTO);
    }

    public String saveHeadImg(Bitmap head) {
        FileOutputStream fos = null;
        String path = "";
        path = Environment.getExternalStorageDirectory()
                + "/biubiu/" + System.currentTimeMillis() + ".png";
        File file = new File(path);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            head.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return path;

    }

    //倒计时线程
    Runnable taskR = new Runnable() {

        @Override
        public void run() {
            taskView.updeteTask(currentTime--);
            if (currentTime <= 0) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity().getApplicationContext(), "你的biubiu暂时无人应答，请重新发送", Toast.LENGTH_SHORT).show();
                }
                taskView.setVisibility(View.GONE);
                userBiuImv.setImageResource(R.drawable.biu_btn_unfinished);
                userBiuImv.setVisibility(View.VISIBLE);
                currentTime = 0;
                taskHandler.removeCallbacks(taskR);
                return;
            }
            taskHandler.postDelayed(taskR, 1000);
        }
    };
    Runnable shouBiuR = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            int time = 0;
            if (inveralTime == 1) {
                time = 1;
            } else {
                time = random.nextInt(inveralTime) + 1;
            }

            if (!isBiuLoading && isBiuLoaded) {
                int biuCount = biuDao.getBiuListUnread();
                //数目小于5 则去网上继续请求
                if (biuCount < 5 && isBiuHasNext) {
                    if (isBiuHasNext) {
                        long requestTime = biuDao.getBiuListUnread();
                        getBiuList(requestTime);
                    }
                }
                newUserBean = biuDao.getBiuToShow();
                if (null != newUserBean) {
                    biuDao.updateBiuState(newUserBean.getUserCode());
                    addCircle1View(newUserBean);
                }
            } else {
                if (!isBiuLoading) {
                    getBiuList(0);
                }
            }
            showBiuHandler.postDelayed(shouBiuR, time * 1000);
        }
    };

    @Override
    public void updateView(BiuBean userBean, int type) {
        // TODO Auto-generated method stub
        switch (type) {
            case 0:
                //有新的匹配消息
                if (!isOnCircle(userBean)) {
                    biuDao.addOneBiu(userBean, SharePreferanceUtils.getInstance().getReceiveSex(getActivity(), SharePreferanceUtils.RECEIVE_SEX, ""));
                }
                break;
            case 1:
                //biubiu被抢啦
                updateBiuView(userBean);
                break;
            default:
                break;
        }
    }

    public void pauseDraw(){
        showBiuHandler.removeCallbacks(shouBiuR);
    }

    public void resumeDraw(){
        showBiuHandler.post(shouBiuR);
    }
}
