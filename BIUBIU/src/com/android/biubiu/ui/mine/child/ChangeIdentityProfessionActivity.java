package com.android.biubiu.ui.mine.child;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import cc.imeetu.iu.R;

import com.android.biubiu.bean.UserInfoBean;
import com.android.biubiu.common.city.OnWheelChangedListener;
import com.android.biubiu.common.city.WheelView;
import com.android.biubiu.component.util.Constants;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.component.util.LogUtil;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.ui.base.BaseActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangeIdentityProfessionActivity extends BaseActivity implements
        OnWheelChangedListener, OnClickListener {
//    private WheelView mViewConstellation;
    private RelativeLayout backLayout, completeLayout;
    private LinearLayout isStudentLinear, graduateLinear;
    private ImageView isStudentImv,graduateImv;
    private boolean isStudent = true;
    /**
     * 所有身份职业
     */
    /*private String mIdentity[] = { "学生", "媒体/公关", "金融", "法律", "销售", "咨询",
			"IT/互联网/通信", "文化/艺术", "影视/娱乐", "教育/科研", "医疗/健康", "房地产/建筑", "政府机构" };*/
    private String mIdentity[] = {"学生", "上班族"};
    private TextView identity;
    UserInfoBean infoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_identity_profession);
        infoBean = (UserInfoBean) getIntent().getSerializableExtra("userInfoBean");
        initView();
        setUpListener();
        setUpData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        isStudentLinear = (LinearLayout) findViewById(R.id.is_student_linear);
        isStudentLinear.setOnClickListener(this);
        graduateLinear = (LinearLayout) findViewById(R.id.graduate_linear);
        graduateLinear.setOnClickListener(this);

        isStudentImv = (ImageView) findViewById(R.id.in_school_imv);
        graduateImv = (ImageView) findViewById(R.id.out_school_imv);

        identity = (TextView) findViewById(R.id.identity_profession_change_city_tv);
        if (infoBean != null) {
            if (infoBean.getIsStudent() != null && infoBean.getIsStudent().equals(Constants.IS_STUDENT_FLAG)) {
                identity.setText("学生");
            } else {
                identity.setText("上班族");
				/*if(infoBean.getCareer() != null){
					identity.setText(infoBean.getCareer());
				}*/
            }
        }

//        mViewConstellation = (WheelView) findViewById(R.id.id_identify_profession);
        backLayout = (RelativeLayout) findViewById(R.id.back_changeIdentity_mine_rl);
        completeLayout = (RelativeLayout) findViewById(R.id.mine_identity_profession_wancheng_rl);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        completeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == identity.getText() || identity.getText().toString().equals("")) {
                    return;
                }
                if (identity.getText().equals("学生")) {
                    infoBean.setIsStudent(Constants.IS_STUDENT_FLAG);
					/*infoBean.setCareer("");
					infoBean.setCompany("");
					Intent intent=new Intent(ChangeIdentityProfessionActivity.this,ChangeSchoolActivity.class);
					startActivityForResult(intent, 10);*/
                } else {
                    //infoBean.setCareer(identity.getText().toString());
                    infoBean.setIsStudent(Constants.HAS_GRADUATE);
                    //infoBean.setSchool("");
                    //updateInfo("career,school,isgraduated");
                }
                updateInfo("isgraduated");
            }
        });
    }

    private void setUpListener() {
        // TODO Auto-generated method stub
//        mViewConstellation.addChangingListener(this);
    }

    private void setUpData() {
        // TODO Auto-generated method stub
        // initProvinceDatas();
        identity.setText(mIdentity[0]);
//        mViewConstellation.setViewAdapter(new ArrayWheelAdapter<String>(
//                ChangeIdentityProfessionActivity.this, mIdentity));

        // Log.e("lucifer", "mProvinceDatas.length==" + mProvinceDatas.length);
        // 设置可见条目数量
//        mViewConstellation.setVisibleItems(7);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
//        if (wheel == mViewConstellation) {
//
//            int pCurrent = mViewConstellation.getCurrentItem();
//            identity.setText(mIdentity[pCurrent]);
//        }
    }

    private void updateInfo(String keys) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS + HttpContants.UPDATE_USETINFO);
        String token = SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
        String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, "");
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", token);
            requestObject.put("device_code", deviceId);
            requestObject.put("isgraduated", infoBean.getIsStudent());
            requestObject.put("career", infoBean.getCareer());
            requestObject.put("school", infoBean.getSchool());
            requestObject.put("company", infoBean.getCompany());
            requestObject.put("parameters", keys);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        params.addBodyParameter("data", requestObject.toString());
        x.http().post(params, new CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(Throwable ex, boolean arg1) {
                // TODO Auto-generated method stub
                LogUtil.d("mytest", "error--" + ex.getMessage());
                LogUtil.d("mytest", "error--" + ex.getCause());
            }

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(String result) {
                // TODO Auto-generated method stub
                LogUtil.d("mytest", "identity==" + result);
                try {
                    JSONObject jsons = new JSONObject(result);
                    String state = jsons.getString("state");
                    if (!state.equals("200")) {
                        toastShort("修改信息失败");
                        return;
                    }
                    JSONObject data = jsons.getJSONObject("data");
//					String token = data.getString("token");
//					SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.TOKEN, token);
                    Intent intent = new Intent();
                    intent.putExtra("userInfoBean", infoBean);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.is_student_linear:
                if(!isStudent){
                    isStudent = true;
                    isStudentImv.setImageResource(R.drawable.register_shenfen_imageview_btn_light);
                    graduateImv.setImageResource(R.drawable.register_shenfen_imageview_normal);
                    identity.setText(mIdentity[0]);
                }
                break;
            case R.id.graduate_linear:
                if(isStudent){
                    isStudent = false;
                    isStudentImv.setImageResource(R.drawable.register_shenfen_imageview_normal);
                    graduateImv.setImageResource(R.drawable.register_shenfen_imageview_btn_light);
                    identity.setText(mIdentity[1]);
                }
                break;
        }
    }
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		switch (requestCode) {
		case 10:
			if(resultCode == this.RESULT_OK){
				Schools school = (Schools) data.getSerializableExtra("school");
				infoBean.setSchool(school.getUnivsId());
				updateInfo("career,school,isgraduated,company");
			}
			break;
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/

}
