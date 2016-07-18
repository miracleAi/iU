package com.android.biubiu.ui.mine.child;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.component.util.NetUtils;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.ui.base.BaseActivity;
import com.android.biubiu.ui.half.Adapter.HalfChildAdapter;
import com.android.biubiu.ui.half.bean.HalfData;
import com.android.biubiu.ui.half.bean.HalfUserBean;
import com.android.biubiu.ui.mine.MyPagerActivity;
import com.android.biubiu.ui.mine.bean.MyCareData;
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

public class MyCareActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>{
    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;
    private ArrayList<HalfUserBean> mData = new ArrayList<>();
    private HalfChildAdapter halfChildAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_care);
        initView();
        getData();
    }
    private void initView() {
        pulltoRefreshListview = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pulltoRefreshListview.setSexFlag(MyCareActivity.this, SharePreferanceUtils.getInstance().getUserSex(MyCareActivity.this,SharePreferanceUtils.USER_SEX,0));
        pulltoRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pulltoRefreshListview.setOnRefreshListener(this);
        pulltoRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = pulltoRefreshListview.getRefreshableView();

        halfChildAdapter = new HalfChildAdapter(MyCareActivity.this,mData);
        mListview.setAdapter(halfChildAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyCareActivity.this, MyPagerActivity.class);
                intent.putExtra("userCode",mData.get(position).getUser_code());
                startActivity(intent);
            }
        });
    }
    public void getData() {
        if (mData.size() == 0) {
            showLoadingLayout(getResources().getString(R.string.loading));
        }
        if (!NetUtils.isNetworkConnected(MyCareActivity.this)) {
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
        RequestParams params = new RequestParams(HttpContants.HALF_USERS);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(MyCareActivity.this, SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("mytest","care--"+result);
                stopLoad();
                dismissLoadingLayout();
                Data<MyCareData> response = CommonUtils.parseJsonToObj(result, new TypeToken<Data<MyCareData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), MyCareActivity.this)) {
                    return;
                }
                MyCareData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(MyCareActivity.this, SharePreferanceUtils.TOKEN, data.getToken());
                }
                List<HalfUserBean> list = data.getUsers();
                if(list != null && list.size()>0){
                    mData.addAll(list);
                    halfChildAdapter.notifyDataSetChanged();
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
