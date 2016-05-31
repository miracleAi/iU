package com.android.biubiu.community.homepage;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.bean.community.DiscoveryData;
import com.android.biubiu.bean.community.DiscoveryResponse;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * 帖子列表页面,分推荐、新鲜、biubiu
 */
public class PostsFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private final String TAG = PostsFragment.class.getSimpleName();
    private int mType;//0--新鲜，1--推荐，2--biubiu

    private int mHasNext;

    private PullToRefreshListView mPullToRefreshListview;
    private ListView mListview;
    private PostsAdapter mAdapter;

    private List<Posts> mData = new ArrayList<Posts>();

    public PostsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_posts, container, false);
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mPullToRefreshListview = (PullToRefreshListView) mRootview.findViewById(R.id.pull_refresh_list);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(this);
        mPullToRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = mPullToRefreshListview.getRefreshableView();
    }

    private void initData() {
        mAdapter = new PostsAdapter(mData, getActivity());
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳到详情界面
            }
        });
        getData(0);
    }

    private void getData(final long time) {
        RequestParams params = new RequestParams(HttpContants.POST_GETPOSTLISTBYTYPE);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("type", mType);
            requestObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                stopLoad();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                stopLoad();
            }

            @Override
            public void onFinished() {
                stopLoad();
            }

            @Override
            public void onSuccess(String result) {
                stopLoad();
                LogUtil.d(TAG, "getPostlist--" + result);
                DiscoveryResponse response = CommonUtils.parseJsonToObj(result, new TypeToken<DiscoveryResponse>() {
                });
                if (response.getState().equals("303")) {
                    Toast.makeText(getActivity(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, "");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_NAME, "");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_HEAD, "");
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_CODE, "");
//                  exitHuanxin();
                    return;
                }
                if (!response.getState().equals("200")) {
                    return;
                }
                DiscoveryData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, data.getToken());
                }
                mHasNext = data.getHasNext();
                List<Posts> postsList = data.getPostList();
                if (time == 0) {//第一次请求或下拉刷新
                    //实例化banner
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                }
                if (postsList != null && postsList.size() > 0) {
                    mData.addAll(postsList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void stopLoad() {
        if(mPullToRefreshListview.isRefreshing()){
            mPullToRefreshListview.onRefreshComplete();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHasNext == 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            stopLoad();
        } else {
            getData(mData.get(mData.size() - 1).getCreateAt());
        }
    }
}
