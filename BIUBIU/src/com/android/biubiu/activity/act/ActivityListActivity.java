package com.android.biubiu.activity.act;

import android.os.Bundle;
import android.view.Window;

import com.android.biubiu.BaseActivity;
import com.android.biubiu.common.Umutils;

import cc.imeetu.iu.R;

public class ActivityListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_activity_list);
        Umutils.count(this,Umutils.ACTY_LIST_INTO);
    }

}
