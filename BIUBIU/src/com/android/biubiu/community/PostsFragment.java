package com.android.biubiu.community;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.biubiu.BaseFragment;

import cc.imeetu.iu.R;

/**
 * 帖子列表页面,分推荐、新鲜、biubiu
 */
public class PostsFragment extends BaseFragment {


    public PostsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_posts, container, false);
        return mRootview;
    }

}
