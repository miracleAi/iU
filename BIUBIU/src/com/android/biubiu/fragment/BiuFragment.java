package com.android.biubiu.fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;

import com.android.biubiu.MatchSettingActivity;
import com.android.biubiu.activity.LoginOrRegisterActivity;
import com.android.biubiu.activity.act.ActivityListActivity;
import com.android.biubiu.activity.act.WebviewActivity;
import com.android.biubiu.callback.BiuBooleanCallback;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.common.Umutils;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.component.util.Constants;
import com.android.biubiu.component.util.DensityUtil;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.component.util.HttpUtils;
import com.android.biubiu.component.util.LogUtil;
import com.android.biubiu.component.util.LoginUtils;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.component.util.UploadImgUtils;
import com.android.biubiu.component.util.Utils;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.avos.avoscloud.LogUtil.log;

import android.annotation.SuppressLint;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cc.imeetu.iu.R;

@SuppressLint("NewApi")
public class BiuFragment extends Fragment implements  FragmentIndicator.OnClickListener {
    View view;
    ImageOptions imageOptions;
    private static final int SELECT_PHOTO = 1002;
    private static final int CROUP_PHOTO = 1003;
    private static final int ACTIVITY_LIST = 1004;
    String headPath = "";
    LinearLayout loadingLayout;
    GifView loadGif;
    TextView loadTv;
    public static boolean isUploadingPhoto = false;
    private TopTitleView mTopTitle;
    private PopupWindow mAdPopup;
    private String mAdUrl, mAdName, mAdCover;
    private String mUserCode;
    //标记是否在首页
    private boolean isHome = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.biu_fragment_layout, null);
        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, true);
        init();
        if (SharePreferanceUtils.getInstance().isScanBeginGuid(getActivity(), SharePreferanceUtils.IS_SCAN_BEGINGUID, false)) {
            getAd();
        }
        //检查是否提交了channelID
        if (!SharePreferanceUtils.getInstance().getShared(getActivity(), SharePreferanceUtils.IS_COMMIT_CHANNEL, false)) {
            HttpUtils.commitChannelId(getActivity());
        }
        return view;
    }

    /**
     * 获得广告
     */
    public void getAd() {
        mUserCode = SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, "");
        RequestParams params = new RequestParams(HttpContants.ACTIVITY_GETACTIVITY);
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
                LogUtil.e("mytest", "getactivity = " + s);
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
    }


    private void init() {
        loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        loadGif = (GifView) view.findViewById(R.id.load_gif);
        loadTv = (TextView) view.findViewById(R.id.loading_tv);

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

      /*  mTopTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    ((MainActivity) getActivity()).reverse();
                }
            }
        });*/
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



    private void showShenHeDaiog(final int flag) {
        String title = "";
        String msg = "";
        String strBtn1 = "";
        String strBtn2 = "";
        title = getResources().getString(R.string.head_no_egis);
        msg = getResources().getString(R.string.head_no_egis_info1);
        strBtn1 = "取消";
        strBtn2 = "重新上传";
        CommonDialog.doubleBtnDialog(getActivity(), title, msg, strBtn1, strBtn2, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showHeadDialog();
                dialog.dismiss();
            }
        });
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



    @Override
    public void onTabClick() {
        isHome = true;
    }

    @Override
    public void onLeaveTab() {
        isHome = false;
    }

    //新的获取biu列表
    private void getBiuList(final long requestTime) {
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
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    String headFlag = data.getString("iconStatus");
                    com.android.biubiu.common.Constant.headState = headFlag;
                   /* String recSex = data.getString("s_sex");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.RECEIVE_SEX, recSex);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.EXCHANGE_FROUNT, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CROUP_PHOTO:
                headPath = Utils.getImgPath();
                uploadPhoto(headPath);
                break;
            case SELECT_PHOTO:
                if (data != null) {
                    Utils.startPhotoZoom(getActivity(),data.getData(),CROUP_PHOTO);// 裁剪图片
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
                    com.android.biubiu.common.Constant.headState = Constants.HEAD_VERIFYING+"";
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

}
