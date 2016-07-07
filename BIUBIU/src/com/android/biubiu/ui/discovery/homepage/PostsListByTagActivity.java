package com.android.biubiu.ui.discovery.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.ui.overall.BaseActivity;
import com.android.biubiu.bean.TagBean;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.transport.http.response.community.Posts;
import com.android.biubiu.transport.http.response.community.PostsListTagData;
import com.android.biubiu.common.Constant;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.component.util.SharePreferanceUtils;
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

public class PostsListByTagActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener, PostsAdapter.IRefreshUi {
    private TopTitleView mTopTitle;
    private PullToRefreshListView mPTRLV;
    private ListView mListview;
    private PostsAdapter mAdapter;

    private List<Posts> mData = new ArrayList<Posts>();
    private TagBean mTag;
    private int mHasNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list_by_tag);
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
        mPTRLV = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPTRLV.setMode(PullToRefreshBase.Mode.BOTH);
        mPTRLV.setOnRefreshListener(this);
        mPTRLV.setScrollingWhileRefreshingEnabled(true);
        mListview = mPTRLV.getRefreshableView();
        mListview.setOnItemClickListener(this);
    }

    private void initData() {
        mAdapter = new PostsAdapter(mData, this);
        mAdapter.setIRefreshUi(this);
        mListview.setAdapter(mAdapter);
        mAdapter.setIsTagPostListPage(true);
        mTag = (TagBean) getIntent().getSerializableExtra(Constant.TAG);
        if (mTag != null) {
            mTopTitle.setTitle(mTag.getContent());
        }
        getPostsList(0l);
    }

    private void getPostsList(final long time) {
        RequestParams params = new RequestParams(HttpContants.POST_GETPOSTLISTBYTAG);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(this, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(this, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("tagId", mTag.getId());
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
                Data<PostsListTagData> response = CommonUtils.parseJsonToObj(result, new TypeToken<Data<PostsListTagData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), PostsListByTagActivity.this)) {
                    return;
                }
                PostsListTagData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(PostsListByTagActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                mHasNext = data.getHasNext();
                List<Posts> postsList = data.getPostList();
                if (time == 0) {//第一次请求或下拉刷新
                    if (mData.size() > 0) {
                        mData.clear();
                    }
                }
                if (postsList != null && postsList.size() > 0) {
                    mData.addAll(postsList);
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (time == 0) {
                        Toast.makeText(PostsListByTagActivity.this, getResources().getString(R.string.posts_is_null), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void stopLoad() {
        if (mPTRLV.isRefreshing()) {
            mPTRLV.onRefreshComplete();
        }
    }

    @Override
    public void whenDelete(Posts posts) {
        if (mData.contains(posts)) {
            mData.remove(posts);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getPostsList(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mHasNext == 0) {
            if (mData.size() > 0) {
                Toast.makeText(this, getResources().getString(R.string.data_end), Toast.LENGTH_SHORT).show();
            }
        }
        getPostsList(mData.get(mData.size() - 1).getCreateAt());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Posts posts = mData.get(position - mListview.getHeaderViewsCount());
        if (posts != null) {
            Intent i = new Intent(this, PostsDetailActivity.class);
            i.putExtra(Constant.POSTS, posts);
            i.putExtra(Constant.FROM_POSTLIST_BY_TAG, true);
            startActivity(i);
        }
    }
}
