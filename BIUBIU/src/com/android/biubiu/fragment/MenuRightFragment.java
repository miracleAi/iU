package com.android.biubiu.fragment;

import cc.imeetu.iu.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.biubiu.MainActivity;
import com.android.biubiu.activity.LoginActivity;
import com.android.biubiu.activity.RegisterThreeActivity;
import com.android.biubiu.callback.BiuBooleanCallback;
import com.android.biubiu.chat.ChatActivity;
import com.android.biubiu.chat.MyHintDialog;
import com.android.biubiu.chat.MyHintDialog.OnDialogClick;
import com.android.biubiu.chat.UserListActivity;
import com.android.biubiu.common.CommonDialog;
import com.android.biubiu.common.Constant;
import com.android.biubiu.community.CommunityBiuListActivity;
import com.android.biubiu.component.indicator.FragmentIndicator;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.HttpUtils;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.UploadImgUtils;
import com.ant.liao.GifView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MenuRightFragment extends EaseConversationListFragment implements FragmentIndicator.OnClickListener {
    private TextView errorText;
    private Button register, login;
    private String TAG = "MenuRightFragment";
    private ReceiveBroadCast receiveBroadCast;  //广播实例
    private View errorView, noLoginView;
    private static final int TO_LOGIN = 1007;
    private static final int SELECT_PHOTO = 1002;
    private static final int CROUP_PHOTO = 1003;
    Bitmap userheadBitmap = null;
    String headPath = "";
    LinearLayout loadingLayout;
    GifView loadGif;
    TextView loadTv;
    public static boolean isUploadingPhoto = false;
    private static final int TO_CHAT = 0;
    private static final int TO_FRIENDS = 1;
    private static final int DELETE_CHAT = 2;
    private static final int TO_REGISTER = TO_LOGIN + 1;
    private static final int TO_CHATPAGE = TO_REGISTER + 1;
    private int newMsgCount = 0;
    private static final int TO_BIU_PAGE = TO_CHATPAGE + 1;

    @Override
    protected void initView() {
        super.initView();
        errorView = (LinearLayout) View.inflate(getActivity(), R.layout.right_menu, null);
        noLoginView = (LinearLayout) View.inflate(getActivity(), R.layout.item_right_no_rigister, null);

//        if (!LoginUtils.isLogin(getActivity())) {
        errorItemContainer.addView(noLoginView);
        register = (Button) noLoginView.findViewById(R.id.register_item_btn);
        login = (Button) noLoginView.findViewById(R.id.login_item_btn);
        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), RegisterThreeActivity.class);
                startActivityForResult(intent, TO_REGISTER);
            }
        });
        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, TO_LOGIN);
            }
        });
