package com.android.biubiu.activity.act;

import android.os.Bundle;
import android.view.Window;

import cc.imeetu.iu.R;

import com.android.biubiu.BaseActivity;
import com.android.biubiu.common.Constant;

public class WebviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);
        WebviewActivityFragment fragment = new WebviewActivityFragment();
        Bundle b = new Bundle();
        b.putString(Constant.ACTIVITY_NAME, getIntent().getStringExtra(Constant.ACTIVITY_NAME));
        b.putString(Constant.ACTIVITY_COVER, getIntent().getStringExtra(Constant.ACTIVITY_COVER));
        b.putString(Constant.ACTIVITY_URL, getIntent().getStringExtra(Constant.ACTIVITY_URL));
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().add(R.id.layout_body, fragment).commit();
    }

}
