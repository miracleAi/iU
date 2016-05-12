package com.android.biubiu.activity.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import cc.imeetu.iu.R;

public class ActivityListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_activity_list);
    }

}
