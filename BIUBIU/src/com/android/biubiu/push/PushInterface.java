package com.android.biubiu.push;

import com.android.biubiu.bean.BiuBean;
import com.android.biubiu.bean.UserBean;
import com.android.biubiu.bean.UserInfoBean;

public interface PushInterface {
	void updateView(BiuBean userBean, int type);
}
