package com.android.biubiu.ui.mine.child;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.ui.base.BaseActivity;
import com.android.biubiu.transport.http.response.community.Posts;
import com.android.biubiu.callback.HttpCallback;
import com.android.biubiu.common.Constant;
import com.android.biubiu.ui.discovery.homepage.PostsAdapter;
import com.android.biubiu.ui.discovery.homepage.PostsDetailActivity;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.component.util.Constants;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.component.util.HttpRequestUtils;
import com.android.biubiu.component.util.NetUtils;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

public class UserDynamicActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {
    private TopTitleView titleView;
    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;

    private long lastTime = 0;
    private int hasNext = 0;
    ArrayList<Posts> postList = new ArrayList<Posts>();
    private PostsAdapter postAdapter;

    private static final int TO_DETAIL_PAGE = 1001;
    private String userCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dynamic);
        userCode = getIntent().getStringExtra("userCode");
        initView();
        getDynamicList(true);
    }

    private void initView() {
        titleView = (TopTitleView) findViewById(R.id.top_title_view);
        pulltoRefreshListview = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pulltoRefreshListview.setSexFlag(UserDynamicActivity.this,SharePreferanceUtils.getInstance().getUserSex(UserDynamicActivity.this,SharePreferanceUtils.USER_SEX,0));
        pulltoRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        pulltoRefreshListview.setOnRefreshListener(this);
        pulltoRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = pulltoRefreshListview.getRefreshableView();

        titleView.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postAdapter = new PostsAdapter(postList, UserDynamicActivity.this);
        mListview.setAdapter(postAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Posts posts = postList.get(position - mListview.getHeaderViewsCount());
                if (posts != null) {
                    Intent intent = new Intent(UserDynamicActivity.this, PostsDetailActivity.class);
                    intent.putExtra(Constant.POSTS, posts);
                    startActivityForResult(intent, TO_DETAIL_PAGE);
                }
            }
        });
    }

    private void getDynamicList(final boolean isRefresh) {
        if (lastTime == 0 && postList.size() == 0) {
            showLoadingLayout(getResources().getString(R.string.loading));
        }
        if (!NetUtils.isNetworkConnected(this)) {
            dismissLoadingLayout();
            if (lastTime == 0 && postList.size() == 0) {
                showErrorLayout(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismissErrorLayout();
                        getDynamicList(lastTime == 0 ? true : false);
                    }
                });
            }
            toastShort(getResources().getString(R.string.net_error));
            return;
        }
        JSONObject requestObject = new JSONObject();
        try {
            if (isRefresh) {
                requestObject.put("time", 0);
            } else {
                requestObject.put("time", lastTime);
            }
            requestObject.put("userCode", userCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestUtils.commonRequest(UserDynamicActivity.this, requestObject, HttpContants.USER_DYNAMIC, new HttpCallback() {
            @Override
            public void callback(JSONObject object, String error) {
                dismissLoadingLayout();
                pulltoRefreshListview.onRefreshComplete();
                if (object != null) {
                    try {
                        hasNext = object.getInt("hasNext");
                        lastTime = object.getLong("time");
                        JSONArray postArray = object.optJSONArray("postList");
                        Gson gson = new Gson();
                        ArrayList<Posts> list = gson.fromJson(postArray.toString(), new TypeToken<List<Posts>>() {
                        }.getType());
                        if (list != null && list.size() > 0) {
                            if (isRefresh) {
                                postList.clear();
                                postList.addAll(list);
                            } else {
                                postList.addAll(list);
                            }
                            postAdapter.notifyDataSetChanged();
                        } else {
                            if (lastTime == 0) {
                                showDataEmpty(null, getResources().getString(R.string.personal_dynamic_null));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    if (lastTime == 0 && postList.size() == 0) {
                        dismissLoadingLayout();
                        showErrorLayout(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dismissErrorLayout();
                                getDynamicList(true);
                            }
                        });
                    } else {
                        toastShort(getResources().getString(R.string.pull_up_failed));
                    }
                }
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
        getDynamicList(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (hasNext == Constants.HAS_NO_DATA) {
            Toast.makeText(UserDynamicActivity.this, "已经到底了", Toast.LENGTH_SHORT).show();
        } /*else {
            getDynamicList(false);
        }*/
        getDynamicList(false);
    }
}
