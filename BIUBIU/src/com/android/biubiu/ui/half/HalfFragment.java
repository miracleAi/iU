package com.android.biubiu.ui.half;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.biubiu.component.customview.RangeSeekBar;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.ui.base.BaseFragment;


import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HalfFragment extends BaseFragment implements FragmentIndicator.OnClickListener{
    private RelativeLayout titleLayout;
    private RelativeLayout bodyLayout;
    private ImageView centerImv;
    private LinearLayout rightLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragments;
    private View mRootView;
    private int currentIndex = 0;
    private View popBg;
    TextView ageMinTv;
    TextView ageMaxTv;
    TextView distanceMaxTv;
    LinearLayout customView;

    private AlphaAnimation mHideAnimation= null;

    private AlphaAnimation mShowAnimation= null;

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
        popBg.setVisibility(View.GONE);
        titleLayout = (RelativeLayout) mRootView.findViewById(R.id.title_layout);
        bodyLayout = (RelativeLayout) mRootView.findViewById(R.id.body_layout);

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
                if(customView != null){
                    if(customView.getVisibility() == View.VISIBLE){
                        customView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.up_out_anim));
                        customView.setVisibility(View.GONE);
                        setHideAnimation(popBg,500);
                        popBg.setVisibility(View.GONE);
                    }else{
                        //popupWindow.showAtLocation(bodyLayout,Gravity.TOP,0, (int)DensityUtil.dip2px(getActivity(),76));
                        customView.setVisibility(View.VISIBLE);
                        customView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.up_in_anim));
                        popBg.setVisibility(View.VISIBLE);
                        setShowAnimation(popBg,500);
                    }
                }
            }
        });
    }
    private void initPopWindow() {
        RangeSeekBar<Integer> seekBar;
        // // 获取自定义布局文件pop.xml的视图
        customView = (LinearLayout) mRootView.findViewById(R.id.half_pop);
        LinearLayout seekLinear = (LinearLayout) customView.findViewById(R.id.age_seek_linear);
        ageMinTv = (TextView) customView.findViewById(R.id.age_min_tv);
        ageMaxTv = (TextView) customView.findViewById(R.id.age_max_tv);
        SeekBar distanceBar = (SeekBar) customView.findViewById(R.id.distance_bar);
        distanceMaxTv = (TextView) customView.findViewById(R.id.distance_max_tv);
        seekBar = new RangeSeekBar<Integer>(16, 40, getActivity());
        ageMinTv.setText("16");
        ageMaxTv.setText("40");
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
                                                    Integer minValue, Integer maxValue) {
                ageMinTv.setText(minValue+"");
                ageMaxTv.setText(maxValue+"");
            }
        });
        seekBar.setNotifyWhileDragging(true);
        seekLinear.addView(seekBar);
        distanceBar.setProgress(10);
        distanceMaxTv.setText(10+"");
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceMaxTv.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

    /**

     * View渐隐动画效果

     *

     */

    private void setHideAnimation( View view, int duration ){

        if( null == view || duration < 0 ){

            return;

        }

        if( null != mHideAnimation ){

            mHideAnimation.cancel( );

        }

        mHideAnimation = new AlphaAnimation(1.0f, 0.0f);

        mHideAnimation.setDuration( duration );

        mHideAnimation.setFillAfter( true );

        view.startAnimation( mHideAnimation );

    }

    /**

     * View渐现动画效果

     *

     */

    private void setShowAnimation( View view, int duration ){

        if( null == view || duration < 0 ){

            return;

        }

        if( null != mShowAnimation ){

            mShowAnimation.cancel( );

        }

        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);

        mShowAnimation.setDuration( duration );

        mShowAnimation.setFillAfter( true );

        view.startAnimation( mShowAnimation );
    }
}
