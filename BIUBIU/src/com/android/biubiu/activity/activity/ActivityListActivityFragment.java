package com.android.biubiu.activity.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.biubiu.bean.Act;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.LogUtil;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActivityListActivityFragment extends Fragment {
    private View mRootview;
    private TopTitleView mTopTitle;
    private ListView mListview;
    private List<Act> mActList = new ArrayList<Act>();
    private ActAdapter mAdapter;

    public ActivityListActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_activity_list, container, false);
        initView();
        initData();
        return mRootview;
    }

    private void initView() {
        mTopTitle = (TopTitleView) mRootview.findViewById(R.id.top_title_view);
        mTopTitle.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mListview = (ListView) mRootview.findViewById(R.id.listview_activity);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Act act = mActList.get(position);
                if (act != null) {
                    Intent i = new Intent(getActivity(), WebviewActivity.class);
                    i.putExtra(com.android.biubiu.common.Constant.ACTIVITY_NAME, act.getName());
                    i.putExtra(com.android.biubiu.common.Constant.ACTIVITY_URL, act.getUrl());
                    i.putExtra(com.android.biubiu.common.Constant.ACTIVITY_COVER, act.getCover());
                    startActivity(i);
                }
            }
        });
        SharePreferanceUtils.getInstance().saveHaveToView(getActivity(),
                SharePreferanceUtils.getInstance().getUserCode(getActivity(),SharePreferanceUtils.USER_CODE,""),true);
    }

    private void initData() {
        getActivites();
        mAdapter = new ActAdapter();
        mListview.setAdapter(mAdapter);
    }

    private void getActivites() {
        RequestParams params = new RequestParams(HttpContants.ACTIVITY_GETTAGS);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtil.d("mytest", "activitytag--" + s);
                JSONObject jsons;
                try {
                    jsons = new JSONObject(s);
                    String state = jsons.getString("state");
                    if (state.equals("303")) {
                        Toast.makeText(getActivity(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.TOKEN, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_NAME, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_HEAD, "");
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.USER_CODE, "");
                        LogUtil.d("mytest", "tok---" + SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
                        exitHuanxin();
                        return;
                    }
                    if (!state.equals("200")) {
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
                    JSONObject activity = data.getJSONObject("activity");
                    JSONArray array = activity.getJSONArray("actys");
                    Gson gson = new Gson();
                    ArrayList<Act> list = gson.fromJson(array.toString(), new TypeToken<List<Act>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        mActList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    /**
     * 退出环信登录
     */
    public void exitHuanxin() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                //清空本地token
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.HX_USER_NAME, "");
                SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.HX_USER_PASSWORD, "");
            }

            @Override
            public void onProgress(int arg0, String arg1) {

            }

            @Override
            public void onError(int arg0, String arg1) {
            }
        });

    }

    private class ActAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mActList.size();
        }

        @Override
        public Object getItem(int position) {
            return mActList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Act act = mActList.get(position);
            ViewHolder vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.act_item_layout, null);
                vh.cover = (ImageView) convertView.findViewById(R.id.cover_imageview);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            x.image().bind(vh.cover, act.getCover());
            return convertView;
        }
    }

    class ViewHolder {
        ImageView cover;
    }
}
