/************************************************************
  *  * EaseMob CONFIDENTIAL 
  * __________________ 
  * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
  *  
  * NOTICE: All information contained herein is, and remains 
  * the property of EaseMob Technologies.
  * Dissemination of this information or reproduction of this material 
  * is strictly forbidden unless prior written permission is obtained
  * from EaseMob Technologies.
  */
package com.android.biubiu.chat;

import com.android.biubiu.utils.LogUtil;
import com.hyphenate.chat.EMChatService;
import com.hyphenate.util.EMLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @deprecated instead of use {@link EMReceiver}
 *
 */
public class StartServiceReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		EMLog.d("boot", "start easemob service on boot");
		LogUtil.e("StartServiceReceiver", "start");
		Intent startServiceIntent=new Intent(context, EMChatService.class);
		startServiceIntent.putExtra("reason", "boot");
		context.startService(startServiceIntent);
	}
}
