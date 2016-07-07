package com.android.biubiu.ui.overall;

import cc.imeetu.iu.R;


import com.android.biubiu.component.util.SharePreferanceUtils;
import com.android.biubiu.persistence.base.DBManager;
import com.android.biubiu.persistence.DBManagerCity;
import com.avos.avoscloud.LogUtil.log;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends BaseActivity {
	private String TAG="WelcomeActivity";
	private boolean isFirstInstall = false;
	//导入城市数据库到本地

	private  DBManagerCity dbHelperCity;
	//导入学校数据库到本地
	private DBManager dbHelperSchool;

	Handler handler ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		handler = new Handler();
		//读取设备ID
		//SharePreferanceUtils.getInstance().putShared(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, Utils.getDeviceID(getApplicationContext()));
		log.e(TAG, SharePreferanceUtils.getInstance().getChannelId(getApplicationContext(), SharePreferanceUtils.DEVICE_ID, ""));
		next();
	
		
	}

	private void next() {
		isFirstInstall = SharePreferanceUtils.getInstance().isFirstInstall(getApplicationContext(), SharePreferanceUtils.IS_FIRST_INSTALL, true);
		if(isFirstInstall){
			goIndex();
		}else{
			goHome();
		}
	}

	/**
	 * 首次进入app 进行的操作
	 */
	private void goIndex() {
		// 导入数据库
		dbHelperCity = new DBManagerCity(this);
		dbHelperCity.openDatabase();
		dbHelperCity.closeDatabase();

		dbHelperSchool=new DBManager(this);
		dbHelperSchool.openDatabase();
		dbHelperSchool.closeDatabase();
		
		Intent intent = new Intent(WelcomeActivity.this,GuildActivity.class);
		startActivity(intent);
		finish();
	}

	private void goHome() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 允许用户使用应用
				/*if(!LoginUtils.isLogin(WelcomeActivity.this)){
					Intent intent = new Intent(WelcomeActivity.this,LoginOrRegisterActivity.class);
					intent.putExtra("tag",true);
					startActivity(intent);
				}else{*/
					Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
					startActivity(intent);
			//	}
				finish();
			}
		}, 2000);
	}

}
