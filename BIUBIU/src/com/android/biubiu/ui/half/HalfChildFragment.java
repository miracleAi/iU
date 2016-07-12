package com.android.biubiu.ui.half;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.ui.base.BaseFragment;
import com.android.biubiu.ui.half.Adapter.HalfChildAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import cc.imeetu.iu.R;

public class HalfChildFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>{
    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;
    private HalfChildAdapter halfChildAdapter;
    public HalfChildFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_half_child, container, false);
        initView();
        return mRootview;
    }

    private void initView() {
        pulltoRefreshListview = (PullToRefreshListView) mRootview.findViewById(R.id.pull_refresh_list);
        pulltoRefreshListview.setSexFlag(getActivity(), SharePreferanceUtils.getInstance().getUserSex(getActivity(),SharePreferanceUtils.USER_SEX,0));
        pulltoRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        pulltoRefreshListview.setOnRefreshListener(this);
        pulltoRefreshListview.setScrollingWhileRefreshingEnabled(true);
        mListview = pulltoRefreshListview.getRefreshableView();

        halfChildAdapter = new HalfChildAdapter(getActivity());
        mListview.setAdapter(halfChildAdapter);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
