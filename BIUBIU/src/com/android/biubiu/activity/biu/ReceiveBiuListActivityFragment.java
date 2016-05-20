package com.android.biubiu.activity.biu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.bean.BiuBean;
import com.android.biubiu.bean.HistoryBiuBean;
import com.android.biubiu.bean.UserFriends;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReceiveBiuListActivityFragment extends BaseFragment {
    private TopTitleView mTopTitle;
    private ListView mListview;
    private List<UserFriends> mData = new ArrayList<UserFriends>();
    private long mBiuTime, mBiuEndTime;
    private AlertDialog mValidDialog, mInvalidDialog;
    private ReceiveBiuListAdapter mAdapter;
    public ReceiveBiuListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_receive_biu_list, container, false);
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mTopTitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        mListview = (ListView) mRootview.findViewById(R.id.pull_refresh_list);
        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() >= mBiuEndTime) {
                    showInvalidDialog();
                } else {//90s内
                    showValidDialog();
                }
            }
        });

        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void showValidDialog() {
        if (mValidDialog == null) {
            mValidDialog = CommonDialog.singleBtnDialog(getActivity(), getResources().getString(R.string.end_biu_title),
                    getResources().getString(R.string.biu_valid), getResources().getString(R.string.biu_valid_btn),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        mValidDialog.show();
    }

    private void showInvalidDialog() {
        if (mInvalidDialog == null) {
            mInvalidDialog = CommonDialog.doubleBtnDialog(getActivity(), getResources().getString(R.string.end_biu_title),
                    getResources().getString(R.string.biu_invalid),
                    getResources().getString(R.string.cancel),
                    getResources().getString(R.string.sure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            endBiu();
                        }
                    });
        }
        mInvalidDialog.show();
    }

    private void endBiu() {
        RequestParams params = new RequestParams(HttpContants.APP_BIU_ENDBIU);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
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
            public void onError(Throwable arg0, boolean arg1) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                LogUtil.d("mytest", "endbiu--" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (state.equals("303")) {
                        Toast.makeText(getActivity(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_NAME, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_HEAD, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_CODE, "");
                        com.android.biubiu.utils.LogUtil.d("mytest", "tok---" + SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
//                        exitHuanxin();
                        return;
                    }
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    String token = data.getString("token");
                    if (!TextUtils.isEmpty(token)) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, token);
                    }
                    String message = data.getString("message");
                    if ("0".equals(message)) {
                        showValidDialog();
                        return;
                    } else if ("1".equals(message)) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(),SharePreferanceUtils.IS_BIU_END,true);
                        Toast.makeText(getActivity(), getResources().getString(R.string.end_biu_suc), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        mAdapter = new ReceiveBiuListAdapter(mData,getActivity());
        mListview.setAdapter(mAdapter);
        getGrabList();
        String sendBiuTime = SharePreferanceUtils.getInstance().getShared(getActivity(), SharePreferanceUtils.SEND_BIU_TIME, "");
        if(!TextUtils.isEmpty(sendBiuTime)){
            mBiuTime = Long.parseLong(sendBiuTime);
            mBiuEndTime = mBiuTime + 90 * 1000;
        }
    }

    private void getGrabList() {
        RequestParams params = new RequestParams(HttpContants.APP_BIU_GETGRABBIULIST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
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
            public void onError(Throwable arg0, boolean arg1) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                LogUtil.d("mytest", "getGrabList--" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (state.equals("303")) {
                        Toast.makeText(getActivity(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_NAME, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_HEAD, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_CODE, "");
                        com.android.biubiu.utils.LogUtil.d("mytest", "tok---" + SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
//                        exitHuanxin();
                        return;
                    }
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    String token = data.getString("token");
                    if (!TextUtils.isEmpty(token)) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, token);
                    }
                    String message = data.getString("message");
                    if ("0".equals(message)) {
                        SharePreferanceUtils.getInstance().putShared(getActivity(),SharePreferanceUtils.IS_BIU_END,true);
                        Toast.makeText(getActivity(), getResources().getString(R.string.biu_end), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray userArray = data.getJSONArray("users");
                    Gson gson = new Gson();
                    ArrayList<UserFriends> list = gson.fromJson(userArray.toString(), new TypeToken<List<UserFriends>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        mData.addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
