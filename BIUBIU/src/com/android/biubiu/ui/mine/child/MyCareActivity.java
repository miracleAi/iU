package com.android.biubiu.ui.mine.child;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.ui.base.BaseActivity;
import com.android.biubiu.ui.half.Adapter.HalfChildAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import cc.imeetu.iu.R;

public class MyCareActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>{
    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;
    private HalfChildAdapter halfChildAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_care);
        initView();
    }
    private void initView() {
        pulltoRefreshListview = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pulltoRefreshListview.setSexFlag(MyCareActivity.this, SharePreferanceUtils.getInstance().getUserSex(MyCareActivity.this,SharePreferanceUtils.USER_SEX,0));
        pulltoRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        pulltoRefreshListview.setOnRefreshListener(this);
        pulltoRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = pulltoRefreshListview.getRefreshableView();

        //halfChildAdapter = new HalfChildAdapter(MyCareActivity.this);
        mListview.setAdapter(halfChildAdapter);
    }
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
