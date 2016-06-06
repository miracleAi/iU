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
import com.android.biubiu.bean.base.Data;
import com.android.biubiu.bean.community.CommBiuBean;
import com.android.biubiu.bean.community.CommBiuListData;
import com.android.biubiu.bean.community.CommNotify;
import com.android.biubiu.bean.community.CommNotifyData;
import com.android.biubiu.bean.community.PostDetailData;
import com.android.biubiu.bean.community.Posts;
import com.android.biubiu.bean.community.SimpleRespData;
import com.android.biubiu.common.Constant;
import com.android.biubiu.community.homepage.PostsDetailActivity;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.CommonUtils;
import com.android.biubiu.utils.HttpContants;
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

public class CommNotifyActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private TopTitleView mTopTitle;

    private int mHasNext;

    private PullToRefreshListView mPullToRefreshListview;
    private ListView mListview;
    private List<CommNotify> mData = new ArrayList<CommNotify>();
    private CommNotifyAdapter mAdapter;
    private CommNotify mNotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_notify);
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
                CommNotify item = mData.get(position - mListview.getHeaderViewsCount());
                if (item != null) {
                    Intent intent = new Intent(CommNotifyActivity.this, PostsDetailActivity.class);
                    intent.putExtra(Constant.POSTS_ID, item.getPostId());
                    intent.putExtra(Constant.FROM_COMM_NOTIFY_PAGE, true);
                    startActivityForResult(intent, 0);
                    mNotify = item;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mNotify.setIsRead(1);
        mAdapter.notifyDataSetChanged();
    }

    private void clear() {
        RequestParams params = new RequestParams(HttpContants.NOTIFY_DELETNOTIFIES);
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
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), CommNotifyActivity.this)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(CommNotifyActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
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
        mAdapter = new CommNotifyAdapter(mData, this);
        mListview.setAdapter(mAdapter);
        getNotifyList(0);
    }

    private void getNotifyList(final long time) {
        RequestParams params = new RequestParams(HttpContants.NOTIFY_GETNOTIFYLIST);
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
                stopLoad();
                Data<CommNotifyData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<CommNotifyData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), CommNotifyActivity.this)) {
                    return;
                }
                CommNotifyData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(CommNotifyActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                mHasNext = data.getHasNext();
                if (time == 0) {
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                }
                List<CommNotify> list = data.getNotifies();
                if (list != null && list.size() > 0) {
                    mData.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CommNotifyActivity.this, getResources().getString(R.string.comm_notify_null), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                stopLoad();
            }

            @Override
            public void onCancelled(CancelledException e) {
                stopLoad();
            }

            @Override
            public void onFinished() {
                stopLoad();
            }
        });
    }

    private void stopLoad() {
        if (mPullToRefreshListview.isRefreshing()) {
            mPullToRefreshListview.onRefreshComplete();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getNotifyList(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHasNext == 0) {
            if (mData.size() > 0) {
                Toast.makeText(this, getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            }
            stopLoad();
        } else {
            getNotifyList(mData.get(mData.size() - 1).getCreateAt());
        }
    }
}