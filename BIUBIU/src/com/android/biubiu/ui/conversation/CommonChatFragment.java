package com.android.biubiu.ui.conversation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.biubiu.ui.base.BaseFragment;
import com.android.biubiu.ui.base.BasicFragment;

import cc.imeetu.iu.R;

/**
 * A simple {@link Fragment} subclass.
 * 普通聊天界面
 */
public class CommonChatFragment extends BasicFragment {


    public CommonChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_common_chat, container, false);
        return mRootView;
    }

    @Override
    protected void initView() {
        mRootView.findViewById(R.id.chat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(getActivity(), ChatActivity.class);
                startActivity(chat);
            }
        });
    }

    @Override
    protected void initData() {

    }
}
