package com.android.biubiu.activity.biu;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.biubiu.bean.BiuBean;
import com.android.biubiu.bean.HistoryBiuBean;
import com.android.biubiu.bean.UserFriends;
import com.android.biubiu.chat.ChatActivity;
import com.android.biubiu.common.Constant;
import com.android.biubiu.sqlite.SchoolDao;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/5/18.
 */
public class ReceiveBiuListAdapter extends BaseAdapter {
    private List<UserFriends> mData = new ArrayList<UserFriends>();
    private LayoutInflater mInflater;
    private Context mCon;
    private SchoolDao schoolDao;
    private ImageOptions imageOptions;

    public ReceiveBiuListAdapter(List<UserFriends> mData, Context mCon) {
        this.mData = mData;
        this.mCon = mCon;
        mInflater = LayoutInflater.from(mCon);
        schoolDao = new SchoolDao();
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)

                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(true)
                .build();
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
        ViewHolder holder;
        final UserFriends item = mData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mCon).inflate(R.layout.item_chat_user_list, null);
            holder.img = (ImageView) convertView.findViewById(R.id.userHead_chat_user_list_img);
            holder.userName = (TextView) convertView.findViewById(R.id.userName_chat_user_List_tv);
            holder.age = (TextView) convertView.findViewById(R.id.userInfo_chat_user_List_tv);
            holder.star = (TextView) convertView.findViewById(R.id.userXingzuo_chat_user_List_tv);
            holder.school = (TextView) convertView.findViewById(R.id.userJob_chat_user_List_tv);
            holder.grabLayout = (RelativeLayout) convertView.findViewById(R.id.grab_layout);
            holder.acceptTv = (TextView) convertView.findViewById(R.id.accept_textview);
            holder.giveUTv = (TextView) convertView.findViewById(R.id.give_u_textview);
            holder.grabLayout.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(item.getNickname());
        holder.age.setText(item.getAge() + "岁");
        holder.star.setText(item.getStarsign() + "");
//        if (item.getIsgraduated().equals("1")) {
        if (schoolDao.getschoolName(item.getSchool()).get(0).getUnivsNameString() != null) {
            holder.school.setText(schoolDao.getschoolName(item.getSchool()).get(0).getUnivsNameString());
        }

//        } else {
//            holder.school.setText(item.getCarrer());
//        }
        x.image().bind(holder.img, item.getIcon_thumbnailUrl(), imageOptions);
        holder.img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mCon, MyPagerActivity.class);
                intent.putExtra("userCode", item.getUserCode());
                mCon.startActivity(intent);
            }
        });
        final int status = item.getStatus();
        if (status == 1) {
            holder.acceptTv.setBackgroundResource(R.drawable.accepted_bg);
            holder.acceptTv.setText(mCon.getResources().getString(R.string.accepted));
        } else {
            int biuvc = item.getBiuVc();
            holder.acceptTv.setBackgroundResource(R.drawable.accept_bg);
            holder.acceptTv.setText(mCon.getResources().getString(R.string.accept));
            if (biuvc > 0) {
                holder.giveUTv.setVisibility(View.VISIBLE);
                holder.giveUTv.setText(mCon.getString(R.string.give_U, biuvc));
            } else {
                holder.giveUTv.setVisibility(View.GONE);
            }
        }
        holder.acceptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 0) {
                    accept(item.getUserCode());
                }
            }
        });
        return convertView;
    }

    private void accept(final String userCode) {
        RequestParams params = new RequestParams(HttpContants.APP_BIU_ACCEPTBIU);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("grab_user_code", userCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onSuccess(String result) {
                LogUtil.d("mytest", "acceptBiu--" + result);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (state.equals("303")) {
                        Toast.makeText(mCon, "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                        SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, "");
                        SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.USER_NAME, "");
                        SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.USER_HEAD, "");
                        SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.USER_CODE, "");
//                        exitHuanxin();
                        return;
                    }
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    String token = data.getString("token");
                    if (!TextUtils.isEmpty(token)) {
                        SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, token);
                    }
                    String message = data.getString("message");
                    if ("0".equals(message)) {
                        Toast.makeText(mCon, mCon.getResources().getString(R.string.accept_fail), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Intent chat = new Intent(mCon, ChatActivity.class);
                        chat.putExtra(Constant.EXTRA_USER_ID, userCode);
                        mCon.startActivity(chat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class ViewHolder {
        private ImageView img;
        private TextView userName;
        private TextView userInfo;
        private TextView age;
        private TextView star;
        private TextView school;
        private RelativeLayout grabLayout;
        private TextView acceptTv, giveUTv;
    }
}
