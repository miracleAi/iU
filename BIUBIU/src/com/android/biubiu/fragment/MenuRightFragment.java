package com.android.biubiu.fragment;

import cc.imeetu.iu.R;

import java.util.List;

import com.android.biubiu.MainActivity;
import com.android.biubiu.MatchSettingActivity;
import com.android.biubiu.activity.LoginActivity;
import com.android.biubiu.activity.LoginOrRegisterActivity;
import com.android.biubiu.activity.RegisterOneActivity;
import com.android.biubiu.activity.RegisterThreeActivity;
import com.android.biubiu.chat.ChatActivity;
import com.android.biubiu.chat.DemoHelper;
import com.android.biubiu.chat.MyHintDialog;
import com.android.biubiu.chat.MyHintDialog.OnDialogClick;
import com.android.biubiu.chat.UserListActivity;
import com.android.biubiu.common.Constant;
import com.android.biubiu.utils.Constants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.LoginUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.avos.avoscloud.LogUtil.log;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.NetUtils;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;


public class MenuRightFragment extends EaseConversationListFragment {
    private TextView errorText;
    private Button register, login;
    private String TAG = "MenuRightFragment";
    private ReceiveBroadCast receiveBroadCast;  //广播实例
    private View errorView, noLoginView;
    private static final int TO_LOGIN = 1007;
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
        filter.addAction(com.android.biubiu.common.Constant.EXIT_APP_BROADCAST);
        getActivity().registerReceiver(receiveBroadCast, filter);

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
                } else if (action.equals(Constant.EXIT_APP_BROADCAST)) {
                    judgeVisibleGone();
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
                    startActivity(new Intent(getActivity(), UserListActivity.class));
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
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivityForResult(intent, TO_CHATPAGE);
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
                MyHintDialog.getDialog(getActivity(), "删除会话", "嗨~确定要删除会话吗", "确定", new OnDialogClick() {

                    @Override
                    public void onOK() {
                        // 删除此会话
                        EMClient.getInstance().chatManager().deleteConversation(username, true);
                        refresh();
                        ((MainActivity) getActivity()).setUnReadVisible(showUnread());
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss() {

                    }
                });
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
}
