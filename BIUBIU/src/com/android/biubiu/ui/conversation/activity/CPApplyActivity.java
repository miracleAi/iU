package com.android.biubiu.ui.conversation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.component.util.NetUtils;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.transport.http.response.community.SimpleRespData;
import com.android.biubiu.transport.http.response.conversation.CPApply;
import com.android.biubiu.ui.conversation.adapter.CPApplyAdapter;
import com.android.biubiu.ui.mine.MyPagerActivity;
import com.android.biubiu.ui.overall.BaseActivity;
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

public class CPApplyActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private TopTitleView mTopTitle;

    private int mHasNext;

    private PullToRefreshListView mPullToRefreshListview;
    private ListView mListview;
    private List<CPApply> mData = new ArrayList<CPApply>();
    private CPApplyAdapter mAdapter;
    private long mNextStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpapply);
        initView();
        initData();
    }

    private void initView() {
        mTopTitle = (TopTitleView) findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.size() > 0) {
                    clear();
                }
            }
        });
        mPullToRefreshListview = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(this);
        mPullToRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = mPullToRefreshListview.getRefreshableView();
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CPApply item = mData.get(position - mListview.getHeaderViewsCount());
                if (item != null) {
                    Intent intent = new Intent(CPApplyActivity.this, MyPagerActivity.class);
//                    intent.putExtra("userCode", String.valueOf(item.getUserCode()));
                    startActivity(intent);
                }
            }
        });
    }

    private void clear() {
        RequestParams params = new RequestParams(HttpContants.COMBIU_DELETECOMBIU);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), CPApplyActivity.this)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(CPApplyActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                mData.clear();
                mAdapter.notifyDataSetChanged();
                finish();
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

    private void initData() {
        mAdapter = new CPApplyAdapter(this,R.layout.item_chat_user_list,mData);
        mListview.setAdapter(mAdapter);
        getBiuList(0);
    }

    private void getBiuList(final long time) {
        if (time == 0 && mData.size() == 0) {
            showLoadingLayout(getResources().getString(R.string.loading));
        }
        if (!NetUtils.isNetworkConnected(this)) {
            dismissLoadingLayout();
            if (time == 0 && mData.size() == 0) {
                showErrorLayout(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissErrorLayout();
                        getBiuList(mNextStart);
                    }
                });
            }
            toastShort(getResources().getString(R.string.net_error));
            return;
        }
        RequestParams params = new RequestParams(HttpContants.COMBIU_GETCOMBIULIST);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                dismissLoadingLayout();
                mPullToRefreshListview.onRefreshComplete();
                /*Data<CommBiuListData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<CommBiuListData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), CPApplyActivity.this)) {
                    return;
                }
                CommBiuListData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(CPApplyActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                mHasNext = data.getHasNext();
                if (time == 0) {
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                }
                List<CommBiuBean> list = data.getBiuList();
                if (list != null && list.size() > 0) {
                    mData.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (time == 0) {
                        showDataEmpty(null, getResources().getString(R.string.cp_apply_null));
                    }
                }*/

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                dismissLoadingLayout();
                mPullToRefreshListview.onRefreshComplete();
                if (time == 0 && mData.size() == 0) {
                    dismissLoadingLayout();
                    showErrorLayout(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dismissErrorLayout();
                            getBiuList(mNextStart);
                        }
                    });
                } else {
                    toastShort(getResources().getString(R.string.pull_up_failed));
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                dismissLoadingLayout();
                mPullToRefreshListview.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                dismissLoadingLayout();
                mPullToRefreshListview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getBiuList(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHasNext == 0) {
            if (mData.size() > 0) {
                Toast.makeText(this, getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            }
        } /*else {
            getBiuList(mData.get(mData.size() - 1).getCreateAt());
        }*/
        if (mData.size() > 0) {
//            mNextStart = mData.get(mData.size() - 1).getCreateAt();
//            getBiuList(mData.get(mData.size() - 1).getCreateAt());
        } else {
            getBiuList(0);
        }
    }

    private void stopLoad() {
        if (mPullToRefreshListview.isRefreshing()) {
            mPullToRefreshListview.onRefreshComplete();
        }
    }
}