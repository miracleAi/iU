package com.android.biubiu.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.MainActivity;
import com.android.biubiu.common.Constant;
import com.android.biubiu.community.CardTagActivity;
import com.android.biubiu.community.CommNotifyActivity;
import com.android.biubiu.community.homepage.PostsFragment;
import com.android.biubiu.community.PublishHomeActivity;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.indicator.PagerSlidingTabStrip;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * 发现页面，包括推荐、新鲜、biubiu三个界面
 */
public class DiscoveryFragment extends BaseFragment implements FragmentIndicator.OnClickListener, PostsFragment.ITabPageIndicatorAnim {

    private static final int TO_PUBLISH_PAGE = 0;
    private static final int TO_NOTIFY_PAGE = TO_PUBLISH_PAGE + 1;
    private static final String[] CONTENT = new String[3];

    private TopTitleView mTopTitle;
    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPager;
    private List<PostsFragment> mFragments = new ArrayList<PostsFragment>();
    private int newMsgCount = 0;
    private boolean mNeedRefresh;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constant.PUBLISH_POST_ACTION)) {
                    mNeedRefresh = true;
                }else if(action.equals(Constant.NOTIFU_ACTION)){
                    int notifyCount = intent.getIntExtra("notifu_count",1);
                    updateNotify(notifyCount);
                }
            }
        }
    };

    public DiscoveryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_discovery, container, false);
        initView();
        initData();
        return mRootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!LoginUtils.isLogin(getActivity())) {
            newMsgCount = 0;
            judgeTab();
        }
        if (mNeedRefresh) {
            if (mViewPager.getCurrentItem() == 1) {
                mFragments.get(1).refreshList();
            } else {
                mViewPager.setCurrentItem(1);
                mFragments.get(1).refreshList();
            }
            mNeedRefresh = false;
        }
    }
    private void initView() {
        mTopTitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    Intent i = new Intent(getActivity(), CommNotifyActivity.class);
                    startActivityForResult(i, TO_NOTIFY_PAGE);
                }
            }
        });

        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    Intent i = new Intent(getActivity(), PublishHomeActivity.class);
                    startActivityForResult(i, TO_PUBLISH_PAGE);
                    getActivity().overridePendingTransition(R.anim.alpha_in_anim, R.anim.alpha_out_anim);
                }
            }
        });

        mTopTitle.setCenterOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginUtils.isLogin(getActivity())){
                    Intent i = new Intent(getActivity(), CardTagActivity.class);
                    i.putExtra(Constant.TO_TAG_TYPE, Constant.TAG_TYPE_SELSET);
                    startActivityForResult(i, TO_PUBLISH_PAGE);
                }
            }
        });
        mIndicator = (PagerSlidingTabStrip) mRootview.findViewById(R.id.indicator);
        mViewPager = (ViewPager) mRootview.findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
    }

    private void initData() {
        CONTENT[0] = getResources().getString(R.string.recommand);
        CONTENT[1] = getResources().getString(R.string.fresh);
        CONTENT[2] = getResources().getString(R.string.left_menu_biubiu);

        PostsFragment recommand = new PostsFragment();
        recommand.setTabPageIndicatorAnim(this);
        Bundle b = new Bundle();
        b.putInt("type", 1);
        recommand.setArguments(b);

        PostsFragment refresh = new PostsFragment();
        refresh.setTabPageIndicatorAnim(this);
        Bundle b2 = new Bundle();
        b2.putInt("type", 0);
        refresh.setArguments(b2);

        PostsFragment biubiu = new PostsFragment();
        biubiu.setTabPageIndicatorAnim(this);
        Bundle b3 = new Bundle();
        b3.putInt("type", 2);
        biubiu.setArguments(b3);

        mFragments.add(recommand);
        mFragments.add(refresh);
        mFragments.add(biubiu);

        DiscoveryAdapter adapter = new DiscoveryAdapter(getFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1, true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PUBLISH_POST_ACTION);
        filter.addAction(Constant.NOTIFU_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onTabClick() {
        for (PostsFragment fragment : mFragments) {
            fragment.refreshData();
        }
    }

    @Override
    public void onLeaveTab() {

    }

    @Override
    public void slideUp() {
        mIndicator.setVisibility(View.GONE);
    }

    @Override
    public void slideDown() {
        mIndicator.setVisibility(View.VISIBLE);
    }

    class DiscoveryAdapter extends FragmentPagerAdapter {
        public DiscoveryAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length]/*.toUpperCase()*/;
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

    public void updateNotify(int num) {
        newMsgCount = num;
        judgeTab();
        if (num > 0) {
            mTopTitle.setLeftImage(R.drawable.found_activity_light);
        } else {
            mTopTitle.setLeftImage(R.drawable.found_btn_activity_nor);
        }
    }

    public void judgeTab() {
        if (newMsgCount > 0) {
            ((MainActivity) getActivity()).setDisUnReadVisible(true);
        } else {
            ((MainActivity) getActivity()).setDisUnReadVisible(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_NOTIFY_PAGE:
                newMsgCount = 0;
                judgeTab();
                mTopTitle.setLeftImage(R.drawable.biu_btn_activity_nor);
                break;
        }
    }
}
