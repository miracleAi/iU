package com.android.biubiu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.android.biubiu.activity.biu.PagerFragment;
import com.android.biubiu.chat.DemoHelper;
import com.android.biubiu.chat.LoadUserFriend;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.indicator.Indicator;
import com.android.biubiu.fragment.BiuFragment;
import com.android.biubiu.fragment.MenuLeftFragment;
import com.android.biubiu.fragment.MenuRightFragment;
import com.android.biubiu.sqlite.PushMatchDao;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LocationUtils;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.avos.avoscloud.LogUtil.log;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

import com.android.biubiu.component.indicator.FragmentIndicator.OnIndicateListener;

public class MainActivity extends FragmentActivity implements AMapLocationListener {
    //定位相关
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private String TAG = MainActivity.class.getSimpleName();
    RelativeLayout beginGuidLayout;
    ImageView guidImv;
    Button guidBtn;
    //点击后需要显示的引导页
    int guidIndex = 2;
    UpdateResponse updateInfoAll;
    PushMatchDao pushDao;
    private ReceiveBroadCast receiveBroadCast;  //广播实例

    private FragmentIndicator mIndicator;
    private FragmentManager mFragmentManager;
    private List<Indicator> mIndicators = new ArrayList<Indicator>();

    OnIndicateListener mOnIndicateListener = new OnIndicateListener() {
        @Override
        public void onIndicate(int id) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            for (Indicator i : mIndicators) {
                if (id == i.getId()) {
                    Fragment f = null;
                    if(i.getTitle()==0){
                        f = mFragmentManager.findFragmentByTag(getResources().getString(R.string.left_menu_biubiu));
                    }else{
                        f = mFragmentManager.findFragmentByTag(getResources().getString(i.getTitle()));
                    }
                    if (f != null) {
                        transaction.show(i.getFragment());
                        i.setClickTime(i.getClickTime() + 1);
                    } else {
                        transaction.add(R.id.layout_body, i.getFragment(), getResources().getString(i.getTitle()));
                    }
                } else {
                    Fragment f = null;
                    if(i.getTitle()==0){
                        f = mFragmentManager.findFragmentByTag(getResources().getString(R.string.left_menu_biubiu));
                    }else{
                        f = mFragmentManager.findFragmentByTag(getResources().getString(i.getTitle()));
                    }
                    if (f != null) {
                        transaction.hide(i.getFragment());
                        i.setClickTime(0);
                    }
                }
            }
            transaction.commit();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initData();

        log.e("Token", SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, ""));
    }

    private void initView() {
        //引导图
        beginGuidLayout = (RelativeLayout) findViewById(R.id.guid_layout);
        guidImv = (ImageView) findViewById(R.id.guid_imv);
        guidBtn = (Button) findViewById(R.id.guid_btn);

        mIndicator = (FragmentIndicator) findViewById(R.id.indicator_main);
    }

    private void initData() {
        initPush();
        initBeginGuid();
        initFragments();
        initEase();
        if (!SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "").equals("")) {
            LogUtil.e(TAG, "有token");
            LoadUserFriend.getUserFriends(this);
        }
        location();
        //更新活跃时间
        updateActivityTime();
        checkUpdate();

        // 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.FLAG_RECEIVE);    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(receiveBroadCast, filter);
    }

    private void initEase() {
        if (DemoHelper.getInstance().isLoggedIn() == false) {
            LogUtil.e(TAG, "未登录环信");
            if (!TextUtils.isEmpty(SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, ""))) {
                LogUtil.e(TAG, "有token");
                loginHuanXin(SharePreferanceUtils.getInstance().getHxUserName(getApplicationContext(), SharePreferanceUtils.HX_USER_NAME, ""),
                        SharePreferanceUtils.getInstance().getHxUserName(getApplicationContext(), SharePreferanceUtils.HX_USER_PASSWORD, ""));
            }
        } else {

        }
    }

    private void initFragments() {
        mFragmentManager = getSupportFragmentManager();

        Indicator message = new Indicator(R.id.tab_message, R.string.left_menu_message, R.drawable.main_tab_icon_mes_nor,
                R.drawable.main_tab_icon_mes_light, new MenuRightFragment());
        Indicator biu = new Indicator(R.id.tab_biu, R.drawable.main_tab_icon_biu_nor,
                R.drawable.main_tab_icon_biu_light, new BiuFragment());
        Indicator mine = new Indicator(R.id.tab_mine, R.string.mine, R.drawable.main_tab_icon_mine_nor,
                R.drawable.main_tab_icon_mine_light, new PagerFragment());

        mIndicators.add(message);
        mIndicators.add(biu);
        mIndicators.add(mine);

        for (Indicator i : mIndicators) {
            mIndicator.addChild(i);
        }

        mFragmentManager.beginTransaction()
                .add(R.id.layout_body, biu.getFragment(), getResources().getString(R.string.left_menu_biubiu))
                .commit();
        mFragmentManager.executePendingTransactions();

        mIndicator.setDefaultView(R.id.tab_biu);
        mIndicator.setIndicator(R.id.tab_biu);
        mIndicator.setOnIndicateListener(mOnIndicateListener);
        mOnIndicateListener.onIndicate(R.id.tab_biu);
    }

    /**
     * //启动百度云推送
     */
    private void initPush() {
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "v3FkYC4w53w46uuvw9L6qBF1");
        pushDao = new PushMatchDao(getApplicationContext());
        if (!com.android.biubiu.utils.NetUtils.isNetworkConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUpdate() {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                updateInfoAll = updateInfo;
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        //UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
                        showUpdateDialog();
                        break;
                    case UpdateStatus.No: // has no update
                        //Toast.makeText(MainActivity.this, "已经是最新版本啦！", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        //Toast.makeText(getApplicationContext(), "no wifi ", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // time out
                        //Toast.makeText(getApplicationContext(), "time out", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        });
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        //UmengUpdateAgent.forceUpdate(SystemSettingsActivity.this);
        UmengUpdateAgent.update(MainActivity.this);
    }

    private void showUpdateDialog() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("有新版本").setMessage("\n" + "最新版本：" + updateInfoAll.version + "\n\n更新内容：\n" + updateInfoAll.updateLog + "\n")
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
                        File file = UmengUpdateAgent.downloadedFile(MainActivity.this, updateInfoAll);
                        if (file == null) {
                            UmengUpdateAgent.startDownload(MainActivity.this, updateInfoAll);
                        } else {
                            UmengUpdateAgent.startInstall(MainActivity.this, file);
                        }
                        dialog.dismiss();
                    }
                }).setNeutralButton("以后再说", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void updateActivityTime() {
        if (!LoginUtils.isLogin(getApplicationContext())) {
            return;
        }
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.UPDATE_ACTIVITY_TIME);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("activity_time", System.currentTimeMillis());
            requestObject.put("parameters", "activity_time");
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                LogUtil.d("mytest", "acty_time" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
//					String token = data.getString("token");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initBeginGuid() {
        boolean isScanGuid = SharePreferanceUtils.getInstance().isScanBeginGuid(getApplicationContext(), SharePreferanceUtils.IS_SCAN_BEGINGUID, false);
        if (!isScanGuid) {
            beginGuidLayout.setVisibility(View.VISIBLE);
            guidImv.setImageResource(R.drawable.help_imageview_01);
            guidBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (guidIndex) {
                        case 2:
                            guidImv.setImageResource(R.drawable.help_imageview_02biubi);
                            guidIndex = guidIndex + 1;
                            break;
                        case 3:
                            guidBtn.setBackgroundResource(R.drawable.guide_begin2_btn);
                            guidImv.setImageResource(R.drawable.help_imageview_03biubi);
                            guidIndex = guidIndex + 1;
                            break;
                        case 4:
                            SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.IS_SCAN_BEGINGUID, true);
                            beginGuidLayout.setVisibility(View.GONE);
                            ((BiuFragment)mIndicators.get(1).getFragment()).getAd();
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.IS_APP_OPEN, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.IS_APP_OPEN, false);

    }

    private void location() {
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        //设置定位模式为    高精度模式Hight_Accuracy，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        if (SharePreferanceUtils.getInstance().isAppOpen(getApplicationContext(), SharePreferanceUtils.IS_APP_OPEN, true)) {
            locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        } else {
            locationOption.setLocationMode(AMapLocationMode.Battery_Saving);
        }
        // 设置定位监听
        locationClient.setLocationListener(this);
        //设置定位参数相关
        locationOption.setOnceLocation(false);
        locationOption.setInterval(Long.valueOf(1000 * 60 * 10));
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                //开始定位
                case LocationUtils.MSG_LOCATION_START:
                    break;
                // 定位完成
                case LocationUtils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = LocationUtils.getLocationStr(loc);
                    String[] ss = result.split(",");
                    if (ss.length == 2) {
                        double longitude = Double.parseDouble(ss[0]);
                        double latitide = Double.parseDouble(ss[1]);
                        updateLocation(longitude, latitide);
                        LogUtil.d("mytest", "gaode" + result);
                    }
                    break;
                //停止定位
                case LocationUtils.MSG_LOCATION_STOP:
                    break;
                default:
                    break;
            }
        }

        ;
    };

    //更新位置信息
    protected void updateLocation(double lontitide, double latitude) {
        if (!LoginUtils.isLogin(getApplicationContext())) {
            return;
        }
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.UPDATE_LACATION);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("longitude", lontitide);
            requestObject.put("dimension", latitude);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                LogUtil.d("mytest", "location--" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
//					String token = data.getString("token");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 实现环信 ConnectionListener接口
     *
     * @author lucifer
     */
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登陆
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                            Toast.makeText(getApplicationContext(), "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                        } else {
                            //当前网络不可用，请检查网络设置
                            Toast.makeText(getApplicationContext(), "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = LocationUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.IS_APP_OPEN, false);
            // 停止定位
            locationClient.stopLocation();
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        pushDao.deleteAllPush();
        unregisterReceiver(receiveBroadCast);
//		EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    /**
     * 登录环信客户端 建立长连接
     *
     * @param username
     * @param password
     */
    public void loginHuanXin(String username, String password) {
        if (password.equals("") || username.equals("")) {
            return;
        }
        EMClient.getInstance().login(username, password, new EMCallBack() {

            @Override
            public void onSuccess() {
                LogUtil.e(TAG, "登录成功环信");
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }

            @Override
            public void onProgress(int arg0, String arg1) {

            }

            @Override
            public void onError(int arg0, String arg1) {
                Log.e(TAG, "登陆环信失败！");
            }
        });

    }

    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //tab上显示未读消息红点
            mIndicator.setUnReadVisible(R.id.tab_message, true);
        }
    }

    @Override
    public void onBackPressed() {
        if (BiuFragment.isUploadingPhoto) {
            return;
        }
        SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.IS_APP_OPEN, false);
        this.moveTaskToBack(true);
    }
}