package com.android.biubiu.ui.conversation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.biubiu.BaseFragment;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/7/5.
 */
public class ConversationFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.conversation_fragment, container, false);
        return mRootview;
    }
}
