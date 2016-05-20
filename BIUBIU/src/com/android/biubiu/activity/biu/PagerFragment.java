package com.android.biubiu.activity.biu;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.biubiu.BaseFragment;
import com.android.biubiu.activity.LoginActivity;
import com.android.biubiu.activity.RegisterThreeActivity;
import com.android.biubiu.activity.mine.AboutMeActivity;
import com.android.biubiu.activity.mine.ChangeBrithdayActivity;
import com.android.biubiu.activity.mine.ChangeCityActivity;
import com.android.biubiu.activity.mine.ChangeConstellationActivity;
import com.android.biubiu.activity.mine.ChangeHeightWeightActivity;
import com.android.biubiu.activity.mine.ChangeHomeTwonActivity;
import com.android.biubiu.activity.mine.ChangeIdentityProfessionActivity;
import com.android.biubiu.activity.mine.ChangeNameActivity;
import com.android.biubiu.activity.mine.ChangeSchoolActivity;
import com.android.biubiu.activity.mine.InterestLabelActivity;
import com.android.biubiu.activity.mine.MainSetActivity;
import com.android.biubiu.activity.mine.PersonalityTagActivity;
import com.android.biubiu.activity.mine.ScanUserHeadActivity;
import com.android.biubiu.activity.mine.SuperMainInfoActivity;
import com.android.biubiu.adapter.UserInterestAdapter;
import com.android.biubiu.adapter.UserPagerPhotoAdapter;
import com.android.biubiu.adapter.UserPagerTagAdapter;
import com.android.biubiu.bean.InterestByCateBean;
import com.android.biubiu.bean.InterestTagBean;
import com.android.biubiu.bean.PersonalTagBean;
import com.android.biubiu.bean.UserFriends;
import com.android.biubiu.bean.UserInfoBean;
import com.android.biubiu.bean.UserPhotoBean;
import com.android.biubiu.chat.MyHintDialog;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.sqlite.CityDao;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.sqlite.UserDao;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.DensityUtil;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.NetUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.android.biubiu.view.MyGridView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PagerFragment extends BaseFragment implements View.OnClickListener, FragmentIndicator.OnClickListener {
    private static final int UPDATE_INFO = 1001;
    private static final int UPDATE_PHOTOS = 1002;
    private static final int UPDATE_PERSONAL_TAG = 1003;
    private static final int UPDATE_INTEREST_TAG = 1004;
    private static final int UPDATE_HEAD = 1005;
    private static final int DELETE_PHOTOS = 1006;
    private static final int TO_LOGIN = 1007;
    private static final int TO_REGISTER = 1008;
    private static final int TO_SETTING = TO_REGISTER + 1;
    private ImageView userheadImv;
    private TextView usernameTv;
    private ImageView addPhotoImv;
    private ViewPager photoPager;
    private TextView userInfoTv;
    private TextView userInfoBigTv;
    private TextView userOpenTv;
    private LinearLayout nicknameLinear;
    private TextView nicknameTv;
    private LinearLayout sexLinear;
    private TextView sexTv;
    private LinearLayout birthdayLinear;
    private TextView birthdayTv;
    private LinearLayout starSignLinear;
    private TextView starSignTv;
    private LinearLayout cityLinear;
    private TextView cityTv;
    private LinearLayout hometownLinear;
    private TextView hometownTv;
    private LinearLayout heightWeightLinear;
    private TextView heightWeightTv;
    private LinearLayout identityLinear;
    private TextView identityTagTv;
    private TextView identityTv;
    private LinearLayout schoolLinear;
    private TextView schoolTagTv;
    private TextView schoolTv;
    private LinearLayout personalTagLinear;
    private MyGridView personalTagGv;
    private LinearLayout interestTagLinear;
    private MyGridView interestTagGv;
    private RelativeLayout backRl;
    private RelativeLayout userInfoLinear;
    private RelativeLayout otherInfoLayout;
    private TextView locationTv;
    private TextView matchTv;
    private TextView timeTv;
    private TextView iconVerify;
    private ImageView aboutMeArrow;
    private ImageView nickArrwo;
    private ImageView starArrow;
    private ImageView birthArrow;
    private ImageView heightArrow;
    private ImageView cityArrow;
    private ImageView homeArrwo;
    private ImageView identityArrow;
    private ImageView schoolArrow;
    private ImageView personalArrow;
    private ImageView interestArrow;
    private ImageView sexArrow;
    private TextView noPhotoTv;
    private ImageView superManIv;
    private UserInfoBean infoBean;
    ImageOptions imageOptions;
    private UserPagerPhotoAdapter photoAdapter;
    //标记个人描述是否已经展开
    private boolean isOpen = false;
    private boolean isMyself = false;
    private UserPagerTagAdapter personalAdapter;
    private UserInterestAdapter interestAdapter;
    ArrayList<PersonalTagBean> personalTagList = new ArrayList<PersonalTagBean>();
    ArrayList<InterestTagBean> interestTagList = new ArrayList<InterestTagBean>();
    //上传文件相关
    String accessKeyId = "";
    String accessKeySecret = "";
    String securityToken = "";
    String expiration = "";
    private CityDao cityDao = new CityDao();
    private SchoolDao schoolDao = new SchoolDao();
    private String userCode = "";
    private UserDao userDao;
    private String TAG = PagerFragment.class.getSimpleName();

    private TopTitleView mTopTitle;
    private LinearLayout mLoginView;
    private FrameLayout mLoginedView;
    private Button register, login;
    Bundle b;

    private boolean mRequestSuccess;

    public PagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_pager, container, false);
        b = getArguments();
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mLoginView = (LinearLayout) mRootview.findViewById(R.id.login_view);
        register = (Button) mRootview.findViewById(R.id.register_item_btn);
        login = (Button) mRootview.findViewById(R.id.login_item_btn);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        mLoginedView = (FrameLayout) mRootview.findViewById(R.id.logined_view);

        mTopTitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        userheadImv = (ImageView) mRootview.findViewById(R.id.userhead_imv);
        userheadImv.setOnClickListener(this);
        //解决scrollview初始在顶部
        userheadImv.setFocusable(true);
        userheadImv.setFocusableInTouchMode(true);
        userheadImv.requestFocus();
        iconVerify = (TextView) mRootview.findViewById(R.id.virify_tv);
        usernameTv = (TextView) mRootview.findViewById(R.id.username_tv);
        addPhotoImv = (ImageView) mRootview.findViewById(R.id.add_userphoto_imv);
        photoPager = (ViewPager) mRootview.findViewById(R.id.userphoto_pager);
        userInfoTv = (TextView) mRootview.findViewById(R.id.userinfo_tv);
        userInfoBigTv = (TextView) mRootview.findViewById(R.id.userinfo_big_tv);
        userOpenTv = (TextView) mRootview.findViewById(R.id.open_tv);
        userOpenTv.setOnClickListener(this);
        userInfoLinear = (RelativeLayout) mRootview.findViewById(R.id.userinfo_linear);
        nicknameLinear = (LinearLayout) mRootview.findViewById(R.id.nickname_linear);
        nicknameTv = (TextView) mRootview.findViewById(R.id.nickname_tv);
        sexLinear = (LinearLayout) mRootview.findViewById(R.id.sex_linear);
        sexTv = (TextView) mRootview.findViewById(R.id.sex_tv);
        birthdayLinear = (LinearLayout) mRootview.findViewById(R.id.birthday_linear);
        birthdayTv = (TextView) mRootview.findViewById(R.id.birthday_tv);
        starSignLinear = (LinearLayout) mRootview.findViewById(R.id.starsign_linear);
        starSignTv = (TextView) mRootview.findViewById(R.id.starsign_tv);
        cityLinear = (LinearLayout) mRootview.findViewById(R.id.city_linear);
        cityTv = (TextView) mRootview.findViewById(R.id.city_tv);
        hometownLinear = (LinearLayout) mRootview.findViewById(R.id.hometown_linear);
        hometownTv = (TextView) mRootview.findViewById(R.id.hometown_tv);
        heightWeightLinear = (LinearLayout) mRootview.findViewById(R.id.height_weight_linear);
        heightWeightTv = (TextView) mRootview.findViewById(R.id.height_weight_tv);
        identityLinear = (LinearLayout) mRootview.findViewById(R.id.identity_linear);
        identityTv = (TextView) mRootview.findViewById(R.id.identity_tv);
        identityTagTv = (TextView) mRootview.findViewById(R.id.identity_tag_tv);
        schoolLinear = (LinearLayout) mRootview.findViewById(R.id.school_linear);
        schoolTagTv = (TextView) mRootview.findViewById(R.id.school_tag_tv);
        schoolTv = (TextView) mRootview.findViewById(R.id.school_tv);
        personalTagLinear = (LinearLayout) mRootview.findViewById(R.id.personal_tag_linear);
        personalTagGv = (MyGridView) mRootview.findViewById(R.id.personal_tag_gv);
        interestTagLinear = (LinearLayout) mRootview.findViewById(R.id.interest_tag_linear);
        interestTagGv = (MyGridView) mRootview.findViewById(R.id.interest_tag_gv);
        backRl = (RelativeLayout) mRootview.findViewById(R.id.back_rl);
        backRl.setOnClickListener(this);
        otherInfoLayout = (RelativeLayout) mRootview.findViewById(R.id.other_info_layout);
        locationTv = (TextView) mRootview.findViewById(R.id.location_tv);
        matchTv = (TextView) mRootview.findViewById(R.id.match_tv);
        timeTv = (TextView) mRootview.findViewById(R.id.time_tv);
        aboutMeArrow = (ImageView) mRootview.findViewById(R.id.about_arrow);
        nickArrwo = (ImageView) mRootview.findViewById(R.id.nickname_arrow);
        birthArrow = (ImageView) mRootview.findViewById(R.id.birth_arrow);
        starArrow = (ImageView) mRootview.findViewById(R.id.star_arrow);
        cityArrow = (ImageView) mRootview.findViewById(R.id.city_arrow);
        homeArrwo = (ImageView) mRootview.findViewById(R.id.home_arrow);
        heightArrow = (ImageView) mRootview.findViewById(R.id.height_arrow);
        identityArrow = (ImageView) mRootview.findViewById(R.id.identity_arrow);
        schoolArrow = (ImageView) mRootview.findViewById(R.id.school_arrow);
        personalArrow = (ImageView) mRootview.findViewById(R.id.personal_arrow);
        interestArrow = (ImageView) mRootview.findViewById(R.id.interest_arrow);
        noPhotoTv = (TextView) mRootview.findViewById(R.id.no_photo);
        sexArrow = (ImageView) mRootview.findViewById(R.id.sex_arrow);
        superManIv = (ImageView) mRootview.findViewById(R.id.super_man_iv);
        superManIv.setOnClickListener(this);

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.loadingbbbb)
                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(false)
                .build();

        photoPager.setOffscreenPageLimit(3);
        photoPager.setPageMargin(DensityUtil.dip2px(getActivity(), 10));
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    if (null != b) {
                        getActivity().finish();
                    } else {
                        Intent i = new Intent(getActivity(), BiuChargeActivity.class);
                        startActivity(i);
                    }
                }
            }
        });

        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    if (isMyself) {
                        Intent setIntent = new Intent(getActivity(), MainSetActivity.class);
                        startActivityForResult(setIntent, TO_SETTING);
                    } else {
                        getMosterDialog();
                    }
                }
            }
        });
    }

    private void initData() {
        userDao = new UserDao(getActivity());
        if (b == null) {
            mTopTitle.setLeftImage(R.drawable.main_nav_bar_icon_left);
            mTopTitle.setRightImage(R.drawable.main_nav_bar_icon_right);
            // mTopTitle.setVisibility(View.VISIBLE);
            backRl.setVisibility(View.GONE);
            isMyself = true;
            userCode = SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, "");
            initOnclick();
            switchView();
        } else {
            mTopTitle.setLeftImage(R.drawable.back_main);
            userCode = b.getString("userCode");
            if (userCode.equals(SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, ""))) {
                mTopTitle.setRightImage(R.drawable.main_nav_bar_icon_right);
                isMyself = true;
                initOnclick();
                switchView();
            } else {
                mTopTitle.setRightImage(R.drawable.mes_btn_right);
                isMyself = false;
                switchView();
            }
        }
    }

    private void switchView() {
        if (!LoginUtils.isLogin(getActivity())) {
            if (mLoginView.getVisibility() == View.GONE) {
                mLoginView.setVisibility(View.VISIBLE);
            }
            if (mLoginedView.getVisibility() == View.VISIBLE) {
                mLoginedView.setVisibility(View.GONE);
            }
            if (mRequestSuccess) {
                mRequestSuccess = false;
            }
        } else {
            if (mLoginView.getVisibility() == View.VISIBLE) {
                mLoginView.setVisibility(View.GONE);
            }
            if (mLoginedView.getVisibility() == View.GONE) {
                mLoginedView.setVisibility(View.VISIBLE);
            }
            if (!mRequestSuccess) {
                getUserInfo();
            }
        }
    }

    private void getUserInfo() {
        showLoadingLayout(getResources().getString(R.string.loading));
        if (!NetUtils.isNetworkConnected(getActivity())) {
            dismissLoadingLayout();
            showErrorLayout(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismissErrorLayout();
                    getUserInfo();
                }
            });
            toastShort(getResources().getString(R.string.net_error));
            return;
        }
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.MY_PAGER_INFO);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("code", SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {


            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                dismissLoadingLayout();
                showErrorLayout(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissErrorLayout();
                        getUserInfo();
                    }
                });
                Log.d("mytest", "error--pp" + ex.getMessage());
                Log.d("mytest", "error--pp" + ex.getCause());
            }

            @Override
            public void onFinished() {


            }

            @Override
            public void onSuccess(String result) {
                LogUtil.d("mytest", result);
                dismissLoadingLayout();
                dismissErrorLayout();
                try {
                    JSONObject jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        toastShort("获取数据失败");
                        mRequestSuccess = false;
                        return;
                    }
                    mRequestSuccess = true;
                    JSONObject data = jsons.getJSONObject("data");
                    String info = data.getJSONObject("userinfo").toString();
                    //					String token = data.getString("token");
                    //					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                    Gson gson = new Gson();
                    UserInfoBean bean = gson.fromJson(info, UserInfoBean.class);
                    if (bean == null) {
                        toastShort("获取数据失败");
                        return;
                    }
                    infoBean = bean;
                    ArrayList<PersonalTagBean> per = infoBean.getPersonalTags();
                    ArrayList<UserPhotoBean> phos = infoBean.getUserPhotos();
                    ArrayList<InterestByCateBean> cates = infoBean.getInterestCates();

                    if (phos.size() == 0 && !isMyself) {
                        noPhotoTv.setVisibility(View.VISIBLE);
                    } else {
                        noPhotoTv.setVisibility(View.GONE);
                    }

                    setPersonalTags(per);
                    setInterestTags(cates);
                    setUserInfoView(bean);
                    setUserPhotos(phos);
                    saveUserFriend(infoBean.getUserCode(), infoBean.getNickname(), infoBean.getIconCircle());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveUserFriend(String userCode, String nickname, String iconCircle) {
        UserFriends item = new UserFriends();
        item.setUserCode(userCode);
        item.setIcon_thumbnailUrl(iconCircle);
        item.setNickname(nickname);
        userDao.insertOrReplaceUser(item);
    }

    private void setUserPhotos(ArrayList<UserPhotoBean> phos) {
        photoAdapter = new UserPagerPhotoAdapter(getActivity(), phos, imageOptions, isMyself);
        photoPager.setAdapter(photoAdapter);
    }

    private void setUserInfoView(UserInfoBean bean) {
        mTopTitle.setTitle(bean.getNickname());
        if (isMyself) {
            addPhotoImv.setVisibility(View.VISIBLE);
            otherInfoLayout.setVisibility(View.GONE);
            aboutMeArrow.setVisibility(View.VISIBLE);
            nickArrwo.setVisibility(View.VISIBLE);
            birthArrow.setVisibility(View.VISIBLE);
            starArrow.setVisibility(View.VISIBLE);
            cityArrow.setVisibility(View.VISIBLE);
            homeArrwo.setVisibility(View.VISIBLE);
            heightArrow.setVisibility(View.VISIBLE);
            identityArrow.setVisibility(View.VISIBLE);
            schoolArrow.setVisibility(View.VISIBLE);
            personalArrow.setVisibility(View.VISIBLE);
            interestArrow.setVisibility(View.VISIBLE);

        } else {
            if (bean.getDistance() > 1000) {
                locationTv.setText(Math.round(bean.getDistance() / 1000) / 10.0 + "km");
            } else {
                locationTv.setText(bean.getDistance() + "m");
            }
            if (((System.currentTimeMillis() - bean.getActivityTime()) / 1000) > (24 * 60 * 60 * 1000)) {
                timeTv.setText(((System.currentTimeMillis() - bean.getActivityTime()) / 1000) / 60 % 60 % 24 + "day");
            } else if (((System.currentTimeMillis() - bean.getActivityTime()) / 1000) > (60 * 60 * 1000)) {
                timeTv.setText(((System.currentTimeMillis() - bean.getActivityTime()) / 1000) / 60 % 60 + "h");
            } else {
                timeTv.setText(((System.currentTimeMillis() - bean.getActivityTime()) / 1000) % 60 + "min");
            }
            matchTv.setText("" + bean.getMatchScore() + "%");
            addPhotoImv.setVisibility(View.GONE);
            otherInfoLayout.setVisibility(View.VISIBLE);
            aboutMeArrow.setVisibility(View.GONE);
            nickArrwo.setVisibility(View.GONE);
            birthArrow.setVisibility(View.GONE);
            starArrow.setVisibility(View.GONE);
            cityArrow.setVisibility(View.GONE);
            homeArrwo.setVisibility(View.GONE);
            heightArrow.setVisibility(View.GONE);
            identityArrow.setVisibility(View.GONE);
            schoolArrow.setVisibility(View.GONE);
            personalArrow.setVisibility(View.GONE);
            interestArrow.setVisibility(View.GONE);
            sexArrow.setVisibility(View.GONE);
        }
        x.image().bind(userheadImv, bean.getIconCircle(), imageOptions);


//		if(isMyself){
        iconVerify.setVisibility(View.VISIBLE);
        if (bean.getIconVerify().equals("0")) {
            iconVerify.setText("待审核");
        } else if (bean.getIconVerify().equals("1")) {
            iconVerify.setText("审核中");
        } else if (bean.getIconVerify().equals("2") || bean.getIconVerify().equals("3")) {
            //	iconVerify.setText("审核通过");
            iconVerify.setVisibility(View.GONE);

        } else {
            iconVerify.setText("未通过");
        }

//		}else{
//			iconVerify.setVisibility(View.GONE);
//		}


        usernameTv.setText(bean.getNickname());
        nicknameTv.setText(bean.getNickname());
        sexTv.setText(bean.getSexStr(bean.getSex()));
        birthdayTv.setText(bean.getBirthday());
        starSignTv.setText(bean.getStar());
        cityTv.setText(cityDao.getCity(bean.getCity()).get(0).getPrivance() + "  " + cityDao.getCity(bean.getCity()).get(0).getCity());
        if (bean.getHomeTown() != null && !bean.getHomeTown().equals("")) {
            hometownTv.setText(cityDao.getCity(bean.getHomeTown()).get(0).getPrivance() + "  " + cityDao.getCity(bean.getHomeTown()).get(0).getCity());
        } else {
            hometownTv.setText("世界很大，你的家在哪儿");
            hometownTv.setTextColor(getResources().getColor(R.color.about_gray2_txt));
        }
        if (bean.getHeight() != 0 && bean.getWeight() != 0) {
            heightWeightTv.setText(bean.getHeight() + "cm  " + bean.getWeight() + "kg");
        } else {
            heightWeightTv.setText("茁壮成长中");
            heightWeightTv.setTextColor(getResources().getColor(R.color.about_gray2_txt));
        }

        if (bean.getIsStudent().equals(Constants.IS_STUDENT_FLAG)) {
            identityTv.setText("学生");
        } else {
            identityTv.setText("上班族");
        }
        if (bean.getSchool() != null && !bean.getSchool().equals("")) {
            schoolTv.setText(schoolDao.getschoolName(bean.getSchool()).get(0).getUnivsNameString());
        } else {
            if (isMyself) {
                goCompleteSchool();
            }
        }
        if (isMyself && bean.getAboutMe().equals("")) {
            userInfoTv.setText(getResources().getString(R.string.description_me));
            userInfoBigTv.setText(getResources().getString(R.string.description_me));
        } else if (!isMyself && bean.getAboutMe().equals("")) {
            userInfoTv.setText(getResources().getString(R.string.description_other));
            userInfoBigTv.setText(getResources().getString(R.string.description_other));
        } else {
            userInfoTv.setText(bean.getAboutMe());
            userInfoBigTv.setText(bean.getAboutMe());
        }
        userInfoBigTv.setVisibility(View.VISIBLE);
        userInfoTv.setVisibility(View.GONE);
        ViewTreeObserver vto = userInfoBigTv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                userInfoBigTv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Log.d("mytest", "hh" + userInfoBigTv.getMeasuredHeight());
                if (userInfoBigTv.getMeasuredHeight() > DensityUtil.dip2px(getActivity(), 50)) {
                    userOpenTv.setVisibility(View.VISIBLE);
                } else {
                    userOpenTv.setVisibility(View.GONE);
                }
                userInfoBigTv.setVisibility(View.GONE);
                userInfoTv.setVisibility(View.VISIBLE);
            }
        });
        switch (bean.getSuperMan()) {
            case 0:
                superManIv.setVisibility(View.GONE);
                break;
            case 1:
                superManIv.setImageResource(R.drawable.special_label_icon_pink);
                superManIv.setVisibility(View.VISIBLE);
                break;
            case 2:
                superManIv.setImageResource(R.drawable.special_label_icon_yellow);
                superManIv.setVisibility(View.VISIBLE);
                break;
            case 3:
                superManIv.setImageResource(R.drawable.special_label_icon_blue);
                superManIv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public void updateHeadStatus() {
        String flag = Constant.headState;
        if (null != iconVerify && !TextUtils.isEmpty(flag)) {
            if (flag.equals("0")) {
                iconVerify.setText("待审核");
            } else if (flag.equals("1")) {
                iconVerify.setText("审核中");
            } else if (flag.equals("2") || flag.equals("3")) {
                //	iconVerify.setText("审核通过");
                iconVerify.setVisibility(View.GONE);
            } else {
                iconVerify.setText("未通过");
            }
        }
    }

    private void goCompleteSchool() {
        CommonDialog.singleBtnDialog(getActivity(), "完善信息", "你还没有学校哦", "去设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent schoolIntent = new Intent(getActivity(), ChangeSchoolActivity.class);
                schoolIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(schoolIntent, UPDATE_INFO);
            }
        });
    }

    private void setInterestTags(ArrayList<InterestByCateBean> cates) {
        ArrayList<InterestTagBean> inters = new ArrayList<InterestTagBean>();
        if (cates != null && cates.size() > 0) {
            for (int i = 0; i < cates.size(); i++) {
                for (int j = 0; j < cates.get(i).getmInterestList().size(); j++) {
                    InterestTagBean tagBean = cates.get(i).getmInterestList().get(j);
                    tagBean.setTagType(cates.get(i).getTypename());
                    inters.add(tagBean);
                }
            }
        }
        infoBean.setInterestTags(inters);
        interestAdapter = new UserInterestAdapter(getActivity(), inters);
        interestTagGv.setAdapter(interestAdapter);
    }

    private void setPersonalTags(ArrayList<PersonalTagBean> per) {
        personalAdapter = new UserPagerTagAdapter(getActivity(), per);
        personalTagGv.setAdapter(personalAdapter);
    }

    private void initOnclick() {
        addPhotoImv.setOnClickListener(this);
        userInfoLinear.setOnClickListener(this);
        nicknameLinear.setOnClickListener(this);
        birthdayLinear.setOnClickListener(this);
        starSignLinear.setOnClickListener(this);
        cityLinear.setOnClickListener(this);
        hometownLinear.setOnClickListener(this);
        heightWeightLinear.setOnClickListener(this);
        identityLinear.setOnClickListener(this);
        schoolLinear.setOnClickListener(this);
        personalTagLinear.setOnClickListener(this);
        interestTagLinear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userhead_imv:
                Intent headIntent = new Intent(getActivity(), ScanUserHeadActivity.class);
                headIntent.putExtra("userhead", infoBean.getIconOrigin());
                headIntent.putExtra("isMyself", isMyself);
                startActivityForResult(headIntent, UPDATE_HEAD);
                break;
            case R.id.open_tv:
                if (isOpen) {
                    userInfoBigTv.setVisibility(View.GONE);
                    userInfoTv.setVisibility(View.VISIBLE);
                    userOpenTv.setText("展开");
                    isOpen = false;
                } else {
                    userInfoTv.setVisibility(View.GONE);
                    userInfoBigTv.setVisibility(View.VISIBLE);
                    userOpenTv.setText("收起");
                    isOpen = true;
                }
                break;
            case R.id.add_userphoto_imv:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, UPDATE_PHOTOS);
                break;
            case R.id.userinfo_linear:
                Intent aboutIntent = new Intent(getActivity(), AboutMeActivity.class);
                aboutIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(aboutIntent, UPDATE_INFO);
                break;
            case R.id.nickname_linear:
                Intent nicknameIntent = new Intent(getActivity(), ChangeNameActivity.class);
                nicknameIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(nicknameIntent, UPDATE_INFO);
                break;
            case R.id.birthday_linear:
                Intent birthIntent = new Intent(getActivity(), ChangeBrithdayActivity.class);
                birthIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(birthIntent, UPDATE_INFO);
                break;
            case R.id.starsign_linear:
                Intent starIntent = new Intent(getActivity(), ChangeConstellationActivity.class);
                starIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(starIntent, UPDATE_INFO);
                break;
            case R.id.city_linear:
                Intent cityIntent = new Intent(getActivity(), ChangeCityActivity.class);
                cityIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(cityIntent, UPDATE_INFO);
                break;
            case R.id.hometown_linear:
                Intent homeIntent = new Intent(getActivity(), ChangeHomeTwonActivity.class);
                homeIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(homeIntent, UPDATE_INFO);
                break;
            case R.id.height_weight_linear:
                Intent tallIntent = new Intent(getActivity(), ChangeHeightWeightActivity.class);
                tallIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(tallIntent, UPDATE_INFO);
                break;
            case R.id.identity_linear:
                Intent identityIntent = new Intent(getActivity(), ChangeIdentityProfessionActivity.class);
                identityIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(identityIntent, UPDATE_INFO);
                break;
            case R.id.school_linear:
                //if(infoBean.getIsStudent().equals(Constants.IS_STUDENT_FLAG)){
                Intent schoolIntent = new Intent(getActivity(), ChangeSchoolActivity.class);
                schoolIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(schoolIntent, UPDATE_INFO);
            /*}else{
                Intent companyIntent = new Intent(MyPagerActivity.this,ChangeCompanyActivity.class);
				companyIntent.putExtra("userInfoBean", infoBean);
				startActivityForResult(companyIntent, UPDATE_INFO);
			}*/
                break;
            case R.id.personal_tag_linear:
                Intent personalTagIntent = new Intent(getActivity(), PersonalityTagActivity.class);
                personalTagIntent.putExtra("personalTags", infoBean.getPersonalTags());
                personalTagIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(personalTagIntent, UPDATE_PERSONAL_TAG);
                break;
            case R.id.interest_tag_linear:
                Intent interestLableIntent = new Intent(getActivity(), InterestLabelActivity.class);
                interestLableIntent.putExtra("interestTags", infoBean.getInterestCates());
                interestLableIntent.putExtra("userInfoBean", infoBean);
                startActivityForResult(interestLableIntent, UPDATE_INTEREST_TAG);
                break;
            case R.id.back_rl:
                getActivity().finish();
                break;
            case R.id.super_man_iv:
                Intent superIntent = new Intent(getActivity(), SuperMainInfoActivity.class);
                startActivity(superIntent);
                break;
            case R.id.register_item_btn:
                Intent register = new Intent(getActivity(), RegisterThreeActivity.class);
                startActivityForResult(register, TO_REGISTER);
                break;
            case R.id.login_item_btn:
                Intent login = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(login, TO_LOGIN);
                break;
            default:
                break;
        }
    }

    /**
     * 更多
     */
    public void getMosterDialog() {
        final AlertDialog portraidlg = new AlertDialog.Builder(getActivity()).create();
        portraidlg.show();
        Window win = portraidlg.getWindow();
        win.setContentView(R.layout.item_hint_moster_dralog_mypage);

        RelativeLayout dismissLayout, jubaoLayout;

        jubaoLayout = (RelativeLayout) win.findViewById(R.id.jubao_dialog_mupage_rl);
        dismissLayout = (RelativeLayout) win.findViewById(R.id.dismiss_dialog_mypage_rl);


        dismissLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                portraidlg.dismiss();
            }
        });
        jubaoLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                portraidlg.dismiss();
                MyHintDialog.getDialog(getActivity(), "举报Ta", "嗨~确定要举报Ta吗？", "确定", new MyHintDialog.OnDialogClick() {

                    @Override
                    public void onOK() {

                        //			                Toast.makeText(getActivity(), "举报成功", Toast.LENGTH_SHORT).show();;
                        jubao(userCode);
                    }

                    @Override
                    public void onDismiss() {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });
    }

    /**
     * 举报好友
     *
     * @param userCode
     */
    public void jubao(String userCode) {
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.REPORT);
        JSONObject object = new JSONObject();
        try {
            object.put("token", SharePreferanceUtils.getInstance().
                    getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            object.put("device_code", SharePreferanceUtils.getInstance().
                    getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            object.put("user_code", userCode);
            object.put("reason", "个人主页举报");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        params.addBodyParameter("data", object.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

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
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
                LogUtil.e(TAG, arg0);

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(arg0);
                    String state = jsonObject.getString("state");
                    LogUtil.e(TAG, state);
                    if (!state.equals("200")) {

                        Toast.makeText(getActivity(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "举报成功", Toast.LENGTH_SHORT).show();

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2 = jsonObject.getJSONObject("data");
                    //					String token=jsonObject2.getString("token");
                    //					if(!token.equals("")&&token!=null){
                    //						SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, token);
                    //					}

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPDATE_INFO:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                if (data == null) {
                    return;
                }
                UserInfoBean bean = (UserInfoBean) data.getSerializableExtra("userInfoBean");
                infoBean = bean;
                setUserInfoView(bean);
                break;
            case UPDATE_PHOTOS:
                if (null == data) {
                    return;
                }
                Bitmap bm = null;
                //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
                ContentResolver resolver = getActivity().getContentResolver();
                try {
                    //获得图片的uri
                    Uri originalUri = data.getData();
                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    String filePath = saveHeadImg(bm);
                /*String[] proj = {MediaStore.Images.Media.DATA};
                //好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(originalUri, proj, null, null, null);
				//按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				//最后根据索引值获取图片路径
				String path = cursor.getString(column_index);*/
                    showLoadingLayout(getResources().getString(R.string.uploading));
                    if (!NetUtils.isNetworkConnected(getActivity())) {
                        dismissLoadingLayout();
                        toastShort(getResources().getString(R.string.net_error));
                        return;
                    }
                    //上传图片鉴权
                    getOssToken(filePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    // TODO: handle exception
                }
                break;
            case UPDATE_HEAD:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                if (null == data) {
                    return;
                }
                String headUrl = data.getStringExtra("headUrl");
                String thumUrl = data.getStringExtra("thumUrl");
                infoBean.setIconCircle(thumUrl);
                infoBean.setIconOrigin(headUrl);
                x.image().bind(userheadImv, thumUrl, imageOptions);
                break;
            case UPDATE_INTEREST_TAG:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                if (null == data) {
                    return;
                }
                ArrayList<InterestByCateBean> listIn = (ArrayList<InterestByCateBean>) data.getSerializableExtra("interestTags");
                infoBean.getInterestCates().clear();
                infoBean.getInterestCates().addAll(listIn);
                setInterestTags(listIn);
                break;
            case UPDATE_PERSONAL_TAG:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                if (null == data) {
                    return;
                }
                ArrayList<PersonalTagBean> listPa = (ArrayList<PersonalTagBean>) data.getSerializableExtra("personalTags");
                infoBean.getPersonalTags().clear();
                infoBean.getPersonalTags().addAll(listPa);
                setPersonalTags(listPa);
                break;
            case TO_LOGIN:
            case TO_REGISTER:
            case TO_SETTING:
                mRequestSuccess = false;
                switchView();
                break;
            default:
                break;
        }
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
            head.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return path;
    }

    //鉴权
    public void getOssToken(final String path) {
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.REGISTER_OSS_TOKEN);
        String token = SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, "");
        String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, "");
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", token);
            requestObject.put("device_code", deviceId);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {


            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                dismissLoadingLayout();
                LogUtil.d("mytest", "error--" + ex.getMessage());
                LogUtil.d("mytest", "error--" + ex.getCause());
            }

            @Override
            public void onFinished() {


            }

            @Override
            public void onSuccess(String arg0) {
                LogUtil.d("mytest", "userphotoTok==" + arg0);
                try {
                    JSONObject jsonObjs = new JSONObject(arg0);
                    String state = jsonObjs.getString("state");
                    if (!state.equals("200")) {
                        dismissLoadingLayout();
                        toastShort("上传照片失败");
                        return;
                    }
                    JSONObject obj = jsonObjs.getJSONObject("data");
                    accessKeyId = obj.getString("accessKeyId");
                    accessKeySecret = obj.getString("accessKeySecret");
                    securityToken = obj.getString("securityToken");
                    expiration = obj.getString("expiration");
                    //					String token = obj.getString("token");
                    //					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                    //上传到阿里云
                    asyncPutObjectFromLocalFile(path);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    // 从本地文件上传，使用非阻塞的异步接口
    public void asyncPutObjectFromLocalFile(String path) {
        String endpoint = HttpContants.A_LI_YUN;
        //OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("XWp6VLND94vZ8WNJ", "DSi9RRCv4bCmJQZOOlnEqCefW4l1eP");
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {

                return new OSSFederationToken(accessKeyId, accessKeySecret, securityToken, expiration);
            }
        };
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(getActivity(), endpoint, credentialProvider, conf);
        String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, "");
        final String fileName = "photos/" + System.currentTimeMillis() + deviceId + ".jpeg";
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest("protect-app", fileName, path);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("mytest", "UploadSuccess");
                Log.d("mytest", result.getETag());
                Log.d("mytest", result.getRequestId());
                //上传照片成功，调用修改头像接口
                uploadPhoto(fileName);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                dismissLoadingLayout();
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.d("mytest", serviceException.getErrorCode());
                    Log.d("mytest", serviceException.getRequestId());
                    Log.d("mytest", serviceException.getHostId());
                    Log.d("mytest", serviceException.getRawMessage());
                }
            }
        });
    }

    //将图片上传到后台
    protected void uploadPhoto(String fileName) {
        // 上传成功后更新界面
        String token = SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, "");
        String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, "");
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.UPLOAD_PHOTO);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", token);
            requestObject.put("device_code", deviceId);
            requestObject.put("photo", fileName);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {


            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                dismissLoadingLayout();
                LogUtil.d("mytest", "error--" + ex.getMessage());
                LogUtil.d("mytest", "error--" + ex.getCause());
            }

            @Override
            public void onFinished() {


            }

            @Override
            public void onSuccess(String result) {
                dismissLoadingLayout();
                LogUtil.d("mutest", "uploadph==" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        toastShort("上传照片失败");
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    //					String token = data.getString("token");
                    //					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                    UserPhotoBean bean = new UserPhotoBean();
                    String photoCode = data.getString("photo_code");
                    String photoOrigin = data.getString("photo_url");
                    String photoThumbnail = data.getString("photo_thumbnailUrl");
                    String photoName = data.getString("photo_name");
                    bean.setPhotoCode(photoCode);
                    bean.setPhotoName(photoName);
                    bean.setPhotoOrigin(photoOrigin);
                    bean.setPhotoThumbnail(photoThumbnail);
                    ArrayList<UserPhotoBean> list = new ArrayList<UserPhotoBean>();
                    list.addAll(infoBean.getUserPhotos());
                    list.add(bean);
                    infoBean.setUserPhotos(list);
                    setUserPhotos(list);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTabClick() {
        if (getActivity() != null) {
            switchView();
        }
    }
}
