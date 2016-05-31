package com.android.biubiu.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.biubiu.BaseFragment;
import com.android.biubiu.community.CardTagActivity;
import com.android.biubiu.community.homepage.PostsFragment;
import com.android.biubiu.community.PublishHomeActivity;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.title.TopTitleView;
import com.viewpagerindicator.TabPageIndicator;

import cc.imeetu.iu.R;

/**
 * 发现页面，包括推荐、新鲜、biubiu三个界面
 */
public class DiscoveryFragment extends BaseFragment implements FragmentIndicator.OnClickListener {

    private static final int TO_PUBLISH_PAGE = 0;
    private static final int TO_NOTIFY_PAGE = TO_PUBLISH_PAGE + 1;
    private static final int TO_DETAIL_PAGE = TO_NOTIFY_PAGE + 1;
    private static final String[] CONTENT = new String[3];

    private TopTitleView mTopTitle;
    private TabPageIndicator mIndicator;
    private ViewPager mViewPager;

    public DiscoveryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_discovery, container, false);
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mTopTitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PublishHomeActivity.class);
                startActivityForResult(i, TO_PUBLISH_PAGE);
            }
        });

        mTopTitle.setCenterOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CardTagActivity.class);
                startActivityForResult(i, TO_PUBLISH_PAGE);
            }
        });
        mIndicator = (TabPageIndicator) mRootview.findViewById(R.id.indicator);
        mViewPager = (ViewPager) mRootview.findViewById(R.id.pager);
    }

    private void initData() {
        CONTENT[0] = getResources().getString(R.string.recommand);
        CONTENT[1] = getResources().getString(R.string.fresh);
        CONTENT[2] = getResources().getString(R.string.left_menu_biubiu);

        DiscoveryAdapter adapter = new DiscoveryAdapter(getFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onTabClick() {

    }

    @Override
    public void onLeaveTab() {

    }

    class DiscoveryAdapter extends FragmentPagerAdapter {
        public DiscoveryAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new PostsFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
