package com.android.biubiu.ui.mine.child;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import cc.imeetu.iu.R;

import com.android.biubiu.bean.Citybean;
import com.android.biubiu.bean.UserInfoBean;
import com.android.biubiu.common.city.ArrayWheelAdapter;
import com.android.biubiu.common.city.BaseCityActivity;
import com.android.biubiu.common.city.OnWheelChangedListener;
import com.android.biubiu.common.city.WheelView;
import com.android.biubiu.transport.http.HttpContants;
import com.android.biubiu.component.util.LogUtil;
import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.persistence.CityDao;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangeCityActivity extends BaseCityActivity implements
OnClickListener, OnWheelChangedListener{
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	private TextView mBtnConfirm;
	private String city;
	private RelativeLayout wanchLayout, backLayout;

	private CityDao cityDao = new CityDao();

	// 控件相关
	private TextView cityName;
	UserInfoBean infoBean;
	private String cityCode="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_city_main);
		infoBean = (UserInfoBean) getIntent().getSerializableExtra("userInfoBean");
		initView();
		city = infoBean.getCity();
		setUpViews();
		setUpListener();
		setUpData();
		//		cityName.setText("" + mCurrentProviceName + mCurrentCityName
		//				);
	}

	private void initView() {
		// TODO Auto-generated method stub
		cityName = (TextView) super.findViewById(R.id.cityName_change_city_tv);
		try {
			Citybean bean=cityDao.getCity(infoBean.getCity()).get(0);
			cityName.setText(bean.getPrivance()+"  "+bean.getCity());
		} catch (Exception e) {
			// TODO: handle exception
		}	
	}

	private void setUpViews() {
		mViewProvince = (WheelView) findViewById(R.id.id_province);
		mViewCity = (WheelView) findViewById(R.id.id_city);
		mViewDistrict = (WheelView) findViewById(R.id.id_district);
		mBtnConfirm = (TextView) findViewById(R.id.city_selector_shengshiqu_tv);
		wanchLayout = (RelativeLayout) super
				.findViewById(R.id.city_selector_shengshiqu_rl);
		wanchLayout.setOnClickListener(this);
		backLayout = (RelativeLayout) super
				.findViewById(R.id.back_changecity_mine_rl);
		backLayout.setOnClickListener(this);
	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// ���change�¼�
		mViewCity.addChangingListener(this);
		// ���change�¼�
		//	mViewDistrict.addChangingListener(this);
		// ���onclick�¼�
		mBtnConfirm.setOnClickListener(this);
	}

	private void setUpData() {
		// initProvinceDatas();
		initProvinceDatasNews();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(
				ChangeCityActivity.this, mProvinceDatas));

		Log.e("lucifer", "mProvinceDatas.length==" + mProvinceDatas.length);
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		//	mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
			updateCities();
			cityName.setText("" + mCurrentProviceName + mCurrentCityName
					);
		} else if (wheel == mViewCity) {
			updateAreas();
			cityName.setText("" + mCurrentProviceName + mCurrentCityName
					);
		} else if (wheel == mViewDistrict) {
			// mCurrentDistrictName =
			// mDistrictDatasMap.get(mCurrentCityName)[newValue];
			// mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
			int pCurrent = mViewDistrict.getCurrentItem();
			List<Citybean> townList = new ArrayList<Citybean>();
			townList = cityDao
					.getAllTown(mCurrentProviceName, mCurrentCityName);
			String[] towns = new String[townList.size()];
			for (int i = 0; i < townList.size(); i++) {
				towns[i] = townList.get(i).getTown();
			}
			mCurrentDistrictName = towns[pCurrent];

			cityName.setText("" + mCurrentProviceName + mCurrentCityName
					+ mCurrentDistrictName);
		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	@SuppressWarnings("unused")
	private void updateAreas() {
		Citybean city = new Citybean();
		List<Citybean> provinceList = new ArrayList<Citybean>();
		List<Citybean> cityList = new ArrayList<Citybean>();
		List<Citybean> townList = new ArrayList<Citybean>();

		int pCurrent = mViewCity.getCurrentItem();
		cityList = cityDao.getAllCity(mCurrentProviceName);
		// cityList=cityDao.getAllCity(mCurrentProviceName);
		String[] citys = new String[cityList.size()];

		for (int i = 0; i < cityList.size(); i++) {
			citys[i] = cityList.get(i).getCity();
		}
		mCurrentCityName = cityList.get(pCurrent).getCity();

	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	@SuppressWarnings("unused")
	private void updateCities() {
		Citybean city = new Citybean();
		List<Citybean> provinceList = new ArrayList<Citybean>();
		List<Citybean> cityList = new ArrayList<Citybean>();
		List<Citybean> townList = new ArrayList<Citybean>();

		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		Log.e("lucifer", "mCurrentProviceName==" + mCurrentProviceName);

		cityList = cityDao.getAllCity(mCurrentProviceName);
		String[] cities = new String[cityList.size()];
		for (int i = 0; i < cityList.size(); i++) {
			cities[i] = cityList.get(i).getCity();
		}
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}
	/**
	 * 修改个人信息
	 */
	private void updateInfo() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams(HttpContants.HTTP_ADDRESS+HttpContants.UPDATE_USETINFO);
		String token = SharePreferanceUtils.getInstance().getToken(getApplicationContext(), SharePreferanceUtils.TOKEN, "");
		String deviceId = SharePreferanceUtils.getInstance().getDeviceId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, "");
		JSONObject requestObject = new JSONObject();
		try {
			requestObject.put("token", token);
			requestObject.put("device_code", deviceId);
			requestObject.put("city", infoBean.getCity());
			requestObject.put("cityf", cityCode);
			requestObject.put("parameters", "city,cityf");
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
				LogUtil.d("mytest", "error--"+ex.getMessage());
				LogUtil.d("mytest", "error--"+ex.getCause());
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				LogUtil.d("mytest", "city=="+result);
				try {
					JSONObject jsons = new JSONObject(result);
					String state = jsons.getString("state");
					if(!state.equals("200")){
						toastShort("修改信息失败");
						return ;
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
		case R.id.city_selector_shengshiqu_rl:
			if(null == cityName.getText() || cityName.getText().toString().equals("")){
				return;
			}
			String cityiId=cityDao.getID(mCurrentProviceName, mCurrentCityName).get(0).getId();
			cityCode=cityDao.getID(mCurrentProviceName, mCurrentCityName).get(0).getCity_num();
			infoBean.setCity(cityiId);
			updateInfo();
			break;
		case R.id.back_changecity_mine_rl:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置点击返回键的状态
	 */
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
}






