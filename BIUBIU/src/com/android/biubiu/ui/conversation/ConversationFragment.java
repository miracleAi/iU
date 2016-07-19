package com.android.biubiu.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.biubiu.ui.conversation.activity.CPApplyActivity;
import com.android.biubiu.ui.conversation.activity.CPSettingActivity;
import com.android.biubiu.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/7/5.
 */
public class ConversationFragment extends BaseFragment implements View.OnClickListener {
    private final int TO_APPLY_PAGE = 100;

    private RelativeLayout mCPApplyLayout;
    private LinearLayout mCPSettingLayout;
    private ViewPager mPager;
    private List<Fragment> mFragments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.conversation_fragment, container, false);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mCPApplyLayout = (RelativeLayout) mRootView.findViewById(R.id.apply_layout);
        mCPSettingLayout = (LinearLayout) mRootView.findViewById(R.id.cp_setting_layout);
        mCPApplyLayout.setOnClickListener(this);
        mCPSettingLayout.setOnClickListener(this);
        mRootView.findViewById(R.id.cp_button).setOnClickListener(this);
        mRootView.findViewById(R.id.chat_button).setOnClickListener(this);
        mPager = (ViewPager) mRootView.findViewById(R.id.body_viewpager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData() {
        mFragments.add(new CoupleFragment());
        mFragments.add(new CommonChatFragment());
        mPager.setAdapter(new MessageAdapter(getFragmentManager()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_layout:
                Intent i = new Intent(getActivity(), CPApplyActivity.class);
                startActivityForResult(i, TO_APPLY_PAGE);
                break;
            case R.id.cp_setting_layout:
                Intent setting = new Intent(getActivity(), CPSettingActivity.class);
                startActivity(setting);
                break;
            case R.id.cp_button:
                mPager.setCurrentItem(0);
                break;
            case R.id.chat_button:
                mPager.setCurrentItem(1);
                break;
        }
    }

    class MessageAdapter extends FragmentPagerAdapter {

        public MessageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
