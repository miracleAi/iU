package com.android.biubiu.ui.conversation;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.biubiu.ui.base.BaseActivity;
import com.android.biubiu.component.ChatInput;
import com.android.biubiu.component.title.TopTitleView;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;

import java.util.List;

import cc.imeetu.iu.R;
import cc.imeetu.iu.timlibrary.presentation.presenter.ChatPresenter;
import cc.imeetu.iu.timlibrary.presentation.viewfeatures.ChatView;

public class ChatActivity extends BaseActivity implements ChatView {
    private TopTitleView mToptitle;
    private ChatInput mChatinput;
    private ListView mChatListview;
    private ChatPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
    }

    private void initView() {
        mToptitle = (TopTitleView) findViewById(R.id.top_title_view);
        mToptitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToptitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mChatinput = (ChatInput) findViewById(R.id.chat_input);
        mChatListview = (ListView) findViewById(R.id.chat_listview);
    }

    private void initData() {
        mChatinput.setChatView(this);
        mPresenter = new ChatPresenter(this, "", TIMConversationType.C2C);
    }

    @Override
    public void showMessage(TIMMessage message) {

    }

    @Override
    public void showMessage(List<TIMMessage> messages) {

    }

    @Override
    public void onSendMessageSuccess(TIMMessage message) {

    }

    @Override
    public void onSendMessageFail(int code, String desc) {

    }

    @Override
    public void sendImage() {

    }

    @Override
    public void sendPhoto() {

    }

    @Override
    public void sendText() {

    }

    @Override
    public void sendFile() {

    }

    @Override
    public void startSendVoice() {

    }

    @Override
    public void endSendVoice() {

    }

    @Override
    public void sendVideo(String fileName) {

    }

    @Override
    public void cancelSendVoice() {

    }
}
