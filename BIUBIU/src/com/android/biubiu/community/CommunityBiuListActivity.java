package com.android.biubiu.community;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.BaseActivity;
import com.android.biubiu.activity.biu.MyPagerActivity;
import com.android.biubiu.bean.UserFriends;
import com.android.biubiu.bean.base.Data;
import com.android.biubiu.bean.community.CommBiuBean;
import com.android.biubiu.bean.community.CommBiuListData;
import com.android.biubiu.bean.community.PostDetailData;
import com.android.biubiu.bean.community.SimpleRespData;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.NetUtils;
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

public class CommunityBiuListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private TopTitleView mTopTitle;

    private int mHasNext;

    private PullToRefreshListView mPullToRefreshListview;
    private ListView mListview;
    private List<CommBiuBean> mData = new ArrayList<CommBiuBean>();
    private CommBiuListAdapter mAdapter;
    private long mNextStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_biu_list);
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
        mPullToRefreshListview.setSexFlag(CommunityBiuListActivity.this,SharePreferanceUtils.getInstance().getUserSex(CommunityBiuListActivity.this,SharePreferanceUtils.USER_SEX,0));
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(this);
        mPullToRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = mPullToRefreshListview.getRefreshableView();
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommBiuBean item = mData.get(position - mListview.getHeaderViewsCount());
                if (item != null) {
                    Intent intent = new Intent(CommunityBiuListActivity.this, MyPagerActivity.class);
                    intent.putExtra("userCode", String.valueOf(item.getUserCode()));
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
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), CommunityBiuListActivity.this)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(CommunityBiuListActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
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
        mAdapter = new CommBiuListAdapter(mData, this);
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
                Data<CommBiuListData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<CommBiuListData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), CommunityBiuListActivity.this)) {
                    return;
                }
                CommBiuListData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(CommunityBiuListActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
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
                        showDataEmpty(null, getResources().getString(R.string.biu_me_null));
                    }
                }

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
            mNextStart = mData.get(mData.size() - 1).getCreateAt();
            getBiuList(mData.get(mData.size() - 1).getCreateAt());
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
