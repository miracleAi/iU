package com.android.biubiu.ui.discovery;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

import com.android.biubiu.BaseActivity;

import cc.imeetu.iu.R;

public class PublishHomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish_home);
    }

}
