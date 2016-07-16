package com.android.biubiu.ui.conversation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.component.record.AudioRecordLayout;
import com.android.biubiu.component.util.FileUtil;
import com.android.biubiu.ui.base.BaseActivity;
import com.android.biubiu.component.ChatInput;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.ui.conversation.models.ImageMessage;
import com.android.biubiu.ui.conversation.models.Message;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;

import java.io.File;
import java.util.List;

import cc.imeetu.iu.R;
import cc.imeetu.iu.timlibrary.presentation.presenter.ChatPresenter;
import cc.imeetu.iu.timlibrary.presentation.viewfeatures.ChatView;

public class ChatActivity extends BaseActivity implements ChatView ,AudioRecordLayout.onRecordStatusListener {
    private final String TAG = ChatActivity.class.getSimpleName();
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;

    private TopTitleView mToptitle;
    private ChatInput mChatinput;
    private ListView mChatListview;
    private ChatPresenter mPresenter;
    private Uri mFileUri;
//    private RecorderUtil mRecorder = new RecorderUtil();

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
        mChatinput.setRecordStatusListener(this);
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
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                mFileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void sendText() {
        /*Message message = new TextMessage(mChatinput.getText());
        mPresenter.sendMessage(message.getMessage());
        mChatinput.setText("");*/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                sendImage(mFileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK) {
                sendImage(FileUtil.getImageFilePath(this, data.getData()));
            }

        }
    }

    private void sendImage(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            if (file.length() > 1024 * 1024 * 10) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new ImageMessage(path);
//                mPresenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRecordStart() {

    }

    @Override
    public void onRecordComplete() {

    }

    @Override
    public void onRecordCancle() {

    }
}
