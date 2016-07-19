package com.android.biubiu.ui.half;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.ui.base.BaseFragment;
import com.android.biubiu.ui.half.Adapter.SquareAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SquareFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>{

    private PullToRefreshListView pulltoRefreshListview;
    private ListView mListview;
    private SquareAdapter squareAdapter;
    public SquareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_square, container, false);
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

        squareAdapter = new SquareAdapter(getActivity());
        mListview.setAdapter(squareAdapter);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
