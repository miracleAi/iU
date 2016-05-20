package com.android.biubiu.activity.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.MainActivity;
import com.android.biubiu.MatchSettingActivity;
import com.android.biubiu.activity.act.ActivityListActivity;
import com.android.biubiu.activity.biu.BiuBiuReceiveActivity;
import com.android.biubiu.bean.HistoryBiuBean;
import com.android.biubiu.common.Umutils;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.stagger.PullToRefreshStaggeredGridView;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

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
public class HistoryActivityFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<StaggeredGridView> {
    private TopTitleView mTopTitle;
    private StaggeredGridLayoutManager mLayoutMgr;
    private RecyclerView mRecycleView;
    private HistoryBiuAdapter mAdapter;
    private List<HistoryBiuBean> mData = new ArrayList<HistoryBiuBean>();

    private PullToRefreshStaggeredGridView mPullToRefreshStaggerdGridView;
    private StaggeredGridView mDongTaiGridView;

    private static final int ACTIVITY_LIST = 1004;
    private String mHasNext = "1";
    public HistoryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_history_, container, false);
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mTopTitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        mTopTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).reverseBack();
            }
        });
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), MatchSettingActivity.class);
                    startActivity(intent);
                }
            }
        });
        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRecycleView = (RecyclerView) mRootview.findViewById(R.id.id_recyclerview);
        mPullToRefreshStaggerdGridView = (PullToRefreshStaggeredGridView) mRootview.findViewById(R.id.pull_grid_view);
        mPullToRefreshStaggerdGridView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshStaggerdGridView.setOnRefreshListener(this);
        mPullToRefreshStaggerdGridView.setScrollingWhileRefreshingEnabled(true);
        mDongTaiGridView = mPullToRefreshStaggerdGridView.getRefreshableView();
    }

    private void initData() {
        mAdapter = new HistoryBiuAdapter(getActivity(), mData);
        mDongTaiGridView.setAdapter(mAdapter);
        mDongTaiGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryBiuBean bean = mData.get(position);
                Intent intent = new Intent(getActivity(), BiuBiuReceiveActivity.class);
                intent.putExtra("userCode", String.valueOf(bean.getId()));
                startActivity(intent);
            }
        });
        getHistoryBiu(0);
    }

    private void getHistoryBiu(final long value) {
        RequestParams params = new RequestParams(HttpContants.APP_BIU_GETTARGETBIULIST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("num", 10);
            requestObject.put("last_date", value);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                mPullToRefreshStaggerdGridView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                mPullToRefreshStaggerdGridView.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                mPullToRefreshStaggerdGridView.onRefreshComplete();
            }

            @Override
            public void onSuccess(String result) {
                mPullToRefreshStaggerdGridView.onRefreshComplete();
                LogUtil.d("mytest", "getHistory--" + result);
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
                    mHasNext = data.getString("has_next");
                    JSONArray userArray = data.getJSONArray("users");
                    Gson gson = new Gson();
                    ArrayList<HistoryBiuBean> list = gson.fromJson(userArray.toString(), new TypeToken<List<HistoryBiuBean>>() {
                    }.getType());
                    if (value == 0) {
                        if (mData.size() > 0) {
                            mData.clear();
                        }
                    }
                    if (list != null && list.size() > 0) {
//                        int index = mData.size()+1;
                        mData.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        /*if (value != 0) {
                            mDongTaiGridView.setSelection(index);
                        }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
        getHistoryBiu(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
        if("0".equals(mHasNext)){
            Toast.makeText(getActivity(),getResources().getString(R.string.data_end),Toast.LENGTH_SHORT).show();
            mPullToRefreshStaggerdGridView.onRefreshComplete();
        }else{
            getHistoryBiu(mData.get(mData.size() - 1).getTime());
        }
    }

    /**
     * 获得广告
     */
    public void getAd() {
        final String userCode = SharePreferanceUtils.getInstance().getUserCode(getActivity(), SharePreferanceUtils.USER_CODE, "");
        RequestParams params = new RequestParams(HttpContants.ACTIVITY_GETTAGS);
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
            public void onSuccess(String s) {
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
                        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent activities = new Intent(getActivity(), ActivityListActivity.class);
                                startActivityForResult(activities, ACTIVITY_LIST);
                            }
                        });
                        mTopTitle.setRightImage(R.drawable.biu_btn_activity_nor);
                        int updateAt = activity.getInt("updateAt");
                        int cacheUpdate = SharePreferanceUtils.getInstance().getUpdateAd(getActivity(), userCode);
                        if (updateAt != cacheUpdate) {
                            boolean haveView = SharePreferanceUtils.getInstance().getHaveToView(getActivity(), userCode);
                            if (haveView) {
                                SharePreferanceUtils.getInstance().saveHaveToView(getActivity(), userCode, false);
                            }
                            mTopTitle.setRightImage(R.drawable.biu_btn_activity_light);
                            SharePreferanceUtils.getInstance().saveUpdateAd(getActivity(), userCode, updateAt);
                        } else {
                            boolean haveView = SharePreferanceUtils.getInstance().getHaveToView(getActivity(), userCode);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_LIST:
                mTopTitle.setRightImage(R.drawable.biu_btn_activity_nor);
                break;
        }
    }
}
