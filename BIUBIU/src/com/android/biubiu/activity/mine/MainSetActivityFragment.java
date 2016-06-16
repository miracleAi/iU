package com.android.biubiu.activity.mine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.biubiu.AboutOurActivity;
import com.android.biubiu.activity.LoginOrRegisterActivity;
import com.android.biubiu.bean.PersonalTagBean;
import com.android.biubiu.bean.SettingBean;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.myapplication.BiubiuApplication;
import com.android.biubiu.transport.xg.utils.XGUtils;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.NetUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.avos.avoscloud.LogUtil;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import cc.imeetu.iu.R;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainSetActivityFragment extends Fragment implements View.OnClickListener {
    View rootView;
    private TopTitleView mTopTitle;
    private boolean isRecvMsg = true;
    private boolean isOpenVoice = true;
    private boolean isOpenShck = true;
    private ImageView newMsgToggle;
    private ImageView voiceToggle;
    private ImageView shockToggle;
    private RelativeLayout msgLayout;
    private RelativeLayout voiceLayout;
    private RelativeLayout shockLayout;
    SettingBean setBean;
    private RelativeLayout logoutRl;
    private AlertDialog mExitDialog;

    public MainSetActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_set, container, false);
        initView();
        initlodo();
        return rootView;
    }

    private void initView() {
        newMsgToggle = (ImageView) rootView.findViewById(R.id.newmsg_toggle);
        voiceToggle = (ImageView) rootView.findViewById(R.id.voice_toggle);
        shockToggle = (ImageView) rootView.findViewById(R.id.shock_toggle);
        msgLayout = (RelativeLayout) rootView.findViewById(R.id.msg_layout);
        msgLayout.setOnClickListener(this);
        voiceLayout = (RelativeLayout) rootView.findViewById(R.id.voice_layout);
        voiceLayout.setOnClickListener(this);
        shockLayout = (RelativeLayout) rootView.findViewById(R.id.shock_layout);
        shockLayout.setOnClickListener(this);
        mTopTitle = (TopTitleView) rootView.findViewById(R.id.top_title_view);
        logoutRl = (RelativeLayout) rootView.findViewById(R.id.logout_rl);
        logoutRl.setOnClickListener(this);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetInfo();
            }
        });
        rootView.findViewById(R.id.about_iu_layout).setOnClickListener(this);
        rootView.findViewById(R.id.share_iu_layout).setOnClickListener(this);
    }

    private void setToggle() {
        //声音 0--关闭 1--打开
        if (setBean.getSound() == 0) {
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false);
            LogUtil.log.d("SharePreferanceUtils" + SharePreferanceUtils.getInstance().isOpenVoice(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, true));
            isOpenVoice = false;
            voiceToggle.setImageResource(R.drawable.setting_btn_yes);
        } else {
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, true);
            LogUtil.log.d("SharePreferanceUtils" + SharePreferanceUtils.getInstance().isOpenVoice(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false));
            isOpenVoice = true;
            voiceToggle.setImageResource(R.drawable.setting_btn_no);
        }
        //振动 0--关闭 1--打开
        if (setBean.getVibration() == 0) {
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, false);
            isOpenShck = false;
            shockToggle.setImageResource(R.drawable.setting_btn_yes);
        } else {
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, true);
            isOpenShck = true;
            shockToggle.setImageResource(R.drawable.setting_btn_no);
        }
        //接收消息 0--关闭，不接收  1--打开，接收
        if (setBean.getMessage() == 0) {
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, false);
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false);
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, false);
            isRecvMsg = false;
            newMsgToggle.setImageResource(R.drawable.setting_btn_yes);
            isOpenVoice = false;
            voiceToggle.setImageResource(R.drawable.setting_btn_yes);
            isOpenShck = false;
            shockToggle.setImageResource(R.drawable.setting_btn_yes);
        } else {
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, true);
            isRecvMsg = true;
            newMsgToggle.setImageResource(R.drawable.setting_btn_no);
        }
    }

    /**
     * 加载数据
     */
    public void initlodo() {
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.GET_SETTING);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity().getApplicationContext(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            params.addBodyParameter("data", requestObject.toString());
            x.http().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onCancelled(CancelledException arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(x.app(), arg0.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinished() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onSuccess(String arg0) {
                    LogUtil.log.d("mytest", "set--" + arg0);
                    JSONObject jsons;
                    try {
                        jsons = new JSONObject(arg0);
                        String code = jsons.getString("state");
                        if (!code.equals("200")) {
                            if (code.equals("303")) {
                                Toast.makeText(x.app(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                                exitHuanxin();
                                return;
                            }
                            return;
                        }
                        JSONObject data = jsons.getJSONObject("data");
//					String token = data.getString("token");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                        Gson gson = new Gson();
                        SettingBean settingBean = gson.fromJson(data.toString(), SettingBean.class);
                        if (settingBean == null) {
                            return;
                        }
                        setBean = settingBean;
                        setToggle();
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存设置信息
     */
    public void saveSetInfo() {
        if (setBean == null) {
            getActivity().finish();
            return;
        }
        if (!NetUtils.isNetworkConnected(getActivity())) {
            getActivity().finish();
            return;
        }
        updateSetBean();
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.UPDATE_SETTING);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("sound", setBean.getSound());
            requestObject.put("message", setBean.getMessage());
            requestObject.put("vibration", setBean.getVibration());
            String paramStr = "message,sound,vibration";
            requestObject.put("parameters", paramStr);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(x.app(), "保存失败", Toast.LENGTH_SHORT).show();
                com.android.biubiu.utils.LogUtil.d("mytest", ex.getMessage() + "");
                com.android.biubiu.utils.LogUtil.d("mytest", ex.getCause() + "");
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
                com.android.biubiu.utils.LogUtil.d("mytest", "updateset--" + arg0);
                try {
                    JSONObject jsons = new JSONObject(arg0);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        Toast.makeText(x.app(), "保存失败", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (setBean.getSound() == 0) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false);

                    } else {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, true);

                    }
                    //振动 0--关闭 1--打开
                    if (setBean.getVibration() == 0) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, false);

                    } else {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, true);
                    }
                    //新消息通知 0--关闭 1--打开
                    if (setBean.getMessage() == 0) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, false);

                    } else {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, true);
                    }
