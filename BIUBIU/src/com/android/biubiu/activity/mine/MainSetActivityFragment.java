package com.android.biubiu.activity.mine;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.biubiu.bean.PersonalTagBean;
import com.android.biubiu.bean.SettingBean;
import com.android.biubiu.component.title.TopTitleView;
import com.android.biubiu.utils.HttpContants;
import com.android.biubiu.utils.NetUtils;
import com.android.biubiu.utils.SharePreferanceUtils;
import com.avos.avoscloud.LogUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import cc.imeetu.iu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainSetActivityFragment extends Fragment implements View.OnClickListener{
    View rootView;
    private TopTitleView mTopTitle;
    private boolean isRecvMsg = true;
    private boolean isOpenVoice = true;
    private boolean isOpenShck = true;
    private ImageView newMsgToggle;
    private ImageView voiceToggle;
    private ImageView shockToggle;
    SettingBean setBean;

    public MainSetActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_set, container, false);
        initView();
        return rootView;
    }

    private void initView() {
        newMsgToggle = (ImageView) rootView.findViewById(R.id.newmsg_toggle);
        voiceToggle = (ImageView) rootView.findViewById(R.id.voice_toggle);
        shockToggle = (ImageView) rootView.findViewById(R.id.shock_toggle);
        mTopTitle = (TopTitleView) rootView.findViewById(R.id.top_title_view);
        mTopTitle.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveSetInfo();
            }
        });
    }
    private void setToggle(){
        //声音 0--关闭 1--打开
        if(setBean.getSound() == 0){
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false);
            LogUtil.log.d("SharePreferanceUtils"+SharePreferanceUtils.getInstance().isOpenVoice(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, true));
            isOpenVoice = false;
            voiceToggle.setImageResource(R.drawable.setting_btn_yes);
        }else{
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, true);
            LogUtil.log.d("SharePreferanceUtils"+SharePreferanceUtils.getInstance().isOpenVoice(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false));
            isOpenVoice = true;
            voiceToggle.setImageResource(R.drawable.setting_btn_no);
        }
        //振动 0--关闭 1--打开
        if(setBean.getVibration() == 0){
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, false);
            isOpenShck = false;
            shockToggle.setImageResource(R.drawable.setting_btn_yes);
        }else{
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, true);
            isOpenShck = true;
            shockToggle.setImageResource(R.drawable.setting_btn_no);
        }
        //接收消息 0--关闭，不接收  1--打开，接收
        if(setBean.getMessage() == 0){
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, false);
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false);
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, false);
            isRecvMsg = false;
            newMsgToggle.setImageResource(R.drawable.setting_btn_yes);
            isOpenVoice = false;
            voiceToggle.setImageResource(R.drawable.setting_btn_yes);
            isOpenShck = false;
            shockToggle.setImageResource(R.drawable.setting_btn_yes);
        }else{
            SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, true);
            isRecvMsg = true;
            newMsgToggle.setImageResource(R.drawable.setting_btn_no);
        }
    }
    /**
     * 加载数据
     */
    public void initlodo(){
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS+HttpContants.GET_SETTING);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity().getApplicationContext(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
                params.addBodyParameter("data",requestObject.toString());
                x.http().post(params, new Callback.CommonCallback<String>() {

                    @Override
                    public void onCancelled(CancelledException arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(Throwable arg0, boolean arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(x.app(), arg0.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onSuccess(String arg0) {
                        LogUtil.log.d("mytest", "set--"+arg0);
                        JSONObject jsons;
                        try {
                            jsons = new JSONObject(arg0);
                            String code = jsons.getString("state");
                            if(!code.equals("200")){
                                if(code.equals("303")){
                                    Toast.makeText(x.app(), "登录过期，请重新登录", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                return;
                            }
                            JSONObject data = jsons.getJSONObject("data");
//					String token = data.getString("token");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                            Gson gson=new Gson();
                            SettingBean settingBean=gson.fromJson(data.toString(), SettingBean.class);
                            if(settingBean == null){
                                return;
                            }
                            setBean = settingBean;
                            setToggle();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                });

            } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存设置信息
     */
    private void saveSetInfo() {
        if(setBean==null){
            getActivity().finish();
            return;
        }
        if(!NetUtils.isNetworkConnected(getActivity())){
            getActivity().finish();
            return;
        }
        updateSetBean();
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS+HttpContants.UPDATE_SETTING);
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", SharePreferanceUtils.getInstance().getToken(getActivity(), SharePreferanceUtils.TOKEN, ""));
            requestObject.put("device_code", SharePreferanceUtils.getInstance().getDeviceId(getActivity(), SharePreferanceUtils.DEVICE_ID, ""));
            requestObject.put("sound", setBean.getSound());
            requestObject.put("message", setBean.getMessage());
            requestObject.put("vibration", setBean.getVibration());
            String paramStr = "message,sound,vibration";
            requestObject.put("parameters",paramStr);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        params.addBodyParameter("data",requestObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(x.app(), "保存失败", Toast.LENGTH_SHORT).show();
                com.android.biubiu.utils.LogUtil.d("mytest", ex.getMessage()+"");
                com.android.biubiu.utils.LogUtil.d("mytest", ex.getCause()+"");
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String arg0) {
                // TODO Auto-generated method stub
                com.android.biubiu.utils.LogUtil.d("mytest", "updateset--"+arg0);
                try {
                    JSONObject jsons = new JSONObject(arg0);
                    String state = jsons.getString("state");
                    if(!state.equals("200")){
                        Toast.makeText(x.app(), "保存失败", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(setBean.getSound() == 0){
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, false);

                    }else{
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_OPEN_VOICE, true);

                    }
                    //振动 0--关闭 1--打开
                    if(setBean.getVibration() == 0){
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, false);

                    }else{
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_SHOCK, true);
                    }
                    //新消息通知 0--关闭 1--打开
                    if(setBean.getMessage() == 0){
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, false);

                    }else{
                        SharePreferanceUtils.getInstance().putShared(getActivity(), SharePreferanceUtils.IS_RECEIVE_MSG, true);
                    }
//					String token = (jsons.getJSONObject("data").getString("token"));
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        getActivity().finish();
    }
    private void updateSetBean(){
        if(isOpenVoice){
            setBean.setSound(1);
        }else{
            setBean.setSound(0);
        }
        if(isRecvMsg){
            setBean.setMessage(1);
        }else{
            setBean.setMessage(0);
        }
        if(isOpenShck){
            setBean.setVibration(1);
        }else{
            setBean.setVibration(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.msg_layout:
                if(isRecvMsg){
                    isRecvMsg = false;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_yes);
                    isOpenVoice = false;
                    voiceToggle.setImageResource(R.drawable.setting_btn_yes);
                    isOpenShck = false;
                    shockToggle.setImageResource(R.drawable.setting_btn_yes);
                }else{
                    isRecvMsg = true;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_no);
                }
                break;
            case R.id.voice_layout:
                if(isOpenVoice){
                    isOpenVoice = false;
                    voiceToggle.setImageResource(R.drawable.setting_btn_yes);
                }else{
                    isRecvMsg = true;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_no);
                    isOpenVoice = true;
                    voiceToggle.setImageResource(R.drawable.setting_btn_no);
                }
                break;
            case R.id.shock_layout:
                if(isOpenShck){
                    isOpenShck = false;
                    shockToggle.setImageResource(R.drawable.setting_btn_yes);
                }else{
                    isRecvMsg = true;
                    newMsgToggle.setImageResource(R.drawable.setting_btn_no);
                    isOpenShck = true;
                    shockToggle.setImageResource(R.drawable.setting_btn_no);
                }
                break;
        }
    }
}
