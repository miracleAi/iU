package com.android.biubiu.component.indicator;

import android.support.v4.app.Fragment;

/**
 * Created by yhj-pc on 15/9/16.
 */
public class Indicator {
    private int mId;
    private int mTitle;
    private int mDefaultIcon;
    private int mSelectedIcon;
    private Fragment mFragment;
    private int mSelectorIcon;
    /**
     * 当且仅当位于当前tab页面时，点击tab，当前页面刷新，如果页面有列表，刷新并回到顶端
     */
    private int mClickTime;

    public Indicator(int id, int title, int defaultIcon, int selectedIcon, Fragment fragment) {
        mId = id;
        mTitle = title;
        mDefaultIcon = defaultIcon;
        mSelectedIcon = selectedIcon;
        mFragment = fragment;
    }

    public Indicator(int id, int defaultIcon, int selectedIcon, Fragment fragment) {
        mId = id;
        mDefaultIcon = defaultIcon;
        mSelectedIcon = selectedIcon;
        mFragment = fragment;
    }

    public interface IRefreshPageListener {
        void refreshPage();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int title) {
        mTitle = title;
    }

    public int getDefaultIcon() {
        return mDefaultIcon;
    }

    public void setDefaultIcon(int defaultIcon) {
        mDefaultIcon = defaultIcon;
    }

    public int getSelectedIcon() {
        return mSelectedIcon;
    }

    public void setSelectedIcon(int selectedIcon) {
        mSelectedIcon = selectedIcon;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    public int getSelectorIcon() {
        return mSelectorIcon;
    }

    public void setSelectorIcon(int selectorIcon) {
        mSelectorIcon = selectorIcon;
    }

    public int getClickTime() {
        return mClickTime;
    }

    public void setClickTime(int clickTime) {
        mClickTime = clickTime;
    }
}
