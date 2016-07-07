package com.android.biubiu.ui.discovery.homepage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.ui.mine.MyPagerActivity;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.transport.http.response.community.Comment;
import com.android.biubiu.transport.http.response.community.SimpleRespData;
import com.android.biubiu.chat.MyHintDialog;
import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.component.util.DateUtils;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.persistence.SchoolDao;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/6/1.
 */
public class CommentAdapter extends BaseAdapter {
    private List<Comment> mData;
    private Context mCon;
    private LayoutInflater mInflater;
    private SchoolDao schoolDao;
    private int mUserCode;
    private int mPostId;
    private IRefreshUi mRefreshUi;

    public CommentAdapter(List<Comment> mData, Context mCon) {
        this.mData = mData;
        this.mCon = mCon;
        mUserCode = Integer.parseInt(SharePreferanceUtils.getInstance().getUserCode(mCon, SharePreferanceUtils.USER_CODE, ""));
        mInflater = LayoutInflater.from(mCon);
        schoolDao = new SchoolDao();
    }

    public void setPostId(int postId) {
        mPostId = postId;
    }

    public interface IRefreshUi {
        void whenDelete(Comment comment);
    }

    public void setIRefreshUi(IRefreshUi refreshUi) {
        mRefreshUi = refreshUi;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Comment comment = mData.get(position);
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.comment_item_layout, parent, false);
            vh.head = (ImageView) convertView.findViewById(R.id.head_imageview);
            vh.moreLayout = (LinearLayout) convertView.findViewById(R.id.more_layout);
            vh.nickname = (TextView) convertView.findViewById(R.id.nickname_textview);
            vh.school = (TextView) convertView.findViewById(R.id.school_textview);
            vh.time = (TextView) convertView.findViewById(R.id.time_textview);
            vh.content = (TextView) convertView.findViewById(R.id.content_textview);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        x.image().bind(vh.head, comment.getUserFromHead());
        vh.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCon, MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(comment.getUserFromCode()));
                mCon.startActivity(intent);
            }
        });
        vh.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCon, MyPagerActivity.class);
                intent.putExtra("userCode", String.valueOf(comment.getUserFromCode()));
                mCon.startActivity(intent);
            }
        });
        vh.nickname.setText(comment.getUserFromName());
        if ("1".equals(comment.getUserFromSex())) {
            vh.nickname.setTextColor(Color.parseColor("#8883bc"));
        } else if ("2".equals(comment.getUserFromSex())) {
            vh.nickname.setTextColor(Color.parseColor("#f0637f"));
        }
        if (!TextUtils.isEmpty(comment.getUserFromSchool())) {
            if (schoolDao.getschoolName(comment.getUserFromSchool()).get(0).getUnivsNameString() != null) {
                vh.school.setText(schoolDao.getschoolName(comment.getUserFromSchool()).get(0).getUnivsNameString());
            }
        }
        vh.time.setText(DateUtils.getDateFormatInList2(mCon, comment.getCreateAt() * 1000));
        int parentId = comment.getParentId();
        if (parentId != 0) {
            String replyComm = mCon.getResources().getString(R.string.reply_comment, comment.getUserToName(),
                    comment.getContent());
            SpannableString ss = new SpannableString(replyComm);
            ForegroundColorSpan foregroundColorSpan = null;
            if ("1".equals(comment.getUserFromSex())) {
                foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#8883bc"));
            } else if ("2".equals(comment.getUserFromSex())) {
                foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#f0637f"));
            }
            int start = 3;
            int end = start + comment.getUserToName().length();
            ss.setSpan(foregroundColorSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            vh.content.setText(ss);
        } else {
            vh.content.setText(comment.getContent());
        }
        vh.moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOperation(mUserCode == comment.getUserFromCode(), comment);
            }
        });
        return convertView;
    }

    /**
     * 举报或删除
     */
    private void showOperation(final boolean b, final Comment comment) {
        final AlertDialog portraidlg = new AlertDialog.Builder(mCon).create();
        portraidlg.show();

        Window win = portraidlg.getWindow();
        win.setContentView(R.layout.item_hint_moster_dralog_mypage);
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(params);

        RelativeLayout dismissLayout, jubaoLayout;
        jubaoLayout = (RelativeLayout) win.findViewById(R.id.jubao_dialog_mupage_rl);
        dismissLayout = (RelativeLayout) win.findViewById(R.id.dismiss_dialog_mypage_rl);
        dismissLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                portraidlg.dismiss();
            }
        });
        if (b) {
            ((TextView) jubaoLayout.findViewById(R.id.operation_textview)).setText(mCon.getResources().getString(R.string.delete));
        }
        jubaoLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String tips, title;
                if (b) {
                    title = mCon.getResources().getString(R.string.delete_title);
                    tips = mCon.getResources().getString(R.string.delete_tips);
                } else {
                    title = mCon.getResources().getString(R.string.report_title);
                    tips = mCon.getResources().getString(R.string.report_tips);
                }
                portraidlg.dismiss();
                MyHintDialog.getDialog(mCon, title, tips, mCon.getResources().getString(R.string.sure),
                        new MyHintDialog.OnDialogClick() {

                            @Override
                            public void onOK() {
                                if (b) {
                                    deleteComment(comment);
                                } else {
                                    reportComment(comment);
                                }
                            }

                            @Override
                            public void onDismiss() {

                            }
                        });
            }
        });
    }

    private void deleteComment(final Comment comment) {
        RequestParams params = new RequestParams(HttpContants.COMMENT_DELETECOMMENT);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("commentId", comment.getCommentId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), mCon)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, data.getToken());
                }
                Toast.makeText(mCon, mCon.getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                if (mRefreshUi != null) {
                    mRefreshUi.whenDelete(comment);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void reportComment(Comment comment) {
        RequestParams params = new RequestParams(HttpContants.REPORT_CREATEREPORT);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("postId", mPostId);
            requestObject.put("commentId", comment.getCommentId());
            requestObject.put("userCode", comment.getUserFromCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Data<SimpleRespData> response = CommonUtils.parseJsonToObj(s, new TypeToken<Data<SimpleRespData>>() {
                });
                if (!CommonUtils.unifyResponse(Integer.parseInt(response.getState()), mCon)) {
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, data.getToken());
                }
                Toast.makeText(mCon, mCon.getResources().getString(R.string.report_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    class ViewHolder {
        ImageView head;
        LinearLayout moreLayout;
        TextView nickname, school, time, content;
    }
}
