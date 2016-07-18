package com.android.biubiu.ui.half;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.android.biubiu.ui.half.Adapter.SquareAdapter;
import com.android.biubiu.ui.half.bean.SquareBean;
import com.android.biubiu.ui.half.bean.SquareData;
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
 * A simple {@link Fragment} subclass.
 */
public class SquareFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>{

    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;
    private SquareAdapter squareAdapter;
    ArrayList<SquareBean> mData = new ArrayList<>();
    public SquareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_square, container, false);
        initView();
        getData();
        return mRootview;
    }
    private void initView() {
        pulltoRefreshListview = (PullToRefreshListView) mRootview.findViewById(R.id.pull_refresh_list);
        pulltoRefreshListview.setSexFlag(getActivity(), SharePreferanceUtils.getInstance().getUserSex(getActivity(),SharePreferanceUtils.USER_SEX,0));
        pulltoRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pulltoRefreshListview.setOnRefreshListener(this);
        pulltoRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = pulltoRefreshListview.getRefreshableView();

        squareAdapter = new SquareAdapter(getActivity(),mData);
        mListview.setAdapter(squareAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    public void getData() {
        if (mData.size() == 0) {
            showLoadingLayout(getResources().getString(R.string.loading));
        }
        if (!NetUtils.isNetworkConnected(getActivity())) {
            dismissLoadingLayout();
            if (mData.size() == 0) {
                showErrorLayout(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissErrorLayout();
                        getData();
                    }
                });
            }
            toastShort(getResources().getString(R.string.net_error));
            return;
        }
        RequestParams params = new RequestParams(HttpContants.SQUARE_REQUEST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("mytest","square--"+result);
                stopLoad();
                dismissLoadingLayout();
                Data<SquareData> response = CommonUtils.parseJsonToObj(result, new TypeToken<Data<SquareData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), getActivity())) {
                    return;
                }
                SquareData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, data.getToken());
                }
                List<SquareBean> list = data.getSquare();
                if(list != null && list.size()>0){
                    mData.addAll(list);
                    squareAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                stopLoad();
                if ( mData.size() == 0) {
                    dismissLoadingLayout();
                    showErrorLayout(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dismissErrorLayout();
                            getData();
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
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

}
