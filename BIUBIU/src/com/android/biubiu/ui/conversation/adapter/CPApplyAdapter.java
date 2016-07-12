package com.android.biubiu.ui.conversation.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.biubiu.component.util.CommonUtils;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.persistence.SchoolDao;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.transport.http.response.base.Data;
import com.android.biubiu.transport.http.response.community.SimpleRespData;
import com.android.biubiu.transport.http.response.conversation.CPApply;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import cc.imeetu.iu.R;

/**
 * Created by yanghj on 16/7/11.
 */
public class CPApplyAdapter extends ArrayAdapter<CPApply> {
    private int resourceId;
    private Context mCon;
    private SchoolDao schoolDao;
    private ImageOptions imageOptions;
    public CPApplyAdapter(Context context, int resource, List<CPApply> objects) {
        super(context, resource, objects);
        resourceId = resource;
        mCon = context;
        schoolDao = new SchoolDao();
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)

                .setFailureDrawableId(R.drawable.photo_fail)
                .setIgnoreGif(true)
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final CPApply bean = getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mCon).inflate(resourceId, null);
            holder.img = (ImageView) convertView.findViewById(R.id.userHead_chat_user_list_img);
            holder.userName = (TextView) convertView.findViewById(R.id.userName_chat_user_List_tv);
            holder.star = (TextView) convertView.findViewById(R.id.userXingzuo_chat_user_List_tv);
            holder.school = (TextView) convertView.findViewById(R.id.userJob_chat_user_List_tv);
            holder.acceptTv = (TextView) convertView.findViewById(R.id.accept_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.userName.setText(bean.getUserName());
        holder.star.setText(bean.getStarsign() + "");
        if(!TextUtils.isEmpty(bean.getUserSchool())){
            if (schoolDao.getschoolName(bean.getUserSchool()).get(0).getUnivsNameString() != null) {
                holder.school.setText(schoolDao.getschoolName(bean.getUserSchool()).get(0).getUnivsNameString());
            }
        }
        x.image().bind(holder.img, bean.getUserHead(), imageOptions);
        final int isAccept = bean.getIsAccept();
        if (isAccept == 1) {
            holder.acceptTv.setBackgroundResource(R.drawable.accepted_bg);
            holder.acceptTv.setText(mCon.getResources().getString(R.string.accepted));
            holder.acceptTv.setEnabled(false);
        } else {
            holder.acceptTv.setBackgroundResource(R.drawable.accept_bg);
            holder.acceptTv.setText(mCon.getResources().getString(R.string.accept));
        }
        holder.acceptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccept == 0) {
                    accept(bean);
                }
            }
        });
        return convertView;
    }

    private void accept(final CPApply bean) {
        RequestParams params = new RequestParams(HttpContants.COMBIU_ACCEPTCOMBIU);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(mCon, SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(mCon, SharePreferanceUtils.TOKEN, ""));
            requestObject.put("userCode", bean.getUserCode());
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
                /*Data<SimpleRespData> response = CommonUtils.parseJsonToObj(result,new TypeToken<Data<SimpleRespData>>(){});
                if(!CommonUtils.unifyResponse(Integer.parseInt(response.getState()),mCon)){
                    return;
                }
                SimpleRespData data = response.getData();
                if (!TextUtils.isEmpty(data.getToken())) {
                    SharePreferanceUtils.getInstance().putShared(mCon, SharePreferanceUtils.TOKEN, data.getToken());
                }
                bean.setIsAccept(1);
                notifyDataSetChanged();
                Intent chat = new Intent(mCon, ChatActivity.class);
                chat.putExtra(Constant.EXTRA_USER_ID, String.valueOf(bean.getUserCode()));
                chat.putExtra(Constant.EXTRA_USER_NAME, String.valueOf(bean.getUserName()));
                mCon.startActivity(chat);*/
            }
        });
    }

    class ViewHolder {
        private ImageView img;
        private TextView userName;
        private TextView age;
        private TextView star;
        private TextView school;
        private TextView acceptTv;
    }

}