//        } else {
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
//        }
        // 注册广播接收
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.FLAG_RECEIVE);    //只有持有相同的action的接受者才能接收此广播
        getActivity().registerReceiver(receiveBroadCast, filter);

        setConversationListItemClickListener(new EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                if (conversation != null) {
                    String username = conversation.getUserName();
                    if (username.equals(EMClient.getInstance().getCurrentUser()))
                        Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                    else {
                        headStateCharge(TO_CHAT, username);
                    }
                }
            }
        });
    }

    @Override
    public void onTabClick() {
        judgeVisibleGone();
    }

    @Override
    public void onLeaveTab() {

    }

    public class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constants.FLAG_RECEIVE)) {
                    LogUtil.e(TAG, "收到刷新广播");
                    handler.sendEmptyMessage(MSG_REFRESH);
//                    refresh();
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!LoginUtils.isLogin(getActivity())){
            titleBar.setNewMsgGone();
        }
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        titleBar.setTitle(getResources().getString(R.string.biu_msg));
        titleBar.setBackgroundColor(getResources().getColor(R.color.main_green));
        titleBar.setRightImageResource(R.drawable.message_btn_right);
        titleBar.setLeftImageResource(R.drawable.mes_btn_left);
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (LoginUtils.isLogin(getActivity())) {
                    headStateCharge(TO_FRIENDS, "");
                }

            }
        });
        titleBar.setLeftImageResource(R.drawable.mes_btn_left);
        titleBar.setLeftLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginUtils.isLogin(getActivity())){
                    Intent i = new Intent(getActivity(), CommunityBiuListActivity.class);
                    startActivityForResult(i,TO_BIU_PAGE);
                }
            }
        });

        // 注册上下文菜单
        registerForContextMenu(conversationListView);// why to do this?
        /*conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    headStateCharge(TO_CHAT, username);
                }
            }
        });*/
        conversationListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                EMConversation conversation = conversationListView.getItem(position);
                final String username = conversation.getUserName();
                headStateCharge(DELETE_CHAT, username);
                return true;
            }
        });
        judgeVisibleGone();
    }

    public void updateNewMsg(int num){
        newMsgCount = num;
        judgeVisibleGone();
        String countStr = "";
        if(num >0){
            if(num > 99){
                countStr = num%100+"+";
            }else{
                countStr = num + "";
            }
            titleBar.setNewMsgCount(countStr);
        }else{
            titleBar.setNewMsgGone();
        }
    }

    /**
     * 判断显示隐藏
     */
    private void judgeVisibleGone() {
        if (!LoginUtils.isLogin(getActivity())) {
            if (errorItemContainer.getVisibility() == View.GONE) {
                errorItemContainer.setVisibility(View.VISIBLE);
            }
            if (errorView.getVisibility() == View.VISIBLE) {
                errorView.setVisibility(View.GONE);
            }
            if (noLoginView.getVisibility() == View.GONE) {
                noLoginView.setVisibility(View.VISIBLE);
            }
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            errorItemContainer.setVisibility(View.GONE);
            refresh();
            ((MainActivity) getActivity()).setUnReadVisible(showUnread());
        }
    }

    public void headStateCharge(int toflag, final String userName) {
        String headFlag = Constant.headState;
        int flag = 0;
        if (!TextUtils.isEmpty(headFlag)) {
            flag = Integer.parseInt(headFlag);
        }
        if(flag == Constants.HEAD_VERIFYFAIL){
            showShenHeDaiog(flag);
        }else{
            if (toflag == TO_CHAT) {
                // 进入聊天页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constant.EXTRA_USER_ID, userName);
                startActivityForResult(intent, TO_CHATPAGE);
            } else if (toflag == TO_FRIENDS) {
                startActivityForResult(new Intent(getActivity(), UserListActivity.class),TO_FRIENDS);
            } else if (toflag == DELETE_CHAT) {
                MyHintDialog.getDialog(getActivity(), "删除会话", "嗨~确定要删除会话吗", "确定", new OnDialogClick() {

                    @Override
                    public void onOK() {
                        // TODO Auto-generated method stub
                        // 删除此会话
                        EMClient.getInstance().chatManager().deleteConversation(userName, true);
                        refresh();
                        ((MainActivity) getActivity()).setUnReadVisible(showUnread());
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onDismiss() {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }
    }

    private void showShenHeDaiog(final int flag) {
        String title = "";
        String msg = "";
        String strBtn1 = "";
        String strBtn2 = "";
        title = getResources().getString(R.string.head_no_egis);
        msg = getResources().getString(R.string.head_no_egis_info1);
        strBtn1 = "取消";
        strBtn2 = "重新上传";
        CommonDialog.doubleBtnDialog(getActivity(), title, msg, strBtn1, strBtn2, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showHeadDialog();
                dialog.dismiss();
            }
        });
    }

    public void showHeadDialog() {
        CommonDialog.headDialog(getActivity(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, SELECT_PHOTO);
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CROUP_PHOTO:
                try {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        userheadBitmap = extras.getParcelable("data");
                        if (userheadBitmap != null) {
                            headPath = saveHeadImg(userheadBitmap);
                            uploadPhoto(headPath);
                        }
                    }
                } catch (NullPointerException e) {
                    // TODO: handle exception
                }
                break;
            case SELECT_PHOTO:
                if (data != null) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case TO_CHATPAGE:
                ((MainActivity) getActivity()).setUnReadVisible(showUnread());
                refresh();
                break;
            case TO_LOGIN:
                judgeVisibleGone();
                break;
            case TO_REGISTER:
                judgeVisibleGone();
                break;
            case TO_FRIENDS:
                refresh();
                break;
            case TO_BIU_PAGE:
                newMsgCount = 0;
                titleBar.setNewMsgGone();
                judgeVisibleGone();
                break;
            default:
                break;
        }
    }

    private void uploadPhoto(String headPath) {
        // TODO Auto-generated method stub
        isUploadingPhoto = true;
        UploadImgUtils.uploadPhoto(getActivity(), headPath, new BiuBooleanCallback() {

            @Override
            public void callback(boolean result) {
                // TODO Auto-generated method stub
                isUploadingPhoto = false;
                if (result) {
                    Constant.headState = Constants.HEAD_VERIFYING+"";
                    Toast.makeText(getActivity(), "上传照片成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "上传照片失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        // 调用拍照的裁剪功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽和搞的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROUP_PHOTO);
    }

    public String saveHeadImg(Bitmap head) {
        FileOutputStream fos = null;
        String path = "";
        path = Environment.getExternalStorageDirectory()
                + "/biubiu/" + System.currentTimeMillis() + ".png";
        File file = new File(path);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
            head.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return path;

    }

    //	/**
//	 * 会话消息监听
//	 */
//	EMMessageListener msgListener = new EMMessageListener() {
//
//
//		@Override
//		public void onMessageReceived(List<EMMessage> messages) {
//			//收到消息
//			
//			log.e(TAG, "收到消息");
//			refresh();
//			handler.sendEmptyMessage(2);
//			
//		}
//
//		@Override
//		public void onCmdMessageReceived(List<EMMessage> messages) {
//			//收到透传消息
//			//收到消息
//			
//			log.e(TAG, "收到透传消息");
//			refresh();
//		}
//
//		@Override
//		public void onMessageReadAckReceived(List<EMMessage> messages) {
//			//收到已读回执
//		}
//
//		@Override
//		public void onMessageDeliveryAckReceived(List<EMMessage> message) {
//			//收到已送达回执
//		}
//
//		@Override
//		public void onMessageChanged(EMMessage message, Object change) {
//			//消息状态变动
//		}
//	};
    private boolean showUnread() {
        int unread = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if ((unread + newMsgCount) > 0) {
            return true;
        }
        return false;
    }

    @Override
    protected void onConnectionConnected() {
        super.onConnectionConnected();
        if (LoginUtils.isLogin(getActivity())) {
            errorItemContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (LoginUtils.isLogin(getActivity())) {
            errorItemContainer.setVisibility(View.VISIBLE);
            noLoginView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiveBroadCast);
    }
}