//					String token = (jsons.getJSONObject("data").getString("token"));
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        getActivity().finish();
    }

    private void updateSetBean() {
        if (isOpenVoice) {
            setBean.setSound(1);
        } else {
            setBean.setSound(0);
        }
        if (isRecvMsg) {
            setBean.setMessage(1);
        } else {
            setBean.setMessage(0);
        }
        if (isOpenShck) {
            setBean.setVibration(1);
        } else {
            setBean.setVibration(0);
        }
    }

    /**
     * 退出登录
     */
    private void exitApp() {
        if (!NetUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams("" + HttpContants.HTTP_ADDRESS + HttpContants.EXIT);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("user_code", SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, ""));
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(x.app(), arg0.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String arg0) {
                PushManager.stopWork(getActivity());
                JSONObject jsons;
                try {
                    jsons = new JSONObject(arg0);
                    String code = jsons.getString("state");
                    com.android.biubiu.utils.LogUtil.d("set", "" + code);
                    if (!code.equals("200")) {
                        String error = jsons.getString("error");
                        Toast.makeText(x.app(), error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_NAME, "");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_HEAD, "");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_CODE, "");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
                    exitHuanxin();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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
                com.android.biubiu.utils.LogUtil.e("set", "环信退出成功");
                //清空本地token
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, "");
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.HX_USER_NAME, "");
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.HX_USER_PASSWORD, "");
                XGUtils.getInstance(getActivity()).unRegisterPush();
                getActivity().setResult(Constant.EXIT_APP_SUCCESS);
                getActivity().finish();
                /*Intent intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
                intent.putExtra("tag",true);
                startActivity(intent);
                ((BiubiuApplication)getActivity().getApplication()).clearAllActivity();*/
            }

            @Override
            public void onProgress(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                com.android.biubiu.utils.LogUtil.e("set", "环信退出失败" + arg1);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_layout:
                if (isRecvMsg) {
                    isRecvMsg = false;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_yes);
                    isOpenVoice = false;
                    voiceToggle.setImageResource(R.drawable.setting_btn_yes);
                    isOpenShck = false;
                    shockToggle.setImageResource(R.drawable.setting_btn_yes);
                } else {
                    isRecvMsg = true;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_no);
                }
                break;
            case R.id.voice_layout:
                if (isOpenVoice) {
                    isOpenVoice = false;
                    voiceToggle.setImageResource(R.drawable.setting_btn_yes);
                } else {
                    isRecvMsg = true;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_no);
                    isOpenVoice = true;
                    voiceToggle.setImageResource(R.drawable.setting_btn_no);
                }
                break;
            case R.id.shock_layout:
                if (isOpenShck) {
                    isOpenShck = false;
                    shockToggle.setImageResource(R.drawable.setting_btn_yes);
                } else {
                    isRecvMsg = true;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_no);
                    isOpenShck = true;
                    shockToggle.setImageResource(R.drawable.setting_btn_no);
                }
                break;
            case R.id.logout_rl:
                showExitDialog();
                break;
            case R.id.about_iu_layout:
                Intent intent = new Intent(getActivity(), AboutOurActivity.class);
                startActivity(intent);
                break;
            case R.id.share_iu_layout:
                showShare();
                break;
        }
    }

    private void showExitDialog() {
        if (mExitDialog == null) {
            mExitDialog = CommonDialog.doubleBtnDialog(getActivity(), getResources().getString(R.string.tips),
                    getResources().getString(R.string.exit_tips),
                    getResources().getString(R.string.cancel), getResources().getString(R.string.sure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            exitApp();
                        }
                    });
        }
        mExitDialog.show();
    }

    private void showShare() {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setTitle("iU—青春恋爱成长平台");
        oks.setTitleUrl("http://www.imeetu.cc");
        oks.setText("星星发亮，是为了让每一个人都能找到属于自己的星星。而从我到你，只有一个iU的距离。");
        oks.setImageUrl("http://protect-app.oss-cn-beijing.aliyuncs.com/app-resources/img/icon/ShareIconAndroid.png");
        oks.setUrl("http://www.imeetu.cc/mobile/index.html");

        // 启动分享GUI
        oks.show(getActivity());
    }
}
