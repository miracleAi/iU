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


public class MenuRightFragment extends EaseConversationListFragment implements FragmentIndicator.OnClickListener{
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

    }

    @Override
    public void onTabClick() {
        judgeVisibleGone();
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
    protected void setUpView() {
        super.setUpView();
        titleBar.setTitle(getResources().getString(R.string.biu_msg));
        titleBar.setBackgroundColor(getResources().getColor(R.color.main_green));
        titleBar.setRightImageResource(R.drawable.mes_btn_people);
        titleBar.setRightLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (LoginUtils.isLogin(getActivity())) {
                    headStateCharge(TO_FRIENDS, "");
                }

            }
        });

        // 注册上下文菜单
        registerForContextMenu(conversationListView);// why to do this?
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

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
        });
        conversationListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                // TODO Auto-generated method stub
                EMConversation conversation = conversationListView.getItem(position);
                final String username = conversation.getUserName();
                headStateCharge(DELETE_CHAT, username);
                return true;
            }
        });
        judgeVisibleGone();
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
        } else {
            errorItemContainer.setVisibility(View.GONE);
            refresh();
            ((MainActivity) getActivity()).setUnReadVisible(showUnread());
        }
    }

    public void headStateCharge(int toflag, final String userName) {
        // 头像审核flag：0：待审核  1：审核中 2：审核成功未读 3：审核成功已读  4:审核失败（第一次）未读 5：审核失败已读 6：审核失败 (未回滚)
        String headFlag = Constant.headState;
        int flag = 0;
        if (!TextUtils.isEmpty(headFlag)) {
            flag = Integer.parseInt(headFlag);
        }
        switch (flag) {
            case Constants.HEAD_VERIFYSUC_UNREAD:
            case Constants.HEAD_VERIFYFAIL_UNREAD:
            case Constants.HEAD_VERIFYFAIL:
            case Constants.HEAD_VERIFYFAIL_UPDATE:
                showShenHeDaiog(flag);
                break;
            default:
                if (toflag == TO_CHAT) {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(Constant.EXTRA_USER_ID, userName);
                    startActivityForResult(intent, TO_CHATPAGE);
                } else if (toflag == TO_FRIENDS) {
                    startActivity(new Intent(getActivity(), UserListActivity.class));
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
                break;
        }
    }

    private void showShenHeDaiog(final int flag) {
        String title = "";
        String msg = "";
        String strBtn1 = "";
        String strBtn2 = "";
        switch (flag) {
            case Constants.HEAD_VERIFYSUC_UNREAD:
                title = getResources().getString(R.string.head_egis);
                msg = getResources().getString(R.string.head_egis_info);
                strBtn1 = "我知道了";
                break;
            case Constants.HEAD_VERIFYFAIL_UNREAD:
            case Constants.HEAD_VERIFYFAIL:
                title = getResources().getString(R.string.head_no_egis);
                msg = getResources().getString(R.string.head_no_egis_info1);
                strBtn1 = "取消";
                strBtn2 = "重新上传";
                break;
            case Constants.HEAD_VERIFYFAIL_UPDATE:
                title = getResources().getString(R.string.head_no_egis);
                msg = getResources().getString(R.string.head_no_egis_info2);
                strBtn1 = "取消";
                strBtn2 = "重新上传";
                break;
            default:
                break;
        }
        if (flag == Constants.HEAD_VERIFYSUC_UNREAD) {
            CommonDialog.singleBtnDialog(getActivity(), title, msg, strBtn1, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            HttpUtils.commitIconState(getActivity(), flag);
        } else {
            CommonDialog.doubleBtnDialog(getActivity(), title, msg, strBtn1, strBtn2, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (flag == Constants.HEAD_VERIFYFAIL_UPDATE) {
                        HttpUtils.commitIconState(getActivity(), flag);
                    }
                    dialog.dismiss();
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    switch (flag) {
                        case Constants.HEAD_VERIFYFAIL_UNREAD:
                            showHeadDialog();
                            break;
                        case Constants.HEAD_VERIFYFAIL:
                            showHeadDialog();
                            break;
                        case Constants.HEAD_VERIFYFAIL_UPDATE:
                            showHeadDialog();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }
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
                break;
            case TO_LOGIN:
                judgeVisibleGone();
                break;
            case TO_REGISTER:
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
                    Constant.headState = "3";
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
        if (unread > 0) {
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
