package com.android.biubiu.ui.half;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.component.util.NetUtils;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.ui.base.BaseFragment;
import com.android.biubiu.ui.half.Adapter.HalfChildAdapter;
import com.android.biubiu.ui.half.bean.HalfData;
import com.android.biubiu.ui.half.bean.HalfUserBean;
import com.android.biubiu.ui.mine.MyPagerActivity;
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

public class HalfChildFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>{
    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;
    private HalfChildAdapter halfChildAdapter;
    private ArrayList<HalfUserBean> mData = new ArrayList<>();
    private int mHasNext;
    long requestTime;
    public HalfChildFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_half_child, container, false);
        initView();
        return mRootView;

    }

    private void initView() {
        pulltoRefreshListview = (PullToRefreshListView) mRootView.findViewById(R.id.pull_refresh_list);
        pulltoRefreshListview.setSexFlag(getActivity(), SharePreferanceUtils.getInstance().getUserSex(getActivity(),SharePreferanceUtils.USER_SEX,0));
        pulltoRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        pulltoRefreshListview.setOnRefreshListener(this);
        pulltoRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = pulltoRefreshListview.getRefreshableView();

        halfChildAdapter = new HalfChildAdapter(getActivity(),mData);
        mListview.setAdapter(halfChildAdapter);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MyPagerActivity.class);
                intent.putExtra("userCode",mData.get(position).getUser_code());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getDatas(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getDatas(requestTime);
    }

    public void getDatas(final long time) {
        if (time == 0 && mData.size() == 0) {
            showLoadingLayout(getResources().getString(R.string.loading));
        }
        if (!NetUtils.isNetworkConnected(getActivity())) {
            dismissLoadingLayout();
            if (time == 0 && mData.size() == 0) {
                showErrorLayout(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissErrorLayout();
                        getDatas(time);
                    }
                });
            }
            toastShort(getResources().getString(R.string.net_error));
            return;
        }
        RequestParams params = new RequestParams(HttpContants.HALF_USERS);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("mytest","half--"+result);
                stopLoad();
                dismissLoadingLayout();
                Data<HalfData> response = CommonUtils.parseJsonToObj(result, new TypeToken<Data<HalfData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), getActivity())) {
                    return;
                }
                HalfData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, data.getToken());
                }
                mHasNext = data.getHas_next();
                List<HalfUserBean> list = data.getUserList();
                if(time == 0){
                    //实例化banner
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                    if(halfChildAdapter == null){
                        halfChildAdapter = new HalfChildAdapter(getActivity(),mData);
                    }
                }
                if(list != null && list.size()>0){
                    mData.addAll(list);
                    halfChildAdapter.notifyDataSetChanged();
                    requestTime = mData.get(mData.size()-1).getActytime();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                stopLoad();
                if (time == 0 && mData.size() == 0) {
                    dismissLoadingLayout();
                    showErrorLayout(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dismissErrorLayout();
                            getDatas(time);
                        }
                    });
                } else {
                    toastShort(getResources().getString(R.string.pull_up_failed));
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private void stopLoad() {
        if (pulltoRefreshListview.isRefreshing()) {
            pulltoRefreshListview.onRefreshComplete();
        }
    }
}
