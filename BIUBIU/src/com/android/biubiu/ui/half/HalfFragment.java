package com.android.biubiu.ui.half;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.ui.base.BaseFragment;


import java.lang.reflect.Array;
import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HalfFragment extends BaseFragment implements FragmentIndicator.OnClickListener{
    private ImageView centerImv;
    private LinearLayout rightLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragments;
    private View mRootView;
    private int currentIndex = 0;
    private View popBg;

    public HalfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_half, container, false);
        initView();
        initPopWindow();
        initPager();
        return mRootView;
    }

    private void initView() {
        centerImv = (ImageView) mRootView.findViewById(R.id.center_imv);
        rightLayout = (LinearLayout) mRootView.findViewById(R.id.right_layout);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.pager);
        popBg = mRootView.findViewById(R.id.pop_bg);

        centerImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex == 0){
                    currentIndex = 1;
                    centerImv.setImageResource(R.drawable.cp_nav_btn_public);
                }else{
                    currentIndex = 0;
                    centerImv.setImageResource(R.drawable.cp_nav_btn_half);
                }
                mViewPager.setCurrentItem(currentIndex);
            }
        });

        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void initPopWindow() {
    }
    private void initPager() {
        mViewPager.setOffscreenPageLimit(2);
        fragments = new ArrayList<Fragment>();
        Fragment halfFragment = new HalfChildFragment();
        Fragment squareFragment = new SquareFragment();

        fragments.add(halfFragment);
        fragments.add(squareFragment);

        HalfAdapter halfAdapter = new HalfAdapter(getFragmentManager());
        mViewPager.setAdapter(halfAdapter);
        mViewPager.setCurrentItem(0,true);
        currentIndex = 0;
        mViewPager.setOnPageChangeListener(new MyOnPagerChangeListener());

    }

    @Override
    public void onTabClick() {

    }

    @Override
    public void onLeaveTab() {

    }
    public class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0){
                currentIndex = 0;
                centerImv.setImageResource(R.drawable.cp_nav_btn_half);
            }else{
                currentIndex = 1;
                centerImv.setImageResource(R.drawable.cp_nav_btn_public);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    class HalfAdapter extends FragmentPagerAdapter{

        public HalfAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
