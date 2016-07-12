package com.android.biubiu.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.biubiu.ui.conversation.activity.CPApplyActivity;
import com.android.biubiu.ui.conversation.activity.CPSettingActivity;
import com.android.biubiu.ui.discovery.CommNotifyActivity;
import com.android.biubiu.ui.overall.BaseFragment;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/7/5.
 */
public class ConversationFragment extends BaseFragment implements View.OnClickListener {
    private final int TO_APPLY_PAGE = 100;

    private RelativeLayout mCPApplyLayout;
    private LinearLayout mCPSettingLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.conversation_fragment, container, false);
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mCPApplyLayout = (RelativeLayout) mRootview.findViewById(R.id.apply_layout);
        mCPSettingLayout = (LinearLayout) mRootview.findViewById(R.id.cp_setting_layout);
        mCPApplyLayout.setOnClickListener(this);
        mCPSettingLayout.setOnClickListener(this);
        mRootview.findViewById(R.id.chat_button).setOnClickListener(this);
    }

    private void initData() {

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
            case R.id.chat_button:
                Intent chat = new Intent(getActivity(),ChatActivity.class);
                startActivity(chat);
                break;
        }
    }
}
